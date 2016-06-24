package com.downloadmanager.schedule.task;

import java.util.Date;

public abstract class ScheduleTask implements Runnable, Comparable<ScheduleTask> {

	protected Date date;

	public ScheduleTask(Date date) {
		this.date = date;
	}

	public Date getTime() {
		return date;
	}

}
