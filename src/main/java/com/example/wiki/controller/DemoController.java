package com.example.wiki.controller;

import com.example.wiki.domain.Demo;
import com.example.wiki.service.DemoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Crystal
 * @version 1.0
 * @date 2021/9/13 20:28
 */
@RestController //这个注解一般返回一个字符串 如果使用Controller注解，一般返回一个页面
@RequestMapping("/demo")
public class DemoController {

    @Resource
    private DemoService demoService;

    @GetMapping("list")
    public List<Demo> list(String name) {
        return demoService.list();
    }

}
