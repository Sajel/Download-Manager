package com.downloadmanager.downloading;

public enum DownloadingState {
	DOWNLOADING, PAUSED, STOPED, DELETED, COMPLETED, ERROR;

	@Override
	public String toString() {
	    char c[] = name().toLowerCase().toCharArray();
	    c[0] = Character.toUpperCase(c[0]);
	    return new String(c);
	}
}
