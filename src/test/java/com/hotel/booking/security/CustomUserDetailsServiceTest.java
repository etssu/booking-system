package com.hotel.booking.security;

import com.hotel.booking.entity.User;
import com.hotel.booking.entity.enums.Role;
import com.hotel.booking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService service;


    @Test
    void loadUserByUsername_shouldReturnUserDetails() {

        User user = new User();
        user.setUsername("roma");
        user.setPassword("encoded");
        user.setRole(Role.GUEST);

        when(userRepository.findByUsername("roma"))
                .thenReturn(Optional.of(user));


        UserDetails result =
                service.loadUserByUsername("roma");


        assertEquals("roma", result.getUsername());
        assertEquals("encoded", result.getPassword());
    }
}