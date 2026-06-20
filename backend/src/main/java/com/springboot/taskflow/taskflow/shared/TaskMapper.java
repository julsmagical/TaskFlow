package com.springboot.taskflow.taskflow.shared;

import com.springboot.taskflow.taskflow.entities.Task;
import com.springboot.taskflow.taskflow.responses.TaskResponse;

public final class TaskMapper {

    private TaskMapper() {}

    public static TaskResponse toResponse(Task task) {
        var assignedUser = task.getAssignedUser();
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                task.getStatus(),
                task.getDueDate(),
                assignedUser != null ? assignedUser.getId() : null,
                assignedUser != null ? assignedUser.getUsername() : null,
                task.getAudit().getCreatedAt(),
                task.getAudit().getUpdatedAt()
        );
    }
}
