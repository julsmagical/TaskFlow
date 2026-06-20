package com.springboot.taskflow.taskflow.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.springboot.taskflow.taskflow.enums.ProjectStatus;
import com.springboot.taskflow.taskflow.shared.Audit;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectStatus status;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();

    @Embedded
    private Audit audit = new Audit();

    protected Project() {}

    public Project(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = ProjectStatus.ACTIVO;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void archive() {
        this.status = ProjectStatus.ARCHIVADO;
    }

    public boolean isArchived() {
        return status == ProjectStatus.ARCHIVADO;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public Audit getAudit() {
        return audit;
    }
}