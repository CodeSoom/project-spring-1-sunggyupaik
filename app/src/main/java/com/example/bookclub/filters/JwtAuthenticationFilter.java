package com.example.bookclub.filters;

import com.example.bookclub.application.AuthenticationService;
import com.example.bookclub.security.AccountAuthenticationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    private final AuthenticationService authenticationService;
    private final AccountAuthenticationService accountAuthenticationService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   AuthenticationService authenticationService,
                                   AccountAuthenticationService accountAuthenticationService) {
        super(authenticationManager);
        this.authenticationService = authenticationService;
        this.accountAuthenticationService = accountAuthenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws IOException, ServletException {
//        String authorization = request.getHeader("Authorization");
//
//        if (authorization != null) {
//            String accessToken = authorization.substring("Bearer ".length());
//            ParseResultDto parseResultDto = authenticationService.parseToken(accessToken);
//            Claims claims = parseResultDto.getClaims();
//            String email = claims.getSubject();
//
//            UserDetails userDetails = accountAuthenticationService.loadUserByUsername(email);
//            Authentication authentication = new UsernamePasswordAuthenticationToken(
//                    userDetails, userDetails.getPassword(), userDetails.getAuthorities());
//            SecurityContext context = SecurityContextHolder.getContext();
//            context.setAuthentication(authentication);
//        }

        chain.doFilter(request, response);
    }
}
