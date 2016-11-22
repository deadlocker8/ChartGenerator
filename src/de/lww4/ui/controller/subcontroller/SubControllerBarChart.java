package de.lww4.ui.controller.subcontroller;

import java.util.Locale;
import java.util.ResourceBundle;

import de.lww4.ui.controller.NewChartController;

public class SubControllerBarChart
{	
	private NewChartController newChartController;	
	public final ResourceBundle bundle = ResourceBundle.getBundle("de/lww4/main/", Locale.GERMANY);	

	public void init(NewChartController newChartController)
	{	
		this.newChartController = newChartController;			
	}	
}