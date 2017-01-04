package de.lww4.ui.controller;

import java.util.ArrayList;

import de.lww4.logic.ScaleHandler;
import de.lww4.logic.models.Scale.Scale;
import de.lww4.logic.models.Scale.ScaleItem;
import de.lww4.logic.utils.AlertGenerator;
import de.lww4.ui.cells.EditScaleCell;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Callback;
import logger.LogLevel;
import logger.Logger;

public class EditScaleController
{
	@FXML private ListView<ScaleItem> listView;

	private Stage stage;
	private Controller controller;
	private SelectScaleController selectScaleController;
	private Scale scale;
	private ArrayList<ScaleItem> scaleItems;

	public void init(Stage stage, Controller controller, SelectScaleController selectScaleController, Scale scale)
	{		
		this.stage = stage;
		this.controller = controller;
		this.scale = scale;
		this.selectScaleController = selectScaleController;

		EditScaleController context = this;

		Label labelPlaceholder = new Label("Keine Werte verfügbar.");
		labelPlaceholder.setStyle("-fx-font-size: 14");
		listView.setPlaceholder(labelPlaceholder);
		
		listView.setCellFactory(new Callback<ListView<ScaleItem>, ListCell<ScaleItem>>()
		{
			@Override
			public ListCell<ScaleItem> call(ListView<ScaleItem> param)
			{				
				return new EditScaleCell(context);
			}
		});				
		
		scaleItems = new ArrayList<>();	
		//placeholder --> will be replaced with headings for the two columns in EditScaleCell
		scaleItems.add(new ScaleItem(Double.MAX_VALUE, ""));
		
		for(Double key : scale.getScaleHashMap().keySet())
		{				
			scaleItems.add(new ScaleItem(key, scale.getScaleHashMap().get(key)));			
		}			
		
		refreshListView();
	}

	public void refreshListView()
	{
		listView.getItems().clear();
		System.out.println(scaleItems);
		listView.getItems().addAll(scaleItems);
	}
	
	public void addRow()
	{		
		scaleItems.add(new ScaleItem(null,  null));
		refreshListView();
	}
	
	public void deleteRow(ScaleItem scaleItem)
	{
		scaleItems.remove(scaleItem);		
		refreshListView();
	}
	
	public ArrayList<ScaleItem> addRowsDataToScale()
	{	
		ArrayList<ScaleItem> newItems = new ArrayList<>();
				
		for(ScaleItem currentItem : listView.getItems())
		{				
			if(currentItem.getKey() != null && currentItem.getKey() != Double.MIN_VALUE && currentItem.getValue() != null && !currentItem.getValue().equals(""))
			{
				newItems.add(new ScaleItem(currentItem.getKey(), currentItem.getValue()));				
			}
			else
			{
				AlertGenerator.showAlert(AlertType.WARNING, "Warnung", "", controller.getBundle().getString("warning.empty.scaleitems"), controller.getIcon(), stage, null, false);
				return null;
			}
		}
		
		return newItems;
	}
	
	public void save()
	{
		ArrayList<ScaleItem> newItems = addRowsDataToScale();
		
		if(newItems != null)
		{
			scale.setScaleItems(newItems);
			try
			{
				controller.getDatabase().updateScale(scale);						
				controller.setScaleHandler(new ScaleHandler(controller.getDatabase().getAllScales()));
				selectScaleController.refreshListView();				
				stage.close();
			}
			catch(Exception e)
			{
				Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
				AlertGenerator.showAlert(AlertType.ERROR, "Fehler", "", controller.getBundle().getString("error.save"), controller.getIcon(), stage, null, false);
			}	
		}
	}

	public void cancel()
	{	
		stage.close();
	}
}