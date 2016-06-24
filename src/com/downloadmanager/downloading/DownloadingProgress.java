package com.downloadmanager.downloading;

import java.util.Date;

public interface DownloadingProgress {

	Date getStartDownloadingTime();

	Date getFinishDownloadingTime();

	long getDownloadingTime();

	double getAverageSpeed();

	double getCurrentSpeed();

	int getSize();

	int getDownloadedSize();

	double getProgressPercents();
}
