package com.junt.checkdomain.service.impl;


import com.junt.checkdomain.model.Check;
import com.junt.checkdomain.model.Domain;
import com.junt.checkdomain.model.Proxy;
import com.junt.checkdomain.repository.CheckRepository;
import com.junt.checkdomain.service.CheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckServiceImpl implements CheckService {
    private final CheckRepository checkRepository;

    @Override
    public List<Check> getAllCheck() {
        return checkRepository.findAll();
    }

    @Override
    public List<Check> getAllCheckByDomain(Domain domain) {
        return checkRepository.getAllByDomain(domain);
    }

    @Override
    public Check getCheckById(long id) {
        return checkRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Mono<Void> deleteChecksByDomain(Domain domain) {
        return Mono.fromRunnable(() -> checkRepository.deleteByDomain(domain))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    @Override
    public void delChecksByDomain(Domain domain) {
        checkRepository.deleteByDomain(domain);
    }

    @Override
    public Mono<Check> saveReactive(Check check) {
        return Mono.fromCallable(() -> checkRepository.save(check))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Check save(Check check) {
        return checkRepository.save(check);
    }

    @Override
    public boolean deleteCheckById(long id) {
        Check check = checkRepository.findById(id).orElse(null);
        if (check != null) {
            checkRepository.delete(check);
            return true;
        }
        return false;
    }

    @Override
    public Check getCheckByProxy(Proxy proxy) {
        return checkRepository.getFirstByProxy(proxy);
    }
}
