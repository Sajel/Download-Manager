package com.downloadmanager.downloading.http;

import static com.downloadmanager.downloading.DownloadingState.COMPLETED;
import static com.downloadmanager.downloading.DownloadingState.DELETED;
import static com.downloadmanager.downloading.DownloadingState.DOWNLOADING;
import static com.downloadmanager.downloading.DownloadingState.ERROR;
import static com.downloadmanager.downloading.DownloadingState.PAUSED;
import static com.downloadmanager.downloading.DownloadingState.STOPED;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.downloadmanager.configuration.Configuration;
import com.downloadmanager.downloading.Downloading;
import com.downloadmanager.downloading.DownloadingException;
import com.downloadmanager.downloading.DownloadingProgress;
import com.downloadmanager.downloading.DownloadingState;
import com.downloadmanager.util.FileUtil;

public class HTTPDownloading implements Downloading, Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(HTTPDownloading.class);

	private transient Configuration configuration;
	private URL url;
	private File outputFile;
	private static final int THREADS_COUNT = 5;
	private List<HTTPDownloadingPart> downloadParts;
	private transient List<Thread> threads;
	private volatile DownloadingState state;
	private File progressFile;
	private Date createDate;
	boolean started;
	private String name;

	private int fileSize;

	private int speedLimit;

	private HTTPDownloadingProgress progress;

	private transient ObjectOutputStream progressOutputStream;

	@Override
	public DownloadingState getState() {
		return state;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	HTTPDownloading(String name, URL url, Configuration configuration, File file, int fileSize) {
		this.configuration = configuration;
		this.name = name;
		this.fileSize = fileSize;
		this.url = url;
		outputFile = file;
		createDate = new Date();
		state = STOPED;
		progress = new HTTPDownloadingProgress(this);
		logger.info("HTTPFileDownloading " + name + " created.");
		progressFile = new File(configuration.getDownloadsFolder(),
				FileUtil.normalizeFileName(name + DOWNLOADING_FILE_EXTENSTION));
		if (progressFile.exists()) {
			throw new DownloadingException("Downloading with such name already exists.");
		}
		saveProgress();
	}

	public void start() {
		if (!started) {
			int lengthPerPart = fileSize / THREADS_COUNT;
			int additionalLength = fileSize % THREADS_COUNT;
			int newPosition = 0;
			downloadParts = new LinkedList<HTTPDownloadingPart>();
			for (int i = 0; i < THREADS_COUNT; i++) {
				if (i == THREADS_COUNT - 1) {
					lengthPerPart += additionalLength;
				}
				HTTPDownloadingPart part = new HTTPDownloadingPart(this, newPosition, lengthPerPart, i);
				downloadParts.add(part);
				newPosition += lengthPerPart;
				part.registerObserver(progress);
			}
			started = true;
			logger.info("Starting " + name + ".");
			progress.setStartDownloadingTime(new Date());
			resume();
		}
	}

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

	public void pause() {
		if (state == DOWNLOADING) {
			logger.info("Pausing " + name + ".");
			state = PAUSED;
			saveProgress();
		} else {
			throw new IllegalStateException();
		}
	}

	public void resume() {
		if (state == STOPED || state == PAUSED) {
			logger.info("Resuming " + name + ".");
			if (state == STOPED) {
				threads = new LinkedList<Thread>();
				for (HTTPDownloadingPart downloadPart : downloadParts) {
					Thread thread = new Thread(downloadPart);
					thread.setUncaughtExceptionHandler((t, e) -> {
						logger.error("Downloading {} has been stop cause of {}.", name, e.getMessage());
						state = ERROR;
					});
					threads.add(thread);
				}
				progress.resume();
				state = DOWNLOADING;
				for (Thread thread : threads) {
					thread.start();
				}
			} else {
				state = DOWNLOADING;
				for (HTTPDownloadingPart downloadPart : downloadParts) {
					downloadPart.notifyAll();
				}
			}
		} else {
			throw new IllegalStateException();
		}
	}

	public void delete() {
		if (state != DELETED) {
			state = DELETED;
			saveProgress();
		}
	}

	void saveProgress() {
		try {
			if (progressOutputStream == null) {
				progressOutputStream = new ObjectOutputStream(new FileOutputStream(progressFile));
			}
			progressOutputStream.writeObject(this);
			logger.info(name + " was saved.");
		} catch (IOException e) {
			logger.error("Cannot save downloading's progress.", e);
			throw new DownloadingException(e);
		}
	}

	Configuration getConfiguration() {
		return configuration;
	}

	List<HTTPDownloadingPart> getDownloadParts() {
		return downloadParts;
	}

	void complete() {
		if (state != DELETED) {
			state = COMPLETED;
			logger.info(name + " completed.");
		}
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

	@Override
	public DownloadingProgress getProgress() {
		return progress;
	}

	@Override
	public void stop() {
		if (state != DELETED || state != COMPLETED) {
			saveProgress();
			state = STOPED;
			try {
				progressOutputStream.close();
				progressOutputStream = null;
			} catch (IOException e) {
				logger.error("Cannot close downloading's progress's file output stream", e);
				throw new DownloadingException(e);
			}
		}
	}

	@Override
	public void restart() {
		logger.info("Restarting " + name + ".");
		progress = new HTTPDownloadingProgress(this);
		start();
	}

	@Override
	public int getSize() {
		return fileSize;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean isStarted() {
		return started;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int getSpeedLimit() {
		return speedLimit;
	}

	@Override
	public void setSpeedLimit(int speedLimit) {
		this.speedLimit = speedLimit;
	}

}
