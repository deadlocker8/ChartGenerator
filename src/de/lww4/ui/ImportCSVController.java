package de.lww4.ui;


import de.lww4.main.ErrorType;
import de.lww4.main.Importer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;

public class ImportCSVController
{
    @FXML private Button csvFileDialogButton;
    @FXML private ChoiceBox delimiterChoiceBox;
    @FXML private Button csvFileImportButton;
    @FXML private Label filenameLabel;
    @FXML private TextField chartNameTextField;
    @FXML private TextField fillValueTextField;

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
                char delimiter = getDelimiterFromChoiceBox();
                if (!isUserError())
                {
                    importer = new Importer(currentFile, delimiter, fillValueTextField.getText(), chartNameTextField.getText());
                    stage.close();
                }

            }
        });
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

    private void showAlertDialog(ArrayList<ErrorType> errorTypes)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        if(errorTypes.size() == 1)
        {
            alert.setContentText(errorTypes.get(0).getErrorMessage());
        }
        else
        {
            alert.setContentText(ErrorType.getErrorMessage(errorTypes));
        }
        alert.show();
    }

}
