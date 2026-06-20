package com.springboot.taskflow.taskflow.services;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.springboot.taskflow.taskflow.entities.User;
import com.springboot.taskflow.taskflow.exceptions.UnauthorizedException;
import com.springboot.taskflow.taskflow.repositories.UserRepository;
import com.springboot.taskflow.taskflow.requests.LoginRequest;
import com.springboot.taskflow.taskflow.responses.ApiResponse;
import com.springboot.taskflow.taskflow.responses.LoginResponse;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager,
                        UserRepository userRepository,
                        JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public ApiResponse<LoginResponse> login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UnauthorizedException("Credenciales no válidas."));
        String token = jwtService.generateToken(user);
        return ApiResponse.success(new LoginResponse(token), "Sesión iniciada con éxito.");
    }
}