
package com.harsh.helpdesk.mapper;

import com.harsh.helpdesk.dto.request.UserRequestDTO;
import com.harsh.helpdesk.dto.response.UserResponseDTO;
import com.harsh.helpdesk.entity.User;

public class UserMapper {

    // Convert request DTO into User entity
    public static User toEntity(UserRequestDTO dto) {

        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .build();
    }

    // Convert User entity into response DTO
    public static UserResponseDTO toResponseDTO(User user) {

        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
