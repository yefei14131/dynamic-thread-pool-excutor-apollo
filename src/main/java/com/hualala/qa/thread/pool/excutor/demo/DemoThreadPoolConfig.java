package com.hualala.qa.thread.pool.excutor.demo;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.hualala.qa.thread.pool.excutor.queue.ResizeableCapacityLinkedBlockIngQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 集成apollo请参考官方文档 https://github.com/ctripcorp/apollo
 *
 *
 *
 */

/**
 * @author yefei
 * @date: 2020/4/13
 *
 */
//@Configuration  // 实际使用时放开此行注释
//@EnableApolloConfig("qa.thread")  // 实际使用时放开此行注释。必须使用"qa.thread"这个namespace，因为 changeListener监听的是这个namespace
public class DemoThreadPoolConfig {


//    @Value("${demoExecutorBeanName.threadPool.corePoolSize}")  // 实际使用时放开此行注释。demoExecutorBeanName为线程池beanName，必须一一对应。后缀".threadPool.corePoolSize"必须固定。
    private int corePoolSize;

//    @Value("${demoExecutorBeanName.threadPool.maxPoolSize}")  // 实际使用时放开此行注释。demoExecutorBeanName为线程池beanName，必须一一对应。后缀".threadPool.maxPoolSize"必须固定。
    private int maxPoolSize;

//    @Value("${demoExecutorBeanName.threadPool.maxQueueSize}")  // 实际使用时放开此行注释。demoExecutorBeanName为线程池beanName，必须一一对应。后缀".threadPool.maxQueueSize"必须固定。
    private int maxQueueSize;

//    @Bean(name = "demoExecutorBeanName")  // 实际使用时放开此行注释。
    public ThreadPoolExecutor threadPoolExecutor3(){
        return new ThreadPoolExecutor(corePoolSize, maxPoolSize, 60, TimeUnit.SECONDS, new ResizeableCapacityLinkedBlockIngQueue<>(maxQueueSize));
    }

}
