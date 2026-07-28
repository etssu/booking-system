package com.hotel.booking.security;

import com.hotel.booking.entity.User;
import com.hotel.booking.entity.enums.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomUserDetailsTest {

    @Test
    void customUserDetails_shouldReturnUserData() {

        User user = new User();
        user.setUsername("roma");
        user.setPassword("encodedPassword");
        user.setRole(Role.GUEST);

        CustomUserDetails details = new CustomUserDetails(user);

        assertEquals("roma", details.getUsername());
        assertEquals("encodedPassword", details.getPassword());
        assertEquals("ROLE_GUEST",
                details.getAuthorities().iterator().next().getAuthority());
    }


}
