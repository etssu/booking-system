package com.hotel.booking.repository;

import com.hotel.booking.entity.User;
import com.hotel.booking.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {

    List<User> findByRole(Role role);
    User findByEmail(String email);

}
