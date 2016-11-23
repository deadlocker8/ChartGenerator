package de.lww4.ui.controller;

import java.util.Locale;
import java.util.ResourceBundle;

import de.lww4.logic.Dashboard;
import de.lww4.ui.cells.DashboardCell;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ImportCSVColumnNamesController
{
	@FXML private TableView<String> tableView;
	@FXML private Button buttonCancel;

	public Stage stage;
	private Controller controller;
	public Image icon = new Image("de/lww4/resources/icon.png");
	public final ResourceBundle bundle = ResourceBundle.getBundle("de/lww4/main/", Locale.GERMANY);	

	public void init(Stage stage, Controller controller)
	{
		this.stage = stage;		
		this.controller = controller;
		

	}	

	public void cancel()
	{
		stage.close();
	}
}