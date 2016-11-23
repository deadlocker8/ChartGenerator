package de.lww4.ui.controller;


import java.io.File;
import java.util.ArrayList;

import de.lww4.logic.DelimiterType;
import de.lww4.logic.ErrorType;
import de.lww4.logic.Importer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logger.LogLevel;
import logger.Logger;

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
                if (selectedFile != null && selectedFile.exists() && selectedFile.getName().toLowerCase().endsWith("csv"))
                {
                    currentFile = selectedFile;
                    String filename = currentFile.getName();
                    filenameLabel.setText(filename);
                    chartNameTextField.setText(filename);
                }
            }
        });

        csvFileImportButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
            	DelimiterType delimiter = delimiterChoiceBox.getSelectionModel().getSelectedItem();
                if (!isUserError())
                {
                    importer = new Importer(currentFile, delimiter, fillValueTextField.getText(), chartNameTextField.getText());
                    stage.hide();
                    openCSVColumnNameDialog();
                }

            }
        });
    }

    private void openCSVColumnNameDialog()
    {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxml/ImportCSVColumnNamesGUI.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root, 500, 400));
            newStage.setMinHeight(400);
            newStage.setMinWidth(500);
            newStage.initOwner(stage);
            newStage.setTitle("CSV Spaltennamen festlegen");
            newStage.getScene().getStylesheets().add("de/lww4/main/style.css");

            newStage.getIcons().add(icon);
            ImportCSVColumnNamesController newController = fxmlLoader.getController();
            newController.init(newStage, mainController, importer);

            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.setResizable(true);
            newStage.show();
        }
        catch (Exception e)
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
}