package de.lww4.ui.controller.subcontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.lww4.logic.ColumnTreeItem;
import de.lww4.logic.DataFormats;
import de.lww4.ui.controller.NewChartController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
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
				ArrayList<PieChart.Data> data = new ArrayList<>();
				ArrayList<Double> xValues = super.newChartController.controller.database.getCSVColumn(itemX.getTableUUID(), itemX.getText());

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

	@Override
	public boolean isFilled()
	{
		itemY = new ColumnTreeItem("emtpy", "empty", false);
		return itemX != null;
	}
}