package com.aliamauri.meat.top.manager;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadManager {
	private ThreadPoolExecutor longExecutor;
	private ThreadPoolExecutor shortExecutor;

	// 单例
	private ThreadManager() {
	}

	private static ThreadManager instance = new ThreadManager();

	public static ThreadManager getInstance() {
		return instance;
	}

	/**
	 * 执行任务
	 * 
	 * @param runnable
	 */
	public void executeShortTask(Runnable runnable) {
		if (shortExecutor == null) {
			// 如果当前没有线程池 创建
			// 参数1 管理的线程的数量
			// 参数2 如果队列满了 额外开三个线程
			// 参数3 线程池闲置的存活的时间
			// 参数4 时间的单位
			// 参数5 队列 额外排队的数量 10个
			shortExecutor = new ThreadPoolExecutor(3, 3, 1, TimeUnit.MINUTES,
					new LinkedBlockingQueue<Runnable>(10));
		}
		// 如果有执行任务
		shortExecutor.execute(runnable);
	}

	/**
	 * 执行任务
	 * 
	 * @param runnable
	 */
	public void executeLongTask(Runnable runnable) {
		if (longExecutor == null) {
			// 如果当前没有线程池 创建
			// 参数1 管理的线程的数量
			// 参数2 如果队列满了 额外开三个线程
			// 参数3 线程池闲置的存活的时间
			// 参数4 时间的单位
			// 参数5 队列 额外排队的数量 10个
			longExecutor = new ThreadPoolExecutor(3, 3, 1, TimeUnit.HOURS,
					new LinkedBlockingQueue<Runnable>(10));
		}
		// 如果有执行任务
		longExecutor.execute(runnable);
	}

	/**
	 * 取消任务
	 * 
	 * @param runnable
	 */
	public void cancelLongTask(Runnable runnable) {
		if (longExecutor != null && !longExecutor.isShutdown()
				&& !longExecutor.isTerminated()) {
			longExecutor.remove(runnable);
		}
	}
}
