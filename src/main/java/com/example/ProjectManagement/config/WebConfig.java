package com.example.ProjectManagement.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Allow all paths
                        .allowedOriginPatterns(
                                "https://app.mapx.in",
                                "https://api.mapx.in",
                                "https://*.netlify.app",  // wildcard for all Netlify subdomains
                                "https://mapx-web.netlify.app",
                                "https://mapdesk.mapx.in",
                                "https://mapx-geo-json-service.netlify.app",
                                "http://localhost"
                        )
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // include OPTIONS for preflight
                        .allowedHeaders("*")          // allow Authorization and other headers
                        .allowCredentials(true);      // enable cookies / credentials
            }
        };
    }
}