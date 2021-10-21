package com.example.bookclub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityJavaConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().permitAll()
        .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/")
                .defaultSuccessUrl("/", false)
                .permitAll()
        .and()
                .logout()
                .logoutUrl("/logout");

        http
                .csrf().disable()
                .headers()
                .frameOptions().disable();
    }
}
