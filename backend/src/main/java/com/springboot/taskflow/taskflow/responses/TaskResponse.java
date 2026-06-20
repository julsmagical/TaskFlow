package com.springboot.taskflow.taskflow.responses;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.springboot.taskflow.taskflow.entities.Task;
import com.springboot.taskflow.taskflow.enums.TaskPriority;
import com.springboot.taskflow.taskflow.enums.TaskStatus;

public record TaskResponse(
    UUID id,
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
        return new TaskResponse(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getPriority(),
            task.getStatus(),
            task.getDueDate(),
            task.getAssignedUser().getId(),
            task.getAssignedUser().getUsername(),
            task.getAudit().getCreatedAt(),
            task.getAudit().getUpdatedAt()
        );
    }
}
