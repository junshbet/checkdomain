package com.junt.checkdomain.api;

import com.junt.checkdomain.model.Check;
import com.junt.checkdomain.model.Domain;
import com.junt.checkdomain.model.User;
import com.junt.checkdomain.request.DomainCheckRequest;
import com.junt.checkdomain.service.CheckService;
import com.junt.checkdomain.service.DomainService;
import com.junt.checkdomain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DomainRestController {
    private final DomainService domainService;
    private final CheckService checkService;
    private final UserService userService;
    @PostMapping("/saveDomain")
    public ResponseEntity<Domain> saveDomain(@RequestParam("domain") String domain,
                                             @RequestParam(required = false, name = "id") Optional<String> id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = new User();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
             user = userService.getUserByUsername(userDetails.getUsername());
        }
        Domain existingDomain = domainService.getDomainByNameAndUser(domain,user);
        if (existingDomain == null) {
            Domain dm = new Domain();
            id.filter(s -> !s.isBlank() && s.matches("\\d+"))
                    .ifPresent(s -> dm.setId(Long.parseLong(s)));
            dm.setDomain(domain);
            dm.setUser(user);
            return ResponseEntity.ok(domainService.save(dm));
        }
        return ResponseEntity.status(409).header("status", "unique").build();
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Domain>> getAll() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = new User();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            user = userService.getUserByUsername(userDetails.getUsername());
        }
        return ResponseEntity.ok(user.getDomains());
    }

    @PostMapping("/deleteAll")
    public ResponseEntity<?> deleteAll(@RequestBody DomainCheckRequest request) {
        List<Integer> arrDomain = request.getArrDomain();
        for (Integer id : arrDomain) {
            Domain domain = domainService.getDomainById(id);
            if (domain != null) {
                List<Check> List = checkService.getAllCheckByDomain(domain);
                for (Check check : List) {
                    checkService.deleteCheckById(check.getId());
                }
                domainService.delete(domain);
            }
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteDomain")
    public ResponseEntity deleteDomain(@RequestParam("id")long id) {
        Domain dm = domainService.getDomainById(id);
        if (dm != null) {
            List<Check> List = checkService.getAllCheckByDomain(dm);
            for (Check check : List) {
                checkService.deleteCheckById(check.getId());
            }
            domainService.delete(dm);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}