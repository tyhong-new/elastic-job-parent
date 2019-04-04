package aaa.bbb;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.helper.starter.annotation.EasySimpleJob;
import com.helper.starter.annotation.LocalJob;
import org.springframework.stereotype.Component;

@Component
public class Acc {
    @EasySimpleJob(cron = "0/5 * * * * ?")
    public void ccc() {
        System.out.println("ccc");
    }

    @EasySimpleJob(cron = "0/5 * * * * ?")
    @LocalJob
    public void bbb(ShardingContext shardingContext) {
        System.out.println("bbb:" + shardingContext.getShardingItem());
    }
}
