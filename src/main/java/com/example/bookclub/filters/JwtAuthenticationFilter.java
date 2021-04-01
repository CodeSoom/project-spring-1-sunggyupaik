package com.example.bookclub.filters;

import com.example.bookclub.application.AuthenticationService;
import com.example.bookclub.domain.Role;
import com.example.bookclub.dto.ParseResultDto;
import com.example.bookclub.security.UserAuthentication;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    private final AuthenticationService authenticationService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   AuthenticationService authenticationService) {
        super(authenticationManager);
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws IOException, ServletException {
        String authorization = request.getHeader("Authorization");

        if(authorization != null) {
            ParseResultDto parseResultDto = authenticationService.parseToken(accessToken);
            Claims claims = parseResultDto.getClaims();
            Long id = claims.get("userId", Long.class);
            String email = claims.getSubject();

            List<Role>roles = authenticationService.roles(email);
            Authentication authentication = new UserAuthentication(id, email, roles);

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}
