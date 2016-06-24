package com.downloadmanager.downloading;

public interface DownloadingBuilder {

	DownloadingBuilder setUrlString(String urlString) throws InvalidURLException;

	DownloadingBuilder setFileDestFolder(String fileDestFolder);

	DownloadingBuilder setFileName(String fileName);

	DownloadingBuilder setName(String name);

	
	
}
