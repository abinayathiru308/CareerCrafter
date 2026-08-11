package com.careercrafter.config;

import com.careercrafter.service.MyUserSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final MyUserSecurityService myUserSecurityService;
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Disable CSRF & configure CORS
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Disable frame options so H2 console frames can load
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))

                .authorizeHttpRequests(authorize -> authorize

                        // 1. Allow H2 Console & Error page
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/error").permitAll()

                        // 2. Public Auth & Signup APIs
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/add/admin").denyAll()
                        .requestMatchers(HttpMethod.POST, "/api/jobseeker/add").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/employer/add").permitAll()

                        // 3. Admin APIs
                        .requestMatchers(HttpMethod.POST, "/api/category/add").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/executive/add").hasAuthority("ADMIN")

                        // 4. Employer APIs
                        .requestMatchers("/api/employer/de-activate").hasAnyAuthority("EMPLOYER", "ADMIN")
                        .requestMatchers("/api/joblisting/count/per-employer").hasAuthority("EMPLOYER")
                        .requestMatchers("/api/joblisting/my-listings").hasAuthority("EMPLOYER")

                        // 5. Public Category & Job Listing APIs
                        .requestMatchers("/api/category/get-all").permitAll()
                        .requestMatchers("/api/joblisting/by-category/{categoryId}").permitAll()
                        .requestMatchers("/api/joblisting/search").permitAll()
                        .requestMatchers("/api/joblisting/get-all").permitAll()
                        .requestMatchers("/api/joblisting/get-one/{id}").permitAll()

                        // 6. Job Listing Modifications
                        .requestMatchers(HttpMethod.DELETE, "/api/joblisting/delete/{id}").hasAnyAuthority("EMPLOYER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/joblisting/update/{id}").hasAnyAuthority("EMPLOYER", "ADMIN")

                        // 7. Job Seeker Profile APIs
                        .requestMatchers("/api/jobseeker/my-profile").hasAuthority("JOBSEEKER")
                        .requestMatchers(HttpMethod.DELETE, "/api/jobseeker/delete/{id}").hasAnyAuthority("JOBSEEKER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/jobseeker/update/{id}").hasAnyAuthority("JOBSEEKER", "ADMIN")
                        .requestMatchers("/api/jobseeker/upload-resume/{id}").hasAnyAuthority("JOBSEEKER", "ADMIN")
                        .requestMatchers("/api/jobseeker/update-resume/{id}").hasAnyAuthority("JOBSEEKER", "ADMIN")
                        .requestMatchers("/api/jobseeker/delete-resume/{id}").hasAnyAuthority("JOBSEEKER", "ADMIN")

                        // 8. Application APIs
                        .requestMatchers(HttpMethod.POST, "/api/application/apply/{jobSeekerId}").hasAnyAuthority("JOBSEEKER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/application/update-status/{id}").hasAnyAuthority("EMPLOYER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/application/withdraw/{id}").hasAnyAuthority("JOBSEEKER", "ADMIN")
                        .requestMatchers("/api/application/get-one/{id}").hasAnyAuthority("EMPLOYER", "JOBSEEKER", "ADMIN")
                        .requestMatchers("/api/application/by-jobseeker").hasAnyAuthority("EMPLOYER", "JOBSEEKER")
                        .requestMatchers("/api/application/by-jobseeker-id/{jobSeekerId}").hasAnyAuthority("EMPLOYER", "JOBSEEKER", "ADMIN")
                        .requestMatchers("/api/application/count/{jobListingId}").hasAnyAuthority("EMPLOYER", "ADMIN")

                        // 9. All other endpoints require authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
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