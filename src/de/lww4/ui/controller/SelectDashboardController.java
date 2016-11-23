package de.lww4.ui.controller;

import java.util.Locale;
import java.util.ResourceBundle;

import de.lww4.logic.Dashboard;
import de.lww4.logic.DashboardHandler;
import de.lww4.ui.cells.DashboardCell;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import logger.LogLevel;
import logger.Logger;

public class SelectDashboardController
{
	@FXML private ListView<Dashboard> listView;
	@FXML private Button buttonCancel;

	public Stage stage;
	private Controller controller;
	public Image icon = new Image("de/lww4/resources/icon.png");
	public final ResourceBundle bundle = ResourceBundle.getBundle("de/lww4/main/", Locale.GERMANY);	

	public void init(Stage stage, Controller controller)
	{
		this.stage = stage;		
		this.controller = controller;
		
		final SelectDashboardController selectDashboardController = this;		
		listView.setCellFactory(new Callback<ListView<Dashboard>, ListCell<Dashboard>>()
		{			
			@Override
			public ListCell<Dashboard> call(ListView<Dashboard> param)
			{				
				return new DashboardCell(controller, selectDashboardController);
			}
		});
		
		listView.setOnMouseClicked(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event)
			{
				if(event.getButton().equals(MouseButton.PRIMARY))
				{
					Dashboard selected = listView.getSelectionModel().getSelectedItem();
					if(selected != null)
					{						
						controller.setDashboard(selected);
						stage.close();
					}
				}				
			}
		});
		
		Label labelPlaceholder = new Label("Keine Dashboards verfügbar.");
		labelPlaceholder.setStyle("-fx-font-size: 14");
		listView.setPlaceholder(labelPlaceholder);
		
		refreshListView();			
	}	
	
	public void refreshListView()
	{
		try
		{
			controller.dashboardHandler = new DashboardHandler(controller.database.getAllDashboards());
			listView.getItems().clear();
			listView.getItems().addAll(controller.dashboardHandler.getDashboards());
		}
		catch(Exception e)
		{			
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Fehler");
			alert.setHeaderText("");
			alert.setContentText("Beim Abrufen der Daten aus der Datenbank ist ein Fehler aufgetreten.");
			Stage dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
			dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
			dialogStage.getIcons().add(icon);
			dialogStage.centerOnScreen();
			alert.showAndWait();
		}		
	}

	public void cancel()
	{
		stage.close();
	}
}