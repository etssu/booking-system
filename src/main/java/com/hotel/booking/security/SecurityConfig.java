package com.hotel.booking.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
                        .permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/auth/login")
                        .permitAll()

                        // users
                        .requestMatchers(HttpMethod.POST, "/api/users/**")
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
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class

                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService
    ) {
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }
}
