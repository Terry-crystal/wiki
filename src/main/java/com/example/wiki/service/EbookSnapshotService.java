package com.example.wiki.service;

import com.example.wiki.mapper.EbookSnapshotMapperCust;
import com.example.wiki.resp.StatisticResp;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Crystal
 * @version 1.0
 * @date 2021/9/14 19:40
 */
@Service
public class EbookSnapshotService {

    @Resource
    private EbookSnapshotMapperCust ebookSnapshotMapperCust;

    /**
     * 实现快照功能 sql实现功能有：
     * --查出某的天的数据并且插入一条某天的电子书快照数据
     * --判断今天是否有创建并且将数据从业务表放到中间表,放入今天的阅读数和点赞数
     * --放入与昨天相比的阅读量数和与昨天的比的评论数
     */
    public void getSnapshot() {
        ebookSnapshotMapperCust.getSnapshot();
    }

    /**
     * 获取总阅读数，总点赞数，今天阅读数，今天点赞数
     *
     * @return
     */
    public List<StatisticResp> getStatistic() {
        return ebookSnapshotMapperCust.getStatistic();
    }
}
