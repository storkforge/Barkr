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
    @Order(3)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .oauth2Login(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers("/","/login", "/error", "/css/**", "/js/**", "/images/**").permitAll()
                                .requestMatchers("/post/load").permitAll()
                                .requestMatchers("/barkr/logout").permitAll()
                                .requestMatchers( "/account/{id}/image").permitAll()
                                .requestMatchers("/ai/generate").authenticated()
                                .requestMatchers("/post/add").authenticated()
                                .requestMatchers("/account/{id}/upload").authenticated()
                                .requestMatchers("/{username}").authenticated()
                                .requestMatchers("/apikeys/generate").authenticated()
                                .requestMatchers("/apikeys/apikeyform").authenticated()
                                .requestMatchers("/apikeys/result").authenticated()
                                .requestMatchers("/apikeys/mykeys").authenticated()
                                .requestMatchers("/apikeys/mykeys/revoke").authenticated()
                                .requestMatchers("/apikeys/mykeys/nameupdate").authenticated()

                                .anyRequest().denyAll()

                )
                .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/barkr/logout")


        );

        return http.build();
    }


    @Bean
    @Order(2)
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
                                .requestMatchers("/api/keys").authenticated()
                                .requestMatchers(HttpMethod.POST, "/graphql").authenticated()
                                .anyRequest().denyAll()

                );
        return http.build();

    }





}
