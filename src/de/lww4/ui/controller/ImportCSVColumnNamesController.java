package de.lww4.ui.controller;

import de.lww4.logic.Importer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

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
            TableColumn column = new TableColumn();
            column.setGraphic(textField);
            tableView.getColumns().add(column);
        }

        //generate more columns
        while (tableView.getColumns().size() < importer.getLongestRowSize())
        {
            TextField textField = new TextField(DEFAULT_EMPTY_COLUMN_NAME);
            TableColumn column = new TableColumn();
            column.setGraphic(textField);
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
        ObservableList<String> row = FXCollections.observableArrayList();
        for(ArrayList<String> rowData : importer.getData())
        {
            row.addAll(rowData);
        }
        tableView.setItems(row);
        tableView.refresh();
    }

	public void cancel()
	{
		stage.close();
	}
}