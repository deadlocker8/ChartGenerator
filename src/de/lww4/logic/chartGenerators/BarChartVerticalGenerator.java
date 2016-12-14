package de.lww4.logic.chartGenerators;

import java.util.ArrayList;

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

	public BarChartVerticalGenerator(String xName, String yName, ArrayList<ChartSet> sets, Color color)
	{
		this.xName = xName;
		this.yName = yName;
		this.sets = sets;
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
        
        for(ChartSet currentSet : sets)
        {
        	XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
        	series.setName(String.valueOf(currentSet.getSetName()));
	        
	        for (int i = 0; i < currentSet.getScaleItems().size(); i++)
	        {
	            series.getData().add(new XYChart.Data<String, Number>(String.valueOf(currentSet.getScaleItems().get(i).getLabel()), currentSet.getScaleItems().get(i).getCount()));
	        }
	        chart.getData().add(series);            
        }
        
        chart.setLegendVisible(true);       
      
        for (Node n : chart.lookupAll(".default-color0.chart-bar"))
        {
            n.setStyle("-fx-bar-fill: " + Utils.toRGBHex(color) + ";");
        }

        return chart;
    }
}