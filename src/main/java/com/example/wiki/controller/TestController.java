package com.example.wiki.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Crystal
 * @version 1.0
 * @date 2021/9/13 20:28
 */
@RestController //这个注解一般返回一个字符串 如果使用Controller注解，一般返回一个页面
public class TestController {

    @Value("${test.hello:TEST}")
    private String testHello;

    /**
     * GET,POST,PUT,DELETE 四种增删改查请求
     * 使用RequestMapping默认表示这个接口支持所有的请求方式
     *
     * @return
     */
    @RequestMapping("/hello")
    public String hello() {
        return "Hello World!" + testHello;
    }

    @PostMapping("/hello/post")
    public String helloPost(String name) {
        return "Hello world! Post, " + name;
    }

}
