package de.lww4.ui;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import de.lww4.logic.Dashboard;
import de.lww4.logic.DashboardHandler;
import de.lww4.logic.DatabaseHandler;
import fontAwesome.FontIcon;
import fontAwesome.FontIconType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import logger.LogLevel;
import logger.Logger;
import tools.Worker;

public class Controller
{
	@FXML private AnchorPane anchorPaneMain;
	@FXML private Label labelTitle;
	@FXML private MenuItem importCSVMenuItem;

	public Stage stage;
	public Image icon = new Image("de/lww4/resources/icon.png");
	public final ResourceBundle bundle = ResourceBundle.getBundle("de/lww4/main/", Locale.GERMANY);
	private GridPane gridPane;
	private DatabaseHandler database;
	public DashboardHandler dashboardHandler;
	private Dashboard currentDashboard;

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
			//TODO select last opened dashboard
			currentDashboard = new Dashboard("");		
			
			initDashboard();
		}
		catch(Exception e)
		{
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Fehler");
			alert.setHeaderText("");
			alert.setContentText("Beim Laden der Datenbank ist ein Fehler aufgetreten.");
			Stage dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
			dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
			dialogStage.getIcons().add(icon);
			dialogStage.centerOnScreen();
			alert.showAndWait();
		}	
	}
	
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
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ImportCSVDialog.fxml"));
			Parent root = (Parent)fxmlLoader.load();
			Stage newStage = new Stage();
			newStage.initOwner(stage);
			newStage.initModality(Modality.APPLICATION_MODAL);
			newStage.setTitle("Import CSV");
			newStage.setScene(new Scene(root));
			newStage.getIcons().add(icon);
			newStage.setResizable(false);
			ImportCSVController importCSVController = fxmlLoader.getController();
			importCSVController.init(newStage, icon);
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
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Warnung");
				alert.setHeaderText("");
				alert.setContentText("Das Feld für den Dashboardnamen darf nicht leer sein.");
				Stage dialogStage2 = (Stage)alert.getDialogPane().getScene().getWindow();
				dialogStage2 = (Stage)alert.getDialogPane().getScene().getWindow();
				dialogStage2.getIcons().add(icon);
				dialogStage2.centerOnScreen();
				alert.showAndWait();

				newDashboardMenuItem();			
			}
			else
			{
				if(dashboardHandler.isNameAlreadyInUse(name))
				{
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Warnung");
					alert.setHeaderText("");
					alert.setContentText("Dieser Name wird bereits verwendet.\nBitte verwenden Sie einen anderen Namen.");
					Stage dialogStage2 = (Stage)alert.getDialogPane().getScene().getWindow();
					dialogStage2 = (Stage)alert.getDialogPane().getScene().getWindow();
					dialogStage2.getIcons().add(icon);
					dialogStage2.centerOnScreen();
					alert.showAndWait();

					newDashboardMenuItem();	
				}
				else
				{
					try
					{
						database.saveDashboard(new Dashboard(name));
						dashboardHandler = new DashboardHandler(database.getAllDashboards());
						setDashboard(dashboardHandler.getDashboards().get(dashboardHandler.getDashboards().size()-1));
					}
					catch(Exception e)
					{
						Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
						
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Fehler");
						alert.setHeaderText("");
						alert.setContentText("Beim Speichern ist ein Fehler aufgetreten.");
						Stage dialogStage2 = (Stage)alert.getDialogPane().getScene().getWindow();
						dialogStage2 = (Stage)alert.getDialogPane().getScene().getWindow();
						dialogStage2.getIcons().add(icon);
						dialogStage2.centerOnScreen();
						alert.showAndWait();
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
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SelectDashboardGUI.fxml"));

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
	 * @param dialog TextInputDialog
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
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Warnung");
				alert.setHeaderText("");
				alert.setContentText("Das Feld für den Dashboardnamen darf nicht leer sein.");
				Stage dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
				dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
				dialogStage.getIcons().add(icon);
				dialogStage.centerOnScreen();
				alert.showAndWait();

				checkTextInputTitle(dialog);			
			}
			else
			{
				if(dashboardHandler.isNameAlreadyInUse(name))
				{
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Warnung");
					alert.setHeaderText("");
					alert.setContentText("Dieser Name wird bereits verwendet.\nBitte verwenden Sie einen anderen Namen.");
					Stage dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
					dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
					dialogStage.getIcons().add(icon);
					dialogStage.centerOnScreen();
					alert.showAndWait();
					
					checkTextInputTitle(dialog);
				}
				else
				{
					labelTitle.setText(result.get());
					currentDashboard.setName(name);
					
					try
					{						
						// Dashboard is not existing in DB ("Unbenanntes Dashboard")
						if(currentDashboard.getId() == -1)
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
						
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Fehler");
						alert.setHeaderText("");
						alert.setContentText("Beim Speichern ist ein Fehler aufgetreten.");
						Stage dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
						dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
						dialogStage.getIcons().add(icon);
						dialogStage.centerOnScreen();
						alert.showAndWait();
					}
				}			
			}
		}
	}

	/**
	 * inits the dashboard gridPane
	 * @param empty boolean
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

			AnchorPane currentAnchorPane = new AnchorPane();
			currentAnchorPane.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

			HBox hbox = new HBox();
			hbox.setSpacing(5);
			hbox.setAlignment(Pos.CENTER);

			Label labelChartTitle = new Label("Diagramm " + (i + 1));
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
	 * @param position int - position in dashboard [0,5]
	 * @param edit boolean - editing already existing chart at given position
	 */
	private void addChart(int position, boolean edit)
	{
		try
		{
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/de/lww4/ui/NewChartGUI.fxml"));

			Parent root = (Parent)fxmlLoader.load();
			Stage newStage = new Stage();
			newStage.setScene(new Scene(root, 800, 600));
			newStage.setMinHeight(400);
			newStage.setMinWidth(500);
			newStage.initOwner(stage);

			newStage.getIcons().add(icon);
			NewChartController newController = fxmlLoader.getController();
			if(edit)
			{
				newStage.setTitle("Diagramm bearbeiten");
				newController.init(newStage, this, edit, currentDashboard, position);
			}
			else
			{
				newStage.setTitle("Neues Diagramm");
				newController.init(newStage, this, edit, null, -1);
			}

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
			// TODO delete Chart from dashboard
		}
	}
	
	/**
	 * exports chart at given position
	 * @param position
	 */
	private void exportChart(int position)
	{
		
	}
	
	/** 
	 * @param dashboard Dashboard
	 */
	public void setDashboard(Dashboard dashboard)
	{
		this.currentDashboard = dashboard;
		initDashboard();
	}
	
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
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Fehler");
			alert.setHeaderText("");
			alert.setContentText("Beim Löschen ist ein Fehler aufgetreten.");
			Stage dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
			dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
			dialogStage.getIcons().add(icon);
			dialogStage.centerOnScreen();
			alert.showAndWait();
		}
	}

	/**
	 * opens about dialog
	 */
	public void about()
	{
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("über " + bundle.getString("app.name"));
		alert.setHeaderText(bundle.getString("app.name"));
		alert.setContentText("Version:     " + bundle.getString("version.name") + "\r\nDatum:      " + bundle.getString("version.date") + "\r\nAutoren:    " + bundle.getString("author") + "\r\n");
		Stage dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
		dialogStage.getIcons().add(icon);
		dialogStage.centerOnScreen();
		alert.showAndWait();
	}
}