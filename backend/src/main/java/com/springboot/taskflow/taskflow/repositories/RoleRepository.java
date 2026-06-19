package com.springboot.taskflow.taskflow.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.springboot.taskflow.taskflow.entities.Role;

public interface RoleRepository extends CrudRepository<Role, UUID>{

    Optional<Role> findById(UUID id);

    Optional<Role> findByName(String name);
}
