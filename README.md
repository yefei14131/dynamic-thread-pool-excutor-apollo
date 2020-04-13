# dynamic-thread-pool-excutor-apollo
基于apollo的可动态配置线程池
## 背景
1. 线程池参数设置不合理想要调整怎么办？修改代码里的参数，重新上线。
2. 服务重启后，queue里的任务会丢失，如何保障？……
3. 能否动态修改corePoolSize，maximumPoolSize？ 可以，源码支持，可通过以下方法修改 threadPoolExecutor.setCorePoolSize(corePoolSize);
threadPoolExecutor.setMaximumPoolSize(maxPoolSize);
4. 可以动态修改Queue的长度吗？不可以。Executors.newFixedThreadPool()方法使用的LinkedBlockingQueue，长度熟悉capacity是final，不可修改。可以自定义queue实现。ResizeableCapacityLinkedBlockIngQueue是从LinkedBlockingQueue拷贝代码实现的，去掉了capacity的final修饰，并添加了setCapacity和getCapacity方法。创建线程时使用ResizeableCapacityLinkedBlockIngQueue可实现动态修改长度。

## 结合Apollo动态配置线程池主要参数的实现方案
1. 在Apollo后台配置线程池相关参数
2. Apollo clien端使用ApolloConfigChangeListener监听变化，如有线程池相关配置变化，找到对应的线程池bean并修改其参数

## 具体使用方法
1. 在apollo后台创建namespace： dynamic.thread。 必须使用"dynamic.thread"这个namespace，因为 changeListener监听的是这个namespace。
2. 在dynamic.thread下创建线程池相关的参数，目前支持核心线程数、最大线程数、队列最大长度。具体配置如下，其中demoExecutorBeanName为线程池beanName，与业务系统里的beanName对应。
	 - demoExecutorBeanName.threadPool.corePoolSize。核心线程数。固定以“.threadPool.corePoolSize”结尾、${demoExecutorBeanName}开头，且中间不能包含其他字符。
	 - demoExecutorBeanName.threadPool.maxPoolSize。最大线程数。固定以“.threadPool.maxPoolSize”结尾、${demoExecutorBeanName}开头，且中间不能包含其他字符。
	 - demoExecutorBeanName.threadPool.maxQueueSize。核心线程数。固定以“.threadPool.maxQueueSize”结尾、${demoExecutorBeanName}开头，且中间不能包含其他字符。
3. 业务代码里集成Apollo，参考官方文档：https://github.com/ctripcorp/apollo
4. 业务代码里添加maven依赖
```Xml
<dependency>
	<groupId>com.hualala.qa</groupId>
	<artifactId>dynamic.thread.pool.excutor</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>
```
5. 业务代码里创建线程池实例。
```Java
@Configuration
@EnableApolloConfig("dynamic.thread")
public class ThreadPoolConfig1 {

    @Value("${demoExecutorBeanName.threadPool.corePoolSize}")
    private int corePoolSize;

    @Value("${demoExecutorBeanName.threadPool.maxPoolSize}")
    private int maxPoolSize;

    @Value("${demoExecutorBeanName.threadPool.maxQueueSize}")
    private int maxQueueSize;

    @Bean(name = "demoExecutorBeanName")
    public ThreadPoolExecutor createThreadPoolExecutor(){
        return new ThreadPoolExecutor(corePoolSize, maxPoolSize, 60, TimeUnit.SECONDS, new ResizeableCapacityLinkedBlockIngQueue<>(maxQueueSize));
    }
}
```
