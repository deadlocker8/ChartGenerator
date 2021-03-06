package de.lww4.ui.controller;

import java.io.IOException;
import java.util.ArrayList;

import de.lww4.logic.DataFormats;
import de.lww4.logic.handler.DashboardHandler;
import de.lww4.logic.models.CSVTable;
import de.lww4.logic.models.ColumnTreeItem;
import de.lww4.logic.models.Dashboard;
import de.lww4.logic.models.chart.Chart;
import de.lww4.logic.models.enums.ChartType;
import de.lww4.logic.models.scale.Scale;
import de.lww4.logic.utils.AlertGenerator;
import de.lww4.ui.cells.ColumnTreeCell;
import de.lww4.ui.cells.ComboBoxScaleCell;
import de.lww4.ui.controller.subcontroller.SubControllerEditChart;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import logger.LogLevel;
import logger.Logger;

/**
 * controller class for generating a new chart or editing an existing one
 * @author Robert
 */
public class NewChartController
{
	@FXML private AnchorPane anchorPaneMain;
	@FXML private TextField textFieldTitle;
	@FXML private ColorPicker colorPicker;
	@FXML private TreeView<ColumnTreeItem> treeView;
	@FXML private StackPane stackPaneChart;
	@FXML private Button buttonSave;
	@FXML private Button buttonCancel;
	@FXML private HBox hboxChartTypes;
	@FXML private ComboBox<Scale> comboBoxScale;
	@FXML private ComboBox<Scale> comboBoxLegendScale;

	private Stage stage;
	private Controller controller;
	private ToggleGroup toggleGroupChartTypes;
	private Dashboard dashboard;
	private SubControllerEditChart subController;
	private boolean edit;
	private int position;
	private Chart chart;

	/**
	 * init method
	 * @param stage
	 * @param controller
	 * @param edit
	 * @param dashboard
	 * @param position
	 */
	public void init(Stage stage, Controller controller, boolean edit, Dashboard dashboard, int position)
	{
		this.stage = stage;
		this.controller = controller;
		this.dashboard = dashboard;
		this.edit = edit;
		this.position = position;	
				
		//if dashboard contains chart at given position then load chart data from database
		if(dashboard.getCells().get(position) != -1)
		{
			try
			{
				this.chart = controller.getDatabase().getChart(dashboard.getCells().get(position));
			}
			catch(Exception e)
			{
				Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
	
				AlertGenerator.showAlert(AlertType.ERROR, "Fehler", "", controller.getBundle().getString("error.load.data"), controller.getIcon(), stage, null, false);
				return;
			}
		}
		else
		{			
			chart = new Chart(-1, null, "", "", "", "", null, null, null);
		}

		stackPaneChart.setStyle("-fx-border-color: #212121; -fx-border-width: 2;");

		generatePreview(ChartType.BAR_HORIZONTAL, chart);

		//initialize toggle buttons for chart type
		toggleGroupChartTypes = new ToggleGroup();
		for(ChartType currentType : ChartType.values())
		{
			RadioButton currentRadioButton = new RadioButton(currentType.getName());
			currentRadioButton.setContentDisplay(ContentDisplay.BOTTOM);
			currentRadioButton.setToggleGroup(toggleGroupChartTypes);
			currentRadioButton.setUserData(currentType);
			hboxChartTypes.getChildren().add(currentRadioButton);
		}
		toggleGroupChartTypes.getToggles().get(0).setSelected(true);
		toggleGroupChartTypes.selectedToggleProperty().addListener(new ChangeListener<Toggle>()
		{
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue)
			{
				ChartType selectedType = (ChartType)newValue.getUserData();
				generatePreview(selectedType, chart);
				if(selectedType.equals(ChartType.PIE))
				{
					colorPicker.setDisable(true);
				}
				else
				{
					colorPicker.setDisable(false);
				}
				
				initTreeView(null);
			}
		});

