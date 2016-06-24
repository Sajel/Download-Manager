package com.downloadmanager.schedule.task;

import java.util.Date;

import com.downloadmanager.downloading.Downloading;

public class StopDownloadingTask extends DownloadingScheduleTask {

	public StopDownloadingTask(Date date, Downloading downloading) {
		super(date, downloading);
	}

	@Override
	public void run() {
		downloading.stop();
	}

}
