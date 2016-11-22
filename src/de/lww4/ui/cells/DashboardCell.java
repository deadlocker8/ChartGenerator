package de.lww4.ui.cells;

import java.util.Optional;

import de.lww4.logic.Dashboard;
import de.lww4.ui.controller.Controller;
import de.lww4.ui.controller.SelectDashboardController;
import fontAwesome.FontIcon;
import fontAwesome.FontIconType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class DashboardCell extends ListCell<Dashboard>
{	
	private Controller controller;
	private SelectDashboardController selectDashboardController;
	
	public DashboardCell(Controller controller, SelectDashboardController selectDashboardController)
	{
		super();
		this.controller = controller;
		this.selectDashboardController = selectDashboardController;
	}
	
	@Override
	protected void updateItem(Dashboard item, boolean empty)
	{
		super.updateItem(item, empty);

		if( ! empty)
		{
			HBox hbox = new HBox();
			
			Label labelLevelName = new Label(item.getName());
			labelLevelName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");		
			labelLevelName.getStyleClass().add("greylabel");
			hbox.getChildren().add(labelLevelName);						

			Region r = new Region();
			hbox.getChildren().add(r);
			HBox.setHgrow(r, Priority.ALWAYS);
			
			FontIcon iconDelete = new FontIcon(FontIconType.TRASH);
			iconDelete.setSize(14);
			Button buttonDelete = new Button("");
			buttonDelete.setGraphic(iconDelete);
			buttonDelete.setStyle("-fx-background-color: transparent;");
			buttonDelete.setOnAction(new EventHandler<ActionEvent>()
			{				
				@Override
				public void handle(ActionEvent event)
				{					
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Dashboard löschen");
					alert.setHeaderText("");
					alert.setContentText("Möchten Sie dieses Dashboard wirklich unwiderruflich löschen?");
					Stage dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
					dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
					dialogStage.getIcons().add(controller.icon);
					dialogStage.centerOnScreen();

					Optional<ButtonType> result = alert.showAndWait();
					if(result.get() == ButtonType.OK)
					{
						controller.deleteDashboard(item.getID());
						selectDashboardController.refreshListView();						
					}					
				}
			});
			
			hbox.getChildren().add(buttonDelete);
			HBox.setMargin(labelLevelName, new Insets(0, 0, 0, 5));	

			hbox.setAlignment(Pos.CENTER);
			hbox.setStyle("-fx-border-color: #212121; -fx-border-width: 1px; -fx-background-color: #eeeeee;");
			hbox.setPadding(new Insets(5));

			setGraphic(hbox);			
		}
		else
		{
			setGraphic(null);
		}

		setStyle("-fx-background-color: transparent");
	}
}
