package com.example.wiki.service;

import com.example.wiki.domain.Doc;
import com.example.wiki.domain.DocExample;
import com.example.wiki.mapper.DocMapper;
import com.example.wiki.req.DocQueryReq;
import com.example.wiki.req.DocSaveReq;
import com.example.wiki.resp.DocQueryResp;
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
public class DocService {

    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);

    @Resource
    private DocMapper docMapper;

    @Resource
    private SnowFlake snowFlake;


    public PageResp<DocQueryResp> list(DocQueryReq req) {

        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria(); //以上两行为固定写法
        docExample.setOrderByClause("sort asc");   //设置排序 按顺序asc

        PageHelper.startPage(req.getPage(), req.getSize());  //实现后端分页功能，集成插件即可
        //但是使用这个类，只对一个select语句有作用，如果有多条select语句则会失效
        List<Doc> docList = docMapper.selectByExample(docExample);

        PageInfo<Doc> pageInfo = new PageInfo<>(docList);   //需要把查出数据源放在后面

//        List<DocResp> respList = new ArrayList<>();
//        for (Doc doc : docList) {
//                //DocResp docResp=new DocResp();
//                //BeanUtils.copyProperties(doc,docResp);
//            DocResp docResp = CopyUtil.copy(doc, DocResp.class);    //使用单个对象来进行复制，但是只需将对应的类带入进去就可以了
//            respList.add(docResp);
//        }

        //列表复制
        List<DocQueryResp> respList = CopyUtil.copyList(docList, DocQueryResp.class);

        PageResp<DocQueryResp> pageResp = new PageResp<>();

        pageResp.setTotal(pageInfo.getTotal()); //获取总行数，建议将数据返回到前端中，这样方便前端自己计算页码
        pageResp.setList(respList);

        return pageResp;
    }

    /**
     * 一次性查出所有的分类数据
     *
     * @return 返回装有分类数据的list
     */
    public List<DocQueryResp> all() {
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");   //设置排序 按顺序asc
        List<Doc> docList = docMapper.selectByExample(docExample);
        //列表复制
        List<DocQueryResp> respList = CopyUtil.copyList(docList, DocQueryResp.class);
        return respList;
    }

    /**
     * 保存分类功能 可以是编辑的保存也可以是新增的保存
     */
    public void save(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);  //将请求参数变成我们的实体
        if (ObjectUtils.isEmpty(req.getId())) {
            //新增
            // 新增
            doc.setId(snowFlake.nextId());
            docMapper.insert(doc);
        } else {
            //更新
            docMapper.updateByPrimaryKey(doc);
        }
    }

    /**
     * 使用id对某一分类进行删除
     *
     * @param id
     */
    public void delete(Long id) {
        docMapper.deleteByPrimaryKey(id);
    }

}
