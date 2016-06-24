package com.downloadmanager.downloading.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.downloadmanager.downloading.DownloadingState;
import com.downloadmanager.util.Observer;
import com.downloadmanager.util.Observerable;

public class HTTPDownloadingPart implements Runnable, Serializable, Observerable {

	private static final int RESPONSE_OK = 200;

	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(HTTPDownloadingPart.class);

	private static final int BUFFER_SIZE = 4096;
	private HTTPDownloading fileDownloading;
	private int startPos;
	private int length;
	private int id;
	private List<Observer> observers;
	private transient int connectTries;
	private boolean completed;

	private int bytesDownloaded;

	private RandomAccessFile output = null;
	private InputStream inputStream = null;
	private URL url;
	private File file;

	private transient HttpURLConnection urlConnection;

	public HTTPDownloadingPart(HTTPDownloading fileDownloading, int startPos, int length, int partId) {
		super();
		this.fileDownloading = fileDownloading;
		this.startPos = startPos;
		this.length = length;
		this.id = partId;
		observers = new LinkedList<>();
		url = fileDownloading.getUrl();
		file = fileDownloading.getOutputFile();
	}

	public void run() {
		try {
			try {
				int responseCode = connect();
				if (success(responseCode)) {
					if (responseCode == RESPONSE_OK) {
						if (id > 0) {
							logger.warn("{} - part {} stops, cause server doesn't support partial content.",
									fileDownloading.getName(), id);
							complete();
							return;
						}
						bytesDownloaded = 0;
					}
					initStreams();
					download();
					if (bytesDownloaded >= length) {
						logger.info("{} - part {} completed downloading.", fileDownloading.getName(), id);
						complete();
					}
				} else {
					logger.error("{} - part {}  - server responded with error.", fileDownloading.getName(), id);
					complete();
				}
			} finally {
				closeStreams();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean serverError(int responseCode) {
		return responseCode / 100 == 5;
	}

	private boolean success(int responseCode) {
		return responseCode / 100 == 2;
	}

	private void complete() {
		completed = true;
		notifyObservers(0);
	}

	boolean isFinished() {
		return completed;
	}

	private void download() throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead;
		logger.info("{} - part {} starts downloading.", fileDownloading.getName(), id);
		while (fileDownloading.getState() != DownloadingState.STOPED
				&& (bytesRead = inputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
			while (fileDownloading.getState() == DownloadingState.PAUSED) {
				try {
					synchronized (this) {
						wait();
					}
				} catch (InterruptedException e) {
					logger.error("Error during downloading.", e);
					throw new RuntimeException(e);
				}
			}
			output.write(buffer, 0, bytesRead);
			bytesDownloaded += bytesRead;
			notifyObservers(bytesRead);
		}
		urlConnection.disconnect();
		logger.info("{} - part {} stops downloading.", fileDownloading.getName(), id);
	}

	@Override
	public List<Observer> getObservers() {
		return observers;
	}

	private int connect() throws IOException {
		urlConnection = (HttpURLConnection) url.openConnection();
		String byteRange = (startPos + bytesDownloaded) + "-" + (startPos + length - 1);
		urlConnection.setRequestProperty("Range", "bytes=" + byteRange);
		urlConnection.connect();
		int responseCode = urlConnection.getResponseCode();
		logger.info("{} - part {} connected with response: {}", fileDownloading.getName(), id, responseCode);
		while (serverError(responseCode) && connectTries++ <= 20) {
			urlConnection.disconnect();
			long sleepTime = (long) (id * 1000);
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				logger.error("Error during connecting.", e);
				throw new RuntimeException(e);
			}
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.connect();
			responseCode = urlConnection.getResponseCode();
		}
		return responseCode;
	}

	private void initStreams() throws IOException {
		output = new RandomAccessFile(file, "rw");
		output.seek(startPos + bytesDownloaded);
		inputStream = urlConnection.getInputStream();
	}

	private void closeStreams() throws IOException {
		if (output != null)
			output.close();
		if (inputStream != null)
			inputStream.close();
	}
}
