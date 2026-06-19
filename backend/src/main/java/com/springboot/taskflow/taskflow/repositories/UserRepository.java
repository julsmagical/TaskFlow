package com.springboot.taskflow.taskflow.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.springboot.taskflow.taskflow.entities.User;

public interface UserRepository extends CrudRepository<User, UUID> {

    Optional<User> findByUsername(String username);

}
