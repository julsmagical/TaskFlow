package com.springboot.taskflow.taskflow.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.taskflow.taskflow.aop.LogEjecucion;
import com.springboot.taskflow.taskflow.entities.Project;
import com.springboot.taskflow.taskflow.entities.Task;
import com.springboot.taskflow.taskflow.entities.User;
import com.springboot.taskflow.taskflow.enums.ProjectStatus;
import com.springboot.taskflow.taskflow.enums.RoleName;
import com.springboot.taskflow.taskflow.enums.TaskPriority;
import com.springboot.taskflow.taskflow.enums.TaskStatus;
import com.springboot.taskflow.taskflow.exceptions.BusinessRuleException;
import com.springboot.taskflow.taskflow.exceptions.ResourceNotFoundException;
import com.springboot.taskflow.taskflow.helpers.StatusTransitionHelper;
import com.springboot.taskflow.taskflow.repositories.ProjectRepository;
import com.springboot.taskflow.taskflow.repositories.TaskRepository;
import com.springboot.taskflow.taskflow.repositories.UserRepository;
import com.springboot.taskflow.taskflow.requests.task.CreateTaskRequest;
import com.springboot.taskflow.taskflow.requests.task.UpdateTaskRequest;
import com.springboot.taskflow.taskflow.requests.task.UpdateTaskStatusRequest;
import com.springboot.taskflow.taskflow.responses.ApiResponse;
import com.springboot.taskflow.taskflow.responses.TaskResponse;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final StatusTransitionHelper statusTransitionHelper;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository, UserRepository userRepository, StatusTransitionHelper statusTransitionHelper) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.statusTransitionHelper = statusTransitionHelper;
    }

    @LogEjecucion
    @Transactional
    public ApiResponse<TaskResponse> create(UUID projectId, CreateTaskRequest request, UUID currentUserId, String currentUserRole) {
        Project project = getProjectOrThrow(projectId);
        assertCanModifyProject(project, currentUserId, currentUserRole);

        if (project.getStatus() == ProjectStatus.ARCHIVADO) {
            throw new BusinessRuleException("No se pueden crear tareas en un proyecto archivado.");
        }

        User assignedUser = resolveUser(request.assignedUserId());
        if (assignedUser != null) {
            assertUserHasAllowedRole(assignedUser);
        }

        var task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setPriority(request.priority());
        task.setStatus(TaskStatus.PENDIENTE);
        task.setDueDate(request.dueDate());
        task.setProject(project);
        task.setAssignedUser(assignedUser);

        task.setStatus(assignedUser != null ? TaskStatus.EN_PROGRESO : TaskStatus.PENDIENTE);
        taskRepository.save(task);
        return ApiResponse.success(TaskResponse.from(task), "Tarea creada exitosamente.");
    }

    @LogEjecucion
    public ApiResponse<List<TaskResponse>> findByProject(UUID projectId, Optional<TaskStatus> status, Optional<TaskPriority> priority, UUID currentUserId, String currentUserRole) {
        Project project = getProjectOrThrow(projectId);
        assertCanAccessProject(project, currentUserId, currentUserRole);

        List<Task> tasks = queryTasks(projectId, status, priority);

        // Los desarrolladores solo ven sus propias tareas
        if (RoleName.DESARROLLADOR.name().equals(currentUserRole)) {
            tasks = tasks.stream()
                .filter(t -> t.getAssignedUser() != null && t.getAssignedUser().getId().equals(currentUserId))
                .toList();
        }

        var response = tasks.stream().map(TaskResponse::from).toList();
        return ApiResponse.success(response, "Tareas del proyecto obtenidas exitosamente.");
    }

    // ver detalles
    @LogEjecucion
    public ApiResponse<TaskResponse> findById(UUID id, UUID currentUserId, String currentUserRole) {
        Task task = getTaskOrThrow(id);
        assertCanAccessTask(task, currentUserId, currentUserRole);
        return ApiResponse.success(TaskResponse.from(task), "Tarea obtenida exitosamente.");
    }

    // solo lider o admin
    @LogEjecucion
    @Transactional
    public ApiResponse<TaskResponse> update(UUID id, UpdateTaskRequest request, UUID currentUserId, String currentUserRole) {
        Task task = getTaskOrThrow(id);
        assertCanModifyProject(task.getProject(), currentUserId, currentUserRole);

        User assignedUser = resolveUser(request.assignedUserId());
        if (assignedUser != null) {
            assertUserHasAllowedRole(assignedUser);
        }

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setPriority(request.priority());
        task.setDueDate(request.dueDate());
        task.setAssignedUser(assignedUser);

        if (assignedUser != null && task.getStatus() == TaskStatus.PENDIENTE) {
            task.setStatus(TaskStatus.EN_PROGRESO);
        } else if (assignedUser == null && task.getStatus() == TaskStatus.EN_PROGRESO) {
            task.setStatus(TaskStatus.PENDIENTE);
        }
        
        taskRepository.save(task);
        return ApiResponse.success(TaskResponse.from(task), "Tarea actualizada exitosamente.");
    }

    // transicioes validas segun el rol
    @LogEjecucion
    @Transactional
    public ApiResponse<TaskResponse> updateStatus(UUID id, UpdateTaskStatusRequest request, UUID currentUserId, String currentUserRole) {
        Task task = getTaskOrThrow(id);
        assertCanAccessTask(task, currentUserId, currentUserRole);

        //desarrolladores solo pueden cambiar el estado de sus propias tareas
        if (RoleName.DESARROLLADOR.name().equals(currentUserRole)) {
            boolean isAssigned = task.getAssignedUser() != null && task.getAssignedUser().getId().equals(currentUserId);
            if (!isAssigned) {
                throw new AccessDeniedException("Solo puedes cambiar el estado de las tareas que tienes asignadas.");
            }
        }

        statusTransitionHelper.validateTransition(task.getStatus(), request.newStatus(), currentUserRole);

        task.setStatus(request.newStatus());
        taskRepository.save(task);
        return ApiResponse.success(TaskResponse.from(task), "Estado de la tarea actualizado exitosamente.");
    }

    @LogEjecucion
    @Transactional
    public void delete(UUID id, UUID currentUserId, String currentUserRole) {
        Task task = getTaskOrThrow(id);
        assertCanModifyProject(task.getProject(), currentUserId, currentUserRole);
        task.getAudit().markDeleted();
    }

    // helpers para luego refactorizar
    private Task getTaskOrThrow(UUID id) {
        return taskRepository.findByIdAndAuditDeletedAtIsNull(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Tarea con id " + id + " no encontrada o ya fue eliminada."));
    }

    private Project getProjectOrThrow(UUID projectId) {
        return projectRepository.findByIdAndAuditDeletedAtIsNull(projectId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Proyecto con id " + projectId + " no encontrado o ya fue eliminado."));
    }

    private User resolveUser(UUID userId) {
        if (userId == null) return null;
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Usuario con id " + userId + " no encontrado."));
    }

    // solo puede asignar tareas un desarrollador o lider
    private void assertUserHasAllowedRole(User user) {
        String roleName = user.getRole() != null ? user.getRole().getName() : "";
        boolean allowed = switch (roleName) {
            case "DESARROLLADOR", "LIDER" -> true;
            default -> false;
        };
        if (!allowed) {
            throw new BusinessRuleException("Solo se pueden asignar tareas a usuarios con rol DESARROLLADOR o LIDER."
            );
        }
    }

    private void assertCanAccessProject(Project project, UUID currentUserId, String currentUserRole) {
        boolean isAdmin = RoleName.ADMINISTRADOR.name().equals(currentUserRole);
        boolean isLeader = project.isOwnedBy(currentUserId);
        boolean isDev = RoleName.DESARROLLADOR.name().equals(currentUserRole);

        if (!isAdmin && !isLeader && !isDev) {
            throw new AccessDeniedException("No tienes acceso a las tareas de este proyecto.");
        }
    }

    private void assertCanModifyProject(Project project, UUID currentUserId, String currentUserRole) {
        boolean isAdmin  = RoleName.ADMINISTRADOR.name().equals(currentUserRole);
        boolean isLeader = project.isOwnedBy(currentUserId);
        if (!isAdmin && !isLeader) {
            throw new AccessDeniedException(
                "Solo el líder del proyecto o un administrador pueden realizar esta acción."
            );
        }
    }

    private void assertCanAccessTask(Task task, UUID currentUserId, String currentUserRole) {
        assertCanAccessProject(task.getProject(), currentUserId, currentUserRole);
    }

    // seleccionar tareas activas con los filtros
    private List<Task> queryTasks(UUID projectId, Optional<TaskStatus> status, Optional<TaskPriority> priority) {
        var mode = TaskFilterMode.of(status.isPresent(), priority.isPresent());

        return switch (mode) {
            case STATUS_AND_PRIORITY -> taskRepository
                .findByProjectIdAndStatusAndPriorityAndAuditDeletedAtIsNull(projectId, status.get(), priority.get());
            case STATUS_ONLY -> taskRepository
                .findByProjectIdAndStatusAndAuditDeletedAtIsNull(projectId, status.get());
            case PRIORITY_ONLY -> taskRepository
                .findByProjectIdAndPriorityAndAuditDeletedAtIsNull(projectId, priority.get());
            case NONE -> taskRepository
                .findByProjectIdAndAuditDeletedAtIsNull(projectId);
        };
    }

    private enum TaskFilterMode {
        STATUS_AND_PRIORITY,
        STATUS_ONLY,
        PRIORITY_ONLY,
        NONE;

        static TaskFilterMode of(boolean hasStatus, boolean hasPriority) {
            if (hasStatus && hasPriority) return STATUS_AND_PRIORITY;
            if (hasStatus) return STATUS_ONLY;
            if (hasPriority) return PRIORITY_ONLY;
            return NONE;
        }
    }
}