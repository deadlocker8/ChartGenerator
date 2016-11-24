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
public class BarChartVerticalGenerator
{
	String xName;
	String yName;
	ArrayList<Double> xValues;
	ArrayList<Double> yValues;
	Color color;

	public BarChartVerticalGenerator(String xName, String yName, ArrayList<Double> xValues, ArrayList<Double> yValues, Color color)
	{
		this.xName = xName;
		this.yName = yName;
		this.xValues = xValues;
		this.yValues = yValues;
		this.color = color;
	}

	public BarChart<String, Number> generate()
	{
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		final BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
		chart.setTitle(null);
		
		xAxis.setLabel(xName);
		yAxis.setLabel(yName);
	
		XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();

		for(int i = 0; i < xValues.size(); i++)
		{
			series.getData().add(new XYChart.Data<String, Number>(String.valueOf(xValues.get(i)), yValues.get(i)));
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