package de.lww4.ui;


import de.lww4.main.Importer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;

public class ImportCSVController
{
    @FXML private Button csvFileDialogButton;
    @FXML private ChoiceBox delimiterChoiceBox;
    @FXML private Button csvFileImportButton;
    @FXML private Label filenameLabel;

    private File currentFile;
    private Importer importer;
    private Stage stage;

    public void init(Stage stage)
    {
        this.stage = stage;
        delimiterChoiceBox.getSelectionModel().select("Semicolon");
        csvFileDialogButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("CSV File", "csv"));
                File selectedFile = fileChooser.showOpenDialog(stage.getOwner());
                if (selectedFile != null && selectedFile.exists() && selectedFile.getName().toLowerCase().endsWith("csv"))
                {
                    currentFile = selectedFile;
                    filenameLabel.setText(currentFile.getName());
                }
            }
        });

        csvFileImportButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                char delimiter = getDelimiterFromChoiceBox();
                if (currentFile != null)
                {
                    importer = new Importer(currentFile, delimiter, "0");
                    stage.close();
                }
                else
                {
                    showAlertDialog('f');
                }

            }
        });
    }

    private char getDelimiterFromChoiceBox()
    {
        switch ((String)delimiterChoiceBox.getSelectionModel().getSelectedItem())
        {
            case "Tabulator":
                return '\t';
            case "Leerzeichen":
                return ' ';
            case "Komma":
                return ',';
            case "Semicolon":
                return ';';
            case "Punkt":
                return '.';
            default: return '@';
        }
    }

    private void showAlertDialog(char reason)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        switch (reason)
        {
            case 'd':
                alert.setContentText("Bitte Trennzeichen auswaehlen!");
                break;
            case 'f':
                alert.setContentText("Bitte Datei auswaehlen!");
                break;
            case 'b':
                alert.setContentText("Bitte Datei und Trennzeichen waehlen!");
                break;
            default:
                alert.setContentText("ERROR");
                break;
        }
        alert.show();
    }

}
