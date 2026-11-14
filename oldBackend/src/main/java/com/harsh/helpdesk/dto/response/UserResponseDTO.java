package com.harsh.helpdesk.dto.response;

import com.harsh.helpdesk.entity.Role;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDTO {

    private Long id;

    private String name;

    private String email;

    private Role role;

    private LocalDateTime createdAt;
}
