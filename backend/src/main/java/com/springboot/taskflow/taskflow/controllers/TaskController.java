package com.springboot.taskflow.taskflow.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.taskflow.taskflow.enums.TaskPriority;
import com.springboot.taskflow.taskflow.enums.TaskStatus;
import com.springboot.taskflow.taskflow.requests.task.CreateTaskRequest;
import com.springboot.taskflow.taskflow.requests.task.UpdateTaskRequest;
import com.springboot.taskflow.taskflow.requests.task.UpdateTaskStatusRequest;
import com.springboot.taskflow.taskflow.responses.ApiResponse;
import com.springboot.taskflow.taskflow.responses.TaskResponse;
import com.springboot.taskflow.taskflow.security.AuthHelper;
import com.springboot.taskflow.taskflow.security.RequireLiderOrAdmin;
import com.springboot.taskflow.taskflow.security.RequireRole;
import com.springboot.taskflow.taskflow.services.TaskService;

import jakarta.validation.Valid;

@RestController
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // POST /api/projects/{projectId}/tasks — Crear tarea en un proyecto
    @PostMapping("/api/projects/{projectId}/tasks")
    @RequireLiderOrAdmin
    public ResponseEntity<ApiResponse<TaskResponse>> create(
            @PathVariable UUID projectId,
            @RequestBody @Valid CreateTaskRequest request,
            Authentication authentication) {

        UUID userId = AuthHelper.getCurrentUserId(authentication);
        String role = AuthHelper.getCurrentRole(authentication);

        ApiResponse<TaskResponse> response = taskService.create(projectId, request, userId, role);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/projects/{projectId}/tasks — Listar tareas de un proyecto
    @GetMapping("/api/projects/{projectId}/tasks")
    @RequireRole
    public ResponseEntity<ApiResponse<List<TaskResponse>>> findByProject(
            @PathVariable UUID projectId,
            @RequestParam(required = false) TaskStatus estado,
            @RequestParam(required = false) TaskPriority prioridad,
            Authentication authentication) {

        UUID userId = AuthHelper.getCurrentUserId(authentication);
        String role = AuthHelper.getCurrentRole(authentication);

        var response = taskService.findByProject(
            projectId,
            Optional.ofNullable(estado),
            Optional.ofNullable(prioridad),
            userId,
            role
        );
        return ResponseEntity.ok(response);
    }

    // GET /api/tasks/{id} — Obtener una tarea por id
    @GetMapping("/api/tasks/{id}")
    @RequireRole
    public ResponseEntity<ApiResponse<TaskResponse>> findById(
            @PathVariable UUID id,
            Authentication authentication) {

        UUID userId = AuthHelper.getCurrentUserId(authentication);
        String role = AuthHelper.getCurrentRole(authentication);

        return ResponseEntity.ok(taskService.findById(id, userId, role));
    }

    // PUT /api/tasks/{id} — Actualizar una tarea
    @PutMapping("/api/tasks/{id}")
    @RequireLiderOrAdmin
    public ResponseEntity<ApiResponse<TaskResponse>> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateTaskRequest request,
            Authentication authentication) {

        UUID userId = AuthHelper.getCurrentUserId(authentication);
        String role = AuthHelper.getCurrentRole(authentication);

        return ResponseEntity.ok(taskService.update(id, request, userId, role));
    }

    // PATCH /api/tasks/{id}/status — Cambiar el estado de una tarea
    @PatchMapping("/api/tasks/{id}/status")
    @RequireRole
    public ResponseEntity<ApiResponse<TaskResponse>> updateStatus(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateTaskStatusRequest request,
            Authentication authentication) {

        UUID userId = AuthHelper.getCurrentUserId(authentication);
        String role = AuthHelper.getCurrentRole(authentication);

        return ResponseEntity.ok(taskService.updateStatus(id, request, userId, role));
    }

    // DELETE /api/tasks/{id} — Eliminar una tarea (soft delete)
    @DeleteMapping("/api/tasks/{id}")
    @RequireLiderOrAdmin
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            Authentication authentication) {

        UUID userId = AuthHelper.getCurrentUserId(authentication);
        String role = AuthHelper.getCurrentRole(authentication);

        taskService.delete(id, userId, role);
        return ResponseEntity.noContent().build();
    }
}