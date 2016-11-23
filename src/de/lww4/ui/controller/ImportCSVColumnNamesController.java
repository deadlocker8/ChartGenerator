package de.lww4.ui.controller;

import de.lww4.logic.Importer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class ImportCSVColumnNamesController
{
	@FXML private TableView<String> tableView;
	@FXML private Button buttonCancel;

	public Stage stage;
	private Controller controller;
	public Image icon = new Image("de/lww4/resources/icon.png");
	public final ResourceBundle bundle = ResourceBundle.getBundle("de/lww4/main/", Locale.GERMANY);	
    private Importer importer;
    private final String DEFAULT_EMPTY_COLUMN_NAME = "LEER";

	public void init(Stage stage, Controller controller, Importer importer)
	{
		this.stage = stage;		
		this.controller = controller;
		this.importer = importer;
		populateTableViewHead();
		populateTableViewBody();
	}	

	private void populateTableViewHead()
    {
        for(String columnName : importer.getColumnNames())
        {
            TextField textField = new TextField(columnName);
            TableColumn<String, String> column = new TableColumn<String, String>();
            column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<String,String>, ObservableValue<String>>()
			{				
				@Override
				public ObservableValue<String> call(CellDataFeatures<String, String> param)
				{					
					return new SimpleStringProperty(param.getValue());
				}
			});            		
            		
            column.setGraphic(textField);
            column.setSortable(false);
            tableView.getColumns().add(column);
        }

        //generate more columns
        while (tableView.getColumns().size() < importer.getLongestRowSize())
        {
            TextField textField = new TextField(DEFAULT_EMPTY_COLUMN_NAME);
            TableColumn<String, String> column = new TableColumn<String, String>();
            column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<String,String>, ObservableValue<String>>()
			{
				@Override
				public ObservableValue<String> call(CellDataFeatures<String, String> param)
				{					
					return new SimpleStringProperty(param.getValue());
				}
			});      
            column.setGraphic(textField);
            column.setSortable(false);
            tableView.getColumns().add(column);
        }

        buttonCancel.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                ArrayList<String> newColumnNamesArrayList = new ArrayList<String>();
                for(TableColumn tableColumn : tableView.getColumns())
                {
                    newColumnNamesArrayList.add(((TextField) tableColumn.getGraphic()).getText());
                }

                importer.setColumnNamesArrayList(newColumnNamesArrayList);
            }
        });
    }

    private void populateTableViewBody()
    {
        tableView.widthProperty().addListener(new ChangeListener<Number>()
		{
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
			{
				double width = newValue.doubleValue();
				//columnFileName.setPrefWidth(width * 0.83 - 3);
				//columnFileLength.setPrefWidth(width * 0.17 - 3);
			}
		});
    }

	public void cancel()
	{
		stage.close();
	}
}