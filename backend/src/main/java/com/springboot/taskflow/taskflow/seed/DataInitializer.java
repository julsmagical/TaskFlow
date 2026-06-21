package com.springboot.taskflow.taskflow.seed;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.springboot.taskflow.taskflow.constants.UserConstants;
import com.springboot.taskflow.taskflow.entities.Role;
import com.springboot.taskflow.taskflow.entities.User;
import com.springboot.taskflow.taskflow.enums.RoleName;
import com.springboot.taskflow.taskflow.repositories.RoleRepository;
import com.springboot.taskflow.taskflow.repositories.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(RoleRepository roleRepository, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {

        return args -> {
            if (!userRepository.existsByUsername(UserConstants.DEFAULT_ADMIN_USERNAME)) {
                Role adminRole = roleRepository.findByName(RoleName.ADMINISTRADOR.name()).orElseThrow();
                User admin = new User();
                admin.setUsername(UserConstants.DEFAULT_ADMIN_USERNAME);
                admin.setFullName(UserConstants.DEFAULT_ADMIN_FULLNAME);
                admin.setEmail(UserConstants.DEFAULT_ADMIN_EMAIL);
                admin.setPasswordHash(
                    passwordEncoder.encode(UserConstants.DEFAULT_ADMIN_PASSWORD)
                );
                admin.setRole(adminRole);
                userRepository.save(admin);
            }
        };
    }
}