package com.springboot.taskflow.taskflow.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.taskflow.taskflow.entities.Task;
import com.springboot.taskflow.taskflow.entities.User;
import com.springboot.taskflow.taskflow.enums.TaskStatus;
import com.springboot.taskflow.taskflow.enums.TaskPriority;

public interface TaskRepository extends JpaRepository<Task, UUID>{

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByPriority(TaskPriority priority);

    List<Task> findByStatusAndPriority(TaskStatus status, TaskPriority priority);

    List<Task> findByAssignedUserId(UUID userId);

    List<Task> findByAssignedUser(User assignedUser);
    
    List<Task> findByProjectIdAndAuditDeletedAtIsNull(UUID projectId);

    List<Task> findByProjectIdAndStatus(UUID projectId, TaskStatus status);
   
    List<Task> findByProjectIdAndPriority(UUID projectId, TaskPriority priority);

    // nota: contar tareas no completadas de un user
    // @Query("SELECT COUNT(t) FROM Task t WHERE assignedUser.id")
    // long countByAssignedUser();

}
