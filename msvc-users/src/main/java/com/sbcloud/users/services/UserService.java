package com.sbcloud.users.services;

import java.util.List;
import java.util.Optional;

import com.sbcloud.users.entities.User;

public interface UserService {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    List<User> findAll();
    void delete(Long id);
    Optional<User> update(Long id, User user);
}

