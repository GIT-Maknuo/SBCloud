package com.sbcloud.users.repositories;

import org.springframework.data.repository.CrudRepository;

import com.sbcloud.users.entities.User;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
}

