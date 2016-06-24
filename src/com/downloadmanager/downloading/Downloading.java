package com.downloadmanager.downloading;

import java.util.Date;

public interface Downloading {

	String DOWNLOADING_FILE_EXTENSTION = ".dlp";

	void start();

	void pause();

	void restart();

	void resume();

	void stop();

	void delete();

	DownloadingProgress getProgress();

	boolean isStarted();

	String getName();

	void setName(String name);

	int getSpeedLimit();

	void setSpeedLimit(int speedLimit);

	DownloadingState getState();

	int getSize();

	Date getCreateDate();
}
