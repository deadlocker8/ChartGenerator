package de.lww4.ui.cells;

import de.lww4.logic.models.scale.ScaleItem;
import de.lww4.ui.controller.EditScaleController;
import fontAwesome.FontIcon;
import fontAwesome.FontIconType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;

/**
 * custom cell for ListView<ScaleItem> in EditScaleController
 * @author Robert
 *
 */
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
			hBox.setAlignment(Pos.CENTER);
			
			/* Double.MAX_VALUE is a placeholder to mark a scaleItem
			 * this marked scaleItem will be replaced with two labels describing what to enter into the columns 
			*/
			if(item.getKey() != null && item.getKey() == Double.MAX_VALUE)
			{
				Label labelKey = new Label("Wert");
				labelKey.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
				labelKey.getStyleClass().add("greylabel");
				hBox.getChildren().add(labelKey);
				Label labelValue = new Label("Bezeichnung");				
				labelValue.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
				labelValue.getStyleClass().add("greylabel");
				hBox.getChildren().add(labelValue);
				HBox.setMargin(labelValue, new Insets(0, 0, 0, 100));
				
				setGraphic(hBox);
				
				return;
			}
			
			/*Double.MIN_VALUE is a placeholder for an empty double value
			 * this value will not be shown in the textField but replaced as empty String
			*/
			String key;
			if(item.getKey() == null || item.getKey() == Double.MIN_VALUE)
			{
				key = "";			
			}		
			else
			{
				key = String.valueOf(item.getKey().intValue());
			}	
			
			//TextFormatter only allows positive or negative integers
			TextField keyTextField = new TextField(key);
			keyTextField.setTextFormatter(new TextFormatter<>(c -> {
				if(c.getControlNewText().isEmpty())
				{
					item.setKey(Double.MIN_VALUE);
					return c;
				}

				if(c.getControlNewText().matches("-?[0-9]*"))
				{
					item.setKey(Double.parseDouble(c.getControlNewText()));
					return c;
				}
				else
				{
					item.setKey(Double.MIN_VALUE);
					return null;
				}
			}));		
			hBox.getChildren().add(keyTextField);

			TextField valueTextField = new TextField(item.getValue());
			valueTextField.textProperty().addListener(new ChangeListener<String>()
			{
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
				{
					if(!newValue.trim().equals(""))
					{
						item.setValue(newValue.trim());
					}
					else
					{
						item.setValue(null);
						valueTextField.setText("");
					}
				}
			});
			hBox.getChildren().add(valueTextField);
			HBox.setMargin(valueTextField, new Insets(0, 0, 0, 5));

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