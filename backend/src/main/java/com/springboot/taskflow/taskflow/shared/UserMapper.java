package com.springboot.taskflow.taskflow.shared;

import com.springboot.taskflow.taskflow.entities.User;
import com.springboot.taskflow.taskflow.responses.UserResponse;

public final class UserMapper {

    private UserMapper(){}
    public static UserResponse toResponse(User user){
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getRole().getName()
        );
    }
}