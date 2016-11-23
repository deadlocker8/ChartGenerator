package de.lww4.ui.controller.subcontroller;

import java.util.ArrayList;

import de.lww4.logic.ColumnTreeItem;
import de.lww4.logic.DataFormats;
import de.lww4.logic.Utils;
import de.lww4.ui.controller.NewChartController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logger.LogLevel;
import logger.Logger;

@SuppressWarnings("unchecked")
public class SubControllerEditBarChartHorizontal extends SubControllerEditChart
{
	@FXML private Label labelX;
	@FXML private Label labelY;

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

			final NumberAxis xAxis = new NumberAxis();
			final CategoryAxis yAxis = new CategoryAxis();
			final BarChart<Number, String> chart = new BarChart<Number, String>(xAxis, yAxis);
			chart.setTitle(null);
			xAxis.setLabel("");
			yAxis.setLabel("");

			XYChart.Series<Number, String> series = new XYChart.Series<Number, String>();
			try
			{
				ArrayList<Double> xValues = super.newChartController.controller.database.getCSVColumn(itemX.getTableUUID(), itemX.getText());
				ArrayList<Double> yValues = super.newChartController.controller.database.getCSVColumn(itemY.getTableUUID(), itemY.getText());

				for(int i = 0; i < xValues.size(); i++)
				{
					series.getData().add(new XYChart.Data<Number, String>(xValues.get(i), String.valueOf(yValues.get(i))));
				}
				chart.getData().addAll(series);
				chart.setLegendVisible(false);
				
				Color color = newChartController.colorPicker.getValue();				
				for(Node n : chart.lookupAll(".default-color0.chart-bar"))
				{
					n.setStyle("-fx-bar-fill: " + Utils.toRGBHex(color) + ";");
				}

				stackPaneChart.getChildren().clear();
				stackPaneChart.getChildren().add(chart);
			}
			catch(Exception e)
			{
				Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));

				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Fehler");
				alert.setHeaderText("");
				alert.setContentText("Beim Erzeugen des Diagramms ist ein Fehler aufgetreten.");
				Stage dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
				dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
				dialogStage.getIcons().add(super.newChartController.icon);
				dialogStage.centerOnScreen();
				alert.showAndWait();
			}
		}
	}

	@Override
	public boolean isFilled()
	{
		return itemX != null && itemY != null;
	}
}