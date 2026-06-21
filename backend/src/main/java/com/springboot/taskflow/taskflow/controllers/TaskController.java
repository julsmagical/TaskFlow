package com.springboot.taskflow.taskflow.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.taskflow.taskflow.requests.task.UpdateTaskRequest;
import com.springboot.taskflow.taskflow.requests.task.UpdateTaskStatusRequest;
import com.springboot.taskflow.taskflow.responses.ApiResponse;
import com.springboot.taskflow.taskflow.responses.TaskResponse;
import com.springboot.taskflow.taskflow.services.TaskService;

import jakarta.validation.Valid;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }

    //POST pendiente

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.findById(id));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> update(
        @PathVariable UUID id,
        @RequestBody @Valid UpdateTaskRequest request) {
        return ResponseEntity.ok(taskService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TaskResponse>> updateStatus(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateTaskStatusRequest request) {
        return ResponseEntity.ok(taskService.updateStatus(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
