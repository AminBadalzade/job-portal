package com.amin.jobportal.config;

import com.amin.jobportal.entity.User;
import com.amin.jobportal.filters.JwtAuthFilter;
import com.amin.jobportal.service.CustomerUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableMethodSecurity
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http){
        http.csrf(csrf-> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    // Public Endpoints
                    auth.requestMatchers("/api/register", "/api/login").permitAll();
                    auth.requestMatchers("/api/jobs/search", "/api/jobs/{id}").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/companies/**").permitAll();

                    // Employer endpoints
                    auth.requestMatchers(HttpMethod.POST, "/api/companies").hasRole("EMPLOYER");
                    auth.requestMatchers("/api/companies/*/join").hasRole("EMPLOYER");
                    auth.requestMatchers("/api/companies/*/join-requests/**").hasRole("EMPLOYER");

                    // JOB_SEEKER endpoints
                    auth.requestMatchers("/api/resumes", "/api/resumes/**").hasRole("JOB_SEEKER");

                    // All other endpoints require authentication
                    auth.anyRequest().authenticated();
        });

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(CustomerUserDetailsService customerUserDetailsService, PasswordEncoder passwordEncoder){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(customerUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(daoAuthenticationProvider);
    }

}
