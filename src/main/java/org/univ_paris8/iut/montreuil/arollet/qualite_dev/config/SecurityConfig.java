package org.univ_paris8.iut.montreuil.arollet.qualite_dev.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.CorrelationIdFilter;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.JwtAuthenticationFilter;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.RestAccessDeniedHandler;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.RestAuthenticationEntryPoint;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        CorrelationIdFilter correlationIdFilter,
        JwtAuthenticationFilter jwtAuthenticationFilter,
        RestAuthenticationEntryPoint authenticationEntryPoint,
        RestAccessDeniedHandler accessDeniedHandler
    ) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint).accessDeniedHandler(accessDeniedHandler))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login", "/swagger-ui/**", "/v3/api-docs/**", "/actuator/health/**", "/actuator/info").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll())
            .addFilterBefore(correlationIdFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(jwtAuthenticationFilter, CorrelationIdFilter.class);
        return http.build();
    }
}
