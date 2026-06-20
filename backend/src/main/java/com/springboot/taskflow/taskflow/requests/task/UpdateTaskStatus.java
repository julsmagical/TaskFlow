package com.springboot.taskflow.taskflow.requests.task;

import com.springboot.taskflow.taskflow.enums.TaskStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateTaskStatus(
    @NotNull(message = "El estado es obligatorio.")
    TaskStatus newStatus
) {}