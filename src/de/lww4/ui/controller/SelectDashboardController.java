package de.lww4.ui.controller;

import de.lww4.logic.handler.DashboardHandler;
import de.lww4.logic.models.Dashboard;
import de.lww4.logic.utils.AlertGenerator;
import de.lww4.ui.cells.DashboardCell;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import logger.LogLevel;
import logger.Logger;

/**
 * controller class for selecting a dahboard
 * @author Robert
 *
 */
public class SelectDashboardController
{
	@FXML private ListView<Dashboard> listView;
	@FXML private Button buttonCancel;

	private Stage stage;
	private Controller controller;	

	/**
	 * init method
	 * @param stage
	 * @param controller
	 */
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

        Label labelPlaceholder = new Label("Keine weiteren Dashboards verfügbar.");
        labelPlaceholder.setStyle("-fx-font-size: 14");
		listView.setPlaceholder(labelPlaceholder);

        refreshListView();
    }
	
	public void refreshListView()
	{
		try
		{
			controller.setDashboardHandler(new DashboardHandler(controller.getDatabase().getAllDashboards()));
			listView.getItems().clear();

            //adds all dashboard to list, except current open dashboard
            for (Dashboard dashboard : controller.getDashboardHandler().getDashboards())
            {
                if (dashboard.getID() != controller.getCurrentDashboard().getID())
                {
                    listView.getItems().add(dashboard);
                }
            }
        }
		catch(Exception e)
		{			
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
            AlertGenerator.showAlert(AlertType.ERROR, "Fehler", "", controller.getBundle().getString("error.load.data"), controller.getIcon(), stage, null, false);
        }
    }

	public void cancel()
	{
		stage.close();
	}
}