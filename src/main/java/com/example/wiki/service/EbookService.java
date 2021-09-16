package com.example.wiki.service;

import com.example.wiki.domain.Ebook;
import com.example.wiki.domain.EbookExample;
import com.example.wiki.mapper.EbookMapper;
import com.example.wiki.req.EbookReq;
import com.example.wiki.resp.EbookResp;
import com.example.wiki.resp.PageResp;
import com.example.wiki.util.CopyUtil;
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
public class EbookService {

    private static final Logger LOG = LoggerFactory.getLogger(EbookService.class);

    @Resource
    private EbookMapper ebookMapper;

    public PageResp<EbookResp> list(EbookReq req) {

        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria(); //以上两行为固定写法

        if (!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");    //已经封装好了模糊查询的算法，只需要确认是左查询还是右查询
        }
        PageHelper.startPage(req.getPage(), req.getSize());  //实现后端分页功能，集成插件即可
        //但是使用这个类，只对一个select语句有作用，如果有多条select语句则会失效
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        PageInfo<Ebook> pageInfo = new PageInfo<>(ebookList);   //需要把查出数据源放在后面

//        List<EbookResp> respList = new ArrayList<>();
//        for (Ebook ebook : ebookList) {
//                //EbookResp ebookResp=new EbookResp();
//                //BeanUtils.copyProperties(ebook,ebookResp);
//            EbookResp ebookResp = CopyUtil.copy(ebook, EbookResp.class);    //使用单个对象来进行复制，但是只需将对应的类带入进去就可以了
//            respList.add(ebookResp);
//        }

        //列表复制
        List<EbookResp> respList = CopyUtil.copyList(ebookList, EbookResp.class);

        PageResp<EbookResp> pageResp = new PageResp<>();

        pageResp.setTotal(pageInfo.getTotal()); //获取总行数，建议将数据返回到前端中，这样方便前端自己计算页码
        pageResp.setList(respList);

        return pageResp;
    }

}
