package com.example.wiki.config;

import com.example.wiki.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    @Resource
    LoginInterceptor loginInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)   //注册过滤器
                .addPathPatterns("/**")   //针对所有的请求
                .excludePathPatterns(   //下面的接口请求时不需要拦截的，拦截是为了检验token的有效性，是为了检验权限是否够才开始操作接口的一个检查
                        "/test/**",
                        "/redis/**",
                        "/user/login",
                        "/category/all",
                        "/ebook/list",
                        "/doc/all/**",  //表示后面的值是任意的
                        "/doc/find-content/**",
                        "/doc/vote/**"
                );
    }

}
