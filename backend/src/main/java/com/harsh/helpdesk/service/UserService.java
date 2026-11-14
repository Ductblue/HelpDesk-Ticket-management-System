
package com.harsh.helpdesk.service;

import com.harsh.helpdesk.dto.request.UserRequestDTO;
import com.harsh.helpdesk.dto.response.UserResponseDTO;

import java.util.List;

public interface UserService {

    UserResponseDTO registerUser(UserRequestDTO request);

    UserResponseDTO getUserById(Long id);

    List<UserResponseDTO> getAllUsers();

    void deleteUser(Long id);

    UserResponseDTO createAgent(UserRequestDTO request);

    List<UserResponseDTO> getAllAgents();
}
