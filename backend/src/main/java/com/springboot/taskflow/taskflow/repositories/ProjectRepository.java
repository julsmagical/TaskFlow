package com.springboot.taskflow.taskflow.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.taskflow.taskflow.entities.Project;
import com.springboot.taskflow.taskflow.enums.ProjectStatus;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    List<Project> findByAuditDeletedAtIsNull();

    List<Project> findByStatusAndAuditDeletedAtIsNull(ProjectStatus status);

    List<Project> findByLeaderIdAndAuditDeletedAtIsNull(UUID leaderId);

    List<Project> findByLeaderIdAndStatusAndAuditDeletedAtIsNull(UUID leaderId, ProjectStatus status);

    Optional<Project> findByIdAndAuditDeletedAtIsNull(UUID id);

    List<Project> findDistinctByTasksAssignedUserIdAndAuditDeletedAtIsNull(UUID assignedUserId);

    List<Project> findDistinctByTasksAssignedUserIdAndStatusAndAuditDeletedAtIsNull(UUID assignedUserId,ProjectStatus status);
}