		//initialize color picker
		colorPicker.setValue(Color.web("#508DC7"));
		colorPicker.valueProperty().addListener(new ChangeListener<Color>()
		{
			@Override
			public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue)
			{
				if(subController != null)
				{
					subController.updateChart(subController.getItemX(), subController.getItemY(), subController.getChart());
				}
			}
		});

		initTreeView(null);
		
		//initialize comboboxes for scale and legend scale 
		comboBoxScale.setId("comboBoxScale");
		comboBoxLegendScale.setId("comboBoxLegendScale");
		initComboBoxScales(comboBoxScale, controller.getScaleHandler().getScales());
		initComboBoxScales(comboBoxLegendScale, controller.getScaleHandler().getScales());

		//if in edit mode then prefill all inputs with data from existing chart
		if(edit)
		{					
			textFieldTitle.setText(chart.getTitle());
			colorPicker.setValue(chart.getColor());

			toggleGroupChartTypes.getToggles().get(chart.getType().getID()).setSelected(true);
			
			if(chart.getScale() != null)
			{
				comboBoxScale.setValue(chart.getScale());
			}
			
			if(chart.getLegendScale() != null)
			{
				comboBoxLegendScale.setValue(chart.getLegendScale());
			}

			ColumnTreeItem itemX = new ColumnTreeItem(chart.getTableUUID(), chart.getX(), false);
			ColumnTreeItem itemY = new ColumnTreeItem(chart.getTableUUID(), chart.getY(), false);
			generatePreview(chart.getType(), chart);				
			subController.updateChart(itemX, itemY, chart);				
		}
	}

	/**
	 * initializes the TreeView (for CSV column selection)
	 * @param tableUUID
	 */
	public void initTreeView(String tableUUID)
	{
		ArrayList<CSVTable> tables;
		try
		{
			tables = controller.getDatabase().getAllCSVTables();
		}
		catch(Exception e)
		{
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));

			tables = new ArrayList<>();
		}

		TreeItem<ColumnTreeItem> rootItem;
		if(tables.size() == 0)
		{
			rootItem = new TreeItem<ColumnTreeItem>(new ColumnTreeItem(null, "Keine Daten verfügbar", false));
		}
		else
		{
			rootItem = new TreeItem<ColumnTreeItem>(new ColumnTreeItem(null, "CSV Tabellen", false));

			//for every csv table existing in the database
			for(CSVTable currentTable : tables)
			{
				TreeItem<ColumnTreeItem> currentMainItem = new TreeItem<ColumnTreeItem>(new ColumnTreeItem(null, currentTable.getName(), false));

				//for every column name
				for(String currentColumn : currentTable.getColumnNames())
				{
					TreeItem<ColumnTreeItem> currentSubItem;
					if(tableUUID != null)
					{
						if(currentTable.getUuid().equals(tableUUID))
						{
							currentSubItem = new TreeItem<ColumnTreeItem>(new ColumnTreeItem(currentTable.getUuid(), currentColumn, true));
							currentMainItem.setExpanded(true);
						}
						else
						{
							currentSubItem = new TreeItem<ColumnTreeItem>(new ColumnTreeItem(currentTable.getUuid(), currentColumn, false));
						}
					}
					else
					{
						currentSubItem = new TreeItem<ColumnTreeItem>(new ColumnTreeItem(currentTable.getUuid(), currentColumn, true));
					}

					currentMainItem.getChildren().add(currentSubItem);
				}

				rootItem.getChildren().add(currentMainItem);
			}
		}
		rootItem.setExpanded(true);
		treeView.setRoot(rootItem);

		treeView.setCellFactory(new Callback<TreeView<ColumnTreeItem>, TreeCell<ColumnTreeItem>>()
		{
			@Override
			public TreeCell<ColumnTreeItem> call(TreeView<ColumnTreeItem> param)
			{
				ColumnTreeCell treeCell = new ColumnTreeCell();
				prepareDragAndDropForTreeCell(treeCell);
				return treeCell;
			}
		});
	}

	/**
	 * generate chart preview if all necessary inputs are given
	 * @param type
	 * @param chart
	 */
	private void generatePreview(ChartType type, Chart chart)
	{
		stackPaneChart.getChildren().clear();	
		//other fxml files with extra controllers are included for the chart display
		try
		{
			FXMLLoader fxmlLoader = null;

			switch(type)
			{
				case BAR_HORIZONTAL:
					fxmlLoader = new FXMLLoader(getClass().getResource("/de/lww4/ui/fxml/subfxml/SubEditBarChartHorizontalGUI.fxml"));
					break;
				case BAR_VERTICAL:
					fxmlLoader = new FXMLLoader(getClass().getResource("/de/lww4/ui/fxml/subfxml/SubEditBarChartVerticalGUI.fxml"));
					break;
				case PIE:
					fxmlLoader = new FXMLLoader(getClass().getResource("/de/lww4/ui/fxml/subfxml/SubEditPieChartGUI.fxml"));
					break;
				default:
					break;
			}

			Parent root = fxmlLoader.load();
			stackPaneChart.getChildren().add(root);
			subController = fxmlLoader.getController();
			subController.init(this, chart);
		}
		catch(IOException e)
		{
			Logger.log(LogLevel.DEBUG, Logger.exceptionToString(e));
		}
	}

	/**
	 * checks all inputs and saves the chart inthe database
	 */
	public void save()
	{
		String title = textFieldTitle.getText().trim();
		if(title.equals(""))
		{
			AlertGenerator.showAlert(AlertType.WARNING, "Warnung", "", controller.getBundle().getString("warning.name.empty.chart"), controller.getIcon(), stage, null, false);
			return;
		}

		if(!subController.isFilled())
		{
			AlertGenerator.showAlert(AlertType.WARNING, "Warnung", "", controller.getBundle().getString("warning.values.empty.chart"), controller.getIcon(), stage, null, false);
			return;
		}

		try
		{
			Scale scale = comboBoxScale.getValue();
			if(scale == null)
			{
				scale = new Scale(-1, null, null);
			}
			
			Scale legendScale = comboBoxLegendScale.getValue();
			if(legendScale == null)
			{
				legendScale = new Scale(-1, null, null);
			}

			if(edit)
			{
				int chartID = dashboard.getCells().get(position);
				Chart chart = new Chart(chartID, (ChartType)toggleGroupChartTypes.getSelectedToggle().getUserData(), textFieldTitle.getText().trim(), subController.getItemX().getText(), subController.getItemY().getText(), subController.getItemX().getTableUUID(), colorPicker.getValue(), scale, legendScale);
				controller.getDatabase().updateChart(chart);
				dashboard.getCells().set(position, chartID);
				controller.getDatabase().updateDashboard(dashboard);
			}
			else
			{
				Chart chart = new Chart(-1, (ChartType)toggleGroupChartTypes.getSelectedToggle().getUserData(), textFieldTitle.getText().trim(), subController.getItemX().getText(), subController.getItemY().getText(), subController.getItemX().getTableUUID(), colorPicker.getValue(), scale, legendScale);
				int chartID = controller.getDatabase().saveChart(chart);
				if(chartID != -1)
				{
					dashboard.getCells().set(position, chartID);
					controller.getDatabase().updateDashboard(dashboard);
				}
				else
				{
					throw new Exception("Can't save Chart in DB");
				}
			}

			controller.setDashboardHandler(new DashboardHandler(controller.getDatabase().getAllDashboards()));
			controller.setDashboard(dashboard);
			stage.close();
		}
		catch(Exception e)
		{
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));

			AlertGenerator.showAlert(AlertType.ERROR, "Fehler", "", controller.getBundle().getString("error.save.chart"), controller.getIcon(), stage, null, false);
		}
	}

	public void cancel()
	{
		stage.close();
	}

	/**
	 * prepares drag & drop functionality for the items in the TreeView
	 * @param row
	 */
	private void prepareDragAndDropForTreeCell(TreeCell<ColumnTreeItem> row)
	{
		row.setOnDragDetected(event -> {
			if(!row.isEmpty())
			{
				if(row.getItem().isDraggable())
				{
					Dragboard db = row.startDragAndDrop(TransferMode.ANY);

					//create little image that the user can see while dragging around
					SnapshotParameters snapshotParameters = new SnapshotParameters();
					snapshotParameters.setFill(Color.TRANSPARENT);

					VBox vboxSeledtedItems = new VBox();
					vboxSeledtedItems.setAlignment(Pos.TOP_LEFT);

					Label label = new Label(row.getItem().getText());
					label.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

					new Scene(label);
					db.setDragView(label.snapshot(snapshotParameters, null));

					ClipboardContent content = new ClipboardContent();
					content.put(DataFormats.DATAFORMAT_COLUMN_TREE_ITEM, row.getItem());
					db.setContent(content);
				}
			}

			event.consume();
		});
	}

	/**
	 * initializes given ComboBox with given Scales
	 * @param comboBox
	 * @param scales
	 */
	private void initComboBoxScales(ComboBox<Scale> comboBox, ArrayList<Scale> scales)
	{
		if(scales != null && scales.size() > 0)
		{
			/* Double.MIN_VALUE is a placeholder for an empty object
			 * this value will not be displayed but replaced with an empty String
			 * user can choose this item if he doesn't want a scale anymore
			 */
			comboBox.getItems().add(new Scale(Integer.MIN_VALUE, ""));
			comboBox.getItems().addAll(scales);
			comboBox.setCellFactory(new Callback<ListView<Scale>, ListCell<Scale>>()
			{
				@Override
				public ListCell<Scale> call(ListView<Scale> param)
				{
					return new ComboBoxScaleCell();
				}
			});
			comboBox.setButtonCell(new ComboBoxScaleCell());

			comboBox.valueProperty().addListener(new ChangeListener<Scale>()
			{
				@Override
				public void changed(ObservableValue<? extends Scale> observable, Scale oldValue, Scale newValue)
				{
					if(subController != null)
					{
						Scale selected = comboBox.getValue();
						
						if(comboBox.getId().equals(comboBoxScale.getId()))
						{	
							if(selected.getID() == Integer.MIN_VALUE)
							{
								chart.setScale(null);
							}
							else
							{
								chart.setScale(selected);	
							}							
						}	
						
						if(comboBox.getId().equals(comboBoxLegendScale.getId()))
						{
							if(selected.getID() == Integer.MIN_VALUE)
							{
								chart.setLegendScale(null);
							}
							else
							{
								chart.setLegendScale(selected);	
							}	
						}					
						
						subController.updateChart(subController.getItemX(), subController.getItemY(), subController.getChart());
					}
				}
			});
		}
		else
		{
			comboBox.setDisable(true);
		}
	}
	
	public Stage getStage()
	{
		return stage;
	}

	public Controller getController()
	{
		return controller;
	}

	public ColorPicker getColorPicker()
	{
		return colorPicker;
	}
}