package com.example.wiki.controller;

import com.example.wiki.req.CategoryQueryReq;
import com.example.wiki.req.CategorySaveReq;
import com.example.wiki.resp.CommonResp;
import com.example.wiki.resp.CategoryQueryResp;
import com.example.wiki.resp.PageResp;
import com.example.wiki.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author Crystal
 * @version 1.0
 * @date 2021/9/13 20:28
 */
@RestController //这个注解一般返回一个字符串 如果使用Controller注解，一般返回一个页面
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 获取所有分类消息，有分页参数包括在内
     *
     * @param req 请求参数
     * @return 返回对应的分类列表和对应的分页数据
     */
    @GetMapping("/list")
    public CommonResp list(@Valid CategoryQueryReq req) {
        CommonResp<PageResp<CategoryQueryResp>> resp = new CommonResp<>();
        PageResp<CategoryQueryResp> list = categoryService.list(req);
        resp.setContent(list);
        return resp;
    }


    @PostMapping("/save")//@RequestBody 注解是代表了以json格式post，而不是文件二进制上传格式，没有注解默认为二进制
    public CommonResp save(@Valid @RequestBody CategorySaveReq req) {
        CommonResp resp = new CommonResp<>();
        categoryService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp = new CommonResp<>();
        categoryService.delete(id);
        return resp;
    }


}
