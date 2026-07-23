package com.hotel.booking.repository;

import com.hotel.booking.entity.User;
import com.hotel.booking.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {

    boolean existsByUsername(String username);
    boolean existsByUsernameAndIdNot(String username, Long id);

    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);

    User findByEmail(String email);

}
