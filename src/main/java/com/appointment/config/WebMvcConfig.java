package com.appointment.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置前端静态资源
        registry.addResourceHandler("/admin/**")
                .addResourceLocations("file:frontend/admin/");
        
        registry.addResourceHandler("/doctor/**")
                .addResourceLocations("file:frontend/doctor/");
        
        registry.addResourceHandler("/wechat/**")
                .addResourceLocations("file:frontend/wechat/");
    }
}