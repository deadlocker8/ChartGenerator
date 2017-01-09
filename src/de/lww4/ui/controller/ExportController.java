package de.lww4.ui.controller;


import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;

import de.lww4.logic.models.chart.Chart;
import de.lww4.logic.utils.AlertGenerator;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
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

/**
 * export controller
 * @author Maxie
 *
 */
public class ExportController
{
	private Stage stage;
	private Controller controller;
	private FileChooser fileChooser;
	private GridPane gridPane, grid;
	private StackPane stackPane;

	@FXML private AnchorPane anchorPaneMain;
	@FXML private TextField widthTextfield;
	@FXML private TextField heightTextfield;
	@FXML private Button openSaveDialogButton;
	@FXML private Button saveButton;
	@FXML private Button cancelButton;
	@FXML private Label labelFile;

	private File file;
	private double width, height;	

	/**
	 * this method is called when user wants to export one chart
	 * @param stage
	 * @param controller
	 * @param stackPaneChart
	 * @param chart
	 */
	public void init(Stage stage, Controller controller, StackPane stackPaneChart, Chart chart)
	{
		this.stage = stage;
		this.controller = controller;
		this.stackPane = stackPaneChart;
	
		String name = chart.getTitle();
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
				if(chart != null)
				{
					if(checkInputValues())
					{
						if(file != null)
						{
							createChartSnapshot();
						}
						else
						{
							AlertGenerator.showAlert(Alert.AlertType.WARNING, "Warnung", "", controller.getBundle().getString("warning.name.empty.exportfile"), controller.getIcon(), stage, null, false);
						}
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

		initTextFields();
	}

	/**
	 * this method is called when user wants to export the dashboard
	 * @param stage
	 * @param controller
	 * @param gridPane
	 */
	public void init(Stage stage, Controller controller, GridPane gridPane)
	{
		this.stage = stage;
		this.controller = controller;
		this.gridPane = gridPane;

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
				if(checkInputValues())
				{
					if(file != null)
					{
						grid = clearGridPane(gridPane);
						createDashboardSnapshot(grid);
					}
					else
					{
						AlertGenerator.showAlert(Alert.AlertType.WARNING, "Warnung", "", controller.getBundle().getString("warning.name.empty.exportfile"), controller.getIcon(), stage, null, false);
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
	}

	/**
	 * initializes TextFields with TextFormatters
	 */
	private void initTextFields()
	{
		//textFormatters only allow positive integer values for the width and height TextFields
		widthTextfield.setTextFormatter(new TextFormatter<>(c -> {
			if(c.getControlNewText().isEmpty())
			{
				return c;
			}

			if(c.getControlNewText().matches("[0-9]*"))
			{
				return c;
			}
			else
			{
				return null;
			}
		}));

		heightTextfield.setTextFormatter(new TextFormatter<>(c -> {
			if(c.getControlNewText().isEmpty())
			{
				return c;
			}

			if(c.getControlNewText().matches("[0-9]*"))
			{
				return c;
			}
			else
			{
				return null;
			}
		}));
	}

	/**
	 * checks wether one of the input values is missing, empty or zero
	 * @return
	 */
	private boolean checkInputValues()
	{
		// double width, height;
		if(!heightTextfield.getText().isEmpty() && !widthTextfield.getText().isEmpty())
		{
			height = Double.parseDouble(heightTextfield.getText());

			width = Double.parseDouble(widthTextfield.getText());

			if(height != 0 && width != 0)
				return true;
			else
			{
				AlertGenerator.showAlert(Alert.AlertType.WARNING, "Warnung", "", controller.getBundle().getString("warning.values.null.exportfile"), controller.getIcon(), stage, null, false);
				return false;
			}
				
		}
		else
		{
			AlertGenerator.showAlert(Alert.AlertType.WARNING, "Warnung", "", controller.getBundle().getString("warning.values.empty.exportfile"), controller.getIcon(), stage, null, false);
			return false;
		}
	}

	/**
	 * snapshots the whole dashboard and saves the image to file
	 * @param grid
	 */
	private void createDashboardSnapshot(GridPane grid)
	{
		SnapshotParameters sp = new SnapshotParameters();
		sp.setTransform(Transform.scale(width / gridPane.getWidth(), height / gridPane.getHeight()));

		WritableImage img = grid.snapshot(sp, null);

		try
		{
			ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", file);
		}
		catch(IOException e)
		{
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
			AlertGenerator.showAlert(Alert.AlertType.ERROR, "Fehler", "", controller.getBundle().getString("error.save"), controller.getIcon(), stage, null, false);
		}
		stage.close();
		controller.setDashboard(controller.getCurrentDashboard());
	}

	/**
	 * snapshots one chart and saves the image to file
	 */
	private void createChartSnapshot()
	{
		SnapshotParameters sp = new SnapshotParameters();
		sp.setTransform(Transform.scale(width / stackPane.getWidth(), height / stackPane.getHeight()));

		WritableImage img = stackPane.snapshot(sp, null);
		try
		{
			ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", file);
		}
		catch(IOException e)
		{
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
			AlertGenerator.showAlert(Alert.AlertType.ERROR, "Fehler", "", controller.getBundle().getString("error.save"), controller.getIcon(), stage, null, false);
		}
		stage.close();
		controller.setDashboard(controller.getCurrentDashboard());
	}

	/**
	 * deletes nodes from the GridPane that are not necessary for the snapshot (buttons, etc.)
	 * @param grid
	 * @return
	 */
	private GridPane clearGridPane(GridPane grid)
	{
		for(int i = 0; i < grid.getChildren().size(); i++)
		{
			if(grid.getChildren().get(i) instanceof AnchorPane)
			{
				AnchorPane anchor = (AnchorPane)grid.getChildren().get(i);
				for(int j = 0; j < anchor.getChildren().size(); j++)
				{
					if(anchor.getChildren().get(j) instanceof StackPane)
					{
						StackPane stack = (StackPane)anchor.getChildren().get(j);
						for(int w = 0; w < stack.getChildren().size(); w++)
						{
							if(!(stack.getChildren().get(w) instanceof BarChart) && !(stack.getChildren().get(w) instanceof PieChart))
								stack.getChildren().clear();
						}
					}

					if(anchor.getChildren().get(j) instanceof HBox)
					{
						HBox box = (HBox)anchor.getChildren().get(j);
						Iterator<Node> iterator = box.getChildren().iterator();

						while(iterator.hasNext())
						{
							Node currentItem = iterator.next();

							if(currentItem instanceof Button)
							{
								iterator.remove();
							}
						}
					}
				}
			}
		}
		return grid;
	}

	/**
	 * opens a FileChooser dialog
	 */
	private void openFilechooser()
	{
		fileChooser = new FileChooser();
		fileChooser.setTitle("Exportieren als PNG Bild");

		ExtensionFilter filter = new ExtensionFilter("PNG Dateien (*.png)", "*.png");
		fileChooser.getExtensionFilters().add(filter);

		file = fileChooser.showSaveDialog(stage);
		if(file != null)
		{
			labelFile.setText(file.getAbsolutePath());
		}
	}
}