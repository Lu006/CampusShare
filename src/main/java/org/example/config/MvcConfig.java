package org.example.config;

import org.example.intercentor.AdminInterceptor;
import org.example.intercentor.AuthInterceptor;
import org.example.intercentor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Autowired
    private AdminInterceptor adminInterceptor;

    @Autowired
    private AuthInterceptor authInterceptor;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**").addResourceLocations("file:upload/");
        registry.addResourceHandler("/tmp/**").addResourceLocations("file:tmp/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/upload/**")
                .excludePathPatterns("/tmp/**")
                .excludePathPatterns("/admin/**")
                .excludePathPatterns("/user/info/**")
                .excludePathPatterns("/login/**")
                .excludePathPatterns("/post/**")
                .addPathPatterns("/post/latest/**")
                .addPathPatterns("/post/search/**");
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                //放行哪些原始域
                .allowedOrigins("*")
                .allowedMethods(new String[]{"GET", "POST", "PUT", "DELETE"})
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
