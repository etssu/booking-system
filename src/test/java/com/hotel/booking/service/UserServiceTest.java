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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User();
        ReflectionTestUtils.setField(existingUser, "id", 1L);
        existingUser.setFirstName("John");
        existingUser.setLastName("Doe");
        existingUser.setUsername("johndoe");
        existingUser.setEmail("john@mail.com");
        existingUser.setPassword("password123");
        existingUser.setRole(Role.GUEST);
    }

    @Test
    void getAllUsers_shouldReturnMappedList() {
        when(userRepository.findAll()).thenReturn(List.of(existingUser));

        List<UserResponseDTO> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("johndoe");
        assertThat(result.get(0).getEmail()).isEqualTo("john@mail.com");
    }

    // get user by ID
    @Test
    void getUserById_shouldReturnDto_whenUserExists() {
        when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));

        UserResponseDTO result = userService.getUserById(existingUser.getId());

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("johndoe");
    }

    @Test
    void getUserById_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(99L));
    }

    // create User

    @Test
    void createUser_shouldSaveWithGuestRole_whenDataIsValid() {
        UserCreateRequestDTO request = new UserCreateRequestDTO();

        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setPassword("passw123");
        request.setUsername("janesmith");
        request.setEmail("jane@mail.com");

        when(userRepository.existsByUsername("janesmith")).thenReturn(false);
        when(userRepository.existsByEmail("jane@mail.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponseDTO result = userService.createUser(request);

        assertThat(result.getUsername()).isEqualTo("janesmith");
        assertThat(result.getEmail()).isEqualTo("jane@mail.com");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getRole()).isEqualTo(Role.GUEST);
    }

    // update user
    @Test
    void updateUser_shouldUpdateOnlyProvidedFields() {
        UserUpdateRequestDTO request = new UserUpdateRequestDTO();
        request.setEmail("newemail@mail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailAndIdNot("newemail@mail.com", 1L)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponseDTO result = userService.updateUser(1L, request);

        assertThat(result.getEmail()).isEqualTo("newemail@mail.com");
        // username shouldnt change
        assertThat(result.getUsername()).isEqualTo("johndoe");
        verify(userRepository, never()).existsByUsernameAndIdNot(anyString(), anyLong());
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        UserCreateRequestDTO request = new UserCreateRequestDTO();
        request.setUsername("john");
        request.setEmail("john@gmail.com");

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () ->  userService.createUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        UserCreateRequestDTO request = new UserCreateRequestDTO();
        request.setUsername("john");
        request.setEmail("john@gmail.com");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () ->  userService.createUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    // delete user

    @Test
    void deleteUser_shouldDeleteUser_whenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        userService.deleteUser(1L);

        verify(userRepository).delete(existingUser);
    }

    @Test
    void deleteUser_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser(99L));

        verify(userRepository, never()).delete(any(User.class));
    }

}
