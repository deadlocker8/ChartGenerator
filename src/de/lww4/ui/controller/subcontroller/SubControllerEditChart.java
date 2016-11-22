package de.lww4.ui.controller.subcontroller;

import java.util.Locale;
import java.util.ResourceBundle;

import de.lww4.ui.controller.NewChartController;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public abstract class SubControllerEditChart
{
	@FXML protected StackPane stackPaneChart;
	
	protected NewChartController newChartController;
	protected final ResourceBundle bundle = ResourceBundle.getBundle("de/lww4/main/", Locale.GERMANY);

	public void init(NewChartController newChartController)
	{
		this.newChartController = newChartController;
	}
}