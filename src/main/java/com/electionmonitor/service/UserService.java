package com.electionmonitor.service;

import com.electionmonitor.dto.UserDTO;
import com.electionmonitor.exception.ResourceNotFoundException;
import com.electionmonitor.model.User;
import com.electionmonitor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return toDTO(user);
    }

    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return toDTO(user);
    }

    public UserDTO toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
        return toDTO(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    

    public List<UserDTO> getPendingUsers() {
        return userRepository.findByApprovalStatus(User.ApprovalStatus.PENDING).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public UserDTO approveUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setApprovalStatus(User.ApprovalStatus.APPROVED);
        userRepository.save(user);
        return toDTO(user);
    }

    public UserDTO rejectUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setApprovalStatus(User.ApprovalStatus.REJECTED);
        userRepository.save(user);
        return toDTO(user);
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = modelMapper.map(user, UserDTO.class);
        dto.setRole(user.getRole().name());
        dto.setApprovalStatus(user.getApprovalStatus() != null ? user.getApprovalStatus().name() : "APPROVED");
        return dto;
    }
}
