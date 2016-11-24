package de.lww4.ui.controller.subcontroller;

import java.util.ArrayList;

import de.lww4.logic.ColumnTreeItem;
import de.lww4.logic.DataFormats;
import de.lww4.logic.chartGenerators.PieChartGenerator;
import de.lww4.ui.controller.NewChartController;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import logger.LogLevel;
import logger.Logger;

public class SubControllerEditPieChart extends SubControllerEditChart
{
	@FXML private Label labelX;

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
	}

	@Override
	public void updateChart(ColumnTreeItem itemX, ColumnTreeItem itemY)
	{
		if(itemX != null)
		{
			this.itemX = itemX;

			labelX.setText(itemX.getText());

			try
			{				
				ArrayList<Double> xValues = super.newChartController.getController().getDatabase().getCSVColumn(itemX.getTableUUID(), itemX.getText());
			
				PieChartGenerator generator = new PieChartGenerator(itemX.getText(), xValues);
				PieChart chart = generator.generate();

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
				dialogStage.getIcons().add(super.newChartController.getController().getIcon());
				dialogStage.centerOnScreen();
				alert.showAndWait();
			}
		}
	}
	

	@Override
	public boolean isFilled()
	{
		itemY = new ColumnTreeItem("emtpy", "empty", false);
		return itemX != null;
	}
}