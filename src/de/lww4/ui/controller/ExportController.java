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
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
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
    
    @FXML private AnchorPane anchorPaneMain;
    @FXML private TextField widthTextfield;
    @FXML private TextField heightTextfield;
    @FXML private Button openSaveDialogButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    
    private File file;
    private double widthScale, heightScale;
    private boolean isInputValid;

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
		
		//Notes
		// -Snapshotfunction with preferred size works
		// -Alerts must be configured
		// -exception for input must be defined in detail
		// -cleared gridPane is not done
		// -same for exportchart
		// -stage don't close after saving image
		openSaveDialogButton.setOnAction(new EventHandler<ActionEvent>()
				{
					@Override
					public void handle(ActionEvent arg0) 
					{
						fileChooser = new FileChooser();
						fileChooser.setTitle("Speichere Diagramm als PNG Bild");
						
						ExtensionFilter filter = new ExtensionFilter("PNG Dateien (*.png)","*.png");
						fileChooser.getExtensionFilters().add(filter);
						
						file = fileChooser.showSaveDialog(stage);
						
					
						
					}	
				});
		
		saveButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent arg0) 
			{	
				checkInputValues();
				
				SnapshotParameters sp= new SnapshotParameters();
				sp.setTransform(Transform.scale(getWidthScale(),getHeightScale()));
				
				WritableImage img= gridPane.snapshot(sp, null);

				
				if(file != null && isInputValid)
				{
					
					    try {
					      ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", file);
					    } catch (IOException e) 
					    {
					    	Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
					    	AlertGenerator.showAlert(Alert.AlertType.ERROR,"Fehler", "", controller.getBundle().getString("error.save"), controller.getIcon(),true);
					    }
					
				}
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
		try
		{
			height= Double.parseDouble(heightTextfield.getText());
			setHeightScale(height);
			
			width= Double.parseDouble(widthTextfield.getText());
			setWidthScale(width);
			
			isInputValid=true;
		}
		catch(Exception e)
		{
			//TODO ALERT
			System.out.println("no convertion possible");
			isInputValid=false;
		}
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