package com.harsh.helpdesk.dto.response;

import com.harsh.helpdesk.entity.Role;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponseDTO {
    private String token;
    private String tokenType;
    private Long userId;
    private String name;
    private String email;
    private Role role;
}
