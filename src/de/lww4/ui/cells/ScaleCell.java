package de.lww4.ui.cells;


import de.lww4.logic.models.Scale.Scale;
import de.lww4.ui.controller.Controller;
import de.lww4.ui.controller.SelectScaleController;
import fontAwesome.FontIcon;
import fontAwesome.FontIconType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Optional;

public class ScaleCell extends ListCell<Scale>
{
    private Controller controller;
    private SelectScaleController selectScaleController;

    public ScaleCell(Controller controller, SelectScaleController selectScaleController)
    {
        this.controller = controller;
        this.selectScaleController = selectScaleController;
    }

    @Override
    protected void updateItem(Scale item, boolean empty)
    {
        super.updateItem(item, empty);
        if (!empty)
        {
            HBox hbox = new HBox();
            Label nameLabel = new Label(item.getName());
            hbox.getChildren().addAll(nameLabel);

            FontIcon iconEdit = new FontIcon(FontIconType.PENCIL);
            iconEdit.setSize(14);
            Button buttonEdit = new Button("");
            buttonEdit.setGraphic(iconEdit);
            iconEdit.setStyle("-fx-background-color: transparent;");
            buttonEdit.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    //open new controller
                }
            });

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
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Scale löschen");
                    alert.setHeaderText("");
                    alert.setContentText("Möchten Sie diese Skala wirklich unwiderruflich löschen?");
                    Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
                    dialogStage.getIcons().add(controller.getIcon());
                    dialogStage.centerOnScreen();

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK)
                    {
                        controller.deleteScale(item.getID());
                        selectScaleController.refreshListView();
                    }
                }
            });

            hbox.getChildren().addAll(buttonEdit, buttonDelete);
            HBox.setMargin(nameLabel, new Insets(0, 0, 0, 5));

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
