package com.springboot.taskflow.taskflow.services;

import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.springboot.taskflow.taskflow.entities.Role;
import com.springboot.taskflow.taskflow.entities.User;
import com.springboot.taskflow.taskflow.enums.RoleName;
import com.springboot.taskflow.taskflow.exceptions.NotFoundException;
import com.springboot.taskflow.taskflow.repositories.RoleRepository;
import com.springboot.taskflow.taskflow.repositories.UserRepository;
import com.springboot.taskflow.taskflow.requests.CreateUserRequest;
import com.springboot.taskflow.taskflow.responses.ApiResponse;
import com.springboot.taskflow.taskflow.responses.UserResponse;
import com.springboot.taskflow.taskflow.shared.UserMapper;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            BCryptPasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ApiResponse<UserResponse> create(CreateUserRequest request) {
        if(userRepository.existsByUsername(request.username())){
            new BadRequestException("El nombre de usuario ya existe.");
        }
        Role role = roleRepository.findByName(RoleName.DESARROLLADOR.name())
            .orElseThrow(() -> new NotFoundException("Rol por defecto no configurado."));
        if(request.roleId() != null){
            role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new NotFoundException("Rol no encontrado."));
        }
        User user = new User();
        user.setUsername(request.username());
        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );
        user.setRole(role);
        user.setFullName(request.fullname());
        user.setEmail(request.email());
        userRepository.save(user);
        return ApiResponse.success(
            UserMapper.toResponse(user), "Usuario creado éxitosamente."
        );
    }

    public ApiResponse<UserResponse> findById(UUID id){

        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Usuario no encontrado."));
        return ApiResponse.success(UserMapper.toResponse(user), "Usuario no encontrado.");
    }

}