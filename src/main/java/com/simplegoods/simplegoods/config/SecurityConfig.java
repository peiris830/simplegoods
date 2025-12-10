package com.simplegoods.simplegoods.config;

import com.simplegoods.simplegoods.security.CustomUserDetailsService;
import com.simplegoods.simplegoods.security.JwtAuthenticationFilter;
import com.simplegoods.simplegoods.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean // expose the AuthenticationManager for AuthServiceImpl
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration authConfig) throws Exception {
                return authConfig.getAuthenticationManager();
        }

        @Bean // custom UserDetails + BCrypt provider
        public DaoAuthenticationProvider daoAuthProvider(
                        CustomUserDetailsService uds,
                        PasswordEncoder passwordEncoder) {
                DaoAuthenticationProvider provider = new DaoAuthenticationProvider(uds);
                provider.setPasswordEncoder(passwordEncoder);
                return provider;
        }

        @Bean // the JWT filter itself
        public JwtAuthenticationFilter jwtAuthenticationFilter(
                        JwtUtil jwtUtil,
                        CustomUserDetailsService uds) {
                return new JwtAuthenticationFilter(jwtUtil, uds);
        }

        @Bean // the main security chain
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http,
                        DaoAuthenticationProvider daoAuthProvider,
                        JwtAuthenticationFilter jwtAuthFilter) throws Exception {
                http
                                // disable CSRF for a stateless API
                                .csrf(AbstractHttpConfigurer::disable)
                                // plug in authentication provider
                                .authenticationProvider(daoAuthProvider)
                                // route security rules
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/auth/**").permitAll()
                                                // Allow public access to view products (guest browsing)
                                                .requestMatchers(org.springframework.http.HttpMethod.GET,
                                                                "/api/products/**")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                // no session—JWT only
                                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                // register filter BEFORE Spring's UsernamePasswordAuthenticationFilter
                                .addFilterBefore(
                                                jwtAuthFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
