package com.example;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.helper.annotation.EasySimpleJob;
import com.helper.annotation.LocalJob;
import org.springframework.stereotype.Component;

@Component
public class Dddd {
    @EasySimpleJob(cron = "0/5 * * * * ?")
    public void aVoid(ShardingContext shardingContext){
        System.out.println(shardingContext.getJobName());
        System.out.println("avoid:" + System.currentTimeMillis());
    }
    /*@EasySimpleJob(cron = "0/5 * * * * ?")
    public void dVoid(int a){
        System.out.println("avoid:" + System.currentTimeMillis());
    }*/
    @EasySimpleJob(cron = "0/5 * * * * ?")
    @LocalJob
    public void bVoid(ShardingContext shardingContext){
        System.out.println(shardingContext.getJobName());
        System.out.println(shardingContext.getShardingItem());
        System.out.println("bvoid:" + System.currentTimeMillis());
    }

}
