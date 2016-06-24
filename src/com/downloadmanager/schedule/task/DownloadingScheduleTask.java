package com.downloadmanager.schedule.task;

import java.util.Date;

import com.downloadmanager.downloading.Downloading;

public abstract class DownloadingScheduleTask extends ScheduleTask {

	protected Downloading downloading;

	DownloadingScheduleTask(Date date, Downloading downloading) {
		super(date);
		this.downloading = downloading;
	}

	public Downloading getDownloading() {
		return downloading;
	}

	@Override
	public int compareTo(ScheduleTask o) {
		int dateCmp = this.date.compareTo(o.date);
		if (dateCmp == 0) {
			if (this.getClass() != o.getClass()) {
				return 1;
			}
		}
		return dateCmp;
	}
}
