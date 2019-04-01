package com.helper.strategy;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.lite.api.strategy.JobInstance;
import com.dangdang.ddframe.job.lite.api.strategy.JobShardingStrategy;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.internal.config.ConfigurationService;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.helper.annotation.SimpleJobCreator;
import com.helper.util.ApplicationContextHolder;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class LocalJobStrategy implements JobShardingStrategy {

    @Override
    public Map<JobInstance, List<Integer>> sharding(List<JobInstance> jobInstances, String jobName, int shardingTotalCount) {
        if (jobInstances.isEmpty()) {
            return Collections.emptyMap();
        }
        updateShardingTotalCount(jobInstances.size(), jobName);
        Map<JobInstance, List<Integer>> result = new LinkedHashMap<>(shardingTotalCount, 1);
        int j = 0;
        for (JobInstance jobInstance : jobInstances) {
            List<Integer> list = new ArrayList<>(1);
            list.add(j++);
            result.put(jobInstance, list);
        }
        return result;
    }

    private ConfigurationService getConfigurationService(String jobName) {
        CoordinatorRegistryCenter coordinatorRegistryCenter = ApplicationContextHolder.getBean(CoordinatorRegistryCenter.class);
        return new ConfigurationService(coordinatorRegistryCenter, jobName);
    }

    private void updateShardingTotalCount(int shardingTotalCount, String jobName) {
        ConfigurationService configurationService = getConfigurationService(jobName);
        LiteJobConfiguration jobConfiguration = configurationService.load(true);
        JobCoreConfiguration jobCoreConfiguration = jobConfiguration.getTypeConfig().getCoreConfig();
        try {
            FieldUtils.writeField(jobCoreConfiguration, "shardingTotalCount", shardingTotalCount, true);
            FieldUtils.writeField(jobConfiguration, "overwrite", true, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        configurationService.persist(jobConfiguration);
    }
}
