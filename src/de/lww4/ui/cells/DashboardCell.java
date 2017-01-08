package de.lww4.ui.cells;

import de.lww4.logic.models.Dashboard;
import de.lww4.ui.controller.Controller;
import de.lww4.ui.controller.SelectDashboardController;
import fontAwesome.FontIcon;
import fontAwesome.FontIconType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * custom cell for ListView<Dashboard>
 * @author Robert
 *
 */
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
			
			Label labelname = new Label(item.getName());
			labelname.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");		
			labelname.getStyleClass().add("greylabel");
			hbox.getChildren().add(labelname);						

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
                    Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
                    dialogStage.getIcons().add(controller.getIcon());
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
			HBox.setMargin(labelname, new Insets(0, 0, 0, 5));	

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
