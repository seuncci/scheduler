package com.seun.scheduler.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry
                .addResourceHandler("/member/**")
                .addResourceLocations("file:src/main/resources/static/member");

        registry
                .addResourceHandler("/group/**")
                .addResourceLocations("file:src/main/resources/static/group");
    }
}