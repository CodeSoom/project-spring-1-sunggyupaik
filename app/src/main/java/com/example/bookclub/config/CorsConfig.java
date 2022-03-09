package com.example.bookclub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 모든 경로에 허용하는 메서드를 등록한다.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry
				.addMapping("/**")
				.allowedMethods(
						"OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE"
				);
	}
}
