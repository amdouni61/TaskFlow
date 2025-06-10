package com.example.taskflow.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(true);
        // Use specific origins or use allowedOriginPatterns to match multiple origins
        corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:4200"));  // Example: your frontend URL
        // Or you can use:
        // corsConfig.setAllowedOriginPatterns(Arrays.asList("http://localhost:4200", "http://another-origin.com"));
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        registry.addMapping("/**").allowedOrigins("http://localhost:4200");  // Ensure frontend URL matches
    }
}
