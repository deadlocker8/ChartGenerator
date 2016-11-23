package de.lww4.ui.controller.subcontroller;

import java.util.Locale;
import java.util.ResourceBundle;

import de.lww4.logic.ColumnTreeItem;
import de.lww4.ui.controller.NewChartController;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public abstract class SubControllerEditChart
{
	@FXML protected StackPane stackPaneChart;
	
	protected NewChartController newChartController;
	protected final ResourceBundle bundle = ResourceBundle.getBundle("de/lww4/main/", Locale.GERMANY);
	
	protected ColumnTreeItem itemX;
	protected ColumnTreeItem itemY;	

	public void init(NewChartController newChartController)
	{
		this.newChartController = newChartController;
	}
	
	public ColumnTreeItem getItemX()
	{
		return itemX;
	}
	
	public ColumnTreeItem getItemY()
	{
		return itemY;
	}

	public abstract boolean isFilled();
	
	public abstract void updateChart(ColumnTreeItem itemX, ColumnTreeItem itemY);
}