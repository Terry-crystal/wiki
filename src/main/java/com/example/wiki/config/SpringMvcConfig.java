package com.example.wiki.config;

import com.example.wiki.interceptor.LogInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    @Resource
    LogInterceptor loginInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)   //注册过滤器
                .addPathPatterns("/**")   //针对所有的请求
                .excludePathPatterns("/login"); //但是，不是所有的接口都需要登录校验，比如登录接口,这里就是解除校验
    }

}
