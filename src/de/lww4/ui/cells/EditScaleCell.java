package de.lww4.ui.cells;

import de.lww4.logic.models.Scale.ScaleItem;
import de.lww4.ui.controller.EditScaleController;
import fontAwesome.FontIcon;
import fontAwesome.FontIconType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class EditScaleCell extends ListCell<ScaleItem>
{
	private EditScaleController editScaleController;

	public EditScaleCell(EditScaleController editScaleController)
	{
		this.editScaleController = editScaleController;
	}

	@Override
	protected void updateItem(ScaleItem item, boolean empty)
	{
		super.updateItem(item, empty);
		if(!empty)
		{
			HBox hBox = new HBox();
			
			String key;
			if(item.getKey() == null)
			{
				key = "";				
			}
			else
			{
				key = String.valueOf(item.getKey());
			}		
			
			TextField keyTextField = new TextField(key);
			hBox.getChildren().add(keyTextField);
			TextField valueTextField = new TextField(item.getValue());
			hBox.getChildren().add(valueTextField);

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
					editScaleController.deleteRow(item);					
				}
			});
			
			hBox.getChildren().add(buttonDelete);
			
			setGraphic(hBox);
		}
		else
		{
			setGraphic(null);
		}

		setStyle("-fx-background-color: transparent");
	}
}
