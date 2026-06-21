package com.springboot.taskflow.taskflow.controllers;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.taskflow.taskflow.enums.ProjectStatus;
import com.springboot.taskflow.taskflow.requests.ProjectRequest;
import com.springboot.taskflow.taskflow.responses.ApiResponse;
import com.springboot.taskflow.taskflow.responses.ProjectResponse;
import com.springboot.taskflow.taskflow.security.AuthHelper;
import com.springboot.taskflow.taskflow.security.RequireLiderOrAdmin;
import com.springboot.taskflow.taskflow.services.ProjectService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> findAll(
            @RequestParam(required = false) ProjectStatus estado,
            Authentication authentication) {

        UUID userId = AuthHelper.getCurrentUserId(authentication);
        String role = AuthHelper.getCurrentRole(authentication);

        var projects = projectService.findAll(userId, role, Optional.ofNullable(estado));
        return ResponseEntity.ok(ApiResponse.success(projects, "Proyectos obtenidos con éxito."));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> findById(@PathVariable UUID id, Authentication authentication) {
        UUID userId = AuthHelper.getCurrentUserId(authentication);
        String role = AuthHelper.getCurrentRole(authentication);

        ProjectResponse project = projectService.findById(id, userId, role);
        return ResponseEntity.ok(ApiResponse.success(project, "Proyecto obtenido con éxito."));
    }

    @PostMapping
    @RequireLiderOrAdmin
    public ResponseEntity<ApiResponse<?>> create(
            @Valid @RequestBody ProjectRequest request,
            Authentication authentication) {

        UUID userId = AuthHelper.getCurrentUserId(authentication);
        ProjectResponse created = projectService.create(request, userId);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(created, "Proyecto creado con éxito."));
    }

    @PutMapping("/{id}")
    @RequireLiderOrAdmin
    public ResponseEntity<ApiResponse<?>> update(
            @PathVariable UUID id,
            @Valid @RequestBody ProjectRequest request,
            Authentication authentication) {

        UUID userId = AuthHelper.getCurrentUserId(authentication);
        String role = AuthHelper.getCurrentRole(authentication);

        ProjectResponse updated = projectService.update(id, request, userId, role);
        return ResponseEntity.ok(ApiResponse.success(updated, "Proyecto actualizado con éxito."));
    }

    @PatchMapping("/{id}/archive")
    @RequireLiderOrAdmin
    public ResponseEntity<ApiResponse<?>> archive(@PathVariable UUID id, Authentication authentication) {
        UUID userId = AuthHelper.getCurrentUserId(authentication);
        String role = AuthHelper.getCurrentRole(authentication);

        ProjectResponse archived = projectService.archive(id, userId, role);
        return ResponseEntity.ok(ApiResponse.success(archived, "Proyecto archivado con éxito."));
    }

    @DeleteMapping("/{id}")
    @RequireLiderOrAdmin
    public ResponseEntity<Void> delete(@PathVariable UUID id, Authentication authentication) {
        UUID userId = AuthHelper.getCurrentUserId(authentication);
        String role = AuthHelper.getCurrentRole(authentication);

        projectService.delete(id, userId, role);
        return ResponseEntity.noContent().build();
    }
}