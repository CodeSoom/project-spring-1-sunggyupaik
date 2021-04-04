package com.example.bookclub.config;

import com.example.bookclub.application.AuthenticationService;
import com.example.bookclub.filters.AuthenticationErrorFilter;
import com.example.bookclub.filters.JwtAuthenticationFilter;
import com.example.bookclub.security.UserAuthenticationService;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import javax.servlet.Filter;

@Configuration
public class SecurityJavaConfig extends WebSecurityConfigurerAdapter {
    private final AuthenticationService authenticationService;
    private final UserAuthenticationService userAuthenticationService;

    public SecurityJavaConfig(AuthenticationService authenticationService,
                              UserAuthenticationService userAuthenticationService) {
        this.authenticationService = authenticationService;
        this.userAuthenticationService = userAuthenticationService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        Filter authenticationFilter = new JwtAuthenticationFilter(
                authenticationManager(), authenticationService, userAuthenticationService);

        Filter authenticationErrorFilter = new AuthenticationErrorFilter();

        http
                .csrf().disable()
                .headers()
                .frameOptions().disable()
                .and()
                .addFilter(authenticationFilter)
                .addFilterBefore(authenticationErrorFilter, JwtAuthenticationFilter.class)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
    }
}
