package com.springboot.taskflow.taskflow.services;

import com.springboot.taskflow.taskflow.repositories.UserRepository;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
}
