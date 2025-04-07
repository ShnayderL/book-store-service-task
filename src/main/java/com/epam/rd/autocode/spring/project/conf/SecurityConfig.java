package com.epam.rd.autocode.spring.project.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.Collection;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring security filter chain...");

        http
                .csrf(csrf -> {
                    log.debug("Configuring CSRF protection");
                    csrf.ignoringRequestMatchers(antMatcher("/h2-console/**"));
                })
                .headers(headers -> {
                    log.debug("Disabling frame options for H2 console");
                    headers.frameOptions().disable();
                })
                .authorizeHttpRequests(auth -> {
                    log.info("Configuring authorization rules");
                    auth.requestMatchers(
                                    "/", "/about", "/login", "/register",
                                    "/css/**", "/image/**", "/js/**", "/account",
                                    "/h2-console/**", "/books", "/books/**"
                            ).permitAll()
                            .requestMatchers("/employee/**").hasRole("EMPLOYEE")
                            .requestMatchers("/client/**").hasRole("CLIENT")
                            .anyRequest().authenticated();
                    log.debug("Permitted paths: [/about, /login, /register, etc.]");
                    log.debug("Role-based access: EMPLOYEE → /employee/** | CLIENT → /client/**");
                })
                .formLogin(form -> {
                    log.debug("Configuring form login");
                    form.loginPage("/login")
                            .loginProcessingUrl("/login")
                            .successHandler(customAuthenticationSuccessHandler())
                            .failureUrl("/login?error=true")
                            .permitAll();
                })
                .logout(logout -> {
                    log.debug("Configuring logout");
                    logout.logoutUrl("/logout")
                            .logoutSuccessUrl("/login?logout=true")
                            .permitAll();
                });

        log.info("Security configuration completed successfully");
        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            log.info("Authentication successful for user: {}", authentication.getName());

            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            String redirectUrl = "/login?error";

            log.debug("User authorities: {}", authorities);

            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals("ROLE_CLIENT")) {
                    redirectUrl = "/client";
                    log.debug("Redirecting CLIENT to /client");
                    break;
                } else if (authority.getAuthority().equals("ROLE_EMPLOYEE")) {
                    redirectUrl = "/employee";
                    log.debug("Redirecting EMPLOYEE to /employee");
                    break;
                }
            }

            if (redirectUrl.equals("/login?error")) {
                log.warn("No valid role found for user: {}", authentication.getName());
            }

            try {
                response.sendRedirect(redirectUrl);
            } catch (Exception e) {
                log.error("Redirect failed: {}", e.getMessage(), e);
                throw e;
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("Initializing BCrypt password encoder");
        return new BCryptPasswordEncoder();
    }
}