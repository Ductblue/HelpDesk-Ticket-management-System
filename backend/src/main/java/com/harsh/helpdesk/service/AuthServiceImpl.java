package com.harsh.helpdesk.service;

import com.harsh.helpdesk.dto.request.LoginRequestDTO;
import com.harsh.helpdesk.dto.request.UserRequestDTO;
import com.harsh.helpdesk.dto.response.AuthResponseDTO;
import com.harsh.helpdesk.dto.response.UserResponseDTO;
import com.harsh.helpdesk.entity.User;
import com.harsh.helpdesk.repository.UserRepo;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService{

    private final UserService userService;
    private final UserRepo userRepo;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthServiceImpl(UserService userService, UserRepo userRepo, AuthenticationManager authenticationManager, CustomUserDetailsService userDetailsService, JwtService jwtService) {
        this.userService = userService;
        this.userRepo = userRepo;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }


    @Override
    public UserResponseDTO register(UserRequestDTO requestDTO) {
        return userService.registerUser(requestDTO);
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO loginRequestDTO) {

        // Authenticate user credentials
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(),loginRequestDTO.getPassword()));

        // Load user details
        UserDetails userDetails=userDetailsService.loadUserByUsername(loginRequestDTO.getEmail());

        // Fetch user from database
        User user=userRepo.findByEmail(loginRequestDTO.getEmail()).orElseThrow(()->new UsernameNotFoundException("User not found:"));

        // Generate JWT
        String token= jwtService.generateToken(userDetails);

        // Return authentication response
        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
