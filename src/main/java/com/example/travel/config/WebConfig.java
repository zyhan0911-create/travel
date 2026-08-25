package com.example.travel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 当有人在浏览器访问以 /uploads/ 开头的网址时...
        registry.addResourceHandler("/uploads/**")
                // ...后端会自动去本地电脑的 uploads/ 文件夹中寻找对应的文件并返回
                .addResourceLocations("file:uploads/");
    }
}