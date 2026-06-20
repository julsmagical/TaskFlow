package com.springboot.taskflow.taskflow.requests.task;

import java.time.LocalDate;
import java.util.UUID;

import com.springboot.taskflow.taskflow.enums.TaskPriority;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateTaskRequest(

    @NotBlank
    @Size(min = 3, max = 150)
    String title,

    @Size(max = 255)
    String description,

    @NotNull
    TaskPriority priority,

    @NotNull
    @Future(message = "La fecha de vencimiento debe ser posterior a la de hoy")
    LocalDate dueDate,

    UUID assignedUserId
){}
