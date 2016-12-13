package de.lww4.ui.cells;

import de.lww4.logic.models.Scale.ScaleItem;
import de.lww4.ui.controller.Controller;
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
    private Controller controller;
    private EditScaleController editScaleController;

    public EditScaleCell(Controller controller, EditScaleController editScaleController)
    {
        this.controller = controller;
        this.editScaleController = editScaleController;
    }

    @Override
    protected void updateItem(ScaleItem item, boolean empty)
    {
        HBox hBox = new HBox();
        TextField keyTextField = new TextField(String.valueOf(item.getKey()));
        TextField valueTextField = new TextField(item.getValue());

        super.updateItem(item, empty);
        if (!empty)
        {
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
                    editScaleController.refreshListView();
                }
            });
        }
        else
        {
            setGraphic(null);
        }

        setStyle("-fx-background-color: transparent");
    }
}
