package com.downloadmanager.schedule;

import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.downloadmanager.schedule.task.ScheduleTask;

public class Schedule {

	private SortedMap<ScheduleTask, Future> tasksMap;

	private ScheduledExecutorService scheduledExecutorService;

	public Schedule() {
		tasksMap = new TreeMap<ScheduleTask, Future>();
		scheduledExecutorService = Executors.newScheduledThreadPool(1);
	}

	public boolean addTask(ScheduleTask task) {
		long time = task.getTime().getTime();
		long currentTime = System.currentTimeMillis();
		if (time < currentTime) {
			return false;
		}
		ScheduledFuture<?> scheduledFuture = scheduledExecutorService.schedule(task, time - currentTime,
				TimeUnit.MILLISECONDS);
		tasksMap.put(task, scheduledFuture);
		return true;
	}

	public boolean cancelTask(ScheduleTask task) {
		if (task.getTime().getTime() < System.currentTimeMillis()) {
			return false;
		}
		tasksMap.remove(task).cancel(false);
		return false;
	}

	public Set<ScheduleTask> getTasks() {
		return tasksMap.keySet();
	}

	public void shutdown() {
		scheduledExecutorService.shutdown();
	}

}
