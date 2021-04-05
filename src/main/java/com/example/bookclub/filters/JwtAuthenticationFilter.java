package com.example.bookclub.filters;

import com.example.bookclub.application.AuthenticationService;
import com.example.bookclub.dto.ParseResultDto;
import com.example.bookclub.security.UserAuthenticationService;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    private final AuthenticationService authenticationService;
    private final UserAuthenticationService userAuthenticationService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   AuthenticationService authenticationService,
                                   UserAuthenticationService userAuthenticationService) {
        super(authenticationManager);
        this.authenticationService = authenticationService;
        this.userAuthenticationService = userAuthenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws IOException, ServletException {
        String authorization = request.getHeader("Authorization");

        if(authorization != null) {
            String accessToken = authorization.substring("Bearer ".length());
            ParseResultDto parseResultDto = authenticationService.parseToken(accessToken);
            Claims claims = parseResultDto.getClaims();
            String email = claims.getSubject();

            UserDetails userDetails = userAuthenticationService.loadUserByUsername(email);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, userDetails.getPassword(), userDetails.getAuthorities());
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}
