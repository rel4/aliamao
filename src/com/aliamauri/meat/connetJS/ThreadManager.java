package com.aliamauri.meat.connetJS;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;

/**
 * 线程池，用于管理线程
 * 
 * @author liang
 * 
 */

public class ThreadManager {
	// 代理对象
	private static ThreadProxyPool threadProxyPool;
	private static Object object = new Object();

	public static ThreadProxyPool getThreadProxyPool() {
		synchronized (object) {
			if (threadProxyPool == null) {
				threadProxyPool = new ThreadProxyPool(10, 10, 5L);
			}
			return threadProxyPool;
		}
	}

	public static class ThreadProxyPool {
		private ThreadPoolExecutor threadPoolExecutor;
		private int corePoolSize;
		private int maximumPoolSize;
		private long keepAliveTime;

		public ThreadProxyPool(int corePoolSize, int maximumPoolSize,
				long keepAliveTime) {
			this.corePoolSize = corePoolSize;
			this.maximumPoolSize = maximumPoolSize;
			this.keepAliveTime = keepAliveTime;
		}

		// 在此对象中,去做线程池开启线程中任务的操作
		public void execute(Runnable runnable) {
			if (runnable == null) {
				return;
			}
			// 创建线程池,去线程中的任务
			if (threadPoolExecutor == null || threadPoolExecutor.isShutdown()) {
				threadPoolExecutor = new ThreadPoolExecutor(
				// 核心线程数5
						corePoolSize,
						// 最大线程数10
						maximumPoolSize,
						// 存活时间
						keepAliveTime,
						// 存活时间单位
						TimeUnit.MILLISECONDS,
						// 任务的排队的队列
						new LinkedBlockingQueue<Runnable>(),
						// 创建线程的工厂
						Executors.defaultThreadFactory(),
						// 线程池处理不了任务的时候,异常的处理方式
						new AbortPolicy());
			}

			threadPoolExecutor.execute(runnable);
		}

		// 从线程池中移除任务
		public void cancel(Runnable runnable) {
			if (runnable != null && !threadPoolExecutor.isShutdown()) {
				threadPoolExecutor.getQueue().remove(runnable);
			}
		}
	}
}
