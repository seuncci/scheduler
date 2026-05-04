package com.seun.scheduler.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form
                        .loginPage("/members/login")
                        .defaultSuccessUrl("/members/me")
                        .permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/members/login", "/members/signup", "/api/members/signup").anonymous()
                        .requestMatchers("/h2-console/**", "/error", "/js/profile.js", "/js/common.js", "/api/members/me/notifications/summary").permitAll()
                        .anyRequest().authenticated())
                        // H2-console ( iframe ) 접속 허용을 위해 임시로 프레임 노출 허용
                        .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));;

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}