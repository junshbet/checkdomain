package com.junt.checkdomain.service;


import com.junt.checkdomain.model.Domain;
import com.junt.checkdomain.model.Status;

import java.util.List;

public interface StatusService {
    List<Status> getAllStatus();
    Status getStatusById(int id);
    Status save(Status status);
    boolean deleteStatusById(int id);
    Domain getDomainByStatus(Status status);
}
