package de.lww4.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import de.lww4.logic.CSVTable;
import de.lww4.logic.Chart;
import de.lww4.logic.ChartType;
import de.lww4.logic.ColumnTreeItem;
import de.lww4.logic.Dashboard;
import de.lww4.logic.DashboardHandler;
import de.lww4.logic.DataFormats;
import de.lww4.ui.cells.ColumnTreeCell;
import de.lww4.ui.controller.subcontroller.SubControllerEditChart;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
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

	public Stage stage;
	public Controller controller;
	public Image icon = new Image("de/lww4/resources/icon.png");
	public final ResourceBundle bundle = ResourceBundle.getBundle("de/lww4/main/", Locale.GERMANY);
	private ToggleGroup toggleGroupChartTypes;
	public boolean edit;
	private Dashboard dashboard;
	private int position;
	private SubControllerEditChart subController;

	public void init(Stage stage, Controller controller, boolean edit, Dashboard dashboard, int position)
	{
		this.stage = stage;
		this.controller = controller;
		this.edit = edit;
		this.dashboard = dashboard;
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
				generatePreview((ChartType)newValue.getUserData());
			}
		});

		colorPicker.setValue(Color.web("#508DC7"));
		colorPicker.valueProperty().addListener(new ChangeListener<Color>()
		{
			@Override
			public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue)
			{
				updatePreview(null, null);
			}
		});

		initTreeView();

		if(edit)
		{
			try
			{
				Chart chart = controller.database.getChart(dashboard.getCells().get(position));
				textFieldTitle.setText(chart.getTitle());
				colorPicker.setValue(chart.getColor());
			
				toggleGroupChartTypes.getToggles().get(chart.getType().getID()).setSelected(true);

				ColumnTreeItem itemX = new ColumnTreeItem(chart.getTableUUID(), chart.getX(), false);
				ColumnTreeItem itemY = new ColumnTreeItem(chart.getTableUUID(), chart.getY(), false);
				generatePreview(chart.getType());
				updatePreview(itemX, itemY);
			}
			catch(Exception e)
			{
				// ERRORHANDLING
				Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
			}
		}
	}

	private void initTreeView()
	{
		ArrayList<CSVTable> tables;
		try
		{
			tables = controller.database.getAllCSVTables();
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
					TreeItem<ColumnTreeItem> currentSubItem = new TreeItem<ColumnTreeItem>(new ColumnTreeItem(currentTable.getUuid(), currentColumn, true));
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

			Parent root = (Parent)fxmlLoader.load();
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
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warnung");
			alert.setHeaderText("");
			alert.setContentText("Bitte geben Sie einen Namen für das Diagramm an.");
			Stage dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
			dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
			dialogStage.getIcons().add(icon);
			dialogStage.centerOnScreen();
			alert.showAndWait();
			return;
		}

		if(!subController.isFilled())
		{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warnung");
			alert.setHeaderText("");
			alert.setContentText("Bitte wählen Sie Werte für das Diagramm aus.");
			Stage dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
			dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
			dialogStage.getIcons().add(icon);
			dialogStage.centerOnScreen();
			alert.showAndWait();
			return;
		}

		Chart chart = new Chart(-1, (ChartType)toggleGroupChartTypes.getSelectedToggle().getUserData(), textFieldTitle.getText(), subController.getItemX().getText(), subController.getItemY().getText(), subController.getItemX().getTableUUID(), colorPicker.getValue());
		try
		{
			if(edit)
			{
				controller.database.updateChart(chart);
			}
			else
			{
				int chartID = controller.database.saveChart(chart);
				if(chartID != -1)
				{
					dashboard.getCells().set(position, chartID);
					controller.database.updateDashboard(dashboard);
				}
				else
				{
					throw new Exception("Can't save Chart in DB");
				}				
			}
			
			controller.dashboardHandler = new DashboardHandler(controller.database.getAllDashboards());
			controller.setDashboard(dashboard);
			stage.close();
		}
		catch(

		Exception e)
		{
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));

			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Fehler");
			alert.setHeaderText("");
			alert.setContentText("Beim Speichern des Diagramms ist ein Fehler aufgetreten.");
			Stage dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
			dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
			dialogStage.getIcons().add(icon);
			dialogStage.centerOnScreen();
			alert.showAndWait();
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
}