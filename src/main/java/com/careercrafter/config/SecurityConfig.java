package com.careercrafter.config;

import com.careercrafter.service.MyUserSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final MyUserSecurityService myUserSecurityService;
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securedFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/employer/add").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/jobseeker/add").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/category/add").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/joblisting/add").hasAuthority("EMPLOYER")
                        .requestMatchers("/api/employer/de-activate").hasAnyAuthority("EMPLOYER", "ADMIN")
                        .requestMatchers("/api/auth/login").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authProvider() {
        DaoAuthenticationProvider dao = new DaoAuthenticationProvider(myUserSecurityService);
        dao.setPasswordEncoder(getEncoder());
        return dao;
    }
}