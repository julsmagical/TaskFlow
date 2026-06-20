package com.springboot.taskflow.taskflow.responses;

import java.time.LocalDateTime;
import java.util.UUID;

import com.springboot.taskflow.taskflow.entities.Project;
import com.springboot.taskflow.taskflow.enums.ProjectStatus;


public record ProjectResponse(
    UUID id,
    String name,
    String description,
    ProjectStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static ProjectResponse from(Project project) {
        return new ProjectResponse(
            project.getId(),
            project.getName(),
            project.getDescription(),
            project.getStatus(),
            project.getAudit().getCreatedAt(),
            project.getAudit().getUpdatedAt()
        );
    }
}