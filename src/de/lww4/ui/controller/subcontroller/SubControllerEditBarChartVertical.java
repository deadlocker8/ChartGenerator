package de.lww4.ui.controller.subcontroller;

import de.lww4.ui.controller.NewChartController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SubControllerEditBarChartVertical extends SubControllerEditChart
{
	@FXML private Label labelX;
	@FXML private Label labelY;
	
	public void init(NewChartController newChartController)
	{
		super.init(newChartController);		
	}
}