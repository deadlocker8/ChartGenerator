package de.lww4.ui.controller;

import de.lww4.logic.models.Scale;
import de.lww4.logic.utils.AlertGenerator;
import de.lww4.ui.cells.ScaleCell;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import logger.LogLevel;
import logger.Logger;

public class SelectScaleController
{
    @FXML
    private ListView<Scale> listView;
    @FXML
    private Button buttonCancel;

    private Stage stage;
    private Controller controller;
    private SelectScaleController context;

    public void init(Controller controller, Stage stage)
    {
        this.stage = stage;
        this.controller = controller;
        context = this;


        Label labelPlaceholder = new Label("Keine Skalen verfügbar.");
        labelPlaceholder.setStyle("-fx-font-size: 14");
        listView.setPlaceholder(labelPlaceholder);

        listView.setCellFactory(new Callback<ListView<Scale>, ListCell<Scale>>()
        {
            @Override
            public ListCell<Scale> call(ListView<Scale> param)
            {
                return new ScaleCell(controller, context);
            }
        });
    }

    public void refreshListView()
    {
        try
        {
            listView.getItems().clear();

            for (Scale scale : controller.getScaleHandler().getScales())
            {
                listView.getItems().add(scale);
            }
        }
        catch (Exception e)
        {
            Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
            AlertGenerator.showAlert(Alert.AlertType.ERROR, "Fehler", "", controller.getBundle().getString("error.load.data"), controller.getIcon(), true);
        }
    }

    @FXML
    private void onAddScaleButtonClicked()
    {

    }


    public void cancel()
    {
        stage.close();
    }
}