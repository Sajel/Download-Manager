package com.downloadmanager.util;

import static com.downloadmanager.downloading.Downloading.DOWNLOADING_FILE_EXTENSTION;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import com.downloadmanager.downloading.Downloading;;

public class FileUtil {

	public static boolean isValidFileName(String fileName) {
		File file = new File(fileName);
		try {
			if (!file.exists()) {
				file.createNewFile();
				file.delete();
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	private static final String[] INVALID_SYMBOLS = { "/", "\\" };

	public static String normalizeFileName(String fileName) {
		if (fileName != null) {
			for (String symbol : INVALID_SYMBOLS) {
				fileName = fileName.replace(symbol, "");
			}
		}
		return fileName;
	}

	public static String getFileExtension(String fileName) {
		int index = fileName.lastIndexOf('.');
		if (index != -1)
			return fileName.substring(index);
		else
			return null;
	}

	public static List<Downloading> loadDownloadings(File dir) throws IOException, ClassNotFoundException {
		if (!dir.exists() || !dir.isDirectory()) {
			return null;
		}
		List<Downloading> list = new ArrayList<>();
		for (File file : dir.listFiles()) {
			String fileName = file.getName();
			if (DOWNLOADING_FILE_EXTENSTION.equals(getFileExtension(fileName))) {
				ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
				Downloading downloading = (Downloading) inputStream.readObject();
				list.add(downloading);
				if (inputStream != null) {
					inputStream.close();
				}
			}
		}
		return list;
	}

}
