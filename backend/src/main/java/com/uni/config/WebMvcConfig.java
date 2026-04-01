package com.uni.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * 静态资源映射（本地文件上传后的访问）
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.file.upload-path:./uploads}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 /files/** 映射到本地上传目录
        String location = uploadPath.endsWith("/") ? "file:" + uploadPath : "file:" + uploadPath + "/";
        registry.addResourceHandler("/files/**")
                .addResourceLocations(location);
    }
}
