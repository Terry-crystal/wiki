package com.example.wiki.job;

import com.example.wiki.service.EbookSnapshotService;
import com.example.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Crystal
 * @version 1.0
 * @date 2021/10/17 14:38
 * <p>
 * 任务定时器类
 */

@Component
public class EbookSnapshotJob {

    private static final Logger LOG = LoggerFactory.getLogger(EbookSnapshotJob.class);

    @Resource
    EbookSnapshotService ebookSnapshotService;

    @Resource
    private SnowFlake snowFlake;    //注入雪花算法

    /**
     * 实现业务表中的数据流入到中间表，将数据的阅读量消息和点赞量消息汇总并且放入中间表
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void cron() throws InterruptedException {
        MDC.put("LOG_ID", String.valueOf(snowFlake.nextId()));  //增加日志流水号
        LOG.info("将Ebook中的数据汇总到EbookSnapshot表中");
        long start = System.currentTimeMillis();
        ebookSnapshotService.getSnapshot();
        LOG.info("更新EbookSnapshot表中数据，耗时{}毫秒", System.currentTimeMillis() - start);
    }

}

