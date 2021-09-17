package com.example.wiki.service;

import com.example.wiki.domain.Category;
import com.example.wiki.domain.CategoryExample;
import com.example.wiki.mapper.CategoryMapper;
import com.example.wiki.req.CategoryQueryReq;
import com.example.wiki.req.CategorySaveReq;
import com.example.wiki.resp.CategoryQueryResp;
import com.example.wiki.resp.PageResp;
import com.example.wiki.util.CopyUtil;
import com.example.wiki.util.SnowFlake;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;


/**
 * @author Crystal
 * @version 1.0
 * @date 2021/9/14 19:40
 */
@Service
public class CategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryService.class);

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SnowFlake snowFlake;


    public PageResp<CategoryQueryResp> list(CategoryQueryReq req) {

        CategoryExample categoryExample = new CategoryExample();
        CategoryExample.Criteria criteria = categoryExample.createCriteria(); //以上两行为固定写法

        PageHelper.startPage(req.getPage(), req.getSize());  //实现后端分页功能，集成插件即可
        //但是使用这个类，只对一个select语句有作用，如果有多条select语句则会失效
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);

        PageInfo<Category> pageInfo = new PageInfo<>(categoryList);   //需要把查出数据源放在后面

//        List<CategoryResp> respList = new ArrayList<>();
//        for (Category category : categoryList) {
//                //CategoryResp categoryResp=new CategoryResp();
//                //BeanUtils.copyProperties(category,categoryResp);
//            CategoryResp categoryResp = CopyUtil.copy(category, CategoryResp.class);    //使用单个对象来进行复制，但是只需将对应的类带入进去就可以了
//            respList.add(categoryResp);
//        }

        //列表复制
        List<CategoryQueryResp> respList = CopyUtil.copyList(categoryList, CategoryQueryResp.class);

        PageResp<CategoryQueryResp> pageResp = new PageResp<>();

        pageResp.setTotal(pageInfo.getTotal()); //获取总行数，建议将数据返回到前端中，这样方便前端自己计算页码
        pageResp.setList(respList);

        return pageResp;
    }

    /**
     * 保存分类功能 可以是编辑的保存也可以是新增的保存
     */
    public void save(CategorySaveReq req) {
        Category category = CopyUtil.copy(req, Category.class);  //将请求参数变成我们的实体
        if (ObjectUtils.isEmpty(req.getId())) {
            //新增
            // 新增
            category.setId(snowFlake.nextId());
            categoryMapper.insert(category);
        } else {
            //更新
            categoryMapper.updateByPrimaryKey(category);
        }
    }

    /**
     * 使用id对某一分类进行删除
     *
     * @param id
     */
    public void delete(Long id) {
        categoryMapper.deleteByPrimaryKey(id);
    }

}
