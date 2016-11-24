package de.lww4.logic.chartGenerators;

import java.util.ArrayList;

import de.lww4.logic.utils.Utils;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;

@SuppressWarnings("unchecked")
public class BarChartHorizontalGenerator
{
	String xName; 
	String yName;
	ArrayList<Double> xValues;
	ArrayList<Double> yValues;
	Color color;
	
	public BarChartHorizontalGenerator(String xName, String yName, ArrayList<Double> xValues, ArrayList<Double> yValues, Color color)
	{		
		this.xName = xName;
		this.yName = yName;
		this.xValues = xValues;
		this.yValues = yValues;
		this.color = color;
	}	
	
	public BarChart<Number, String> generate()
	{
		final NumberAxis xAxis = new NumberAxis();
		final CategoryAxis yAxis = new CategoryAxis();
		final BarChart<Number, String> chart = new BarChart<Number, String>(xAxis, yAxis);
		chart.setTitle(null);		
	
		xAxis.setLabel(xName);
		yAxis.setLabel(yName);

		XYChart.Series<Number, String> series = new XYChart.Series<Number, String>();		
	
		for(int i = 0; i < xValues.size(); i++)
		{
			series.getData().add(new XYChart.Data<Number, String>(xValues.get(i), String.valueOf(yValues.get(i))));
		}
		chart.getData().addAll(series);	
		chart.setLegendVisible(false);
					
		for(Node n : chart.lookupAll(".default-color0.chart-bar"))
		{
			n.setStyle("-fx-bar-fill: " + Utils.toRGBHex(color) + ";");
		}	
		
		return chart;
	}		
}