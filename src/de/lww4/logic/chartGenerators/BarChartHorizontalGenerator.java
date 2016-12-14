package de.lww4.logic.chartGenerators;

import de.lww4.logic.ChartSet;
import de.lww4.logic.utils.Utils;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class BarChartHorizontalGenerator
{
    String xName;
    String yName;
    ArrayList<ChartSet> sets;
    Color color;

    public BarChartHorizontalGenerator(String xName, String yName, ArrayList<ChartSet> sets, Color color)
    {
        this.xName = xName;
        this.yName = yName;
        this.sets = sets;
        this.color = color;
        
        System.out.println(sets);
    }

    public BarChart<Number, String> generate()
    {
        final NumberAxis xAxis = new NumberAxis();
        final CategoryAxis yAxis = new CategoryAxis();
        final BarChart<Number, String> chart = new BarChart<Number, String>(xAxis, yAxis);
        chart.setTitle(null);

        xAxis.setLabel(xName);
        yAxis.setLabel(yName);
        
        for(ChartSet currentSet : sets)
        {
	        XYChart.Series<Number, String> series = new XYChart.Series<Number, String>();
	       
	        for (int i = 0; i < currentSet.getScaleItems().size(); i++)
	        {
	            series.getData().add(new XYChart.Data<Number, String>(currentSet.getScaleItems().get(i).getCount(), String.valueOf(currentSet.getScaleItems().get(i).getLabel())));
	        }
	        
	        chart.getData().add(series);	       
        }
        
        chart.setLegendVisible(false);
    	
        for (Node n : chart.lookupAll(".default-color0.chart-bar"))
        {
            n.setStyle("-fx-bar-fill: " + Utils.toRGBHex(color) + ";");
        }

        return chart;
    }
}