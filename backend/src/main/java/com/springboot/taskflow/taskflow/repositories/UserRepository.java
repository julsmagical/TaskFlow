package com.springboot.taskflow.taskflow.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.taskflow.taskflow.entities.User;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    // para seleccionar desarrolladores
    List<User> findByAuditDeletedAtIsNullOrderByFullNameAsc();

    // para asignar tareas
    List<User> findByRole_NameAndAuditDeletedAtIsNullOrderByFullNameAsc(String roleName);
}
