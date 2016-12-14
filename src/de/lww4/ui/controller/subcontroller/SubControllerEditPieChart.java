package de.lww4.ui.controller.subcontroller;

import de.lww4.logic.Chart;
import de.lww4.logic.ChartSetItem;
import de.lww4.logic.ColumnTreeItem;
import de.lww4.logic.DataFormats;
import de.lww4.logic.chartGenerators.PieChartGenerator;
import de.lww4.logic.utils.AlertGenerator;
import de.lww4.ui.controller.NewChartController;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import logger.LogLevel;
import logger.Logger;

import java.util.ArrayList;

public class SubControllerEditPieChart extends SubControllerEditChart
{
	@FXML private Label labelX;

	public void init(NewChartController newChartController, Chart chart)
	{
		super.init(newChartController, chart);

		labelX.setOnDragOver(event -> {
			event.acceptTransferModes(TransferMode.ANY);
			labelX.setStyle("-fx-background-color: #0094FF;");
			event.consume();
		});

		labelX.setOnDragExited(event -> {
			labelX.setStyle("");
			event.consume();
		});

		labelX.setOnDragDropped(event -> {
			Dragboard db = event.getDragboard();
			ColumnTreeItem item = (ColumnTreeItem)db.getContent(DataFormats.DATAFORMAT_COLUMN_TREE_ITEM);

			labelX.setText(item.getText());
			labelX.setStyle("");
			super.itemX = item;

			updateChart(itemX, itemY, chart);

			event.consume();
		});
	}

	@Override
	public void updateChart(ColumnTreeItem itemX, ColumnTreeItem itemY, Chart chart)
	{
		if(itemX != null)
		{
			this.itemX = itemX;

			labelX.setText(itemX.getText());

			try
            {
				ArrayList<ChartSetItem> chartSetItems = super.newChartController.getController().getDatabase().getData(itemX.getTableUUID(), itemX.getText());			

                PieChartGenerator generator = new PieChartGenerator(itemX.getText(), chartSetItems, chart);
                PieChart generatedChart = generator.generate();

				stackPaneChart.getChildren().clear();
				stackPaneChart.getChildren().add(generatedChart);
			}
			catch(Exception e)
			{
				Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));

                AlertGenerator.showAlert(AlertType.ERROR, "Fehler", "", newChartController.getController().getBundle().getString("error.create.chart"), newChartController.getController().getIcon(), true);
            }
        }
    }


    @Override
    public boolean isFilled()
    {
		itemY = new ColumnTreeItem("emtpy", "empty", false);
		return itemX != null;
	}
    
    @Override
    public void buttonReset()
	{
		itemX = null;
		labelX.setText("<Daten hier hin ziehen>");	
		stackPaneChart.getChildren().clear();	
	}
}