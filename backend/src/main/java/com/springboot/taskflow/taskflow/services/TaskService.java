package com.springboot.taskflow.taskflow.services;

import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import com.springboot.taskflow.taskflow.entities.Task;
import com.springboot.taskflow.taskflow.entities.User;
import com.springboot.taskflow.taskflow.enums.TaskStatus;
import com.springboot.taskflow.taskflow.exceptions.NotFoundException;
import com.springboot.taskflow.taskflow.repositories.TaskRepository;
import com.springboot.taskflow.taskflow.repositories.UserRepository;
import com.springboot.taskflow.taskflow.requests.task.CreateTaskRequest;
import com.springboot.taskflow.taskflow.responses.ApiResponse;
import com.springboot.taskflow.taskflow.responses.TaskResponse;
import com.springboot.taskflow.taskflow.shared.TaskMapper;

public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository){
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ApiResponse<TaskResponse> create(CreateTaskRequest request){
        var task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setPriority(request.priority());
        task.setStatus(TaskStatus.PENDIENTE);
        task.setDueDate(request.dueDate());
        task.setAssignedUser(assignUser(request.assignedUserId()));
        
        taskRepository.save(task);
        return ApiResponse.success(TaskMapper.toResponse(task), "Tarea actualizada exitosamente.");
    }

    public ApiResponse<TaskResponse> findById(UUID id) {
        var task = findTaskOrThrow(id);
        return ApiResponse.success(TaskMapper.toResponse(task), "Tarea obtenida exitosamente.");
    }

    // Helpers
    private Task findTaskOrThrow(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tarea no encontrada."));
    }

    private User assignUser(UUID userId) {
        if (userId == null) return null;
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario asignado no encontrado."));
    }

    @Transactional
    public void delete(UUID id) {
        var task = findTaskOrThrow(id);
        taskRepository.delete(task);
    }
}
