package com.springboot.taskflow.taskflow.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectRequest(

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres.")
    String name,

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres.")
    String description
) {}