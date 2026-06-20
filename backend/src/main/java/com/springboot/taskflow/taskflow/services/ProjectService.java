package com.springboot.taskflow.taskflow.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.taskflow.taskflow.aop.LogEjecucion;
import com.springboot.taskflow.taskflow.entities.Project;
import com.springboot.taskflow.taskflow.entities.Task;
import com.springboot.taskflow.taskflow.enums.ProjectStatus;
import com.springboot.taskflow.taskflow.enums.TaskStatus;
import com.springboot.taskflow.taskflow.exceptions.BusinessRuleException;
import com.springboot.taskflow.taskflow.exceptions.ResourceNotFoundException;
import com.springboot.taskflow.taskflow.repositories.ProjectRepository;
import com.springboot.taskflow.taskflow.requests.ProjectRequest;
import com.springboot.taskflow.taskflow.responses.ProjectResponse;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @LogEjecucion
    public List<ProjectResponse> findAll(Optional<ProjectStatus> status) {
        List<Project> projects = status
            .map(projectRepository::findByStatusAndAuditDeletedAtIsNull)
            .orElseGet(projectRepository::findByAuditDeletedAtIsNull);

        return projects.stream()
            .map(ProjectResponse::from)
            .toList();
    }

    @LogEjecucion
    public ProjectResponse findById(UUID id) {
        return ProjectResponse.from(getProjectOrThrow(id));
    }

    @LogEjecucion
    @Transactional
    public ProjectResponse create(ProjectRequest request) {
        Project project = new Project(request.name(), request.description());
        Project saved = projectRepository.save(project);
        return ProjectResponse.from(saved);
    }

    @LogEjecucion
    @Transactional
    public ProjectResponse update(UUID id, ProjectRequest request) {
        Project project = getProjectOrThrow(id);
        project.setName(request.name());
        project.setDescription(request.description());
        return ProjectResponse.from(project);
    }

    @LogEjecucion
    @Transactional
    public ProjectResponse archive(UUID id) {
        Project project = getProjectOrThrow(id);

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
    public void delete(UUID id) {
        Project project = getProjectOrThrow(id);

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
}