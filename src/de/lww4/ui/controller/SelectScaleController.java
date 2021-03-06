package de.lww4.ui.controller;

import java.io.IOException;
import java.util.Optional;

import de.lww4.logic.handler.ScaleHandler;
import de.lww4.logic.models.scale.Scale;
import de.lww4.logic.utils.AlertGenerator;
import de.lww4.ui.cells.ScaleCell;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import logger.LogLevel;
import logger.Logger;

/**
 * controller class for selecting a scale
 * @author Robert
 *
 */
public class SelectScaleController
{
	@FXML private ListView<Scale> listView;
	@FXML private Button buttonCancel;

	private Stage stage;
	private Controller controller;
	private SelectScaleController context;

	/**
	 * init method
	 * @param controller
	 * @param stage
	 */
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
		refreshListView();
	}

	public void refreshListView()
	{
		try
		{
			listView.getItems().clear();
			for(Scale scale : controller.getScaleHandler().getScales())
			{
				listView.getItems().add(scale);
			}
		}
		catch(Exception e)
		{
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
			AlertGenerator.showAlert(Alert.AlertType.ERROR, "Fehler", "", controller.getBundle().getString("error.load.data"), controller.getIcon(), stage, null, false);
		}
	}

	@FXML
	private void onAddScaleButtonClicked()
	{
		TextInputDialog dialog = new TextInputDialog("Neuer Skalenname");
		dialog.setTitle("Neue Skala");
		dialog.setHeaderText("");
		dialog.setContentText("Skalenname:");
		Stage dialogStage = (Stage)dialog.getDialogPane().getScene().getWindow();
		dialogStage.getIcons().add(controller.getIcon());
		dialogStage.centerOnScreen();

		checkTextInputTitle(dialog);
	}

	/**
	 * checks wether the title that the user entered for a new scale is empty
	 * @param dialog
	 */
	private void checkTextInputTitle(TextInputDialog dialog)
	{
		Optional<String> result = dialog.showAndWait();
		if(result.isPresent())
		{
			String name = result.get();
			name = name.trim();
			if(name.equals(""))
			{
				AlertGenerator.showAlert(AlertType.WARNING, "Warnung", "", controller.getBundle().getString("warning.name.empty.scale"), controller.getIcon(), stage, null, false);
				checkTextInputTitle(dialog);
			}
			else
			{
				Scale scale = new Scale(-1, name.trim(), null);
				try
				{
					controller.getDatabase().saveScale(scale);					
					controller.setScaleHandler(new ScaleHandler(controller.getDatabase().getAllScales()));
					refreshListView();
				}
				catch(Exception e)
				{
					Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
					AlertGenerator.showAlert(AlertType.ERROR, "Fehler", "", controller.getBundle().getString("error.save"), controller.getIcon(), stage, null, false);
				}			
			}
		}
	}

	public void openEditStage(Scale scale)
	{
		try
		{
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/de/lww4/ui/fxml/EditScaleGUI.fxml"));
			Parent root = (Parent)fxmlLoader.load();
			Stage newStage = new Stage();
			newStage.initOwner(stage);
			newStage.initModality(Modality.APPLICATION_MODAL);
			newStage.setTitle("Skala bearbeiten");		
			newStage.setScene(new Scene(root));
			newStage.getIcons().add(controller.getIcon());
			newStage.setResizable(false);
			newStage.getScene().getStylesheets().add("de/lww4/main/style.css");
			EditScaleController editScaleController = fxmlLoader.getController();
			editScaleController.init(newStage, controller, this, scale);
			newStage.show();

		}
		catch(IOException io)
		{
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(io));
		}
	}

	public void cancel()
	{
		controller.setDashboard(controller.getCurrentDashboard());
		stage.close();
	}
}