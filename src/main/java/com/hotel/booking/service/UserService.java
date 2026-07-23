package com.hotel.booking.service;

import com.hotel.booking.dto.UserCreateRequestDTO;
import com.hotel.booking.dto.UserResponseDTO;
import com.hotel.booking.dto.UserUpdateRequestDTO;
import com.hotel.booking.entity.User;
import com.hotel.booking.entity.enums.Role;
import com.hotel.booking.exception.EmailAlreadyExistsException;
import com.hotel.booking.exception.UserNotFoundException;
import com.hotel.booking.exception.UsernameAlreadyExistsException;
import com.hotel.booking.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::convertToDTO)
                .toList();
    }

    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        return convertToDTO(user);
    }

    public UserResponseDTO createUser(UserCreateRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException();
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException();
        }

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(Role.GUEST);

        return convertToDTO(userRepository.save(user));
    }

    public UserResponseDTO updateUser(Long id, UserUpdateRequestDTO request) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if (request.getUsername() != null) {

            if (userRepository.existsByUsernameAndIdNot(request.getUsername(), id)) {
                throw new UsernameAlreadyExistsException();
            }

            user.setUsername(request.getUsername());
        }
        if (request.getPassword() != null) {
            user.setPassword(request.getPassword());
        }
        if (request.getEmail() != null) {

            if (userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
                throw new EmailAlreadyExistsException();
            }

            user.setEmail(request.getEmail());
        }
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        return convertToDTO(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        userRepository.delete(user);
    }

    private UserResponseDTO convertToDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }

}
