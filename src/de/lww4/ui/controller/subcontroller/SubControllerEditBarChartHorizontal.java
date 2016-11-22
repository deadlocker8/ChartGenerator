package de.lww4.ui.controller.subcontroller;

import de.lww4.logic.ColumnTreeItem;
import de.lww4.logic.DataFormats;
import de.lww4.ui.controller.NewChartController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class SubControllerEditBarChartHorizontal extends SubControllerEditChart
{
	@FXML private Label labelX;
	@FXML private Label labelY;
	
	private ColumnTreeItem itemX;
	private ColumnTreeItem itemY;

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
			itemX = item;		
			
			updateChart();
			
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
			itemY = item;	
			
			updateChart();
			
			event.consume();
		});
	}
	
	private void updateChart()
	{
		if(itemX != null && itemY != null)
		{
			//TODO generate chart preview
			
		}
	}
}