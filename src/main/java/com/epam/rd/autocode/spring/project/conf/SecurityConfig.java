package com.epam.rd.autocode.spring.project.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Дозволяємо доступ до H2 console (тільки для розробки)
                .csrf(csrf -> csrf.ignoringRequestMatchers(antMatcher("/h2-console/**")))
                .headers(headers -> headers.frameOptions().disable()) // Для H2 console

                .authorizeHttpRequests(auth -> auth
                        // Дозволяємо публічний доступ
                        .requestMatchers(
                                "/",
                                "/about",
                                "/login",
                                "/register",
                                "/css/**",
                                "/image/**",
                                "/js/**",
                                "/h2-console/**",
                                "/books",
                                "/employee",
                                "/clients",
                                "/books/**"
                        ).permitAll()

                        // Захищені маршрути
                        .requestMatchers("/employee/**").hasRole("EMPLOYEE")
                        .requestMatchers("/client/**").hasRole("CLIENT")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/redirect-by-role", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}