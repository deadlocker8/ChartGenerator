package de.lww4.ui.controller;

import de.lww4.logic.Chart;
import de.lww4.logic.Dashboard;
import de.lww4.logic.DashboardHandler;
import de.lww4.logic.DatabaseHandler;
import de.lww4.logic.chartGenerators.BarChartHorizontalGenerator;
import de.lww4.logic.chartGenerators.BarChartVerticalGenerator;
import de.lww4.logic.chartGenerators.PieChartGenerator;
import de.lww4.logic.utils.AlertGenerator;
import fontAwesome.FontIcon;
import fontAwesome.FontIconType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import logger.LogLevel;
import logger.Logger;
import tools.Worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Main Controller Class
 *
 * @author Robert
 */
public class Controller
{
	@FXML private AnchorPane anchorPaneMain;
	@FXML private Label labelTitle;
	@FXML private MenuItem importCSVMenuItem;

	private Stage stage;
	private Image icon = new Image("de/lww4/resources/icon.png");
	private final ResourceBundle bundle = ResourceBundle.getBundle("de/lww4/main/", Locale.GERMANY);
	private GridPane gridPane;
	private DatabaseHandler database;
	private DashboardHandler dashboardHandler;
	private Dashboard currentDashboard;

	/**
	 * init method
	 * 
	 * @param stage
	 *            Stage
	 */
	public void init(Stage stage)
	{
		this.stage = stage;

		stage.setOnCloseRequest(new EventHandler<WindowEvent>()
		{
			public void handle(WindowEvent event)
			{
				Worker.shutdown();
				System.exit(0);
			}
		});

		gridPane = new GridPane();
		gridPane.setGridLinesVisible(false);
		gridPane.setHgap(20);
		gridPane.setVgap(20);
		gridPane.getColumnConstraints().clear();
		double xPercentage = 1.0 / 3;
		for(int i = 0; i < 3; i++)
		{
			ColumnConstraints c = new ColumnConstraints();
			c.setPercentWidth(xPercentage * 100);
			gridPane.getColumnConstraints().add(c);
		}

		gridPane.getRowConstraints().clear();
		double yPercentage = 1.0 / 2;
		for(int i = 0; i < 2; i++)
		{
			RowConstraints c = new RowConstraints();
			c.setPercentHeight(yPercentage * 100);
			gridPane.getRowConstraints().add(c);
		}

		anchorPaneMain.getChildren().remove(gridPane);
		anchorPaneMain.getChildren().add(gridPane);
		AnchorPane.setTopAnchor(gridPane, 80.0);
		AnchorPane.setRightAnchor(gridPane, 25.0);
		AnchorPane.setBottomAnchor(gridPane, 25.0);
		AnchorPane.setLeftAnchor(gridPane, 25.0);

		FontIcon iconEdit = new FontIcon(FontIconType.PENCIL);
		iconEdit.setSize(18);
		labelTitle.setGraphic(iconEdit);
		labelTitle.setOnMouseClicked(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event)
			{
				if(event.getButton() == MouseButton.PRIMARY)
				{
					TextInputDialog dialog = new TextInputDialog(labelTitle.getText());
					dialog.setTitle("Dashboardname");
					dialog.setHeaderText("");
					dialog.setContentText("Neuer Dashboardname:");
					Stage dialogStage = (Stage)dialog.getDialogPane().getScene().getWindow();
					dialogStage.getIcons().add(icon);
					dialogStage.centerOnScreen();

					checkTextInputTitle(dialog);
				}
			}
		});

		try
		{
			database = new DatabaseHandler();
			dashboardHandler = new DashboardHandler(database.getAllDashboards());			
			
			int lastID = database.getLastDashboard();		
			if(lastID <= 0)
			{
				setDashboard(new Dashboard(""));	
			}
			else
			{
				setDashboard(database.getDashboard(lastID));	
			}

			initDashboard();
		}
		catch(Exception e)
		{
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));

			AlertGenerator.showAlert(AlertType.ERROR, "Fehler", "", bundle.getString("error.load.database"), icon, true);
		}
	}

	/**
	 * initalizes label for dashboard title and gridPane
	 */
	private void initDashboard()
	{
		if(currentDashboard.getName() == null || currentDashboard.getName().equals(""))
		{
			labelTitle.setText("Unbenanntes Dashboard");
		}
		else
		{
			labelTitle.setText(currentDashboard.getName());
		}

		initGridPane();
	}

	/**
	 * handles menuItem "import CSV"
	 */
	public void importCSVMenuItem()
	{
		try
		{
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/de/lww4/ui/fxml/ImportCSVDialog.fxml"));
			Parent root = (Parent)fxmlLoader.load();
			Stage newStage = new Stage();
			newStage.initOwner(stage);
			newStage.initModality(Modality.APPLICATION_MODAL);
			newStage.setTitle("Import CSV");
			newStage.setScene(new Scene(root));
			newStage.getIcons().add(icon);
			newStage.setResizable(false);
			ImportCSVController importCSVController = fxmlLoader.getController();
			importCSVController.init(newStage, icon, this);
			newStage.show();

		}
		catch(IOException io)
		{
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(io));
		}
	}

	/**
	 * handles menuItem "new Dashboard"
	 */
	public void newDashboardMenuItem()
	{
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Neues Dashboard");
		dialog.setHeaderText("");
		dialog.setContentText("Dashboardname:");
		Stage dialogStage = (Stage)dialog.getDialogPane().getScene().getWindow();
		dialogStage.getIcons().add(icon);
		dialogStage.centerOnScreen();

		Optional<String> result = dialog.showAndWait();
		if(result.isPresent())
		{
			String name = result.get();
			name.trim();
			if(name.equals(""))
			{
				AlertGenerator.showAlert(AlertType.WARNING, "Warnung", "", bundle.getString("warning.name.empty.dashboard"), icon, true);

				newDashboardMenuItem();
			}
			else
			{
				if(dashboardHandler.isNameAlreadyInUse(name))
				{
					AlertGenerator.showAlert(AlertType.WARNING, "Warnung", "", bundle.getString("warning.name.dashboard.alreadyinuse"), icon, true);

					newDashboardMenuItem();
				}
				else
				{
					try
					{
						database.saveDashboard(new Dashboard(name));
						dashboardHandler = new DashboardHandler(database.getAllDashboards());
						setDashboard(dashboardHandler.getDashboards().get(dashboardHandler.getDashboards().size() - 1));
					}
					catch(Exception e)
					{
						Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));

						AlertGenerator.showAlert(AlertType.ERROR, "Fehler", "",  bundle.getString("error.save"), icon, true);
					}
				}
			}
		}
	}

	/**
	 * handles menuItem "select Dashboard"
	 */
	public void selectDashboardMenuItem()
	{
		try
		{
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/de/lww4/ui/fxml/SelectDashboardGUI.fxml"));

			Parent root = (Parent)fxmlLoader.load();
			Stage newStage = new Stage();
			newStage.setScene(new Scene(root, 500, 400));
			newStage.setMinHeight(400);
			newStage.setMinWidth(500);
			newStage.initOwner(stage);
			newStage.setTitle("Dashboard laden");
			newStage.getScene().getStylesheets().add("de/lww4/main/style.css");

			newStage.getIcons().add(icon);
			SelectDashboardController newController = fxmlLoader.getController();
			newController.init(newStage, this);

			newStage.initModality(Modality.APPLICATION_MODAL);
			newStage.setResizable(true);
			newStage.show();
		}
		catch(IOException io)
		{
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(io));
		}
	}

	/**
	 * handles menuItem "export Dashboard"
	 */
	public void exportDashboardMenuItem()
	{
		// TODO
	}

	/**
	 * checks if the input is empty
	 * 
	 * @param dialog
	 *            TextInputDialog
	 */
	private void checkTextInputTitle(TextInputDialog dialog)
	{
		Optional<String> result = dialog.showAndWait();
		if(result.isPresent())
		{
			String name = result.get();
			name.trim();
			if(name.equals(""))
			{
				AlertGenerator.showAlert(AlertType.WARNING, "Warnung", "", bundle.getString("warning.name.empty.dashboard"), icon, true);

				checkTextInputTitle(dialog);
			}
			else
			{
				if(dashboardHandler.isNameAlreadyInUse(name))
				{
					AlertGenerator.showAlert(AlertType.WARNING, "Warnung", "", bundle.getString("warning.name.alreadyinuse"), icon, true);

					checkTextInputTitle(dialog);
				}
				else
				{
					labelTitle.setText(result.get());
					currentDashboard.setName(name);

					try
					{
						// Dashboard is not existing in DB ("Unbenanntes Dashboard")
						if(currentDashboard.getID() == -1)
						{
							database.saveDashboard(currentDashboard);
						}
						else
						{
							database.updateDashboard(currentDashboard);
						}

						dashboardHandler = new DashboardHandler(database.getAllDashboards());
					}
					catch(Exception e)
					{
						Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));

						AlertGenerator.showAlert(AlertType.ERROR, "ERROR", "", bundle.getString("error.save"), icon, true);
					}
				}
			}
		}
	}

	/**
	 * inits the dashboard gridPane
	 * 
	 * @param empty
	 *            boolean
	 */
	private void initGridPane()
	{
		gridPane.getChildren().clear();
		boolean empty = true;

		for(int i = 0; i < 6; i++)
		{
			final int position = i;

			if(currentDashboard.getCells().size() > 0)
			{
				if(currentDashboard.getCells().get(i) == -1)
				{
					empty = true;
				}
				else
				{
					empty = false;
				}
			}

			Chart chart = null;
			if(!empty)
			{
				try
				{
					chart = database.getChart(currentDashboard.getCells().get(i));
				}
				catch(Exception e)
				{
					Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));

					AlertGenerator.showAlert(AlertType.ERROR, "ERROR", "", bundle.getString("error.load.data"), icon, true);
				}
			}

			AnchorPane currentAnchorPane = new AnchorPane();
			currentAnchorPane.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

			HBox hbox = new HBox();
			hbox.setSpacing(5);
			hbox.setAlignment(Pos.CENTER);

			Label labelChartTitle = new Label();
			if(chart == null)
			{
				labelChartTitle.setText("Diagramm " + (i + 1));
			}
			else
			{
				labelChartTitle.setText(chart.getTitle());
			}

			labelChartTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 18;");
			labelChartTitle.setAlignment(Pos.CENTER);
			labelChartTitle.setMaxWidth(Double.MAX_VALUE);
			hbox.getChildren().add(labelChartTitle);
			HBox.setHgrow(labelChartTitle, Priority.ALWAYS);

			Button buttonEdit = new Button();
			buttonEdit.setStyle("-fx-background-color: transparent");
			FontIcon iconEdit = new FontIcon(FontIconType.PENCIL);
			iconEdit.setSize(18);
			buttonEdit.setGraphic(iconEdit);
			hbox.getChildren().add(buttonEdit);
			buttonEdit.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(ActionEvent event)
				{
					addChart(position, true);
				}
			});

			Button buttonDelete = new Button();
			buttonDelete.setStyle("-fx-background-color: transparent");
			FontIcon iconDelete = new FontIcon(FontIconType.TRASH);
			iconDelete.setSize(18);
			buttonDelete.setGraphic(iconDelete);
			hbox.getChildren().add(buttonDelete);
			buttonDelete.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(ActionEvent event)
				{
					deleteChart(position);
				}
			});

			Button buttonExport = new Button();
			buttonExport.setStyle("-fx-background-color: transparent");
			FontIcon iconExport = new FontIcon(FontIconType.DOWNLOAD);
			iconExport.setSize(18);
			buttonExport.setGraphic(iconExport);
			hbox.getChildren().add(buttonExport);
			buttonExport.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(ActionEvent event)
				{
					exportChart(position);
				}
			});

			currentAnchorPane.getChildren().add(hbox);
			AnchorPane.setTopAnchor(hbox, 5.0);
			AnchorPane.setRightAnchor(hbox, 5.0);
			AnchorPane.setLeftAnchor(hbox, 5.0);

			StackPane currentStackPane = new StackPane();
			currentAnchorPane.getChildren().add(currentStackPane);
			AnchorPane.setTopAnchor(currentStackPane, 40.0);
			AnchorPane.setRightAnchor(currentStackPane, 10.0);
			AnchorPane.setBottomAnchor(currentStackPane, 10.0);
			AnchorPane.setLeftAnchor(currentStackPane, 10.0);

			if(empty)
			{
				buttonEdit.setDisable(true);
				buttonDelete.setDisable(true);
				buttonExport.setDisable(true);

				FontIcon iconAdd = new FontIcon(FontIconType.PLUS);
				iconAdd.setSize(25);

				Button buttonAdd = new Button();
				buttonAdd.setGraphic(iconAdd);
				buttonAdd.setStyle("-fx-background-color: transparent");

				buttonAdd.setOnAction(new EventHandler<ActionEvent>()
				{
					@Override
					public void handle(ActionEvent event)
					{
						addChart(position, false);
					}
				});

				currentStackPane.getChildren().add(buttonAdd);
			}
			else
			{
				currentStackPane.getChildren().clear();

				try
				{					
					ArrayList<Double> xValues;
					ArrayList<Double> yValues;
					switch(chart.getType())
					{
						case BAR_HORIZONTAL:
							xValues = database.getCSVColumn(chart.getTableUUID(), chart.getX());
							yValues = database.getCSVColumn(chart.getTableUUID(), chart.getY());
							BarChartHorizontalGenerator generatorHorizontal = new BarChartHorizontalGenerator(chart.getX(), chart.getY(), xValues, yValues, chart.getColor());
							currentStackPane.getChildren().add(generatorHorizontal.generate());
							break;
						case BAR_VERTICAL:
							xValues = database.getCSVColumn(chart.getTableUUID(), chart.getX());
							yValues = database.getCSVColumn(chart.getTableUUID(), chart.getY());
							BarChartVerticalGenerator generatorVertical = new BarChartVerticalGenerator(chart.getX(), chart.getY(), xValues, yValues, chart.getColor());
							currentStackPane.getChildren().add(generatorVertical.generate());
							break;
						case PIE:
							xValues = database.getCSVColumn(chart.getTableUUID(), chart.getX());
							PieChartGenerator generatorPie = new PieChartGenerator(chart.getX(), xValues);
							currentStackPane.getChildren().add(generatorPie.generate());
							break;

						default:
							break;
					}
				}
				catch(Exception e)
				{
					Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));

					AlertGenerator.showAlert(AlertType.ERROR, "ERROR", "", bundle.getString("error.load.data"), icon, true);
				}
			}

			if(i < 3)
			{
				gridPane.add(currentAnchorPane, i, 0);
			}
			else
			{
				gridPane.add(currentAnchorPane, i % 3, 1);
			}
		}
	}

	/**
	 * opens a new UI for new chart generation
	 * 
	 * @param position
	 *            int - position in dashboard [0,5]
	 * @param edit
	 *            boolean - editing already existing chart at given position
	 */
	private void addChart(int position, boolean edit)
	{	
		if(currentDashboard.getName().equals(""))
		{
			AlertGenerator.showAlert(AlertType.WARNING, "Warnung", "", bundle.getString("warning.name.dashboard.first"), icon, true);
			return;
		}

		try
		{
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/de/lww4/ui/fxml/NewChartGUI.fxml"));

			Parent root = (Parent)fxmlLoader.load();
			Stage newStage = new Stage();
			newStage.setScene(new Scene(root, 800, 600));
			newStage.setMinHeight(600);
			newStage.setMinWidth(700);
			newStage.initOwner(stage);

			newStage.getIcons().add(icon);
			NewChartController newController = fxmlLoader.getController();
			if(edit)
			{
				newStage.setTitle("Diagramm bearbeiten");
			}
			else
			{
				newStage.setTitle("Neues Diagramm");
			}
			newController.init(newStage, this, edit, currentDashboard, position);
			newStage.initModality(Modality.APPLICATION_MODAL);
			newStage.setResizable(true);
			newStage.show();
		}
		catch(IOException e1)
		{
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e1));
		}
	}

	/**
	 * deletes chart at given position after confirmation dialog
	 * 
	 * @param position
	 */
	private void deleteChart(int position)
	{
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Diagramm löschen");
		alert.setHeaderText("");
		alert.setContentText("Möchten Sie dieses Diagramm wirklich unwiderruflich löschen?");
		Stage dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
		dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
		dialogStage.getIcons().add(icon);
		dialogStage.centerOnScreen();

		Optional<ButtonType> result = alert.showAndWait();
		if(result.get() == ButtonType.OK)
		{
			currentDashboard.getCells().set(position, -1);
			try
			{
				database.updateDashboard(currentDashboard);
				dashboardHandler = new DashboardHandler(database.getAllDashboards());
				initDashboard();
			}
			catch(Exception e)
			{
				Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));

				AlertGenerator.showAlert(AlertType.ERROR, "Fehler", "", bundle.getString("error.delete.chart"), icon, true);
			}
		}
	}

	/**
	 * exports chart at given position
	 * 
	 * @param position
	 */
	private void exportChart(int position)
	{

	}

	/**
	 * @param dashboard
	 *            Dashboard
	 */
	public void setDashboard(Dashboard dashboard)
	{
		this.currentDashboard = dashboard;
		try
		{
			database.updateLastDashboard(currentDashboard.getID());
		}
		catch(Exception e)
		{
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
		}
		initDashboard();
	}

	/**
	 * deletes dashboard with given ID in database
	 * 
	 * @param ID
	 *            int
	 */
	public void deleteDashboard(int ID)
	{
		try
		{
			database.deleteDashboard(ID);
			dashboardHandler = new DashboardHandler(database.getAllDashboards());
		}
		catch(Exception e)
		{
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));

			AlertGenerator.showAlert(AlertType.ERROR, "Fehler", "", bundle.getString("error.delete.dashboard"), icon, true);
		}
	}

	/**
	 * opens about dialog
	 */
	public void about()
	{
		AlertGenerator.showAlert(AlertType.INFORMATION, "über " + bundle.getString("app.name"), bundle.getString("app.name"), "Version:     " + bundle.getString("version.name") + "\r\nDatum:      " + bundle.getString("version.date") + "\r\nAutoren:    " + bundle.getString("author") + "\r\n", icon,
				true);
	}

	public DatabaseHandler getDatabase()
	{
		return database;
	}

	public DashboardHandler getDashboardHandler()
	{
		return dashboardHandler;
	}

	public void setDashboardHandler(DashboardHandler dashboardHandler)
	{
		this.dashboardHandler = dashboardHandler;
	}

	public Image getIcon()
	{
		return icon;
	}
	
	public ResourceBundle getBundle()
	{
		return bundle;
	}
}