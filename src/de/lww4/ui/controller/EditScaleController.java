package de.lww4.ui.controller;


import de.lww4.logic.models.Scale.Scale;
import de.lww4.logic.models.Scale.ScaleItem;
import de.lww4.logic.utils.AlertGenerator;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import logger.LogLevel;
import logger.Logger;

public class EditScaleController
{
    @FXML
    private ListView<ScaleItem> listView;
    private Controller controller;
    private SelectScaleController selectScaleController;
    private Scale scale;

    public void init(Controller controller, SelectScaleController selectScaleController, Scale scale)
    {
        this.controller = controller;
        this.scale = scale;
        this.selectScaleController = selectScaleController;
    }

    public void refreshListView()
    {
        try
        {
            listView.getItems().clear();
            for (Double key : scale.getScaleHashMap().keySet())
            {
                ScaleItem scaleItem = new ScaleItem(key, scale.getScaleHashMap().get(key));
                listView.getItems().add(scaleItem);
            }
        }
        catch (Exception e)
        {
            Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
            AlertGenerator.showAlert(Alert.AlertType.ERROR, "Fehler", "", controller.getBundle().getString("error.load.data"), controller.getIcon(), true);
        }
    }

}

