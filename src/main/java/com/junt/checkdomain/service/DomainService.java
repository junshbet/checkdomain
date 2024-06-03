package com.junt.checkdomain.service;

import com.junt.checkdomain.model.Domain;
import com.junt.checkdomain.model.User;
import reactor.core.publisher.Mono;

import java.util.List;

public interface DomainService {
    List<Domain> getAllDomains();
    Domain getDomainById(long id);
    Mono<Domain> getDomainByIdMono(long id);
    Domain save(Domain domain);
    Domain getDomainByNameAndUser(String domain, User user);
    void delete(Domain domain);
}
