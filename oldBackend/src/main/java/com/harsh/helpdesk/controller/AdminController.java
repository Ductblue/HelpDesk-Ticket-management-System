package com.harsh.helpdesk.controller;

import com.harsh.helpdesk.dto.request.UserRequestDTO;
import com.harsh.helpdesk.dto.response.UserResponseDTO;
import com.harsh.helpdesk.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    // Create a support agent
    @PostMapping("/agents")
    public UserResponseDTO createAgent(@Valid @RequestBody UserRequestDTO request) {
        return userService.createAgent(request);
    }

    // Get all support agents
    @GetMapping("/agents")
    public List<UserResponseDTO> getAllAgents() {
        return userService.getAllAgents();
    }
}
