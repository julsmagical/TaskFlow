package com.springboot.taskflow.taskflow.requests;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(

    @NotBlank(message = "El nombre de usuario es obligatorio.")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres.")
    String username,

    @NotBlank(message = "El nombre completo es obligatorio.")
    @Size(min = 3, max = 60, message = "El nombre completo debe tener entre 3 y 60 caracteres.")
    String fullname,

    @NotBlank(message = "El correo electrónico es obligatorio.")
    @Email(message = "El correo electrónico no tiene un formato válido.")
    String email,

    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres.")
    String password,

    UUID roleId

) {}