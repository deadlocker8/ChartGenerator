package de.lww4.ui.controller;

import java.util.ArrayList;

import de.lww4.logic.models.Scale.Scale;
import de.lww4.logic.models.Scale.ScaleItem;
import de.lww4.ui.cells.EditScaleCell;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Callback;

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
	
	public void save()
	{
		scale.setScaleItems(scaleItems);
		//TODO save to DB
	}

	public void cancel()
	{
		stage.close();
	}
}