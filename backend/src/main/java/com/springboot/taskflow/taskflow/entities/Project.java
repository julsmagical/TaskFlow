package com.springboot.taskflow.taskflow.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.springboot.taskflow.taskflow.shared.Audit;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Project")
public class Project extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String description;

    // @Enumerated(EnumType.STRING)
    // private ProjectStatus status;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true
    )
    private List<Task> tasks = new ArrayList<>();
}
