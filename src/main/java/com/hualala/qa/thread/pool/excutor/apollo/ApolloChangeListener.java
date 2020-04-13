package com.hualala.qa.thread.pool.excutor.apollo;

import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.hualala.qa.thread.pool.excutor.config.ApplicationContextProvider;
import com.hualala.qa.thread.pool.excutor.pojo.ChangedThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * @author yefei
 * @date: 2020/4/13
 */

@Slf4j
@Component
@EnableApolloConfig
public class ApolloChangeListener {

    @Autowired
    private ApplicationContextProvider applicationContextProvider;

    private List<ChangedThreadPoolExecutor> empty = new ArrayList<>();

    @ApolloConfigChangeListener("dynamic.thread")
    public synchronized void threadListener(ConfigChangeEvent changeEvent) {
        log.info("changeEvent: {}", changeEvent.changedKeys());
        try {
            List<ChangedThreadPoolExecutor> changedThreadPoolExcutors = getChangedThreadPoolExcutors(changeEvent);
            log.info("apollo变更的线程数量为：{}", changedThreadPoolExcutors.size());

            changedThreadPoolExcutors.forEach((changedThreadPoolExcutor) -> {
                ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) applicationContextProvider.getBean(changedThreadPoolExcutor.getThreadPoolExecutorBeanName());

                if (changedThreadPoolExcutor.getCorePoolSize() <= threadPoolExecutor.getCorePoolSize()) {
                    // 新的核心线程数不大于原值，先设置核心线程数
                    changedThreadPoolExcutor.changeCorePoolSize(threadPoolExecutor);
                    changedThreadPoolExcutor.changeMaxPoolSize(threadPoolExecutor);
                } else {
                    // 新的核心线程数大于原值，先设置最大线程数
                    changedThreadPoolExcutor.changeMaxPoolSize(threadPoolExecutor);
                    changedThreadPoolExcutor.changeCorePoolSize(threadPoolExecutor);
                }

                changedThreadPoolExcutor.changeMaxQueueSize(threadPoolExecutor);
            });
        } catch (Exception e) {
            log.error("apollo change listener run exception", e);
        }
    }


    private List<ChangedThreadPoolExecutor> getChangedThreadPoolExcutors(ConfigChangeEvent changeEvent){
        HashMap<String, ChangedThreadPoolExecutor> changedThreadPoolExecutorHashMap = new HashMap<>();
        changeEvent.changedKeys().forEach((changeKey)->{
            if (changeKey.matches("^\\w+\\.threadPool\\.corePoolSize$")
                    || changeKey.matches("^\\w+\\.threadPool\\.maxPoolSize")
                    || changeKey.matches("^\\w+\\.threadPool\\.maxQueueSize")
                    ){
                String threadPoolExecutorBeanName = changeKey.replaceAll("^(\\w+)\\.threadPool\\.\\w+$", "$1");
                ChangedThreadPoolExecutor changedThreadPoolExecutor;
                if (changedThreadPoolExecutorHashMap.containsKey(threadPoolExecutorBeanName)) {
                    changedThreadPoolExecutor = changedThreadPoolExecutorHashMap.get(threadPoolExecutorBeanName);
                } else {
                    changedThreadPoolExecutor = new ChangedThreadPoolExecutor();
                    changedThreadPoolExecutor.setThreadPoolExecutorBeanName(threadPoolExecutorBeanName);
                    changedThreadPoolExecutorHashMap.put(threadPoolExecutorBeanName, changedThreadPoolExecutor);
                }

                if (changeKey.endsWith(".threadPool.corePoolSize")) {
                    changedThreadPoolExecutor.setCorePoolSize(Integer.valueOf(changeEvent.getChange(changeKey).getNewValue()));

                } else if (changeKey.endsWith(".threadPool.maxPoolSize")) {
                    changedThreadPoolExecutor.setMaxPoolSize(Integer.valueOf(changeEvent.getChange(changeKey).getNewValue()));

                } else if (changeKey.endsWith(".threadPool.maxQueueSize")) {
                    changedThreadPoolExecutor.setMaxQueueSize(Integer.valueOf(changeEvent.getChange(changeKey).getNewValue()));

                }
            }
        });

        return changedThreadPoolExecutorHashMap.isEmpty() ? empty : new ArrayList<>(changedThreadPoolExecutorHashMap.values());
    }
}
