package com.example;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
//
public class SimpleDemoJob  implements SimpleJob{

    public void execute(ShardingContext shardingContext) {
        System.out.println("SimpleDemoJob:" + System.currentTimeMillis());
    }
}
