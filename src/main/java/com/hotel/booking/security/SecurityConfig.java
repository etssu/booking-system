package com.hotel.booking.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
                        .permitAll()

                        // rooms
                        .requestMatchers(HttpMethod.GET, "/api/rooms/**")
                        .hasAnyRole("GUEST", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/rooms/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/rooms/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/rooms/**")
                        .hasRole("ADMIN")

                        // bookings
                        .requestMatchers(HttpMethod.GET, "/api/bookings/**")
                        .hasAnyRole("GUEST", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/bookings/**")
                        .hasRole("GUEST")

                        .requestMatchers(HttpMethod.PUT, "/api/bookings/*/status")
                        .hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
