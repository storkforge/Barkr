package org.storkforge.barkr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.storkforge.barkr.filters.ApiKeyAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .oauth2Login(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers("/","/login", "/error", "/css/**", "/js/**", "/images/**").permitAll()
                                .requestMatchers("/post/load").permitAll()
                                .requestMatchers( "/account/{id}/image").permitAll()
                                .requestMatchers("/ai/generate").authenticated()
                                .requestMatchers("/post/add").authenticated()
                                .requestMatchers("/account/{id}/upload").authenticated()
                                .requestMatchers("/{username}").authenticated()
                                .anyRequest().denyAll()

                )
                .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .logoutSuccessHandler((request, response, authentication) -> {
                    String googleLogoutUrl = "https://accounts.google.com/Logout";
                    response.sendRedirect(googleLogoutUrl);

                })

        );

        return http.build();
    }


    @Bean
    @Order(1)
    public SecurityFilterChain restAPIAndGraphQLFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/**", "/graphql")
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterAfter(new ApiKeyAuthenticationFilter(), LogoutFilter.class)
                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers("/api/accounts").authenticated()
                                .requestMatchers("/api/posts").authenticated()
                                .requestMatchers("/api/accounts/{id}").authenticated()
                                .requestMatchers("/api/posts/{id}").authenticated()
                                .requestMatchers(HttpMethod.POST, "/graphql").authenticated()
                                .anyRequest().denyAll()

                );
        return http.build();

    }




}
