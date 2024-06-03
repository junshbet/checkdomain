package com.junt.checkdomain.api;

import com.junt.checkdomain.model.*;
import com.junt.checkdomain.request.DomainCheckRequest;
import com.junt.checkdomain.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CheckRestController {

    private final DomainService domainService;
    private final StatusService statusService;
    private final ProxyService proxyService;
    private final CheckService checkService;
    private final CheckDomainService checkDomainService;
    private final UserService userService;

    @GetMapping("/clearData") // Xóa toàn bộ dữ liệu check
    public ResponseEntity<?> clearData() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = new User();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            user = userService.getUserByUsername(userDetails.getUsername());
        }
        List<Domain> domains = user.getDomains();
        for (int i = 0; i < domains.size(); i++) {
            List<Check> checks = checkService.getAllCheckByDomain(domains.get(i));
            for (Check check : checks) {
                try {
                    checkService.deleteCheckById(check.getId());
                } catch (EmptyResultDataAccessException e) {
                    System.out.println("Check entity with id " + check.getId() + " not found, skipping.");
                }
            }
        }
        domains.forEach(domain -> {
            domain.setCheckTime(null);
            domain.setStatus(null);
            domainService.save(domain);
        });
        return ResponseEntity.ok().build();
    }
    @GetMapping("/getDomain")
    public ResponseEntity<Domain> getDomain(@RequestParam("id") int id) {
        Domain domain = domainService.getDomainById(id);
        if (domain != null) {
            return ResponseEntity.ok(domain);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/checkAll")
    @Transactional
    public Mono<ResponseEntity<List<Domain>>> checkAll(@RequestBody DomainCheckRequest request) {
        List<Integer> arrDomain = request.getArrDomain();
        List<Proxy> lstProxy = proxyService.getAllProxies();

        Mono<Void> deleteChecksMono = Flux.fromIterable(arrDomain)
                .flatMap(id -> {
                    Domain domain = domainService.getDomainById(id);
                    if (domain != null) {
                        domain.setCheckTime(null);
                        domain.setStatus(null);
                        domainService.save(domain);
                        return checkService.deleteChecksByDomain(domain).then();
                    } else {
                        return Mono.empty();
                    }
                })
                .then();
        return deleteChecksMono
                .thenMany(Flux.fromIterable(arrDomain)
                        .flatMap(id -> domainService.getDomainByIdMono(id)
                                .flatMap(domain -> {
                                    return checkDomainWithProxies(domain, lstProxy)
                                            .flatMap(dm -> {
                                                List<Check> checkList = dm.getCheckList();
                                                if (checkList == null) {
                                                    checkList = new ArrayList<>();
                                                }

                                                List<Mono<Check>> newListMonos = checkList.stream()
                                                        .map(check -> {
                                                            check.setDomain(dm); // Thiết lập domain trước khi lưu
                                                            return checkService.saveReactive(check); // Sử dụng phương thức reactive
                                                        })
                                                        .collect(Collectors.toList());

                                                return Flux.concat(newListMonos)
                                                        .collectList()
                                                        .flatMap(savedChecks -> {
                                                            dm.setCheckList(savedChecks);
                                                            dm.setCheckTime(Timestamp.from(Instant.now()));

                                                            // Sau khi đã lưu checks mới, tiến hành lưu domain
                                                            return Mono.fromCallable(() -> domainService.save(dm))
                                                                    .subscribeOn(Schedulers.boundedElastic()); // Sử dụng boundedElastic để chạy trên thread khác
                                                        });
                                            });
                                })
                        )
                )
                .collectList()
                .map(ResponseEntity::ok);
    }



    private Mono<Domain> checkDomainWithProxies(Domain domain, List<Proxy> proxies) {
        List<Mono<Check>> proxyChecks = proxies.stream()
                .map(proxy -> checkDomainService.getStatusCodeFromExternalApi(
                                domain.getDomain(), proxy.getProxy(), proxy.getPort(), proxy.getUsername(), proxy.getPassword())
                        .map(status -> {
                            Check check = new Check();
                            check.setName(proxy.getName());
                            Integer statusCode = (Integer) status.get("status");
                            Status st = statusService.getStatusById(statusCode);
                            if (st == null) {
                                st = new Status();
                                st.setStatus(statusCode);
                                st.setMessage("");
                                st = statusService.save(st);
                            }
                            check.setStatus(st);
                            check.setDomain(null);
                            check.setProxy(proxy);
                            return check;
                        })
                )
                .collect(Collectors.toList());

        return Flux.merge(proxyChecks)
                .collectList()
                .map(checks -> {
                    domain.setCheckList(checks);
                    domain.setStatus(checks.stream()
                            .map(Check::getStatus)
                            .max(Comparator.comparingInt(Status::getStatus))
                            .orElse(null));
                    return domain;
                });
    }

}
