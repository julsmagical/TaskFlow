package com.springboot.taskflow.taskflow.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.springboot.taskflow.taskflow.entities.Task;
import com.springboot.taskflow.taskflow.enums.TaskStatus;
import com.springboot.taskflow.taskflow.enums.TaskPriority;

public interface TaskRepository extends JpaRepository<Task, UUID>{

    //busqueda tarea por id
    Optional<Task> findByIdAndAuditDeletedAtIsNull(UUID id);
 
    //listar tareas (sin filtros)
    List<Task> findByProjectIdAndAuditDeletedAtIsNull(UUID projectId);
 
    //filtrado por estado
    List<Task> findByProjectIdAndStatusAndAuditDeletedAtIsNull(UUID projectId, TaskStatus status);
 
    //filtrado por prioridad
    List<Task> findByProjectIdAndPriorityAndAuditDeletedAtIsNull(UUID projectId, TaskPriority priority);
 
    //filtrado por estado y prioridad
    List<Task> findByProjectIdAndStatusAndPriorityAndAuditDeletedAtIsNull(
        UUID projectId, TaskStatus status, TaskPriority priority);

    // nota: contar tareas no completadas
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId " +
           "AND t.status <> 'COMPLETADA' AND t.audit.deletedAt IS NULL")
    long countPendingTasksByProject(@Param("projectId") UUID projectId);
}
