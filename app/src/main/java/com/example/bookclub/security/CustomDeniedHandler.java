package com.example.bookclub.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 예외인 경우에 접근제한 페이지로 이동한다.
 */
@Component
public class CustomDeniedHandler implements AccessDeniedHandler {
	@Override
	public void handle(HttpServletRequest request,
					   HttpServletResponse response,
					   AccessDeniedException accessDeniedException)
			throws IOException, ServletException {
		request.getRequestDispatcher("/access-denied")
				.forward(request, response);
	}
}
