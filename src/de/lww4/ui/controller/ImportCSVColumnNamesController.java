package de.lww4.ui.controller;


import de.lww4.logic.Importer;
import de.lww4.logic.models.enums.ForbiddenColumnNames;
import de.lww4.logic.utils.AlertGenerator;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import logger.LogLevel;
import logger.Logger;
import tools.Worker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;


/**
 * ImportCSVColumnNamesController
 *
 * @author max
 */
public class ImportCSVColumnNamesController
{
    @FXML private TableView<ObservableList<StringProperty>> tableView;
    @FXML private Button buttonCancel;
    @FXML private Button buttonSave;
    @FXML private ProgressIndicator progressIndicator;

    public Stage stage;
    private Controller mainController;
    public Image icon = new Image("de/lww4/resources/icon.png");
    public final ResourceBundle bundle = ResourceBundle.getBundle("de/lww4/main/", Locale.GERMANY);
    private Importer importer;
    private final String DEFAULT_EMPTY_COLUMN_NAME = "LEER";
    private ImportCSVController importCSVController;
    private boolean isUserError = false;
    private ArrayList<Integer> disabledColumnNumbers;

    /**
     called after object created
     */
    public void init(Stage stage, ImportCSVController importCSVController, Controller mainController, Importer importer)
    {
        disabledColumnNumbers = new ArrayList<>();
        this.importCSVController = importCSVController;
        this.stage = stage;
        this.mainController = mainController;
        this.importer = importer;
        populateTableViewHead();
        populateTableViewBody();
        progressIndicator.setVisible(false);
    }

    /**
     * helper method to generate a column
     * @param name
     * @param position
     * @return tableColumn
     */
	private TableColumn<ObservableList<StringProperty>, String> generateColumn(String name, int position)
	{
        CheckBox checkBox = new CheckBox();
        if(!ForbiddenColumnNames.isForbidden(name))
            checkBox.setSelected(true);
        TextField textField = new TextField(name);
        VBox vBox = new VBox(checkBox, textField);
        TableColumn<ObservableList<StringProperty>, String> column = new TableColumn<>();
		column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<StringProperty>, String>, ObservableValue<String>>()
		{
			@Override
			public ObservableValue<String> call(CellDataFeatures<ObservableList<StringProperty>, String> cellDataFeatures)
			{
				return cellDataFeatures.getValue().get(position);
			}
		});

        column.setGraphic(vBox);
        column.setSortable(false);

