package com.junt.checkdomain.repository;

import com.junt.checkdomain.model.Check;
import com.junt.checkdomain.model.Domain;
import com.junt.checkdomain.model.Proxy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface CheckRepository extends JpaRepository<Check, Long> {
    Check getFirstByProxy(Proxy proxy);
    List<Check> getAllByDomain(Domain domain);
    @Transactional
    void deleteByDomain(Domain domain);
}
