package com.helper.strategy;

import com.dangdang.ddframe.job.lite.api.strategy.JobInstance;
import com.dangdang.ddframe.job.lite.api.strategy.JobShardingStrategy;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class LocalJobStrategy implements JobShardingStrategy {
    @Override
    public Map<JobInstance, List<Integer>> sharding(List<JobInstance> jobInstances, String jobName, int shardingTotalCount) {
        if (jobInstances.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<JobInstance, List<Integer>> result = new LinkedHashMap<>(shardingTotalCount, 1);
        int j=0;
        for (JobInstance jobInstance : jobInstances) {
            List<Integer> list = new ArrayList<>(1);
            list.add(j++);
            result.put(jobInstance, list);
        }
        return result;
    }
}
