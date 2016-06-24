package com.downloadmanager.schedule.task;

import java.util.Date;

import com.downloadmanager.downloading.Downloading;

public class StartDownloadingTask extends DownloadingScheduleTask {

	public StartDownloadingTask(Date date, Downloading downloading) {
		super(date, downloading);
	}

	@Override
	public void run() {
		if (downloading.isStarted()) {
			downloading.resume();
		} else {
			downloading.start();
		}
	}

}
