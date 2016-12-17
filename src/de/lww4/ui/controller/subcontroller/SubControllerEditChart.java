package de.lww4.ui.controller.subcontroller;

import de.lww4.logic.Chart;
import de.lww4.logic.ColumnTreeItem;
import de.lww4.ui.controller.NewChartController;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public abstract class SubControllerEditChart
{
	@FXML protected StackPane stackPaneChart;

    protected NewChartController newChartController;
    protected Chart chart;

    protected ColumnTreeItem itemX;
    protected ColumnTreeItem itemY;

	public void init(NewChartController newChartController, Chart chart)
	{
		this.newChartController = newChartController;
		this.chart = chart;
	}
	
	public ColumnTreeItem getItemX()
	{
		return itemX;
	}
	
	public ColumnTreeItem getItemY()
	{
		return itemY;
	}
	
	public Chart getChart()
	{
		return chart;
	}

	public abstract boolean isFilled();
	
	public abstract void updateChart(ColumnTreeItem itemX, ColumnTreeItem itemY, Chart chart);
	
	public abstract void buttonReset();
}