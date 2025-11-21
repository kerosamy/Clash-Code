package com.clashcode.backend.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf->csrf.disable())
                .authorizeHttpRequests(auth ->auth.anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2.defaultSuccessUrl("/GoogleSignUp", true))
                .logout(logout -> logout
                        .logoutSuccessUrl("https://accounts.google.com/Logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                )
        ;
        return http.build();
    }
}
