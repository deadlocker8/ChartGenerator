package de.lww4.logic.chartGenerators;

import de.lww4.logic.models.chart.Chart;
import de.lww4.logic.models.chart.ChartSet;
import de.lww4.logic.utils.Utils;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Set;

/**
 * generates a BarChart<Number, String> in horizontal orientation
 * 
 * @author Robert
 */
public class BarChartHorizontalGenerator
{
	String xName;
	String yName;
	ArrayList<ChartSet> sets;
	Color color;
	Chart chart;

	public BarChartHorizontalGenerator(String xName, String yName, ArrayList<ChartSet> sets, Color color, Chart chart)
	{
		this.xName = xName;
		this.yName = yName;
		this.sets = sets;
		this.color = color;
		this.chart = chart;
	}

	/**
	 * generates a new BarChart<Number, String>
	 * @return BarChart<Number, String> chart
	 */
	public BarChart<Number, String> generate()
	{
		final NumberAxis xAxis = new NumberAxis();
		final CategoryAxis yAxis = new CategoryAxis();
		final BarChart<Number, String> generatedChart = new BarChart<Number, String>(xAxis, yAxis);
		generatedChart.setTitle(null);

		xAxis.setLabel(xName);
		yAxis.setLabel(yName);

		for(ChartSet currentSet : sets)
		{
			XYChart.Series<Number, String> series = new XYChart.Series<Number, String>();

			series.setName(String.valueOf(currentSet.getSetName()));
			// replace series name with legend value if existing
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
				// replace item label with scale value if existing
				String label = String.valueOf(currentSet.getScaleItems().get(i).getLabel());
				if(chart != null && chart.getScale() != null)
				{
					String scaleLabel = chart.getScale().getScaleHashMap().get(currentSet.getScaleItems().get(i).getLabel());
					if(scaleLabel != null)
					{
						label = scaleLabel;
					}
				}

				series.getData().add(new XYChart.Data<Number, String>(currentSet.getScaleItems().get(i).getCount(), label));
			}

			generatedChart.getData().add(series);
		}

		generatedChart.setLegendVisible(true);

		// style first bar according to color
		for(Node n : generatedChart.lookupAll(".default-color0.chart-bar"))
		{
			n.setStyle("-fx-bar-fill: " + Utils.toRGBHex(color) + ";");
		}

		//style first legen item according to color
		Set<Node> nodes = generatedChart.lookupAll(".chart-legend-item");
		if(nodes.size() > 0)
		{
			Node node = nodes.iterator().next();
			if(node instanceof Label)
			{
				Label labelLegendItem = (Label)node;
				labelLegendItem.getGraphic().setStyle("-fx-background-color: " + Utils.toRGBHex(color) + ";");
			}
		}
		
		return generatedChart;
	}
}