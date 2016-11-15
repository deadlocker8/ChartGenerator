package de.lww4.ui;

import java.util.Locale;
import java.util.ResourceBundle;

import de.lww4.main.Dashboard;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class NewChartController
{
	@FXML private AnchorPane anchorPaneMain;
	@FXML private Button button;
	
	public Stage stage;
	private Controller controller;
	public Image icon = new Image("de/lww4/resources/icon.png");
	public final ResourceBundle bundle = ResourceBundle.getBundle("de/lww4/main/", Locale.GERMANY);		
	
	public void init(Stage stage, Controller controller, boolean edit, Dashboard dashboard, int position)
	{
		this.stage = stage;
		this.controller = controller;
		
		if(edit)
		{
			//TODO get Chart from DB
			//dashboard.getCells().get(position);
			//TODO fill form with existing data
		}
	}
	
	public void save()
	{
		//TODO
	}
	
	public void cancel()
	{
		stage.close();
	}
}