        return column;
    }

    /**
     * fills the UI with the table head (column name, checkboxes...)
     */
    private void populateTableViewHead()
    {
        for(int i = 0; i < importer.getLongestRowSize(); i++)
        {
            if (i < importer.getColumnNames().size())
            {
                tableView.getColumns().add(generateColumn(importer.getColumnNames().get(i), i));
            }
            else
            {
                tableView.getColumns().add(generateColumn(DEFAULT_EMPTY_COLUMN_NAME, i));
            }
        }
    }

    /**
     * fills UI with the imported values from the Importer
     */
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
                    tableView.getColumns().get(i).setMinWidth(100);
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

    /**
     *
     * @param newColumnNamesArrayList columnNames
     * @return the name of the words, which occur more than once in the table
     */
    private ArrayList<String> getDuplicateColumns(ArrayList<String> newColumnNamesArrayList)
    {
        //string, occurance
        HashMap<String, Integer> hashMap = new HashMap<>();
        for (int i = 0; i < newColumnNamesArrayList.size(); i++)
        {
            String columnName = newColumnNamesArrayList.get(i);
            hashMap.putIfAbsent(columnName, 0);
            hashMap.put(columnName, hashMap.get(columnName) + 1);
        }
        ArrayList<String> duplicateIndexArrayList = new ArrayList<>();

        for (String columnName : hashMap.keySet())
        {
            if (hashMap.get(columnName) > 1)
                duplicateIndexArrayList.add(columnName);
        }

        return duplicateIndexArrayList;
    }

    /**
     *
     * @param newColumnNamesArrayList columnNames
     * @return the index of the empty columns
     */
    private ArrayList<Integer> getEmptyColumnNames(ArrayList<String> newColumnNamesArrayList)
    {
        ArrayList<Integer> emptyColumns = new ArrayList<>();
        for(int i=0; i < newColumnNamesArrayList.size(); i++)
        {
            String columnName = newColumnNamesArrayList.get(i);
            if (columnName.length() == 0)
            {
                emptyColumns.add(i + 1);
            }
        }
        return emptyColumns;
    }

    /**
     *
     * @param newColumnNamesArrayList columnNames
     * @return the forbidden words from ForbiddenColumnNames Enum
     */
    private ArrayList<String> getForbiddenKeyWordsColumnNames(ArrayList<String> newColumnNamesArrayList)
    {
        ArrayList<String> forbiddenColumns = new ArrayList<>();
        for (String nameToCheck : newColumnNamesArrayList)
        {
            if (ForbiddenColumnNames.isForbidden(nameToCheck))
            {
                forbiddenColumns.add(nameToCheck);
            }
        }
        return forbiddenColumns;
    }

    /**
     * checks if a user error occurs
     * @param newColumnNamesArrayList the new column names, that the user changed in the UI
     * @return true, if an error occurred
     */
    private boolean isUserError(ArrayList<String> newColumnNamesArrayList)
    {
        ArrayList<String> forbiddenColumnNames = getForbiddenKeyWordsColumnNames(newColumnNamesArrayList);
        boolean hasForbiddenColumns = !forbiddenColumnNames.isEmpty();

        ArrayList<String> duplicateColumns = getDuplicateColumns(newColumnNamesArrayList);
        boolean hasDuplicateColumns = !duplicateColumns.isEmpty();

        ArrayList<Integer> emptyColumns = getEmptyColumnNames(newColumnNamesArrayList);
        boolean hasEmptyColumns = !emptyColumns.isEmpty();

        String errorMessage = null;
        if(newColumnNamesArrayList.isEmpty())
        {
            errorMessage = "Es muss mindestens eine Spalte zum importieren gewählt werden!";
        }
        else
        {
            if (hasDuplicateColumns && hasEmptyColumns)
            {
                errorMessage = "Einige Spalten sind leer und/oder doppelt!";
            }
            else
            {
                if (hasDuplicateColumns)
                {
                    errorMessage = "Folgende Spaltennamen kommen doppelt vor: " + getStringFromArrayList(duplicateColumns.toString());
                }

                if (hasEmptyColumns)
                {
                    errorMessage = "Folgende Spalten sind leer: " + getStringFromArrayList(emptyColumns.toString());
                }

                if (hasForbiddenColumns)
                {
                    errorMessage = "Folgende verwendete Spaltennamen sind nicht erlaubt: " + getStringFromArrayList(forbiddenColumnNames.toString());
                }

            }
        }
        if (errorMessage != null)
        {
            AlertGenerator.showAlert(Alert.AlertType.ERROR, "Fehler", "", errorMessage, mainController.getIcon(), stage, null, false);
            return true;
        }
        return false;
    }

    /**
     * helper method
     * @param arrayString Array.toString()
     * @return ArrayElement without [, ]
     */
    private String getStringFromArrayList(String arrayString)
    {
        return arrayString.replace("[", "").replace("]", "");
    }

    private ArrayList<String> getColumnNamesArrayList()
    {
        ArrayList<String> newColumnNamesArrayList = new ArrayList<String>();
        for(int i=0; i < tableView.getColumns().size(); i++)
        {
            TableColumn<ObservableList<StringProperty>, ?> tableColumn = tableView.getColumns().get(i);
            VBox vBox = (VBox) tableColumn.getGraphic();
            CheckBox checkBox = (CheckBox) vBox.getChildren().get(0);
            TextField textField = (TextField) vBox.getChildren().get(1);
            if(checkBox.isSelected())
            {
                String newColumnName = textField.getText().trim();
                newColumnNamesArrayList.add(newColumnName);
            }
            else
            {
                disabledColumnNumbers.add(i);
            }
        }

        return newColumnNamesArrayList;
    }

    /**
     * saves changed column names into Importer and database
     * called, when save button in UI clicked
     */
    @FXML
    private void save()
    {
        ArrayList<String> newColumnNamesArrayList = getColumnNamesArrayList();
        isUserError = isUserError(newColumnNamesArrayList);
        Worker.runLater(()->{

            if (!isUserError)
            {
                //don't allow stage to close
                stage.setOnCloseRequest(new EventHandler<WindowEvent>()
                {
                    @Override
                    public void handle(WindowEvent event)
                    {
                        event.consume();
                    }
                });
                tableView.setDisable(true);
                buttonSave.setDisable(true);
                buttonCancel.setDisable(true);
                progressIndicator.setVisible(true);
                importer.setColumnNamesArrayList(newColumnNamesArrayList);
                importer.removeColumns(disabledColumnNumbers);

	            try
	            {
	                mainController.getDatabase().saveCSVTable(importer);
	            }
	            catch (Exception e)
	            {
	                Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
	                Platform.runLater(()->{
		            	stage.close();
		            	AlertGenerator.showAlert(AlertType.ERROR, "Import fehlgeschlagen", "", bundle.getString("error.import"), icon, stage, null, false);	            	
		            });
	            }
	
	            Platform.runLater(()->{
	            	stage.close();
	            	AlertGenerator.showAlert(AlertType.INFORMATION, "Import erfolgreich", "", bundle.getString("information.import.success"), icon, stage, null, false);	            	
	            });
	        }

            isUserError = false;
        });
    }

    /**
     * called when cancel button clicked
     * closes stage and reopens old stage
     */
    @FXML
    private void cancel()
    {
        stage.close();
        importCSVController.getStage().show();
    }
}