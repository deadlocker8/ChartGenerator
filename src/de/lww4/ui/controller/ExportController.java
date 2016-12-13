package de.lww4.ui.controller;

import de.lww4.logic.Chart;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ExportController
{
	private Stage stage;
    private Controller controller;  

    //this method is called when user wants to export one chart
	public void init(Stage stage, Controller controller, StackPane stackPaneChart, Chart chart)
	{
		this.stage = stage;
		this.controller = controller;
		
		//stackPaneChart is the node to snapshot
		
		//Chart is to extract name of chart (we have to find a way to display the name above the stackPane in the exported image	
		//null chek chart! if error --> AlertGenerator.showAlert(AlertType.ERROR, "ERROR", "", bundle.getString("error.load.data"), icon, true);
	}
	
	//this method is called when user wants to export the dashboard
	public void init(Stage stage, Controller controller, GridPane gridPane)
	{
		this.stage = stage;
		this.controller = controller;
		
		/* gridPane is the node to snapshot
		 * 
		 * but before snapshotting you have to delete some children (all buttons)
		 * 
		 * structure inside gridPane:
		 * 
		 * 	-6 cells (3 columns, 2 rows)
		 * 
		 *  -every cell contains one AnchorPane
		 * 		--> every AnchorPane contains: 	HBox 
		 * 										--> loop over all HBox children and remove them if they are instanceof Button
		 * 			
		 * 										StackPane (holds chart) 		 										
		 *										--> check if StackPane child is instanceof BarChart or PieChart
		 *										and if not just clear stackpane children (so the exported area for this stackPane will appear white)									 
		 */		
	}
}