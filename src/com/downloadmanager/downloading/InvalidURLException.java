package com.downloadmanager.downloading;


import com.downloadmanager.downloading.DownloadingException;

public class InvalidURLException extends DownloadingException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidURLException() {
		super();
	}

	public InvalidURLException(String msg) {
		super(msg);
	}

	public InvalidURLException(Throwable e) {
		super(e);
	}

}
