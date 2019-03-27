package com.example;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.helper.annotation.EasySimpleJob;
import org.springframework.stereotype.Component;

@Component
public class Dddd {
    @EasySimpleJob(cron = "0/5 * * * * ?")
    public void aVoid(ShardingContext shardingContext){
        System.out.println(shardingContext.getJobName());
        System.out.println("avoid:" + System.currentTimeMillis());
    }
}
