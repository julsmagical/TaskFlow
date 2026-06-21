package com.springboot.taskflow.taskflow.responses;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.springboot.taskflow.taskflow.entities.Task;
import com.springboot.taskflow.taskflow.enums.TaskPriority;
import com.springboot.taskflow.taskflow.enums.TaskStatus;

public record TaskResponse(
    UUID id,
    UUID projectId,
    String title,
    String description,
    TaskPriority priority,
    TaskStatus status,
    LocalDate dueDate,
    UUID assignedUserID,
    String assignedUsername,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static TaskResponse from(Task task) {
        var user = task.getAssignedUser();
        return new TaskResponse(
            task.getId(),
            task.getProject().getId(),
            task.getTitle(),
            task.getDescription(),
            task.getPriority(),
            task.getStatus(),
            task.getDueDate(),
            user != null ? user.getId() : null,
            user != null ? user.getUsername() : null,
            task.getAudit().getCreatedAt(),
            task.getAudit().getUpdatedAt()
        );
    }
}
