package com.junt.checkdomain.repository;

import com.junt.checkdomain.model.Domain;
import com.junt.checkdomain.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends JpaRepository<Status,Integer > {
    Domain getFirstByStatus(Status status);
}
