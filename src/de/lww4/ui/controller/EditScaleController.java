package de.lww4.ui.controller;

import java.util.ArrayList;

import de.lww4.logic.handler.ScaleHandler;
import de.lww4.logic.models.scale.Scale;
import de.lww4.logic.models.scale.ScaleItem;
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

/**
 * Controller for editing one scale
 * @author Robert
 *
 */
public class EditScaleController
{
	@FXML private ListView<ScaleItem> listView;

	private Stage stage;
	private Controller controller;
	private SelectScaleController selectScaleController;
	private Scale scale;
	private ArrayList<ScaleItem> scaleItems;

	/**
	 * init method
	 * @param stage
	 * @param controller
	 * @param selectScaleController
	 * @param scale
	 */
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
		listView.getItems().addAll(scaleItems);
	}
	
	/**
	 * adds one new row and refreshes listView
	 */
	public void addRow()
	{		
		scaleItems.add(new ScaleItem(null,  null));
		refreshListView();
	}
	
	/**
	 * deletes given ScaleItem from list and refreshes listView
	 * @param scaleItem
	 */	
	public void deleteRow(ScaleItem scaleItem)
	{
		scaleItems.remove(scaleItem);		
		refreshListView();
	}
	
	/**
	 * checks all listView cells
	 * (shows alert if one or more of them are empty or partially empty) 
	 * (adds them to new ArrayList if they are correct)
	 * 
	 * @return ArrayList<ScaleItem>
	 */
	public ArrayList<ScaleItem> addRowsDataToScale()
	{	
		ArrayList<ScaleItem> newItems = new ArrayList<>();
				
		for(ScaleItem currentItem : listView.getItems())
		{				
			if(currentItem.getKey() != Double.MAX_VALUE)
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
		}
		
		return newItems;
	}
	
	/**
	 * this method is called when the user clicks the save button
	 * (saves items to scale and saves scale into database)
	 */
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