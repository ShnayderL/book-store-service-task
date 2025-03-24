package com.epam.rd.autocode.spring.project.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/books", "/books/{id}").permitAll()
                        .requestMatchers("/clients/**").hasRole("CLIENT")
                        .requestMatchers("/employees/**").hasRole("EMPLOYEE")
                        .requestMatchers("/orders/**").hasAnyRole("CLIENT", "EMPLOYEE")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login") // Custom login page
                        .defaultSuccessUrl("/", true) // Redirect after successful login
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(antMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout") // Redirect after logout
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }
}
