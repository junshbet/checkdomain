package com.junt.checkdomain.config;

import com.junt.checkdomain.model.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig{
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> {
                    req
                            .requestMatchers("/proxy", "/status").hasAuthority(Role.ADMIN.name())
                            .requestMatchers("/login","/login-handel", "/css/**", "/js/**", "/images/**", "/webjars/**","/register","/register-handel").permitAll()
                            .anyRequest().authenticated();
                })
                .exceptionHandling(ex -> {
                    ex.accessDeniedPage("/unauthorized");
                })
                .formLogin(form -> {
                    form
                            .loginPage("/login").permitAll();
                })
                .logout(logout -> {
                    logout
                            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                            .logoutSuccessUrl("/login")
                            .invalidateHttpSession(true)
                            .deleteCookies("JSESSIONID")
                            .addLogoutHandler((request, response, authentication) -> {
                                SecurityContextHolder.clearContext();
                            });
                }).sessionManagement(session->{
                    session.sessionFixation().newSession()
                            .maximumSessions(1).maxSessionsPreventsLogin(false);
                });
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
