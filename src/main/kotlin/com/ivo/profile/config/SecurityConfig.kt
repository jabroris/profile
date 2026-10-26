package com.ivo.profile.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf ->
                // Disable CSRF for API endpoints and H2 console (demo purposes)
                csrf.ignoringRequestMatchers("/api/**", "/h2-console/**")
            }
            .headers { headers ->
                // Allow H2 console to render in iframes
                headers.frameOptions { it.sameOrigin() }
            }
            .authorizeHttpRequests { auth ->
                auth
                    // Public: resume page, REST API, Swagger, Actuator, H2 console
                    .requestMatchers(
                        "/", "/css/**", "/js/**",
                        "/api/**",
                        "/swagger-ui/**", "/api-docs/**", "/swagger-ui.html",
                        "/actuator/**",
                        "/h2-console/**"
                    ).permitAll()
                    // Admin endpoints require authentication
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            }
            .httpBasic { }

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun userDetailsService(encoder: PasswordEncoder): UserDetailsService {
        val admin = User.builder()
            .username("admin")
            .password(encoder.encode("portfolio2024"))
            .roles("ADMIN")
            .build()
        return InMemoryUserDetailsManager(admin)
    }
}
