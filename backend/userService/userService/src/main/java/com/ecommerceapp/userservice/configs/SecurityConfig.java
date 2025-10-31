package com.ecommerceapp.userservice.configs;

import com.ecommerceapp.userservice.services.AuthService;
import exceptions.SessionLimitReachedException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthService authService) throws Exception {

        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()
            )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler((request, response, authentication) -> {
                            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                            OAuth2User oauthUser = oauthToken.getPrincipal();


                            String email = oauthUser.getAttribute("email");
                            try{
                                String token = authService.signInWithGoogle(email);
                                response.setHeader("AUTH_TOKEN", token );
                                response.setStatus(HttpServletResponse.SC_OK);
                            } catch(SessionLimitReachedException e){
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                response.setContentType("application/json");
                                response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
                            } catch (Exception e) {
                                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                response.setContentType("application/json");
                                response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
                            }

                        }))

        ;

        return http.build();
    }
}
