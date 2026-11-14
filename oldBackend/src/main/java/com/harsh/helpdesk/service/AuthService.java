package com.harsh.helpdesk.service;

import com.harsh.helpdesk.dto.request.LoginRequestDTO;
import com.harsh.helpdesk.dto.request.UserRequestDTO;
import com.harsh.helpdesk.dto.response.AuthResponseDTO;
import com.harsh.helpdesk.dto.response.UserResponseDTO;

public interface AuthService {
    UserResponseDTO register(UserRequestDTO requestDTO);
    AuthResponseDTO login(LoginRequestDTO loginRequestDTO);
}
