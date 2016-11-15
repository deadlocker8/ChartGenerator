package de.lww4.ui;


import de.lww4.main.Importer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;

public class ImportCSVController
{
    @FXML private Button csvFileDialogButton;
    @FXML private TextField delimiterCharTextField;
    @FXML private Button csvFileImportButton;
    @FXML private Label filenameLabel;

    private File currentFile;
    private Importer importer;
    private Stage stage;

    public void init(Stage stage)
    {
        this.stage = stage;
        csvFileDialogButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("CSV File", "csv"));
                File selectedFile = fileChooser.showOpenDialog(stage.getOwner());
                if(selectedFile != null && selectedFile.exists() && selectedFile.getName().toLowerCase().endsWith("csv"))
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
                String delimiterText = delimiterCharTextField.getText();
                if(isValidDelimiter(delimiterText) && currentFile != null)
                {
                    importer = new Importer(currentFile, delimiterText.charAt(0));
                    stage.close();
                }
                else
                {
                    if(!isValidDelimiter(delimiterText) && currentFile == null)
                    {
                        showAlertDialog('b');
                    }
                    else
                    {
                        if(!isValidDelimiter(delimiterText))
                        {
                            showAlertDialog('d');
                        }

                        if(currentFile == null)
                        {
                            showAlertDialog('f');
                        }
                    }
                }

            }
        });
    }

    private boolean isValidDelimiter(String delimiterText)
    {
        if(delimiterText == null || delimiterText.equals("")
                || delimiterText.length() < 1 || delimiterText.length() > 1)
        {
            return false;
        }
        else
        {
            return true;
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
