package de.lww4.logic.chartGenerators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;

public class PieChartGenerator
{
	String xName;
	ArrayList<Double> xValues;

	public PieChartGenerator(String xName, ArrayList<Double> xValues)
	{
		this.xName = xName;
		this.xValues = xValues;
	}

	public PieChart generate()
	{
		ArrayList<PieChart.Data> data = new ArrayList<>();
		
		Map<Double, Integer> preparedData = prepareData(xValues);

		for(Map.Entry<Double, Integer> entry : preparedData.entrySet())
		{
			data.add(new PieChart.Data(String.valueOf(entry.getKey()), (double)entry.getValue()));
		}
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(data);

		final PieChart chart = new PieChart(pieChartData);
		chart.setTitle(null);

		chart.getData().stream().forEach(tool -> {
			Tooltip tooltip = new Tooltip();

			double total = 0;
			for(PieChart.Data d : chart.getData())
			{
				total += d.getPieValue();
			}

			double pieValue = tool.getPieValue();
			double percentage = (pieValue / total) * 100;
			String percent = String.valueOf(percentage);
			percent = percent.substring(0, percent.indexOf(".") + 2);

			tooltip.setText(percent + " %");
			Tooltip.install(tool.getNode(), tooltip);
			Node node = tool.getNode();
			node.setOnMouseEntered(new EventHandler<MouseEvent>()
			{
				@Override
				public void handle(MouseEvent event)
				{
					Point2D p = node.localToScreen(event.getX() + 5, event.getY() + 7);
					tooltip.show(node, p.getX(), p.getY());
				}
			});
			node.setOnMouseExited(new EventHandler<MouseEvent>()
			{

				@Override
				public void handle(MouseEvent event)
				{
					tooltip.hide();
				}
			});
		});

		return chart;
	}
	
	private Map<Double, Integer> prepareData(ArrayList<Double> values)
	{
		Map<Double, Integer> map = new HashMap<Double, Integer>();

		for(Double temp : values)
		{
			Integer count = map.get(temp);
			map.put(temp, (count == null) ? 1 : count + 1);
		}

		return map;
	}
}