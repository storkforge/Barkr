package org.storkforge.barkr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .oauth2Login(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers("/","/login", "/error", "/css/**", "/js/**", "/images/**").permitAll()
                                .requestMatchers( "/account/{id}/image").permitAll()
                                .requestMatchers("/ai/generate").authenticated()
                                .requestMatchers("/post/add").authenticated()
                                .requestMatchers("/account/{id}/upload").authenticated()
                                .requestMatchers("/{username}").authenticated()
                                .anyRequest().denyAll()

                        );
        return http.build();
    }
 }
