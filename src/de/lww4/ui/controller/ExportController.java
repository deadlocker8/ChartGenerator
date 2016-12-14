package de.lww4.ui.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;

import de.lww4.logic.Chart;
import de.lww4.logic.utils.AlertGenerator;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import logger.LogLevel;
import logger.Logger;

public class ExportController
{
	private Stage stage;
    private Controller controller;  
    private FileChooser fileChooser;
    private GridPane gridPane,grid;
    private StackPane stackPane;
    
    @FXML private AnchorPane anchorPaneMain;
    @FXML private TextField widthTextfield;
    @FXML private TextField heightTextfield;
    @FXML private Button openSaveDialogButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    
    private File file;
    private double widthScale, heightScale;
    private boolean isInputValid;
    
	
	//Notes TODO
	// -Snapshotfunction with preferred size works not correctly -> scale is incorrect 
    // (f.e: User typed 1000. Output is 1400)
    // -Removing Buttons -> Delete Button wasn't removed

    //this method is called when user wants to export one chart
	public void init(Stage stage, Controller controller, StackPane stackPaneChart, Chart chart)
	{
		this.stage = stage;
		this.controller = controller;
		this.stackPane=stackPaneChart;
		
		//Set the title of the diagram at the top of the image
		String name= chart.getTitle();
		Label label = new Label(name);
		StackPane.setAlignment(label, Pos.TOP_CENTER);
		
		stackPane.getChildren().add(label);
		openSaveDialogButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent arg0) 
			{
				openFilechooser();								
			}	
		});
		
		saveButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent arg0) 
			{	
				checkInputValues();
				
				if(file != null && isInputValid && chart != null)
					createChartSnapshot();
				else
					AlertGenerator.showAlert(AlertType.ERROR, "ERROR", "", 
							controller.getBundle().getString("error.load.data"), 
							controller.getIcon(), true);
			}	
		});

		cancelButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent arg0) 
			{
				stage.close();				
			}		
			
		});
	}
	
	//this method is called when user wants to export the dashboard
	public void init(Stage stage, Controller controller, GridPane gridPane)
	{
		this.stage = stage;
		this.controller = controller;
		this.gridPane=gridPane;
		
		openSaveDialogButton.setOnAction(new EventHandler<ActionEvent>()
				{
					@Override
					public void handle(ActionEvent arg0) 
					{
						openFilechooser();								
					}	
				});
		
		saveButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent arg0) 
			{	
				checkInputValues();
				grid = clearGridPane(gridPane);
				if(file != null && isInputValid)
					createDashboardSnapshot(grid);
			}	
		});
		
		cancelButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent arg0) 
			{
				stage.close();				
			}		
			
		});
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
	
	private void checkInputValues()
	{
		double width, height;
		if(!heightTextfield.getText().isEmpty() && !widthTextfield.getText().isEmpty() )
		{
			try
			{
				height= Double.parseDouble(heightTextfield.getText());
				setHeightScale(height);
				
				width= Double.parseDouble(widthTextfield.getText());
				setWidthScale(width);
				
				isInputValid=true;
			}
			catch(NumberFormatException e)
			{
				Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
				AlertGenerator.showAlert(Alert.AlertType.WARNING, "Warnung","", 
										controller.getBundle().getString("warning.values.exportfile"), 
										controller.getIcon(),true);
				isInputValid=false;
			}
		}
		else
			AlertGenerator.showAlert(Alert.AlertType.WARNING, "Warnung","", 
					controller.getBundle().getString("warning.values.empty.exportfile"), 
					controller.getIcon(),true);
	
	}
	
	private void createDashboardSnapshot(GridPane grid)
	{
		SnapshotParameters sp= new SnapshotParameters();
		sp.setTransform(Transform.scale(getWidthScale(),getHeightScale()));

		WritableImage img= grid.snapshot(sp, null);

		    try 
		    {
		      ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", file);
		    } catch (IOException e) 
		    {
		    	Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
		    	AlertGenerator.showAlert(Alert.AlertType.ERROR,"Fehler", "", controller.getBundle().getString("error.save"), controller.getIcon(),true);
		    }
		stage.close();
		controller.setDashboard(controller.getCurrentDashboard());
	}
	
	private void createChartSnapshot()
	{
		SnapshotParameters sp= new SnapshotParameters();
		sp.setTransform(Transform.scale(getWidthScale(),getHeightScale()));

		WritableImage img= stackPane.snapshot(sp, null);
		    try 
		    {
		      ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", file);
		    } catch (IOException e) 
		    {
		    	Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
		    	AlertGenerator.showAlert(Alert.AlertType.ERROR,"Fehler", "", controller.getBundle().getString("error.save"), controller.getIcon(),true);
		    }
		stage.close();
		controller.setDashboard(controller.getCurrentDashboard());
	}

	private GridPane clearGridPane(GridPane  grid)
	{
		for(int i=0; i < grid.getChildren().size(); i++)
		{
			if(grid.getChildren().get(i) instanceof AnchorPane)
			{
				AnchorPane anchor= (AnchorPane) grid.getChildren().get(i);
				for(int j=0; j < anchor.getChildren().size(); j++)
				{
					if(anchor.getChildren().get(j) instanceof HBox)
					{
						HBox box = (HBox) anchor.getChildren().get(j);
						for(int x=0; x < box.getChildren().size(); x++)
						{
							if(box.getChildren().get(x) instanceof Button)
								box.getChildren().remove(x);					
						}
					}
					if(anchor.getChildren().get(j) instanceof StackPane)
					{
						StackPane stack = (StackPane) anchor.getChildren().get(j);
						for(int w= 0; w < stack.getChildren().size();w++)
						{
							if(!(stack.getChildren().get(w) instanceof BarChart) &&
									!(stack.getChildren().get(w) instanceof PieChart))
								stack.getChildren().clear();							
						}
					}
				}
			}	
		}
		return grid;
	}
	
	private void openFilechooser()
	{
		fileChooser = new FileChooser();
		fileChooser.setTitle("Speichere Diagramm als PNG Bild");
		
		ExtensionFilter filter = new ExtensionFilter("PNG Dateien (*.png)","*.png");
		fileChooser.getExtensionFilters().add(filter);
		
		file = fileChooser.showSaveDialog(stage);
	}
	
	public double getWidthScale()
	{
		return widthScale;
	}
	
	public double getHeightScale()
	{
		return heightScale;
	}
	
	public void setHeightScale(double height)
	{
		heightScale= height/stage.getHeight();
	}
	public void setWidthScale(double width)
	{
		widthScale= width/stage.getWidth();
	}
}