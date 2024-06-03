package com.junt.checkdomain.service.impl;

import com.junt.checkdomain.model.Domain;
import com.junt.checkdomain.model.Status;
import com.junt.checkdomain.repository.StatusRepository;
import com.junt.checkdomain.service.StatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatusServiceImpl implements StatusService {
    private final StatusRepository statusRepository;

    @Override
    public List<Status> getAllStatus() {
        return statusRepository.findAll();
    }

    @Override
    public Status getStatusById(int id) {
        return statusRepository.findById(id).orElse(null);
    }

    @Override
    public Status save(Status status) {
        return statusRepository.save(status);
    }

    @Override
    public boolean deleteStatusById(int id) {
        Status st = getStatusById(id);
        if (st != null) {
            statusRepository.delete(st);
            return true;
        }
        return false;
    }

    @Override
    public Domain getDomainByStatus(Status status) {
        return null;
    }
}
