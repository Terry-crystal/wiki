package com.example.wiki.service;

import com.example.wiki.domain.User;
import com.example.wiki.domain.UserExample;
import com.example.wiki.exception.BusinessException;
import com.example.wiki.exception.BusinessExceptionCode;
import com.example.wiki.mapper.UserMapper;
import com.example.wiki.req.UserQueryReq;
import com.example.wiki.req.UserSaveReq;
import com.example.wiki.resp.PageResp;
import com.example.wiki.resp.UserQueryResp;
import com.example.wiki.util.CopyUtil;
import com.example.wiki.util.SnowFlake;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;


/**
 * @author Crystal
 * @version 1.0
 * @date 2021/9/14 19:40
 */
@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserMapper userMapper;

    @Resource
    private SnowFlake snowFlake;


    public PageResp<UserQueryResp> list(UserQueryReq req) {

        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria(); //以上两行为固定写法

        if (!ObjectUtils.isEmpty(req.getLoginName())) {
            criteria.andLoginNameEqualTo(req.getLoginName());
        }

        PageHelper.startPage(req.getPage(), req.getSize());  //实现后端分页功能，集成插件即可
        //但是使用这个类，只对一个select语句有作用，如果有多条select语句则会失效
        List<User> userList = userMapper.selectByExample(userExample);

        PageInfo<User> pageInfo = new PageInfo<>(userList);   //需要把查出数据源放在后面

//        List<UserResp> respList = new ArrayList<>();
//        for (User user : userList) {
//                //UserResp userResp=new UserResp();
//                //BeanUtils.copyProperties(user,userResp);
//            UserResp userResp = CopyUtil.copy(user, UserResp.class);    //使用单个对象来进行复制，但是只需将对应的类带入进去就可以了
//            respList.add(userResp);
//        }

        //列表复制
        List<UserQueryResp> respList = CopyUtil.copyList(userList, UserQueryResp.class);

        PageResp<UserQueryResp> pageResp = new PageResp<>();

        pageResp.setTotal(pageInfo.getTotal()); //获取总行数，建议将数据返回到前端中，这样方便前端自己计算页码
        pageResp.setList(respList);

        return pageResp;
    }

    /**
     * 保存用户功能 可以是编辑的保存也可以是新增的保存
     */
    public void save(UserSaveReq req) {
        User user = CopyUtil.copy(req, User.class);  //将请求参数变成我们的实体
        if (ObjectUtils.isEmpty(req.getId())) {

            //判断数据库中不存在此用户的话就可以进行新增操作
            if (ObjectUtils.isEmpty(selectByLoginName(req.getLoginName()))) {
                //新增
                user.setId(snowFlake.nextId());
                userMapper.insert(user);
            } else {
                // 用户名已经存在，需要对前端进行一个返回，提醒前端用户名存在的话需要进行对应的操作
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
            }

        } else {
            //更新
            userMapper.updateByPrimaryKey(user);
        }
    }

    /**
     * 使用id对某一用户进行删除
     *
     * @param id
     */
    public void delete(Long id) {
        userMapper.deleteByPrimaryKey(id);
    }


    /**
     * 通过id查找用户
     *
     * @param loginName
     * @return
     */
    public User selectByLoginName(String loginName) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria(); //以上两行为固定写法
        criteria.andLoginNameEqualTo(loginName);
        List<User> userList = userMapper.selectByExample(userExample);  //使用了mybatis所以需要使用list来接收
        if (CollectionUtils.isEmpty(userList)) {
            return null;    //没有，则返回空
        } else {
            return userList.get(0); //如果有，则返回第一条数据
        }
    }


}
