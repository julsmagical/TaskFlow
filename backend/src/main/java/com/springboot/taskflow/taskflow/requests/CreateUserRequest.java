package com.springboot.taskflow.taskflow.requests;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank
        @Size(min = 3, max = 50)
        String username,

        @NotBlank
        @Size(min= 3, max=60)
        String fullname,

        @NotBlank
        @Email
        String email,
        
        @NotBlank
        @Size(min = 6)
        String password,

        @NotNull
        UUID roleId

) {}