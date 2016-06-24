package com.downloadmanager.downloading.http;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.downloadmanager.configuration.Configuration;
import com.downloadmanager.downloading.DownloadingBuilder;
import com.downloadmanager.downloading.DownloadingException;
import com.downloadmanager.downloading.InvalidURLException;
import com.downloadmanager.util.FileUtil;

public class HTTPDownloadingBuilder implements DownloadingBuilder {

	private URL url;

	private Configuration configuration;

	private String fileDestFolder;

	private String fileName;

	private String name;

	private String originURL;

	private int fileSize;

	public HTTPDownloadingBuilder(String urlString, Configuration config) throws InvalidURLException {
		this.url = createURL(urlString);
		this.configuration = config;
		if (url != null) {
			initFileInfo();
		}
	}

	private void initFileInfo() {
		fileDestFolder = configuration.getDownloadsFolder();
		String urlString = url.toString();
		String fileName = urlString.substring(urlString.lastIndexOf('/') + 1);
		if (fileName == null && FileUtil.isValidFileName(fileName)) {
			this.fileName = fileName;
		}
	}

	private URL createURL(String urlString) throws InvalidURLException {
		if (!(urlString.startsWith("http://") || urlString.startsWith("https://"))) {
			throw new InvalidURLException("URL must start with 'http://' or 'https://'");
		}
		try {
			url = new URL(urlString);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			while (redirect(urlConnection)) {
				String redirectLocation = urlConnection.getHeaderField("Location");
				urlConnection.disconnect();
				url = new URL(redirectLocation);
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.connect();
			}
			fileSize = urlConnection.getContentLength();
			urlConnection.disconnect();
			return url;
		} catch (IOException e) {
			throw new InvalidURLException(e);
		}
	}

	@Override
	public DownloadingBuilder setUrlString(String urlString) throws InvalidURLException {
		createURL(urlString);
		if (url != null && fileName == null) {
			initFileInfo();
		}
		originURL = urlString;
		return this;
	}

	@Override
	public DownloadingBuilder setFileDestFolder(String fileDestFolder) {
		this.fileDestFolder = fileDestFolder;
		return this;
	}

	@Override
	public DownloadingBuilder setFileName(String fileName) {
		this.fileName = fileName;
		return this;
	}

	@Override
	public DownloadingBuilder setName(String name) {
		if (fileName == null && FileUtil.isValidFileName(name)) {
			this.fileName = name;
		}
		this.name = name;
		return this;
	}

	public HTTPDownloading build() throws DownloadingException {
		if (name == null || url == null || fileDestFolder == null || fileName == null) {
			throw new DownloadingException("Invalid downloading parameteres");
		}
		return new HTTPDownloading(name, url, configuration, new File(fileDestFolder, fileName), fileSize);
	}

	public String getOriginURL() {
		return originURL;
	}

	public void setOriginURL(String originURL) {
		this.originURL = originURL;
	}

	public String getFileDestFolder() {
		return fileDestFolder;
	}

	public String getFileName() {
		return fileName;
	}

	public String getName() {
		return name;
	}

	private boolean redirect(HttpURLConnection conn) throws IOException {
		return conn.getResponseCode() / 100 == 3;
	}
}
