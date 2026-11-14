package com.harsh.helpdesk.service;

import com.harsh.helpdesk.dto.request.UserRequestDTO;
import com.harsh.helpdesk.dto.response.UserResponseDTO;
import com.harsh.helpdesk.entity.Role;
import com.harsh.helpdesk.entity.User;
import com.harsh.helpdesk.exception.DuplicateResourceException;
import com.harsh.helpdesk.exception.ResourceNotFoundException;
import com.harsh.helpdesk.mapper.UserMapper;
import com.harsh.helpdesk.repository.UserRepo;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepo userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponseDTO registerUser(UserRequestDTO request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        User user = UserMapper.toEntity(request);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        return UserMapper.toResponseDTO(savedUser);
    }

    @Override
    public UserResponseDTO getUserById(Long id) {

        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        return UserMapper.toResponseDTO(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponseDTO)
                .toList();
    }

    @Override
    public void deleteUser(Long id) {

        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        userRepository.delete(user);
    }

    @Override
    public UserResponseDTO createAgent(UserRequestDTO request) {
        if(userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateResourceException("Email already exists");
        }

        User agent = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.SUPPORT_AGENT)
                .build();

        return UserMapper.toResponseDTO(userRepository.save(agent));
    }

    @Override
    public List<UserResponseDTO> getAllAgents() {
        return userRepository.findByRole(Role.SUPPORT_AGENT)
                .stream()
                .map(UserMapper::toResponseDTO)
                .toList();
    }
}
