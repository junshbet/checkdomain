package com.junt.checkdomain.service;

import com.junt.checkdomain.model.Proxy;

import java.util.List;

public interface ProxyService {
    List<Proxy> getAllProxies();
    Proxy getProxyById(long id);
    Proxy save(Proxy proxy);
    Proxy getProxyByName(String name);
    boolean deleteProxyById(long id);
}
