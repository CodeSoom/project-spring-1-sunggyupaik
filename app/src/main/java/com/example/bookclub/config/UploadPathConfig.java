package com.example.bookclub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class UploadPathConfig implements WebMvcConfigurer {
    @Value("${resources.uri_path}")
    private String resourcesUriPath;
    @Value("${resources.location}")
    private String resourceLocation;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler(resourcesUriPath + "/**")
        registry.addResourceHandler("static/images/**")
                .addResourceLocations("file:" + resourceLocation);
    }
}
