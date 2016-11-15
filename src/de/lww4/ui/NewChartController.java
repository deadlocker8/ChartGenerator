package de.lww4.ui;

import java.util.Locale;
import java.util.ResourceBundle;

import de.lww4.main.ChartType;
import de.lww4.main.Dashboard;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class NewChartController
{
	@FXML private AnchorPane anchorPaneMain;
	@FXML private TextField textFieldTitle;
	@FXML private ColorPicker colorPicker;
	@FXML private TreeView<String> treeView;
	@FXML private StackPane stackPaneChart;
	@FXML private Button buttonSave;
	@FXML private Button buttonCancel;
	@FXML private HBox hboxChartTypes;
	
	public Stage stage;
	private Controller controller;
	public Image icon = new Image("de/lww4/resources/icon.png");
	public final ResourceBundle bundle = ResourceBundle.getBundle("de/lww4/main/", Locale.GERMANY);

	public void init(Stage stage, Controller controller, boolean edit, Dashboard dashboard, int position)
	{
		this.stage = stage;
		this.controller = controller;
		
		final ToggleGroup toggleGroupChartTypes = new ToggleGroup();
		
		for(ChartType currentType : ChartType.values())
		{
			RadioButton currentRadioButton = new RadioButton(currentType.getName());
			currentRadioButton.setContentDisplay(ContentDisplay.BOTTOM);
			currentRadioButton.setToggleGroup(toggleGroupChartTypes);
			hboxChartTypes.getChildren().add(currentRadioButton);
		}
		
		toggleGroupChartTypes.getToggles().get(0).setSelected(true);	
	
		if(edit)
		{
			// TODO get Chart from DB
			// dashboard.getCells().get(position);
			// TODO fill form with existing data
		}
	}

	public void save()
	{
		// TODO
	}

	public void cancel()
	{
		stage.close();
	}
}