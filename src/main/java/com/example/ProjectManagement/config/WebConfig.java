package com.example.ProjectManagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Apply to all endpoints
                .allowedOrigins("https://app.mapx.in", "https://api.mapx.in", "https://mapx-web.netlify.app", "https://mapdesk.mapx.in", "https://mapx-geo-json-service.netlify.app", "http://localhost") // Your frontend URL
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true) // If you use cookies or HTTP authentication
                .maxAge(3600); // Cache preflight response for 1 hour
    }
}