package de.lww4.ui.controller;

import de.lww4.logic.DelimiterType;
import de.lww4.logic.ErrorType;
import de.lww4.logic.Importer;
import de.lww4.logic.utils.AlertGenerator;
import de.lww4.logic.utils.Utils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logger.LogLevel;
import logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * ImportCSVController
 *
 * @author max
 */
public class ImportCSVController
{
    @FXML private Button csvFileDialogButton;
    @FXML private ComboBox<DelimiterType> delimiterChoiceBox;
    @FXML private Button csvFileImportButton;
    @FXML private Label filenameLabel;
    @FXML private TextField chartNameTextField;
    @FXML private TextField fillValueTextField;

    private File currentFile;
    private Importer importer;
    private Stage stage;
    private Image icon;
    private Controller mainController;

    public void init(Stage stage, Image icon, Controller mainController)
    {
        this.stage = stage;
        this.icon = icon;
        this.mainController = mainController;
        delimiterChoiceBox.getSelectionModel().select(DelimiterType.SEMICOLON);
        delimiterChoiceBox.getItems().addAll(DelimiterType.values());
        csvFileDialogButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Datei (*.csv)", "*.csv", "*.CSV"));
                File selectedFile = fileChooser.showOpenDialog(stage.getOwner());
                if(selectedFile != null && selectedFile.exists() && selectedFile.getName().toLowerCase().endsWith("csv"))
                {
                    currentFile = selectedFile;
                    String filename = currentFile.getName();
                    filenameLabel.setText(filename);
                    chartNameTextField.setText(filename);
                    removeImpossibleDelimiters(currentFile);
                }
            }
        });

        csvFileImportButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                DelimiterType delimiter = delimiterChoiceBox.getSelectionModel().getSelectedItem();
                if(!isUserError())
                {
                    importer = new Importer(currentFile, delimiter, fillValueTextField.getText(), chartNameTextField.getText());
                    if (!containsStrings())
                    {
                        stage.hide();
                        openCSVColumnNameDialog();
                    }
                }
            }
        });

        fillValueTextField.setTextFormatter(new TextFormatter<>(c -> {
            if(c.getControlNewText().isEmpty())
            {
                return c;
            }

            if(c.getControlNewText().matches("^-?[0-9]?\\d*(?:[\\.\\,]\\d*)?$"))
            {
                return c;
            }
            else
            {
                return null;
            }
        }));
    }

    private boolean containsStrings()
    {
        for (ArrayList<String> row : importer.getData())
        {
            for (String value : row)
            {
                try
                {
                    double doubleValue = Double.parseDouble(value);
                }
                catch (NumberFormatException e)
                {
                    AlertGenerator.showAlert(Alert.AlertType.WARNING, "Warnung", "", mainController.getBundle().getString("warning.possible.string.values"), mainController.getIcon(), true);
                    return true;
                }
            }
        }
        return false;
    }

    private void removeImpossibleDelimiters(File file)
    {
        ArrayList<DelimiterType> delimiterTypeArrayList = new ArrayList<>(Arrays.asList(DelimiterType.values()));
        try
        {
            String fileContent = Utils.getContentsFromInputStream(new FileInputStream(file));
            Iterator<DelimiterType> iterator = delimiterTypeArrayList.iterator();
            while(iterator.hasNext())
            {
                DelimiterType delimiterType = iterator.next();
                if(!fileContent.contains(String.valueOf(delimiterType.getDelimiter())))
                {
                    iterator.remove();
                }
            }

            delimiterChoiceBox.setItems(FXCollections.observableArrayList(delimiterTypeArrayList));
            if(delimiterTypeArrayList.size() == 0)
            {
                String errorInvalidDelimiter = mainController.getBundle().getString("error.invalid.delimiter");
                errorInvalidDelimiter = errorInvalidDelimiter.replace("{}", DelimiterType.getPossibleDelimiterString());
                AlertGenerator.showAlert(Alert.AlertType.ERROR, "Fehler", "", errorInvalidDelimiter, mainController.getIcon(), true);
            }
            else
            {
                delimiterChoiceBox.getSelectionModel().select(0);
            }
        }
        catch(Exception e)
        {
            Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
        }

    }

    private void openCSVColumnNameDialog()
    {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/de/lww4/ui/fxml/ImportCSVColumnNamesGUI.fxml"));
            Parent root = (Parent)fxmlLoader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root, 500, 400));
            newStage.setMinHeight(400);
            newStage.setMinWidth(500);
            newStage.initOwner(stage);
            newStage.setTitle("CSV Spaltennamen festlegen");
            newStage.getScene().getStylesheets().add("de/lww4/main/style.css");

            newStage.getIcons().add(icon);
            ImportCSVColumnNamesController newController = fxmlLoader.getController();
            newController.init(newStage, this, mainController, importer);

            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.setResizable(true);
            newStage.show();
        }
        catch(Exception e)
        {
            Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
        }
    }

    private boolean isUserError()
    {
        ArrayList<ErrorType> errorTypes = new ArrayList<>();
        String chartName = chartNameTextField.getText();
        String fillValue = fillValueTextField.getText();
        if(currentFile == null)
        {
            errorTypes.add(ErrorType.NO_FILE);
        }

        if(chartName.equals(""))
        {
            errorTypes.add(ErrorType.NO_CHARTNAME);
        }

        if(fillValue.equals(""))
        {
            errorTypes.add(ErrorType.NO_FILLER);
        }

        if(!fillValueTextField.getText().matches("^-?[0-9]\\d*(?:[\\.\\,]\\d+)?$"))
        {
            errorTypes.add(ErrorType.INVALID_FILLER);
        }

        if(errorTypes.size() > 0)
        {
            showAlertDialog(errorTypes);
            return true;
        }

        return false;
    }

    private void showAlertDialog(ArrayList<ErrorType> errorTypes)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("");
        alert.setTitle("Hinweis");
        if(errorTypes.size() == 1)
        {
            alert.setContentText(errorTypes.get(0).getErrorMessage());
        }
        else
        {
            alert.setContentText(ErrorType.getErrorMessage(errorTypes));
        }

        Stage dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(icon);
        dialogStage.centerOnScreen();
        alert.showAndWait();
    }

    public Stage getStage()
    {
        return stage;
    }
}