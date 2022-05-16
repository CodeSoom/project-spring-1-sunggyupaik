package com.example.bookclub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 파일 업로드 경로를 등록합니다.
 */
@Configuration
public class UploadPathConfig implements WebMvcConfigurer {
//    @Value("${resources.uri_path}")
//    private String resourcesUriPath;
//    @Value("${resources.location}")
//    private String resourceLocation;
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler(resourcesUriPath + "/**")
//                .addResourceLocations("file://" + resourceLocation);
//    }
}
