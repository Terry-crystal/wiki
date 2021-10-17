package com.example.wiki.job;

import com.example.wiki.service.DocService;
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
public class DocJob {

    private static final Logger LOG = LoggerFactory.getLogger(DocJob.class);

    @Resource
    DocService docService;

    @Resource
    private SnowFlake snowFlake;    //注入雪花算法

    /**
     * 每30秒更新电子书信息
     */
    @Scheduled(cron = "5/30 * * * * ?")
    public void cron() throws InterruptedException {
        MDC.put("LOG_ID", String.valueOf(snowFlake.nextId()));  //增加日志流水号
        LOG.info("更新电子书下的文档数据开始");
        long start = System.currentTimeMillis();
        docService.updateEbookInfo();   //定时器收集信息
        LOG.info("更新电子书下的文档数据结束，耗时{}毫秒", System.currentTimeMillis() - start);
    }

}

