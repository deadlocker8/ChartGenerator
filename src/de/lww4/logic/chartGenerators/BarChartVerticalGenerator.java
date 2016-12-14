package de.lww4.logic.chartGenerators;

import java.util.ArrayList;

import de.lww4.logic.Chart;
import de.lww4.logic.ChartSet;
import de.lww4.logic.utils.Utils;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;

public class BarChartVerticalGenerator
{
	String xName;
	String yName;
	ArrayList<ChartSet> sets;
	Color color;
	Chart chart;

	public BarChartVerticalGenerator(String xName, String yName, ArrayList<ChartSet> sets, Color color, Chart chart)
	{
		this.xName = xName;
		this.yName = yName;
		this.sets = sets;
		this.color = color;
		this.chart = chart;
	}

	public BarChart<String, Number> generate()
	{
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		final BarChart<String, Number> generatedChart = new BarChart<>(xAxis, yAxis);
		generatedChart.setTitle(null);

		xAxis.setLabel(xName);
		yAxis.setLabel(yName);

		for(ChartSet currentSet : sets)
		{
			XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();

			series.setName(String.valueOf(currentSet.getSetName()));
			if(chart != null && chart.getLegendScale() != null)
			{
				String name = chart.getLegendScale().getScaleHashMap().get(currentSet.getSetName());
				if(name != null)
				{
					series.setName(name);
				}
			}

			for(int i = 0; i < currentSet.getScaleItems().size(); i++)
			{
				String label = String.valueOf(currentSet.getScaleItems().get(i).getLabel());
				if(chart != null && chart.getScale() != null)
				{
					String scaleLabel = chart.getScale().getScaleHashMap().get(currentSet.getScaleItems().get(i).getLabel());
					if(scaleLabel != null)
					{
						label = scaleLabel;
					}
				}

				series.getData().add(new XYChart.Data<String, Number>(label, currentSet.getScaleItems().get(i).getCount()));
			}
			generatedChart.getData().add(series);
		}

		generatedChart.setLegendVisible(true);

		for(Node n : generatedChart.lookupAll(".default-color0.chart-bar"))
		{
			n.setStyle("-fx-bar-fill: " + Utils.toRGBHex(color) + ";");
		}

		return generatedChart;
	}
}