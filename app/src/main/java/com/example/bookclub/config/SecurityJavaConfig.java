package com.example.bookclub.config;

import com.example.bookclub.application.account.AccountAuthenticationService;
import com.example.bookclub.security.CustomDeniedHandler;
import com.example.bookclub.security.CustomEntryPoint;
import com.example.bookclub.security.PersistTokenRepository;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionEvent;
import java.time.LocalDateTime;

/**
 * 정보 보안 설정을 등록합니다.
 */
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityJavaConfig extends WebSecurityConfigurerAdapter {
    private final AccountAuthenticationService accountAuthenticationService;
    private final CustomEntryPoint customEntryPoint;
    private final CustomDeniedHandler customDeniedHandler;
    private final PersistTokenRepository persistTokenRepository;

    public SecurityJavaConfig(AccountAuthenticationService accountAuthenticationService,
                              CustomEntryPoint customEntryPoint,
                              CustomDeniedHandler customDeniedHandler,
                              PersistTokenRepository persistTokenRepository) {
        this.accountAuthenticationService = accountAuthenticationService;
        this.customEntryPoint = customEntryPoint;
        this.customDeniedHandler = customDeniedHandler;
        this.persistTokenRepository = persistTokenRepository;
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher() {
            @Override
            public void sessionCreated(HttpSessionEvent event) {
                super.sessionCreated(event);
                System.out.printf("===>> [%s] 세션 생성됨 %s \n", LocalDateTime.now(), event.getSession().getId());
            }

            @Override
            public void sessionDestroyed(HttpSessionEvent event) {
                super.sessionDestroyed(event);
                System.out.printf("===>> [%s] 세션 만료됨 %s \n", LocalDateTime.now(), event.getSession().getId());
            }
        });
    }

    @Bean
    SessionRegistry sessionRegistry(){
        return new SessionRegistryImpl();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountAuthenticationService);
    }

//    @Bean
//    PersistentTokenRepository tokenRepository(){
//        JdbcTokenRepositoryImpl repository = new JdbcTokenRepositoryImpl();
//        repository.setDataSource(dataSource);
//        try{
//            repository.removeUserTokens("junk");
//        } catch (Exception ex){
//            repository.setCreateTableOnStartup(true);
//        }
//        return repository;
//    }

    @Bean
    PersistentTokenBasedRememberMeServices rememberMeServices(){
        PersistentTokenBasedRememberMeServices services = new PersistentTokenBasedRememberMeServices(
                "bookclub-remember-me",
                accountAuthenticationService,
                persistTokenRepository
                ) {
            @Override
            protected Authentication createSuccessfulAuthentication(HttpServletRequest request, UserDetails user) {
                return new UsernamePasswordAuthenticationToken(
                        user.getUsername(), user.getPassword(), user.getAuthorities()
                );
            }
        };

        services.setTokenValiditySeconds(60 * 60 * 24 * 31);
        return services;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(request ->
                        request
                                .antMatchers("/", "/favicon.ico", "/login", "/login-error", "/login-required").permitAll()
                                .antMatchers("/access-denied").permitAll()
                                .antMatchers("/users/save").permitAll()
                                .antMatchers("/api/email/authentication").permitAll()
                                .antMatchers("/api/users").permitAll()
                                .antMatchers("/api/kakao-login").permitAll()
                                .antMatchers("/posts").permitAll()
                                .antMatchers("/api/post").permitAll()
                                .antMatchers("/api/search**").permitAll()
                                .antMatchers("/api/diary").permitAll()
                                .antMatchers("/api/diaryRaw").permitAll()
                                .antMatchers("/serverAuth").permitAll()
                                .antMatchers("/h2-console/**").permitAll()
                                .anyRequest()
                                .authenticated()
                )
                .formLogin(login ->
                        login
                                .loginPage("/login")
                                .loginProcessingUrl("/loginprocess")
                                .permitAll()
                                .defaultSuccessUrl("/", false)
                                .failureUrl("/login-error")
                )
                .logout(logout ->
                        logout
                                .logoutSuccessUrl("/")
                )
                .exceptionHandling(error ->
                        error
                                .authenticationEntryPoint(customEntryPoint)
                                .accessDeniedHandler(customDeniedHandler)
                )
                .rememberMe(r ->
                        r
                                .rememberMeServices(rememberMeServices())
                )
                .sessionManagement(s->
                        s
                                .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::changeSessionId)
                                .maximumSessions(1)
                                .maxSessionsPreventsLogin(false)
                                .expiredUrl("/")
                                .sessionRegistry(sessionRegistry())
                );


        http
                .cors()
                .disable()
                .csrf()
                .disable()

                .headers()
                .frameOptions()
                .disable();

        http.requiresChannel()
                .anyRequest().requiresInsecure();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .requestMatchers(
                        PathRequest.toStaticResources().atCommonLocations()
                        //PathRequest.toH2Console()
                )
                .antMatchers("/resources/images/**", "/error")
        ;
    }
}
