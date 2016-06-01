package com.aliamauri.meat.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.R.transition;

import com.aliamauri.meat.utils.LogUtil;
import com.aliamauri.meat.utils.MapUtils;
import com.aliamauri.meat.utils.UUIDUtils;

public abstract class BaseThreadExecutor {

	// private static BaseThread instance;
	protected static final ExecutorService executorService = Executors
			.newSingleThreadExecutor();

	// 任务列表
	protected static final Map<String, ThreadExecutorTask> queueMap = new HashMap<String, ThreadExecutorTask>();

	/**
	 * 添加任务
	 * 
	 * @param mTaskType
	 * @param obj
	 */
	public synchronized void addTask(int mTaskType, Object obj) {
		if (obj == null) {
			throw new IllegalArgumentException("parameter not null");
		}
		ThreadExecutorTask cmdTask = new ThreadExecutorTask();
		cmdTask.setObj(obj);
		cmdTask.setTaskFlag(mTaskType);
		queueMap.put(UUIDUtils.getUUID(), cmdTask);
		if (!isHaveTask) {
			isHaveTask = true;
			runNextTask();
		}
	}

	/**
	 * 添加任务
	 * 
	 * @param mTaskType
	 * @param obj
	 */
	public synchronized void addTask(int mTaskType) {
		ThreadExecutorTask cmdTask = new ThreadExecutorTask();
		cmdTask.setTaskFlag(mTaskType);
		queueMap.put(UUIDUtils.getUUID(), cmdTask);
		if (!isHaveTask) {
			isHaveTask = true;
			runNextTask();
		}
	}

	private boolean isHaveTask = false;

	private synchronized void runNextTask() {
		if (queueMap.size() != 0) {
			runTask();
		} else {
			isHaveTask = false;
		}
	}

	private void runTask() {

		executorService.execute(new Runnable() {

			@Override
			public void run() {
				if (queueMap.size() != 0) {
					LogUtil.e(this, "线程池做任务前的任务数  ：" + queueMap.size() + "个");

					if (LogUtil.getDeBugState()) {
						long currentTime = System.currentTimeMillis();
						String firstOrKey = MapUtils.getFirstOrKey(queueMap);
						ThreadExecutorTask cmdTask = queueMap.get(firstOrKey);
						disposeTask(cmdTask.getTaskFlag(), cmdTask.getObj());
						queueMap.remove(firstOrKey);
						LogUtil.e(
								this,
								"子线程处理任务花费的时间   ："
										+ (System.currentTimeMillis() - currentTime));
						LogUtil.e(this, "线程池做任务后任务数  ：" + queueMap.size() + "个");
						runNextTask();
					} else {
						String firstOrKey = null;
						try {
							firstOrKey = MapUtils.getFirstOrKey(queueMap);
							ThreadExecutorTask cmdTask = queueMap
									.get(firstOrKey);
							disposeTask(cmdTask.getTaskFlag(), cmdTask.getObj());
							// queueMap.remove(firstOrKey);
						} catch (Exception e) {

						} finally {
							// if (firstOrKey != null) {
							queueMap.remove(firstOrKey);
							runNextTask();
							// }
						}

					}

				}
			}
		});

	}

	/**
	 * 处理任务
	 * 
	 * @param taskType
	 * @param curentobj
	 */
	protected abstract void disposeTask(int taskType, Object curentobj);

	private class ThreadExecutorTask {
		private int taskFlag;
		private Object obj;

		public int getTaskFlag() {
			return taskFlag;
		}

		public void setTaskFlag(int taskFlag) {
			this.taskFlag = taskFlag;
		}

		public Object getObj() {
			return obj;
		}

		public void setObj(Object obj) {
			this.obj = obj;
		}

	}

}
