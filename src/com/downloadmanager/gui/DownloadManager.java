package com.downloadmanager.gui;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.downloadmanager.configuration.Configuration;
import com.downloadmanager.configuration.PropertiesConfiguration;
import com.downloadmanager.downloading.Downloading;
import com.downloadmanager.downloading.DownloadingException;
import com.downloadmanager.downloading.DownloadingState;
import com.downloadmanager.downloading.InvalidURLException;
import com.downloadmanager.downloading.http.HTTPDownloading;
import com.downloadmanager.downloading.http.HTTPDownloadingBuilder;
import com.downloadmanager.util.FileUtil;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * 
 * Application's core.
 * 
 * @author Sajel
 *
 */
public class DownloadManager extends Application {

	private static final String TITLE = "Download Manager";

	private Configuration configuration;

	private BorderPane rootLayout;
	private ObservableList<Downloading> downloadings;

	public DownloadManager() throws Exception {
		configuration = new PropertiesConfiguration();
		HttpURLConnection.setFollowRedirects(false);
		downloadings = FXCollections.observableArrayList();
		downloadings.addAll(FileUtil.loadDownloadings(new File(configuration.getDownloadsFolder())));
	}

	public static void main(String[] args) throws InterruptedException, IOException, IOException,
			ClassNotFoundException, DownloadingException, InvalidURLException {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle(TITLE);
		URL layoutLocation = getClass().getClassLoader().getResource("DownloadingList.fxml");
		FXMLLoader loader = new FXMLLoader(layoutLocation);
		rootLayout = loader.load();
		DownloadingsController downloadingsController = loader.getController();
		downloadingsController.setDownloadings(downloadings);
		Scene scene = new Scene(rootLayout);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public ObservableList<Downloading> getDownloadings() {
		return downloadings;
	}
	
	
}
