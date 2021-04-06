package com.example.bookclub.config;

import com.example.bookclub.application.AuthenticationService;
import com.example.bookclub.filters.AuthenticationErrorFilter;
import com.example.bookclub.filters.JwtAuthenticationFilter;
import com.example.bookclub.security.AccountAuthenticationService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.servlet.Filter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityJavaConfig extends WebSecurityConfigurerAdapter {
    private final AuthenticationService authenticationService;
    private final AccountAuthenticationService accountAuthenticationService;

    public SecurityJavaConfig(AuthenticationService authenticationService,
                              AccountAuthenticationService accountAuthenticationService) {
        this.authenticationService = authenticationService;
        this.accountAuthenticationService = accountAuthenticationService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        Filter authenticationFilter = new JwtAuthenticationFilter(
                authenticationManager(), authenticationService, accountAuthenticationService);

        Filter authenticationErrorFilter = new AuthenticationErrorFilter();

        http
                .authorizeRequests()
                .anyRequest().permitAll()
        .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .permitAll()
        .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/");

        http
                .csrf().disable()
                .headers()
                .frameOptions().disable();
    }
}
