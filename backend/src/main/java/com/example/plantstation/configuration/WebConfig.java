package com.example.plantstation.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Forward all unknown paths to index.html for Vue to handle routing
        registry.addViewController("/{spring:[^\\.]*}").setViewName("forward:/index.html");
    }
}
