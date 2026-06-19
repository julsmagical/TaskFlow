package com.springboot.taskflow.taskflow.responses;

import java.util.UUID;

public record UserResponse(

        UUID id,
        String username,
        String role

) {}