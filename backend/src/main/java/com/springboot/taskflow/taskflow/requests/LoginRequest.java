package com.springboot.taskflow.taskflow.requests;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

    @NotBlank(message = "El nombre de usuario es obligatorio.")
    String username,

    @NotBlank(message = "La contraseña es obligatoria.")
    String password
    
) {}
