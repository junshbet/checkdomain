package com.junt.checkdomain.service.impl;

import com.junt.checkdomain.model.Domain;
import com.junt.checkdomain.model.User;
import com.junt.checkdomain.repository.DomainRepository;
import com.junt.checkdomain.service.DomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DomainServiceImpl implements DomainService {

    private final DomainRepository domainRepository;
    @Override
    public List<Domain> getAllDomains() {
        return domainRepository.findAll();
    }

    @Override
    public Domain getDomainById(long id) {
        return domainRepository.findById(id).orElse(null);
    }


    @Override
    public Mono<Domain> getDomainByIdMono(long id) {
        return Mono.fromCallable(() -> domainRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalDomain -> optionalDomain
                        .map(Mono::just)
                        .orElseGet(() -> Mono.error(new Exception("Domain not found with id: " + id))));
    }

    @Override
    public Domain save(Domain domain) {
        return domainRepository.save(domain);
    }

    @Override
    public Domain getDomainByNameAndUser(String domain, User user) {
        return domainRepository.getFirstByDomainAndUser(domain,user);
    }

    @Override
    public void delete(Domain domain) {
        domainRepository.delete(domain);
    }
}
