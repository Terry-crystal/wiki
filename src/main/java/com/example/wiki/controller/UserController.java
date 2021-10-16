package com.example.wiki.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.wiki.req.UserLoginReq;
import com.example.wiki.req.UserQueryReq;
import com.example.wiki.req.UserResetPasswordReq;
import com.example.wiki.req.UserSaveReq;
import com.example.wiki.resp.CommonResp;
import com.example.wiki.resp.PageResp;
import com.example.wiki.resp.UserLoginResp;
import com.example.wiki.resp.UserQueryResp;
import com.example.wiki.service.UserService;
import com.example.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.concurrent.TimeUnit;


/**
 * @author Crystal
 * @version 1.0
 * @date 2021/9/13 20:28
 */
@RestController //这个注解一般返回一个字符串 如果使用Controller注解，一般返回一个页面
@RequestMapping("/user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;    //将登录信息保存在redis里面

    @Resource
    private SnowFlake snowFlake;


    /**
     * 获取所有用户消息，有分页参数包括在内
     *
     * @param req 请求参数
     * @return 返回对应的用户列表和对应的分页数据
     */
    @GetMapping("/list")
    public CommonResp list(@Valid UserQueryReq req) {
        CommonResp<PageResp<UserQueryResp>> resp = new CommonResp<>();
        PageResp<UserQueryResp> list = userService.list(req);
        resp.setContent(list);
        return resp;
    }

    /**
     * 保存用户接口
     *
     * @param req
     * @return
     */
    @PostMapping("/save")//@RequestBody 注解是代表了以json格式post，而不是文件二进制上传格式，没有注解默认为二进制
    public CommonResp save(@Valid @RequestBody UserSaveReq req) {
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));  //对密码进行md5加密处理
        CommonResp resp = new CommonResp<>();
        userService.save(req);
        return resp;
    }

    /**
     * 删除用户接口
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp = new CommonResp<>();
        userService.delete(id);
        return resp;
    }

    /**
     * 重置用户密码接口
     *
     * @param req
     * @return
     */
    @PostMapping("/reset-password")
    public CommonResp resetPassword(@Valid @RequestBody UserResetPasswordReq req) {
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));  //对密码进行md5加密处理
        CommonResp resp = new CommonResp<>();
        userService.resetPassword(req);
        return resp;
    }

    /**
     * 用户登录接口
     *
     * @param req
     * @return
     */
    @PostMapping("/login")
    public CommonResp login(@Valid @RequestBody UserLoginReq req) {
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));  //对密码进行md5加密处理
        CommonResp<UserLoginResp> resp = new CommonResp<>();
        UserLoginResp userLoginResp = userService.login(req);

        Long token = snowFlake.nextId();    //使用雪花算法生成token
        LOG.info("生成单点登录token:{}，并放入redis中", token);
        userLoginResp.setToken(token.toString());   //将生成的token放入到返回的存储着信息的实体类中
        redisTemplate.opsForValue().set(token, JSONObject.toJSONString(userLoginResp), 3600 * 24, TimeUnit.SECONDS); //在redis中存放token，有效期24小时

        resp.setContent(userLoginResp);
        return resp;
    }

    /**
     * 退出登录接口
     *
     * @param token
     * @return
     */
    @GetMapping("/logout/{token}")
    public CommonResp logout(@PathVariable String token) {
        CommonResp resp = new CommonResp<>();
        redisTemplate.delete(token);    //直接将在redis中的token清除即可，当然也可以写在service层
        LOG.info("从Redis中删除token:{}", token);
        return resp;
    }


}
