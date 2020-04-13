package com.hualala.qa.thread.pool.excutor.pojo;

import com.hualala.qa.thread.pool.excutor.queue.ResizeableCapacityLinkedBlockIngQueue;
import lombok.Data;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author yefei
 * @date: 2020/4/13
 */
@Data
public class ChangedThreadPoolExecutor {
    private String threadPoolExecutorBeanName;
    private int corePoolSize = -1;
    private int maxPoolSize = -1;
    private int maxQueueSize = -1;

    public void changeCorePoolSize(ThreadPoolExecutor threadPoolExecutor) {
        if (corePoolSize > 0) {
            threadPoolExecutor.setCorePoolSize(corePoolSize);
        }
    }
    public void changeMaxPoolSize(ThreadPoolExecutor threadPoolExecutor) {
        if (maxPoolSize > 0) {
            threadPoolExecutor.setMaximumPoolSize(maxPoolSize);
        }
    }
    public void changeMaxQueueSize(ThreadPoolExecutor threadPoolExecutor) {
        if (maxQueueSize > 0 && threadPoolExecutor.getQueue() instanceof ResizeableCapacityLinkedBlockIngQueue) {
            ((ResizeableCapacityLinkedBlockIngQueue)threadPoolExecutor.getQueue()).setCapacity(maxQueueSize);
        }
    }
}
