package de.lww4.logic.chartGenerators;

import java.util.ArrayList;

import de.lww4.logic.models.chart.Chart;
import de.lww4.logic.models.chart.ChartSetItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;

/**
 * generates a PieChart 
 * @author Robert
 */
public class PieChartGenerator
{
    String xName;
    ArrayList<ChartSetItem> chartSetItems;
    Chart chart;

    public PieChartGenerator(String xName, ArrayList<ChartSetItem> chartSetItems, Chart chart)
    {
        this.xName = xName;
        this.chartSetItems = chartSetItems;
        this.chart = chart;
    }

    /**
     * generates a new pie chart
     * @return PieChart chart
     */
    public PieChart generate()
    {
        ArrayList<PieChart.Data> data = new ArrayList<>();      

        for(ChartSetItem currentItem : chartSetItems)
        {
        	//replace series name with legend value if existing
        	String label = String.valueOf(currentItem.getLabel());        	
        	if(chart != null && chart.getLegendScale() != null)
        	{
        		String scaleLabel = chart.getLegendScale().getScaleHashMap().get(currentItem.getLabel());
        		if(scaleLabel != null)
        		{
        			label = scaleLabel;
        		}
        	}           	
        	
        	data.add(new PieChart.Data(label, currentItem.getCount()));
        }
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(data);

        final PieChart chart = new PieChart(pieChartData);
        chart.setTitle(null);

        //add tooltip to every segment that shows percentage as double value
        chart.getData().stream().forEach(tool ->
        {
            Tooltip tooltip = new Tooltip();

            double total = 0;
            for (PieChart.Data d : chart.getData())
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
}