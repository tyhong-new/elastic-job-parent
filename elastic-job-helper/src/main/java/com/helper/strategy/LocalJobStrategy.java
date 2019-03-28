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
        List<Integer> list = new ArrayList<>(1);
        list.add(0);
        jobInstances.forEach(e -> result.put(e, list));
        return result;
    }
}
