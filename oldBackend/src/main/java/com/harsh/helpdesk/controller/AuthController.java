package com.harsh.helpdesk.controller;

import com.harsh.helpdesk.dto.request.LoginRequestDTO;
import com.harsh.helpdesk.dto.request.UserRequestDTO;
import com.harsh.helpdesk.dto.response.AuthResponseDTO;
import com.harsh.helpdesk.dto.response.UserResponseDTO;
import com.harsh.helpdesk.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO requestDTO){
        return ResponseEntity.ok(authService.register(requestDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO requestDTO){
        return ResponseEntity.ok(authService.login(requestDTO));
    }
}
