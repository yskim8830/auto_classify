package kr.co.proten.manager.common.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
	private static final Logger log = LoggerFactory.getLogger(AsyncConfig.class);
	
	// 기본 Thread 수
	private static final int TASK_CORE_POOL_SIZE = 30;
	// 최대 Thread 수
	private static final int TASK_MAX_POOL_SIZE = 100;
	// QUEUE 수
	private static final int TASK_QUEUE_CAPACITY = 200;
	// Thread Bean Name
	private final String EXECUTOR_BEAN_NAME = "proworker";
		
	@Resource(name="proworker")
	private ThreadPoolTaskExecutor proworker;
	
	@Bean(name="proworker")
	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(TASK_CORE_POOL_SIZE);
		executor.setMaxPoolSize(TASK_MAX_POOL_SIZE);
		executor.setQueueCapacity(TASK_QUEUE_CAPACITY);
		executor.setBeanName(EXECUTOR_BEAN_NAME);
		executor.setThreadNamePrefix("proworkerThread-");
		executor.setWaitForTasksToCompleteOnShutdown(false);
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
		executor.initialize();
		return executor;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new AsyncExceptionHandler();
	}
	
	/**
	 * task 생성전에 pool이 찼는지를 체크
	 * @return
	 */
	public boolean checkTaskExecute() {
		boolean result = true;
		log.info("[AsyncConfig] [checkTaskExecute] Run Thread Count is " + proworker.getActiveCount());
		
		if(proworker.getActiveCount() >= (TASK_MAX_POOL_SIZE + TASK_QUEUE_CAPACITY)) {
			result = false;
		}
		
		return result;
	}
	
	/**
	 * 유효한 스레드 카운트
	 * @return
	 */
	public int availableThreadCount() {
		int availableThreadCount = 0;
		availableThreadCount = (TASK_MAX_POOL_SIZE + TASK_QUEUE_CAPACITY) - proworker.getActiveCount();		
		if(availableThreadCount < 0) {
			availableThreadCount = 0;
		}
		return availableThreadCount;
	}
}
