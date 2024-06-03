package com.junt.checkdomain.service;

import com.junt.checkdomain.model.User;

import java.util.UUID;

public interface UserService {
    User getUserById(UUID id);
    boolean existsUserByEmail(String email);
    boolean existsUserByUsername(String username);
    User getUserByUsername(String username);
    User saveUser(User user);
    boolean deleteUser(UUID id);
}
