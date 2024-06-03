package com.junt.checkdomain.service;

import com.junt.checkdomain.model.Check;
import com.junt.checkdomain.model.Domain;
import com.junt.checkdomain.model.Proxy;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CheckService {
    List<Check> getAllCheck();
    List<Check> getAllCheckByDomain(Domain domain);
    Check getCheckById(long id);
    Mono<Void> deleteChecksByDomain(Domain domain);
    void delChecksByDomain(Domain domain);
    Mono<Check> saveReactive(Check check);
    Check save(Check check);
    boolean deleteCheckById(long id);
    Check getCheckByProxy(Proxy proxy);
}
