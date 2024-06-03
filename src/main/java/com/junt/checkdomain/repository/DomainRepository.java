package com.junt.checkdomain.repository;

import com.junt.checkdomain.model.Domain;
import com.junt.checkdomain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DomainRepository extends JpaRepository<Domain,Long> {
    Domain getFirstByDomainAndUser(String name, User user);
}