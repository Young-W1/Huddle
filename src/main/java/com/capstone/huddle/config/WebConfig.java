package com.capstone.huddle.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.path:uploads/profiles}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Create the upload directory if it doesn't exist
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String absolutePath = uploadDir.getAbsolutePath();

        registry.addResourceHandler("/huddle/uploads/profiles/**")
                .addResourceLocations("file:" + absolutePath + File.separator);

        log.info("Serving uploaded files from: file:{}{}", absolutePath, File.separator);
    }

    // CORS is handled by CorsConfig.java - removed duplicate config here
}