package com.springboot.taskflow.taskflow.controllers;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.taskflow.taskflow.requests.CreateUserRequest;
import com.springboot.taskflow.taskflow.responses.ApiResponse;
import com.springboot.taskflow.taskflow.responses.UserResponse;
import com.springboot.taskflow.taskflow.security.RequireAdmin;
import com.springboot.taskflow.taskflow.security.RequireLiderOrAdmin;
import com.springboot.taskflow.taskflow.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    @RequireAdmin
    public ResponseEntity<ApiResponse<UserResponse>> create(@RequestBody @Valid CreateUserRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }

    @GetMapping("/{id}")
    @RequireLiderOrAdmin
    public ResponseEntity<ApiResponse<UserResponse>> findById(@PathVariable UUID id){
        return ResponseEntity.ok(userService.findById(id));
    }
}