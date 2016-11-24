package de.lww4.ui.controller;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import de.lww4.logic.Importer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Callback;
import logger.LogLevel;
import logger.Logger;

public class ImportCSVColumnNamesController
{
	@FXML private TableView<ObservableList<StringProperty>> tableView;
	@FXML private Button buttonCancel;
	@FXML private Button buttonSave;

	public Stage stage;
	private Controller mainController;
	public Image icon = new Image("de/lww4/resources/icon.png");
	public final ResourceBundle bundle = ResourceBundle.getBundle("de/lww4/main/", Locale.GERMANY);
	private Importer importer;
	private final String DEFAULT_EMPTY_COLUMN_NAME = "LEER";
	
	public void init(Stage stage, ImportCSVController importCSVController, Controller mainController, Importer importer)
	{	   
		this.stage = stage;
		this.mainController = mainController;
		this.importer = importer;
		populateTableViewHead();
		populateTableViewBody();
	}

	private TableColumn<ObservableList<StringProperty>, String> generateColumn(String name, int position)
	{
		TextField textField = new TextField(name);
		TableColumn<ObservableList<StringProperty>, String> column = new TableColumn<>();
		column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<StringProperty>, String>, ObservableValue<String>>()
		{
			@Override
			public ObservableValue<String> call(CellDataFeatures<ObservableList<StringProperty>, String> cellDataFeatures)
			{
				return cellDataFeatures.getValue().get(position);
			}
		});

		column.setGraphic(textField);
		column.setSortable(false);

		return column;
	}

	private void populateTableViewHead()
	{
		for(int i = 0; i < importer.getLongestRowSize(); i++)
		{
			if(i < importer.getColumnNames().size() - 1)
			{
				tableView.getColumns().add(generateColumn(importer.getColumnNames().get(i), i));
			}
			else
			{
				tableView.getColumns().add(generateColumn(DEFAULT_EMPTY_COLUMN_NAME, i));
			}			
		}
	}

	private void populateTableViewBody()
	{
		tableView.widthProperty().addListener(new ChangeListener<Number>()
		{
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
			{
				double width = newValue.doubleValue();
				int numberOfColumns = tableView.getColumns().size();
				double itemWidth = width / numberOfColumns - 3;

				for(int i = 0; i < numberOfColumns; i++)
				{
					tableView.getColumns().get(i).setPrefWidth(itemWidth);
				}
			}
		});

		for(ArrayList<String> currentRow : importer.getData())
		{
			ObservableList<StringProperty> data = FXCollections.observableArrayList();
			for(String value : currentRow)
			{
				data.add(new SimpleStringProperty(value));
			}
			tableView.getItems().add(data);
		}
	}

    @FXML
    private void save()
    {
        ArrayList<String> newColumnNamesArrayList = new ArrayList<String>();
        for(TableColumn<ObservableList<StringProperty>, ?> tableColumn : tableView.getColumns())
        {
            newColumnNamesArrayList.add(((TextField)tableColumn.getGraphic()).getText());
        }

        importer.setColumnNamesArrayList(newColumnNamesArrayList);
        try
        {
            mainController.getDatabase().saveCSVTable(importer);
        }
        catch (Exception e)
        {
            Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
        }

        stage.close();
    }

	@FXML
	private void cancel()
	{
		stage.close();
	}
}