package com.springboot.taskflow.taskflow.services;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import com.springboot.taskflow.taskflow.enums.TaskStatus;
import com.springboot.taskflow.taskflow.exceptions.BusinessRuleException;
import com.springboot.taskflow.taskflow.exceptions.ResourceNotFoundException;
import com.springboot.taskflow.taskflow.repositories.ProjectRepository;
import com.springboot.taskflow.taskflow.repositories.UserRepository;
import com.springboot.taskflow.taskflow.requests.ProjectRequest;
import com.springboot.taskflow.taskflow.responses.ProjectResponse;


@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @LogEjecucion
    public List<ProjectResponse> findAll(UUID currentUserId, String currentUserRole, Optional<ProjectStatus> status) {
        if (RoleName.ADMINISTRADOR.name().equals(currentUserRole)) {
            List<Project> projects = status
                .map(projectRepository::findByStatusAndAuditDeletedAtIsNull)
                .orElseGet(projectRepository::findByAuditDeletedAtIsNull);

            return projects.stream().map(ProjectResponse::from).toList();
        }

        Set<Project> projects = new LinkedHashSet<>();

        if (status.isPresent()) {
            ProjectStatus projectStatus = status.get();

            projects.addAll(
                projectRepository.findByLeaderIdAndStatusAndAuditDeletedAtIsNull(
                    currentUserId,
                    projectStatus
                )
            );

            projects.addAll(
                projectRepository.findDistinctByTasksAssignedUserIdAndStatusAndAuditDeletedAtIsNull(
                    currentUserId,
                    projectStatus
                )
            );

        } else {
            projects.addAll(
                projectRepository.findByLeaderIdAndAuditDeletedAtIsNull(currentUserId)
            );
            projects.addAll(
                projectRepository.findDistinctByTasksAssignedUserIdAndAuditDeletedAtIsNull(currentUserId)
            );
        }
        return projects.stream().map(ProjectResponse::from).toList();
    }

    @LogEjecucion
    public ProjectResponse findById(UUID id, UUID currentUserId, String currentUserRole) {
        Project project = getProjectOrThrow(id);
        assertCanAccess(project, currentUserId, currentUserRole);
        return ProjectResponse.from(project);
    }

    @LogEjecucion
    @Transactional
    public ProjectResponse create(ProjectRequest request, UUID currentUserId) {
        User leader = userRepository.findById(currentUserId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));

        Project project = new Project(request.name(), request.description(), leader);
        Project saved = projectRepository.save(project);
        return ProjectResponse.from(saved);
    }

    @LogEjecucion
    @Transactional
    public ProjectResponse update(UUID id, ProjectRequest request, UUID currentUserId, String currentUserRole) {
        Project project = getProjectOrThrow(id);
        assertCanModify(project, currentUserId, currentUserRole);

        project.setName(request.name());
        project.setDescription(request.description());
        return ProjectResponse.from(project);
    }

    @LogEjecucion
    @Transactional
    public ProjectResponse archive(UUID id, UUID currentUserId, String currentUserRole) {
        Project project = getProjectOrThrow(id);
        assertCanModify(project, currentUserId, currentUserRole);

        boolean hasUnfinishedTasks = project.getTasks().stream()
            .filter(task -> !task.getAudit().isDeleted())
            .anyMatch(task -> task.getStatus() != TaskStatus.COMPLETADA);

        if (hasUnfinishedTasks) {
            throw new BusinessRuleException(
                "No se puede archivar un proyecto con tareas que no estén en estado COMPLETADA."
            );
        }

        project.archive();
        return ProjectResponse.from(project);
    }

    @LogEjecucion
    @Transactional
    public void delete(UUID id, UUID currentUserId, String currentUserRole) {
        Project project = getProjectOrThrow(id);
        assertCanModify(project, currentUserId, currentUserRole);

        for (Task task : project.getTasks()) {
            if (!task.getAudit().isDeleted()) {
                task.getAudit().markDeleted();
            }
        }

        project.getAudit().markDeleted();
    }

    private Project getProjectOrThrow(UUID id) {
        return projectRepository.findByIdAndAuditDeletedAtIsNull(id)
            .orElseThrow(() -> new ResourceNotFoundException("Proyecto con id " + id + " no encontrado."));
    }

    private void assertCanAccess(Project project, UUID currentUserId, String currentUserRole) {
        boolean isAdmin = RoleName.ADMINISTRADOR.name().equals(currentUserRole);
        boolean isOwner = project.isOwnedBy(currentUserId);

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("No tienes acceso a este proyecto.");
        }
    }

    private void assertCanModify(Project project, UUID currentUserId, String currentUserRole) {
        assertCanAccess(project, currentUserId, currentUserRole);
    }
}