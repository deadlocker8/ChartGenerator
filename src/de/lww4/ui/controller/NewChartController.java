package de.lww4.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import de.lww4.logic.CSVTable;
import de.lww4.logic.ChartType;
import de.lww4.logic.Dashboard;
import de.lww4.ui.controller.subcontroller.SubControllerEditBarChartHorizontal;
import de.lww4.ui.controller.subcontroller.SubControllerEditChart;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logger.LogLevel;
import logger.Logger;

public class NewChartController
{
	@FXML private AnchorPane anchorPaneMain;
	@FXML private TextField textFieldTitle;
	@FXML private ColorPicker colorPicker;
	@FXML private TreeView<String> treeView;
	@FXML private StackPane stackPaneChart;
	@FXML private Button buttonSave;
	@FXML private Button buttonCancel;
	@FXML private HBox hboxChartTypes;

	public Stage stage;
	private Controller controller;
	public Image icon = new Image("de/lww4/resources/icon.png");
	public final ResourceBundle bundle = ResourceBundle.getBundle("de/lww4/main/", Locale.GERMANY);
	private ToggleGroup toggleGroupChartTypes;
	private boolean edit;
	private Dashboard dashboard;
	private int position;

	public void init(Stage stage, Controller controller, boolean edit, Dashboard dashboard, int position)
	{
		this.stage = stage;
		this.controller = controller;
		this.edit = edit;
		this.dashboard = dashboard;
		this.position = position;

		stackPaneChart.setStyle("-fx-border-color: #212121; -fx-border-width: 2;");

		generatePreview(ChartType.BAR_VERTICAL);

		toggleGroupChartTypes = new ToggleGroup();

		for(ChartType currentType : ChartType.values())
		{
			RadioButton currentRadioButton = new RadioButton(currentType.getName());
			currentRadioButton.setContentDisplay(ContentDisplay.BOTTOM);
			currentRadioButton.setToggleGroup(toggleGroupChartTypes);
			currentRadioButton.setUserData(currentType);
			hboxChartTypes.getChildren().add(currentRadioButton);
		}
		toggleGroupChartTypes.getToggles().get(0).setSelected(true);
		toggleGroupChartTypes.selectedToggleProperty().addListener(new ChangeListener<Toggle>()
		{
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue)
			{
				generatePreview((ChartType)newValue.getUserData());
			}
		});

		colorPicker.setValue(Color.web("#508DC7"));
		colorPicker.valueProperty().addListener(new ChangeListener<Color>()
		{
			@Override
			public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue)
			{
				updatePreview();
			}
		});

		initTreeView();

		if(edit)
		{
			// TODO get Chart from DB
			// dashboard.getCells().get(position);
			// TODO fill form with existing data
		}
	}

	private void initTreeView()
	{
		// DEBUG get from DB
		ArrayList<CSVTable> tables;
		try
		{
			tables = controller.database.getAllCSVTables();
		}
		catch(Exception e)
		{
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));

			tables = new ArrayList<>();
		}

		TreeItem<String> rootItem;
		if(tables.size() == 0)
		{
			rootItem = new TreeItem<String>("Keine Daten verfügbar");
		}
		else
		{
			rootItem = new TreeItem<String>("CSV Tabellen");

			for(CSVTable currentTable : tables)
			{
				TreeItem<String> currentMainItem = new TreeItem<String>(currentTable.getName());

				for(String currentColumn : currentTable.getColumnNames())
				{
					TreeItem<String> currentSubItem = new TreeItem<String>(currentColumn);
					currentMainItem.getChildren().add(currentSubItem);
				}

				rootItem.getChildren().add(currentMainItem);
			}
		}
		rootItem.setExpanded(true);
		treeView.setRoot(rootItem);
	}

	private void generatePreview(ChartType type)
	{
		stackPaneChart.getChildren().clear();

		try
		{
			FXMLLoader fxmlLoader = null;
			SubControllerEditChart subController;

			switch(type)
			{
				case BAR_HORIZONTAL:
					fxmlLoader = new FXMLLoader(getClass().getResource("/de/lww4/ui/fxml/subfxml/SubEditBarChartHorizontalGUI.fxml"));					
					break;
				case BAR_VERTICAL:
					fxmlLoader = new FXMLLoader(getClass().getResource("/de/lww4/ui/fxml/subfxml/SubEditBarChartVerticalGUI.fxml"));					
					break;
				case PIE:
					fxmlLoader = new FXMLLoader(getClass().getResource("/de/lww4/ui/fxml/subfxml/SubEditPieChartGUI.fxml"));					
					break;
				default:
					break;
			}		
			
			Parent root = (Parent)fxmlLoader.load();
			stackPaneChart.getChildren().add(root);
			subController = fxmlLoader.getController();		
			subController.init(this);	
			
		}
		catch(IOException e)
		{
			Logger.log(LogLevel.DEBUG, Logger.exceptionToString(e));
		}
	}

	private void updatePreview()
	{
		// TODO
	}

	public void save()
	{
		String title = textFieldTitle.getText();
		if(title == null || title.equals(""))
		{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warnung");
			alert.setHeaderText("");
			alert.setContentText("Bitte geben Sie einen Namen für das Diagramm an.");
			Stage dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
			dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
			dialogStage.getIcons().add(icon);
			dialogStage.centerOnScreen();
			alert.showAndWait();
			return;
		}

		// TODO null check other values
		// TODO save to DB
	}

	public void cancel()
	{
		stage.close();
	}
}