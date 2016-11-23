package de.lww4.ui.controller.subcontroller;

import de.lww4.logic.ColumnTreeItem;
import de.lww4.ui.controller.NewChartController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SubControllerEditPieChart extends SubControllerEditChart
{
	@FXML private Label labelX;
	@FXML private Label labelY;
	
	public void init(NewChartController newChartController)
	{
		super.init(newChartController);		
	}

	@Override
	public void updateChart(ColumnTreeItem itemX, ColumnTreeItem itemY)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean isFilled()
	{
		// TODO Auto-generated method stub
		return false;
	}
}