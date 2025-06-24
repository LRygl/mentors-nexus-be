package com.mentors.applicationstarter.Mapper;

import com.mentors.applicationstarter.DTO.UserResponseDTO;
import com.mentors.applicationstarter.Model.User;

public class UserMapper {
    public static UserResponseDTO mapUserToDto(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .UUID(user.getUUID())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

}
