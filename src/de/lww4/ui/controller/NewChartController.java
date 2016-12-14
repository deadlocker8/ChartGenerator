package de.lww4.ui.controller;

import java.io.IOException;
import java.util.ArrayList;

import de.lww4.logic.CSVTable;
import de.lww4.logic.Chart;
import de.lww4.logic.ChartType;
import de.lww4.logic.ColumnTreeItem;
import de.lww4.logic.Dashboard;
import de.lww4.logic.DashboardHandler;
import de.lww4.logic.DataFormats;
import de.lww4.logic.models.Scale.Scale;
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

	public void init(Stage stage, Controller controller, boolean edit, Dashboard dashboard, int position)
	{
		this.stage = stage;
		this.controller = controller;
		this.dashboard = dashboard;
		this.edit = edit;
		this.position = position;

		stackPaneChart.setStyle("-fx-border-color: #212121; -fx-border-width: 2;");

		generatePreview(ChartType.BAR_HORIZONTAL);

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
				generatePreview(selectedType);
				if(selectedType.equals(ChartType.PIE))
				{
					colorPicker.setDisable(true);
				}
				else
				{
					colorPicker.setDisable(false);
				}
			}
		});

		colorPicker.setValue(Color.web("#508DC7"));
		colorPicker.valueProperty().addListener(new ChangeListener<Color>()
		{
			@Override
			public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue)
			{
				if(subController != null)
				{
					updatePreview(subController.getItemX(), subController.getItemY());
				}
			}
		});

		initTreeView(null);
		initComboBoxScales(comboBoxScale, controller.getScaleHandler().getScales());
		initComboBoxScales(comboBoxLegendScale, controller.getScaleHandler().getScales());

		if(edit)
		{
			try
			{
				Chart chart = controller.getDatabase().getChart(dashboard.getCells().get(position));
				textFieldTitle.setText(chart.getTitle());
				colorPicker.setValue(chart.getColor());

				toggleGroupChartTypes.getToggles().get(chart.getType().getID()).setSelected(true);
				
				if(chart.getScale() != null)
				{
					comboBoxScale.setValue(chart.getScale());
				}

				ColumnTreeItem itemX = new ColumnTreeItem(chart.getTableUUID(), chart.getX(), false);
				ColumnTreeItem itemY = new ColumnTreeItem(chart.getTableUUID(), chart.getY(), false);
				generatePreview(chart.getType());
				//TODO use scale here
				updatePreview(itemX, itemY);
			}
			catch(Exception e)
			{
				Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));

				AlertGenerator.showAlert(AlertType.ERROR, "Fehler", "", controller.getBundle().getString("error.load.data"), controller.getIcon(), true);
				return;
			}
		}
	}

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

			for(CSVTable currentTable : tables)
			{
				TreeItem<ColumnTreeItem> currentMainItem = new TreeItem<ColumnTreeItem>(new ColumnTreeItem(null, currentTable.getName(), false));

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

	private void generatePreview(ChartType type)
	{
		stackPaneChart.getChildren().clear();

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
			subController.init(this);

		}
		catch(IOException e)
		{
			Logger.log(LogLevel.DEBUG, Logger.exceptionToString(e));
		}
	}

	private void updatePreview(ColumnTreeItem itemX, ColumnTreeItem itemY)
	{
		subController.updateChart(itemX, itemY);
	}

	public void save()
	{
		String title = textFieldTitle.getText();
		if(title == null || title.equals(""))
		{
			AlertGenerator.showAlert(AlertType.WARNING, "Warnung", "", controller.getBundle().getString("warning.name.empty.chart"), controller.getIcon(), true);
			return;
		}

		if(!subController.isFilled())
		{
			AlertGenerator.showAlert(AlertType.WARNING, "Warnung", "", controller.getBundle().getString("warning.values.empty.chart"), controller.getIcon(), true);
			return;
		}

		try
		{
			Scale scale = comboBoxScale.getValue();
			if(scale == null)
			{
				scale = new Scale(-1, null, null);
			}
			
			Scale legenScale = comboBoxLegendScale.getValue();
			if(legenScale == null)
			{
				legenScale = new Scale(-1, null, null);
			}

			if(edit)
			{
				int chartID = dashboard.getCells().get(position);
				Chart chart = new Chart(chartID, (ChartType)toggleGroupChartTypes.getSelectedToggle().getUserData(), textFieldTitle.getText(), subController.getItemX().getText(), subController.getItemY().getText(), subController.getItemX().getTableUUID(), colorPicker.getValue(), scale, legenScale);
				controller.getDatabase().updateChart(chart);
				dashboard.getCells().set(position, chartID);
				controller.getDatabase().updateDashboard(dashboard);
			}
			else
			{
				Chart chart = new Chart(-1, (ChartType)toggleGroupChartTypes.getSelectedToggle().getUserData(), textFieldTitle.getText(), subController.getItemX().getText(), subController.getItemY().getText(), subController.getItemX().getTableUUID(), colorPicker.getValue(), scale, legenScale);
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

			AlertGenerator.showAlert(AlertType.ERROR, "Fehler", "", controller.getBundle().getString("error.save.chart"), controller.getIcon(), true);
		}
	}

	public void cancel()
	{
		stage.close();
	}

	private void prepareDragAndDropForTreeCell(TreeCell<ColumnTreeItem> row)
	{
		row.setOnDragDetected(event -> {
			if(!row.isEmpty())
			{
				if(row.getItem().isDragable())
				{
					Dragboard db = row.startDragAndDrop(TransferMode.ANY);

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

	private void initComboBoxScales(ComboBox<Scale> comboBox, ArrayList<Scale> scales)
	{
		if(scales != null && scales.size() > 0)
		{
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
					// TODO update chart
					// TODO add param scale to updateChart method

				}
			});
		}
		else
		{
			comboBox.setDisable(true);
		}
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