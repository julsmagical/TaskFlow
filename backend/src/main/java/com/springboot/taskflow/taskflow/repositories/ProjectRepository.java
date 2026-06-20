package com.springboot.taskflow.taskflow.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.taskflow.taskflow.entities.Project;
import com.springboot.taskflow.taskflow.enums.ProjectStatus;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    List<Project> findByStatusAndAuditDeletedAtIsNull(ProjectStatus status);

    List<Project> findByAuditDeletedAtIsNull();

    Optional<Project> findByIdAndAuditDeletedAtIsNull(UUID id);
}