package com.downloadmanager.downloading.http;

import java.io.Serializable;
import java.util.Date;

import com.downloadmanager.downloading.DownloadingProgress;
import com.downloadmanager.util.Observer;

public class HTTPDownloadingProgress implements DownloadingProgress, Serializable, Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private HTTPDownloading downloading;
	private Date startDownloadingTime;
	private Date finishDownloadingTime;

	private volatile long downloadingTime;

	private volatile int downloadedBytes;

	private volatile int downloadedAfterSave;

	private volatile long lastUpdateTime;

	private static final int CURRENT_SPEED_TIME_NIN_INTERVAL = 1000;

	private volatile int currentSpeedTimeElapsed;

	private volatile int currentSpeedBytesDownloaded;

	private volatile double currentSpeed;

	void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public HTTPDownloadingProgress(HTTPDownloading downloading) {
		this.downloading = downloading;
	}

	void setStartDownloadingTime(Date startDownloadingTime) {
		this.startDownloadingTime = startDownloadingTime;
	}

	@Override
	public Date getStartDownloadingTime() {
		return startDownloadingTime;
	}

	@Override
	public Date getFinishDownloadingTime() {
		return finishDownloadingTime;
	}

	@Override
	public long getDownloadingTime() {
		return downloadingTime;
	}

	@Override
	public double getAverageSpeed() {
		return (double) downloadedBytes / downloadingTime * 1000;
	}

	@Override
	public int getSize() {
		return downloading.getSize();
	}

	@Override
	public int getDownloadedSize() {
		return downloadedBytes;
	}

	@Override
	public double getProgressPercents() {
		return (double) downloadedBytes / getSize() * 100;
	}

	@Override
	public synchronized void update(int newBytes) {
		long newTime = System.currentTimeMillis();
		downloadingTime += newTime - lastUpdateTime;
		currentSpeedTimeElapsed += newTime - lastUpdateTime;
		lastUpdateTime = newTime;
		downloadedBytes += newBytes;
		currentSpeedBytesDownloaded += newBytes;
		downloadedAfterSave += newBytes;
		if (currentSpeedTimeElapsed > CURRENT_SPEED_TIME_NIN_INTERVAL) {
			currentSpeedTimeElapsed = 0;
			currentSpeedBytesDownloaded = 0;
		} else {
			currentSpeed = (double) currentSpeedBytesDownloaded / currentSpeedTimeElapsed;
		}
		if (downloadedAfterSave / 1024 >= downloading.getConfiguration().getKiloBytesPerSave()) {
			downloading.saveProgress();
			downloadedAfterSave = 0;
		}
		boolean finished = true;
		for (HTTPDownloadingPart part : downloading.getDownloadParts()) {
			if (!part.isFinished()) {
				finished = false;
				break;
			}
		}
		if (finished) {
			downloading.complete();
		}
	}

	void resume() {
		lastUpdateTime = System.currentTimeMillis();
	}

	@Override
	public double getCurrentSpeed() {
		return currentSpeed * 1000;
	}

}