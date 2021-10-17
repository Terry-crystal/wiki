package com.example.wiki.job;

import com.example.wiki.service.DocService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    /**
     * 每30秒更新电子书信息
     */
    @Scheduled(cron = "5/30 * * * * ?")
    public void cron() throws InterruptedException {
        docService.updateEbookInfo();   //定时器收集信息
    }

}

