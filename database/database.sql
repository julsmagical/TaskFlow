USE master;

CREATE DATABASE db_taskflow

USE db_taskflow

CREATE TABLE Role (
    id UNIQUEIDENTIFIER NOT NULL
        CONSTRAINT PK_Role PRIMARY KEY
        DEFAULT NEWID(),

    name NVARCHAR(20) NOT NULL
        CONSTRAINT UQ_Role_name UNIQUE
);

CREATE TABLE [User] (
    id UNIQUEIDENTIFIER NOT NULL
        CONSTRAINT PK_User PRIMARY KEY
        DEFAULT NEWID(),

    username NVARCHAR(50) NOT NULL,
    full_name NVARCHAR(100) NOT NULL,
    email NVARCHAR(100) NOT NULL,
    password_hash NVARCHAR(255) NOT NULL,

    role_id UNIQUEIDENTIFIER NOT NULL,

    created_at DATETIME2 NOT NULL
        DEFAULT SYSUTCDATETIME(),
        
    updated_at DATETIME2 NULL
        DEFAULT SYSUTCDATETIME(),
        
    deleted_at DATETIME2 NULL
        DEFAULT SYSUTCDATETIME(),

    CONSTRAINT UQ_User_username UNIQUE (username),
    CONSTRAINT UQ_User_email UNIQUE (email),

    CONSTRAINT FK_User_Role
        FOREIGN KEY (role_id)
        REFERENCES Role(id)
);

CREATE TABLE Project (
    id UNIQUEIDENTIFIER NOT NULL
        CONSTRAINT PK_Project PRIMARY KEY
        DEFAULT NEWID(),

    name NVARCHAR(100) NOT NULL,
    description NVARCHAR(500) NULL,

    status NVARCHAR(20) NOT NULL,

    created_at DATETIME2 NOT NULL
        DEFAULT SYSUTCDATETIME(),
        
    updated_at DATETIME2 NULL
        DEFAULT SYSUTCDATETIME(),
        
    deleted_at DATETIME2 NULL
        DEFAULT SYSUTCDATETIME(),

    CONSTRAINT CHK_Project_name
        CHECK (LEN(name) BETWEEN 3 AND 100),

    CONSTRAINT CHK_Project_status
        CHECK (status IN ('ACTIVO', 'ARCHIVADO'))
);

CREATE TABLE Task (
    id UNIQUEIDENTIFIER NOT NULL
        CONSTRAINT PK_Task PRIMARY KEY
        DEFAULT NEWID(),

    title NVARCHAR(150) NOT NULL,
    description NVARCHAR(255) NULL,

    priority NVARCHAR(20) NOT NULL,
    status NVARCHAR(30) NOT NULL,

    due_date DATE NOT NULL,

    project_id UNIQUEIDENTIFIER NOT NULL,
    
    assigned_user_id UNIQUEIDENTIFIER NULL,

    created_at DATETIME2 NOT NULL
        DEFAULT SYSUTCDATETIME(),
    
    updated_at DATETIME2 NULL
        DEFAULT SYSUTCDATETIME(),
        
    deleted_at DATETIME2 NULL
        DEFAULT SYSUTCDATETIME(),

    CONSTRAINT CHK_Task_title
        CHECK (LEN(title) BETWEEN 3 AND 150),

    CONSTRAINT CHK_Task_priority
        CHECK (priority IN ('BAJO', 'MEDIO', 'ALTO')),

    CONSTRAINT CHK_Task_status
        CHECK (
            status IN (
                'PENDIENTE',
                'EN PROGRESO',
                'EN REVISIÓN',
                'COMPLETADA'
            )
        ),

    CONSTRAINT FK_Task_Project
        FOREIGN KEY (project_id)
        REFERENCES Project(id),

    CONSTRAINT FK_Task_User
        FOREIGN KEY (assigned_user_id)
        REFERENCES [User](id)
);

INSERT INTO Role (name)
VALUES
('LIDER'),
('DESARROLLADOR');