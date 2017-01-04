package de.lww4.ui.controller.subcontroller;

import java.util.ArrayList;

import de.lww4.logic.Chart;
import de.lww4.logic.ChartSet;
import de.lww4.logic.ChartSetItem;
import de.lww4.logic.ColumnTreeItem;
import de.lww4.logic.DataFormats;
import de.lww4.logic.chartGenerators.BarChartVerticalGenerator;
import de.lww4.logic.utils.AlertGenerator;
import de.lww4.logic.utils.Utils;
import de.lww4.ui.controller.NewChartController;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import logger.LogLevel;
import logger.Logger;

public class SubControllerEditBarChartVertical extends SubControllerEditChart
{
	@FXML private Label labelX;
	@FXML private Label labelY;
	@FXML private AnchorPane anchorPane;

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
			newChartController.initTreeView(itemX.getTableUUID());

			event.consume();
		});

		labelY.setOnDragOver(event -> {
			event.acceptTransferModes(TransferMode.ANY);
			labelY.setStyle("-fx-background-color: #0094FF;");
			event.consume();
		});

		labelY.setOnDragExited(event -> {
			labelY.setStyle("");
			event.consume();
		});

		labelY.setOnDragDropped(event -> {
			Dragboard db = event.getDragboard();
			ColumnTreeItem item = (ColumnTreeItem)db.getContent(DataFormats.DATAFORMAT_COLUMN_TREE_ITEM);

			labelY.setText(item.getText());
			labelY.setStyle("");
			super.itemY = item;		
			
			updateChart(itemX, itemY, chart);
			newChartController.initTreeView(itemY.getTableUUID());

			event.consume();
		});

		labelY.prefWidthProperty().bind(anchorPane.heightProperty());
	}

	@Override
	public void updateChart(ColumnTreeItem itemX, ColumnTreeItem itemY, Chart chart)
	{
		if(itemX != null && itemY != null)
		{
			this.itemX = itemX;
			this.itemY = itemY;

			labelX.setText(itemX.getText());
			labelY.setText(itemY.getText());

			try
			{
				ArrayList<ChartSetItem> chartSetItems = super.newChartController.getController().getDatabase().getData(itemX.getTableUUID(), itemX.getText(), itemY.getText());
				ArrayList<ChartSet> sets = Utils.splitIntoChartSets(chartSetItems);

				BarChartVerticalGenerator generator = new BarChartVerticalGenerator("", "", sets, newChartController.getColorPicker().getValue(), chart);
				BarChart<String, Number> generatedChart = generator.generate();

				stackPaneChart.getChildren().clear();
				stackPaneChart.getChildren().add(generatedChart);
			}
			catch(Exception e)
			{
				Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));

				AlertGenerator.showAlert(AlertType.ERROR, "Fehler", "", newChartController.getController().getBundle().getString("error.create.chart"), newChartController.getController().getIcon(), newChartController.getStage(), null, false);
			}
		}
	}

	@Override
	public boolean isFilled()
	{
		return itemX != null && itemY != null;
	}

	@Override
	public void buttonReset()
	{
		itemX = null;
		itemY = null;
		labelX.setText("<Daten für X-Achse hier hin ziehen>");
		labelY.setText("<Daten für Y-Achse hier hin ziehen>");
		stackPaneChart.getChildren().clear();

		newChartController.initTreeView(null);
	}
}