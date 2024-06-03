package com.junt.checkdomain.repository;

import com.junt.checkdomain.model.Proxy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProxyRepository extends JpaRepository<Proxy, Long> {
    Optional<Proxy> findByName(String name);
}
