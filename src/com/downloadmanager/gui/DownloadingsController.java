package com.downloadmanager.gui;

import java.text.SimpleDateFormat;

import com.downloadmanager.downloading.Downloading;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class DownloadingsController {

	@FXML
	private TableView<Downloading> downloadingsTable;

	@FXML
	private TableColumn<Downloading, String> nameColumn;

	@FXML
	private TableColumn<Downloading, String> stateColumn;

	@FXML
	private TableColumn<Downloading, Integer> sizeColumn;

	@FXML
	private TableColumn<Downloading, Double> speedColumn;

	@FXML
	private TableColumn<Downloading, String> dateColumn;

	private TableColumn<Downloading, Double> progressColumn;

	public DownloadingsController() {
	}

	@FXML
	private void initialize() {
		nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
		stateColumn
				.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getState().toString()));
		sizeColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSize()).asObject());
		speedColumn.setCellValueFactory(
				cellData -> new SimpleDoubleProperty(cellData.getValue().getProgress().getCurrentSpeed()).asObject());
		dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
				(new SimpleDateFormat("dd-MM-YYYY").format(cellData.getValue().getCreateDate())).toString()));
		progressColumn.setCellValueFactory(
				cellData -> new SimpleDoubleProperty(cellData.getValue().getProgress().getProgressPercents()).asObject());
	}

	public void setDownloadings(ObservableList<Downloading> downloadings) {
		downloadingsTable.setItems(downloadings);
	}

	@FXML
	public void newDownloadingWindow() {
		System.out.println("op");
	}
}
