package com.example.bookclub.config;

import com.example.bookclub.common.interceptor.CommonHttpRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

/**
 * upload 경로 감지를 등록합니다.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	@Value("${image.path}")
	private String uploadPath;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry
				.addResourceHandler("/upload/**")
				.addResourceLocations(uploadPath)
				.setCachePeriod(60 * 10 * 6)
				.resourceChain(true)
				.addResolver(new PathResourceResolver());
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new CommonHttpRequestInterceptor());
	}
}
