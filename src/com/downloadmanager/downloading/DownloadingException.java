package com.downloadmanager.downloading;

public class DownloadingException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DownloadingException() {
		super();
	}

	public DownloadingException(String msg) {
		super(msg);
	}

	public DownloadingException(Throwable e) {
		super(e);
	}
}
