package com.clashcode.backend.security;

import com.clashcode.backend.config.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider,
            RestAuthenticationEntryPoint restAuthenticationEntryPoint
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationProvider = authenticationProvider;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ===== Disable CSRF for REST APIs =====
                .csrf(csrf -> csrf.disable())

                // ===== Configure CORS =====
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // ===== Exception handling for REST API =====
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(restAuthenticationEntryPoint)
                )

                // ===== Authorization rules =====
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/ws/**").permitAll()
                        .requestMatchers("/files/**").permitAll()
                        .requestMatchers("/test/public").permitAll()  // Public test
                        .requestMatchers("/matches/start-rated-match").permitAll()

                        .requestMatchers("/super-admin/**").hasRole("SUPER_ADMIN") // Most restrictive first
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Super_Admin inherits Admin roles
                        .requestMatchers("/users/**").hasRole("USER") // Everyone authenticated has User roles

                        .anyRequest().authenticated()
                        /*To permit all requests, change .authenticated() to .permitAll()*/
                )

                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("http://localhost:5173/auth/callback", false)
                )

                // --- CHANGE THIS SECTION ---
                .sessionManagement(session ->
                        // Change STATELESS to IF_REQUIRED so the OAuth session persists
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                // ---------------------------

                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of("*"));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
