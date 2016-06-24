package com.downloadmanager.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configration of general options in application.
 * 
 * @author Sajel
 *
 */
public class PropertiesConfiguration implements Configuration {

	private Properties properties;

	private static Logger logger = LoggerFactory.getLogger(PropertiesConfiguration.class);

	public PropertiesConfiguration() {
		logger.info("Starting loading configuration.");
		properties = new Properties();
		File file = new File("config.prop");
		if (!file.exists()) {
			logger.warn("config.prop not found.");
			createDefaultConfig();
			saveConfig(file, properties);
		} else {
			try {
				InputStream inputStream = new FileInputStream(file);
				properties.load(inputStream);
			} catch (IOException e) {
				logger.error("Cannot load configuration.", e);
				createDefaultConfig();
				saveConfig(file, properties);
			}
		}
		logger.info("Configuration was loaded.");
	}

	private void saveConfig(File file, Properties properties) {
		try {
			OutputStream outputStream = new FileOutputStream(file);
			properties.store(outputStream, "Creating default configuration.");
			logger.info("Configuration was saved.");
		} catch (IOException e) {
			logger.error("Trying to save configuration.", e);
		}
	}

	private void createDefaultConfig() {
		logger.info("Creating default configuration.");
		properties.setProperty("DownloadsFolder", "D:\\Java\\workspace\\Download Manager\\downloads");
		properties.setProperty("KiloBytesPerSave", "1048576");
	}

	public String getDownloadsFolder() {
		return properties.getProperty("DownloadsFolder");
	}

	public int getKiloBytesPerSave() {
		return Integer.parseInt(properties.getProperty("KiloBytesPerSave"));
	}

}
