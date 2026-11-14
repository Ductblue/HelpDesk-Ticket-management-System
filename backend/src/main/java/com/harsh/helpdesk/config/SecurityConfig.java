package com.harsh.helpdesk.config;

import com.harsh.helpdesk.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider=new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider){
        return new ProviderManager(authenticationProvider);
    }

    // Security configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())

                .authorizeHttpRequests(auth -> auth

                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()

                        // User management: ADMIN only
                        .requestMatchers("/api/users/**").hasRole("ADMIN")

                        // Ticket creation
                        .requestMatchers(HttpMethod.POST, "/api/tickets")
                        .hasAnyRole("USER", "ADMIN")

                        // View tickets by user
                        .requestMatchers(HttpMethod.GET, "/api/tickets/user/**")
                        .hasAnyRole("USER", "ADMIN")

                        // View tickets assigned to agent
                        .requestMatchers(HttpMethod.GET, "/api/tickets/agent/**")
                        .hasAnyRole("SUPPORT_AGENT", "ADMIN")

                        // View all tickets
                        .requestMatchers(HttpMethod.GET, "/api/tickets")
                        .hasAnyRole("ADMIN", "SUPPORT_AGENT")

                        // Assign tickets: ADMIN only
                        .requestMatchers(HttpMethod.PUT, "/api/tickets/*/assign/*")
                        .hasRole("ADMIN")

                        // Update status and priority
                        .requestMatchers(HttpMethod.PUT, "/api/tickets/*/status")
                        .hasAnyRole("ADMIN", "SUPPORT_AGENT")

                        .requestMatchers(HttpMethod.PUT, "/api/tickets/*/priority")
                        .hasAnyRole("ADMIN", "SUPPORT_AGENT")

                        // Delete tickets: ADMIN only
                        .requestMatchers(HttpMethod.DELETE, "/api/tickets/**")
                        .hasRole("ADMIN")
                        .requestMatchers("/api/comments/**").authenticated()
                        .requestMatchers("/api/ticket-history/**").authenticated()

                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Other endpoints require authentication
                        .anyRequest().authenticated()
                )

                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
