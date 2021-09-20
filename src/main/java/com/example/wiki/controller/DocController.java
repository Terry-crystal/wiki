package com.example.wiki.controller;

import com.example.wiki.req.DocQueryReq;
import com.example.wiki.req.DocSaveReq;
import com.example.wiki.resp.CommonResp;
import com.example.wiki.resp.DocQueryResp;
import com.example.wiki.resp.PageResp;
import com.example.wiki.service.DocService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

/**
 * @author Crystal
 * @version 1.0
 * @date 2021/9/13 20:28
 */
@RestController //这个注解一般返回一个字符串 如果使用Controller注解，一般返回一个页面
@RequestMapping("/doc")
public class DocController {

    @Resource
    private DocService docService;

    /**
     * 获取所有文档消息，有分页参数包括在内
     *
     * @param req 请求参数
     * @return 返回对应的文档列表和对应的分页数据
     */
    @GetMapping("/list")
    public CommonResp list(@Valid DocQueryReq req) {
        CommonResp<PageResp<DocQueryResp>> resp = new CommonResp<>();
        PageResp<DocQueryResp> list = docService.list(req);
        resp.setContent(list);
        return resp;
    }

    /**
     * 一次性将所有文档数据抽取出来，不需要进行分页的处理
     *
     * @return 返回装有文档数据的list
     */
    @GetMapping("/all")
    public CommonResp all() {
        CommonResp<List<DocQueryResp>> resp = new CommonResp<>();
        List<DocQueryResp> list = docService.all();
        resp.setContent(list);
        return resp;
    }


    @PostMapping("/save")//@RequestBody 注解是代表了以json格式post，而不是文件二进制上传格式，没有注解默认为二进制
    public CommonResp save(@Valid @RequestBody DocSaveReq req) {
        CommonResp resp = new CommonResp<>();
        docService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{idsStr}")
    public CommonResp delete(@PathVariable String idsStr) {
        CommonResp resp = new CommonResp<>();
        if (idsStr != null) {
            List<String> list = Arrays.asList(idsStr.split(","));//通过“，”对其转成一个数组
            docService.delete(list);
            return resp;
        }
        resp.setSuccess(false);
        return resp;
    }


}
