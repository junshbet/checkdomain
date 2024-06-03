package com.junt.checkdomain.service.impl;

import com.junt.checkdomain.model.Proxy;
import com.junt.checkdomain.repository.ProxyRepository;
import com.junt.checkdomain.service.ProxyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProxyServiceImpl implements ProxyService {

    private final ProxyRepository proxyRepository;

    @Override
    public List<Proxy> getAllProxies() {
        return proxyRepository.findAll();
    }

    @Override
    public Proxy getProxyById(long id) {
        return proxyRepository.findById(id).orElse(null);
    }

    @Override
    public Proxy save(Proxy proxy) {
        return proxyRepository.save(proxy);
    }

    @Override
    public Proxy getProxyByName(String name) {
        return proxyRepository.findByName(name).orElse(null);
    }

    @Override
    public boolean deleteProxyById(long id) {
        Proxy proxy = proxyRepository.findById(id).orElse(null);
        if (proxy != null) {
            proxyRepository.delete(proxy);
            return true;
        }
        return false;
    }
}
