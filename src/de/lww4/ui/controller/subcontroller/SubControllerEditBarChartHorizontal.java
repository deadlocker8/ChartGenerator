package de.lww4.ui.controller.subcontroller;

import java.util.ArrayList;

import de.lww4.logic.ColumnTreeItem;
import de.lww4.logic.DataFormats;
import de.lww4.logic.chartGenerators.BarChartHorizontalGenerator;
import de.lww4.logic.utils.AlertGenerator;
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

public class SubControllerEditBarChartHorizontal extends SubControllerEditChart
{
	@FXML private Label labelX;
	@FXML private Label labelY;
	@FXML private AnchorPane anchorPane;

	public void init(NewChartController newChartController)
	{
		super.init(newChartController);

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

			updateChart(itemX, itemY);

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

			updateChart(itemX, itemY);

			event.consume();
		});
		
		labelY.prefWidthProperty().bind(anchorPane.heightProperty());
	}

	@Override
	public void updateChart(ColumnTreeItem itemX, ColumnTreeItem itemY)
	{
		if(itemX != null && itemY != null)
		{
			this.itemX = itemX;
			this.itemY = itemY;

			labelX.setText(itemX.getText());
			labelY.setText(itemY.getText());			
			try
			{
				ArrayList<Double> xValues = super.newChartController.getController().getDatabase().getCSVColumn(itemX.getTableUUID(), itemX.getText());
				ArrayList<Double> yValues = super.newChartController.getController().getDatabase().getCSVColumn(itemY.getTableUUID(), itemY.getText());

				BarChartHorizontalGenerator generator = new BarChartHorizontalGenerator("", "", xValues, yValues, newChartController.getColorPicker().getValue());
				BarChart<Number, String> chart = generator.generate();
				
				stackPaneChart.getChildren().clear();
				stackPaneChart.getChildren().add(chart);
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
		return itemX != null && itemY != null;
	}
}