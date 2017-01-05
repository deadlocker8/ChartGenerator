package de.lww4.logic.handler;

import de.lww4.logic.Importer;
import de.lww4.logic.models.CSVTable;
import de.lww4.logic.models.Dashboard;
import de.lww4.logic.models.chart.Chart;
import de.lww4.logic.models.chart.ChartSetItem;
import de.lww4.logic.models.enums.ChartType;
import de.lww4.logic.models.scale.Scale;
import de.lww4.logic.utils.JsonHelper;
import javafx.scene.paint.Color;
import logger.LogLevel;
import logger.Logger;
import tools.PathUtils;

import java.io.Console;
import java.io.File;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class DatabaseHandler
{
	//region SetUp
	private String path = PathUtils.getOSindependentPath() + "ChartGenerator/db.sqlite";

    /**
     * checks if the sqlite file is there and creates a new one if needed
     * @throws Exception
	 * @author Alan Uecker
     */
	public DatabaseHandler() throws Exception
	{
		File db = new File(path);
		if(!db.exists())
		{
			PathUtils.checkFolder(new File(PathUtils.getOSindependentPath() + "ChartGenerator/"));
			createDB();
		}
	}

    /**
     * creates the db and all tables
     * @throws Exception
	 * @author Alan Uecker
     */
	private void createDB() throws Exception
	{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			statement.executeUpdate("PRAGMA foreign_keys = ON");
			//chart table
			statement.executeUpdate("CREATE TABLE Chart (ID INTEGER PRIMARY KEY AUTOINCREMENT, type INTEGER, title VARCHAR, x VARCHAR, y VARCHAR, uuid VARCHAR, color VARCHAR, scale INTEGER REFERENCES Scale (ID), legend INTEGER REFERENCES Scale (ID));");
			//dashboard table
			statement.executeUpdate("CREATE TABLE Dashboard (ID INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, cell_1_1 INTEGER REFERENCES Chart (ID), cell_1_2 INTEGER REFERENCES Chart (ID), cell_1_3 INTEGER REFERENCES Chart (ID), cell_2_1 INTEGER REFERENCES Chart (ID), cell_2_2 INTEGER REFERENCES Chart (ID), cell_2_3 INTEGER REFERENCES Chart (ID));");
			//scale table
			statement.executeUpdate("CREATE TABLE Scale (ID INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, data TEXT);");
			//settings table with value for the last active dashboard
			statement.executeUpdate("CREATE TABLE Settings (ID VARCHAR, value INTEGER REFERENCES Dashboard(ID));");
			statement.executeUpdate("INSERT INTO Settings(ID) VALUES('lastDashboard');");
			statement.close();
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
		}
		finally {
			connection.close();
		}
	}
	//endregion
	//region Dashboard
    /**
     * returns all dashboards in the dashboard table
     * @return ArrayList of all found dashboards
     * @throws Exception
	 * @author Alan Uecker
     */
	public ArrayList<Dashboard> getAllDashboards() throws Exception
	{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			//get all entries from the dashboard table
			ResultSet result = statement.executeQuery("SELECT * FROM Dashboard ORDER BY ID");
			//extract every single dashboard
			ArrayList<Dashboard> dashboards = extractDashboards(result);
			statement.close();

			return dashboards;
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
			return null;
		}
		finally {
			connection.close();
		}
	}

    /**
     * takes the ResultSet and creates a Dashboard object for each row
     * @param result ResultSet from query
     * @return ArrayList of all found dashboards
     * @throws Exception
	 * @author Alan Uecker
     */
	private ArrayList<Dashboard> extractDashboards(ResultSet result) throws Exception
	{
		ArrayList<Dashboard> dashboards = new ArrayList<>();
		//create new dashboard object for each row
		while(result.next())
		{
			int ID = result.getInt("ID");
			String name = result.getString("name");
			int cell_1_1 = result.getInt("cell_1_1");
			int cell_1_2 = result.getInt("cell_1_2");
			int cell_1_3 = result.getInt("cell_1_3");
			int cell_2_1 = result.getInt("cell_2_1");
			int cell_2_2 = result.getInt("cell_2_2");
			int cell_2_3 = result.getInt("cell_2_3");

			Dashboard current = new Dashboard(ID, name, cell_1_1, cell_1_2, cell_1_3, cell_2_1, cell_2_2, cell_2_3);
			dashboards.add(current);
		}
		return dashboards;
	}

    /**
     * get data for one dashboard
     * @param ID in table
     * @return Dashboard object with specific ID
     * @throws Exception
	 * @author Alan Uecker
     */
	public Dashboard getDashboard(int ID) throws Exception
	{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			//search for dashboard with given ID in table
			ResultSet result = statement.executeQuery("SELECT * FROM Dashboard WHERE ID = " + ID);			

			Dashboard dashboard = new Dashboard(result.getInt("ID"), result.getString("name"), result.getInt("cell_1_1"), result.getInt("cell_1_2"), result.getInt("cell_1_3"), result.getInt("cell_2_1"), result.getInt("cell_2_2"), result.getInt("cell_2_3"));
			statement.close();
			
			return dashboard;
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
			return null;
		}
		finally {
			connection.close();
		}
	}

	/**
	 * save new Dashboard in the table
	 * @param dashboard that should be saved
	 * @return biggest id in the table
	 * @throws Exception
	 * @author Alan Uecker
	 */
	public int saveDashboard(Dashboard dashboard) throws Exception
	{
		Connection connection = null;
		try
		{
			ArrayList<Integer> cells = dashboard.getCells();
			//add -1 as reference if all cells of the dashboard are empty
			if(cells.size() == 0)
			{
				for(int i = 0; i < 6; i++)
				{
					cells.add(-1);
				}
			}
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			// id, cell_1_1, cell_1_2, cell_1_3, cell_2_1, cell_2_2, cell_2_3,
			statement.executeUpdate("INSERT INTO Dashboard VALUES(NULL,'" + dashboard.getName() + "'," + cells.get(0) + "," + cells.get(1) + "," + cells.get(2) + "," + cells.get(3) + "," + cells.get(4) + "," + cells.get(5) + ")");
			//get the biggest ID from the table
			ResultSet result = statement.executeQuery("SELECT max(ID) FROM Dashboard");

			int id = result.getInt(1);
			statement.close();

			return id;
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
			return -1;
		}
		finally {
			connection.close();
		}
	}

	/**
	 * update the cells of a dashboard
	 * @param dashboard that should be updated
	 * @throws Exception
	 * @author Alan Uecker
	 */
	public void updateDashboard(Dashboard dashboard) throws Exception
	{
		Connection connection = null;
		try
		{
			ArrayList<Integer> cells = dashboard.getCells();
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			statement.executeUpdate("UPDATE Dashboard SET name = '" + dashboard.getName() + "', cell_1_1 = " + cells.get(0) + ", cell_1_2 = " + cells.get(1) + ", cell_1_3 = " + cells.get(2) + ", cell_2_1 = " + cells.get(3) + ", cell_2_2 = " + cells.get(4) + ", cell_2_3 = " + cells.get(5) + " WHERE ID = " + dashboard.getID());
			statement.close();
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
		}
		finally {
			connection.close();
		}
	}

	/**
	 * delete a dashboard from the table
	 * @param ID of the dashboard that should be deleted
	 * @throws Exception
	 * @author Alan Uecker
	 */
	public void deleteDashboard(int ID) throws Exception
	{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			//delete the dashboard with the given ID
			statement.executeUpdate("DELETE FROM Dashboard WHERE ID=" + ID);
			statement.close();
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
		}
		finally {
			connection.close();
		}
	}
	//endregion
	//region Chart
    /**
     * get data for one chart
     * @param ID of the chart in table
     * @return found chart object
     * @throws Exception
	 * @author Alan Uecker
     */
	public Chart getChart(int ID) throws Exception
	{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM Chart WHERE ID = " + ID);			

			Color color = Color.web(result.getString("color"));
			ChartType type = ChartType.valueOf(result.getInt("type"));
			
			int scaleID = result.getInt("scale");
			Scale scale = null;
			//if the scale is set, create a scale object
			if(scaleID != -1)
			{
				scale = getScale(scaleID);
			}

			int legendID = result.getInt("legend");
			Scale legend = null;
			//if the legend is set, create scale object
			if(legendID != -1)
			{
				legend = getScale(legendID);
			}
			//create a chart object and return it
			Chart chart = new Chart(result.getInt("ID"), type, result.getString("title"), result.getString("x"), result.getString("y"), result.getString("uuid"), color, scale, legend);
			statement.close();

			return chart;
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
			return null;
		}
		finally {
			connection.close();
		}
	}

	/**
	 * save new chart in the table
	 * @param chart that should be saved
	 * @return biggest ID in table
	 * @throws Exception
	 * @author Alan Uecker
	 */
	public int saveChart(Chart chart) throws Exception
	{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			// id, type, title, x, y, uuid, color, scale, legend
			statement.executeUpdate("INSERT INTO Chart VALUES(NULL,'" + chart.getType().getID() + "','" + chart.getTitle() + "','" +
					chart.getX() + "','" + chart.getY() + "','" + chart.getTableUUID() + "','" +
					chart.getColor().toString() + "'," + chart.getScale().getID() + "," + chart.getLegendScale().getID() + ")");
			//get the biggest id from table
			ResultSet result = statement.executeQuery("SELECT max(ID) FROM Chart");
						
			int id = result.getInt(1);
			statement.close();

			return id;		
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
			return -1;
		}
		finally {
			connection.close();
		}
	}

    /**
     * update all data of a chart
     * @param chart that should be updated
     * @throws Exception
	 * @author Alan Uecker
     */
	public void updateChart(Chart chart) throws Exception
	{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			// id, type, title, x, y, uuid, color, scale, legend
			statement.executeUpdate("UPDATE Chart SET type = '" + chart.getType().getID() + "', title ='" +
					chart.getTitle() + "', x = '" + chart.getX() + "', y = '" + chart.getY() + "', uuid = '" +
					chart.getTableUUID() + "', color = '" + chart.getColor() + "', scale = " + chart.getScale().getID() + ", legend = " +
					chart.getLegendScale().getID() + " WHERE ID = " + chart.getID());
			statement.close();
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
		}
		finally {
			connection.close();
		}
	}

    /**
     * delete a chart from the table
     * @param ID from the chart that should be deleted
     * @throws Exception
	 * @author Alan Uecker
     */
	public void deleteChartFromDB(int ID) throws Exception
	{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			statement.executeUpdate("DELETE FROM Chart WHERE ID = " + ID);
			statement.close();
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
		}
		finally {
			connection.close();
		}
	}

	//endregion
	//region CSV
    /**
     * get the data from one column in a csv table
     * @param uuid of csv table
     * @param columnName in the table
     * @return all Data in the specific column
     * @throws Exception
	 * @author Alan Uecker
     */
	public ArrayList<Double> getCSVColumn(String uuid, String columnName) throws Exception
	{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT " + columnName + " FROM '" + uuid + "'");
			
			ArrayList<Double> column = new ArrayList<Double>();

			//add cell data to column ArrayList
			while(result.next())
			{
				column.add(result.getDouble(1));
			}

			statement.close();

			return column;
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
			return null;
		}
		finally {
			connection.close();
		}
	}

    /**
     * get all saved csv tables in the DB
     * @return ArrayList with all csv tables
     * @throws Exception
	 * @author Alan Uecker
     */
	public ArrayList<CSVTable> getAllCSVTables() throws Exception
	{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			ResultSet result = connection.getMetaData().getTables(null, null, "%", new String[]{"TABLE"});
			ArrayList<CSVTable> tables = new ArrayList<>();

			while(result.next())
			{
                String name = result.getString("TABLE_NAME");
                //exclude all known tables
                if (!name.equals("Chart") && !name.equals("Dashboard") && !name.equals("sqlite_sequence") && !name.equals("Settings") && !name.equals("Scale"))
                {
                    tables.add(getCSVTable(name));
				}
			}

			statement.close();

			return tables;
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
			return null;
		}
		finally {
			connection.close();
		}
	}

    /**
     * get information for one CSV Table and return a CSVTable object
     * @param uuid of the csv table
     * @return CSvTable object
	 * @author Alan Uecker
     */
	private CSVTable getCSVTable(String uuid) throws Exception
	{
		Connection connection = null;
		try
		{
			ArrayList<String> columnNames = new ArrayList<String>();
			String name;
			String date;

			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM '" + uuid + "'");
			ResultSetMetaData metadata = result.getMetaData();			
			int columnCount = metadata.getColumnCount();
			for(int i = 1; i <= columnCount; i++)
			{
				String columnName = metadata.getColumnName(i);
				if(!columnName.equals("ID") && !columnName.equals("name") && !columnName.equals("date"))
				{
					columnNames.add(columnName);
				}
			}
			
			result = statement.executeQuery("SELECT name, date FROM '" + uuid + "'");
			name = result.getString("name");
			date = result.getString("date");
			statement.close();

			return new CSVTable(uuid, name, date, columnNames);
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
			return null;
		}
		finally {
			connection.close();
		}
	}

	/**
	 * save CSV from importer to database
	 * @param importer Importer Object with all read data from csv file
	 * @throws Exception
	 * @author Alan Uecker
	 */
	public void saveCSVTable(Importer importer) throws Exception
	{
		Connection connection = null;
		try
		{
			//query to create a new table
			String sqlCreateTable;
			//query for settings like name and date
			String sqlMetaData;
			//query for imported data
			String sqlData;

			//get name and create new uuid
			String name = importer.getName();
			String uuid = UUID.randomUUID().toString();

			//get date
			DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			Date date = new Date();
			String dateString = sdf.format(date);

			ArrayList<String> columnNames = importer.getColumnNames();
			int columnNamesSize = columnNames.size();
			ArrayList<ArrayList<String>> data = importer.getData();
			sqlCreateTable = "CREATE TABLE '" + uuid + "'(ID INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, date VARCHAR";
			//dynamically add data columns
			for(int i = 0; i < columnNamesSize; i++)
			{
				sqlCreateTable += ", '" + columnNames.get(i) + "' VARCHAR";
			}
			sqlCreateTable += ");";
			//set meta data
			sqlMetaData = "INSERT INTO '" + uuid + "' (name, date) VALUES('" + name + "', '" + dateString + "');";
			//dynamically add data columns and their data
			sqlData = "INSERT INTO '" + uuid + "'('";

			for(int i = 0; i < columnNamesSize; i++)
			{
				if(i > 0)
				{
					sqlData += "','";
				}
				sqlData += columnNames.get(i);
			}

			sqlData += "')";

			sqlData += " VALUES";

			for(int i = 0; i < data.size(); i++)
			{
				sqlData += "('";
				for(int j = 0; j < columnNamesSize; j++)
				{
					if(j > 0)
					{
						sqlData += "','";
					}
					sqlData += data.get(i).get(j);
				}
				sqlData += "')";
				if(i < importer.getData().size() - 1)
				{
					sqlData += ",";
				}
				else
				{
					sqlData += ";";
				}
			}

			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			statement.executeUpdate(sqlCreateTable);
			statement.executeUpdate(sqlMetaData);
			statement.executeUpdate(sqlData);

			statement.close();
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
		}
		finally {
			connection.close();
		}
	}

	//endregion
	//region Settings
	/**
	 * get the id of the last open dashboard
	 * @return ID of last active dashboard
	 * @throws Exception
	 * @author Alan Uecker
	 */
	public int getLastDashboard() throws Exception{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT value FROM Settings WHERE ID = 'lastDashboard'");

			int ID = result.getInt("value");
			statement.close();

			return ID;
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
			return -1;
		}
		finally {
			connection.close();
		}
	}

    /**
     * update the id for the last open dashboard
     * @param lastID ID of last used dashboard
     * @throws Exception
	 * @author Alan Uecker
     */
	public void updateLastDashboard(int lastID) throws Exception{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			// id
			statement.executeUpdate("UPDATE Settings SET value = " + lastID + " WHERE ID = 'lastDashboard'");
			statement.close();
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
		}
		finally {
			connection.close();
		}
	}

	//endregion
	//region Scale

	/**
	 * save a scale into the db
	 * @param scale that should be saved
	 * @return biggest id in table
	 * @throws Exception
	 * @author Alan Uecker
	 */
	public int saveScale(Scale scale) throws Exception{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			// id, name, data
            statement.executeUpdate("INSERT INTO Scale VALUES( NULL,'" + scale.getName() + "','" + JsonHelper.convertScaleHashMapToJson(scale.getScaleHashMap()) + "')");
            ResultSet result = statement.executeQuery("SELECT max(ID) FROM Scale");

			int id = result.getInt(1);
			statement.close();

			return id;
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
			return -1;
		}
		finally {
			connection.close();
		}
	}

	/**
	 * get all scales from the db
	 * @return ArrayList with Scale objects
	 * @throws Exception
	 * @author Alan Uecker
	 */
	public ArrayList<Scale> getAllScales() throws Exception
	{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM Scale ORDER BY ID");

			ArrayList<Scale> scales = extractScales(result);
			statement.close();

			return scales;
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
			return null;
		}
		finally {
			connection.close();
		}
	}

	/**
	 * extract all scales from the ResultSet
	 * @param result ResultSet with found data
	 * @return ArrayList with all Scales
	 * @throws Exception
	 * @author Alan Uecker
	 */
	private ArrayList<Scale> extractScales(ResultSet result) throws Exception
	{
		ArrayList<Scale> scales = new ArrayList<>();
		//create scale object for each row
		while(result.next())
		{
			int ID = result.getInt("ID");
			String name = result.getString("name");
			String data = result.getString("data");

            Scale current = new Scale(ID, name, JsonHelper.getScaleHashMapFromJson(data));
            scales.add(current);
		}
		return scales;
	}

	/**
	 * get a specific scale
	 * @param ID for the scale in the db
	 * @return Scale object
	 * @throws Exception
	 * @author Alan Uecker
	 */
	public Scale getScale(int ID) throws Exception{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM Scale WHERE ID = " + ID);
			
			if(!result.isClosed())
			{
			    Scale scale = new Scale(ID, result.getString("name"), JsonHelper.getScaleHashMapFromJson(result.getString("data")));
	            statement.close();
	            return scale;
			}       
			else
			{
				return null;
			}		
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
			return null;
		}
		finally {
			connection.close();
		}
	}

	/**
	 * update a scale
	 * @param scale to be updated
	 * @throws Exception
	 * @author Alan Uecker
	 */
	public void updateScale(Scale scale) throws Exception
	{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			// name, data
            statement.executeUpdate("UPDATE Scale SET name = '" + scale.getName() + "', data ='" + JsonHelper.convertScaleHashMapToJson(scale.getScaleHashMap()) + "' WHERE ID = " + scale.getID());
            statement.close();
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
		}
		finally {
			connection.close();
		}
	}

	/**
	 * delete a scale from the db
	 * @param ID of the scale
	 * @throws Exception
	 * @author Alan Uecker
	 */
	public void deleteScaleFromDB(int ID) throws Exception
	{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			statement.executeUpdate("DELETE FROM Scale WHERE ID = " + ID);
			statement.close();
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
		}
		finally {
			connection.close();
		}
	}

	//endregion
    //region Data

	/**
	 * count the values from a column and return the results
	 * @param uuid of csv table
	 * @param columnNameX column name in csv table
	 * @return counted data of the column
	 * @throws Exception
	 * @author Alan Uecker
	 */
    public ArrayList<ChartSetItem> getData(String uuid, String columnNameX) throws Exception{
        Connection connection = null;
        try
        {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            Statement statement = connection.createStatement();
            //count the values in the column
            ResultSet result = statement.executeQuery("SELECT COUNT(*), `" + columnNameX + "` FROM `" + uuid + "` GROUP BY `" + columnNameX + "` HAVING COUNT(*) > 1");

            ArrayList<ChartSetItem> items = new ArrayList<>();

            while(result.next())
            {
            	ChartSetItem newSetItem = new ChartSetItem(0, result.getDouble(1), result.getDouble(2));
            	items.add(newSetItem);        
            }

            statement.close();
         
            return items;
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory", it probably means no database file is found
            Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
            return null;
        }
        finally {
            connection.close();
        }
    }

	/**
	 * cross two tables and count the resulting values
	 * @param uuid of csv table
	 * @param columnNameX column name in csv table
	 * @param columnNameY column name in csv table
	 * @return counted data with their specific set
	 * @throws Exception
	 * @author Alan Uecker
	 */
    public ArrayList<ChartSetItem> getData(String uuid, String columnNameX, String columnNameY) throws Exception{
        Connection connection = null;
        try
        {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            Statement statement = connection.createStatement();
            // cross columnNameX with columnNameY and count the resulting values
            ResultSet result = statement.executeQuery("SELECT `" + columnNameY + "`, COUNT(*), `" + columnNameX + "` FROM `" + uuid + "` GROUP BY `" + columnNameY + "`, `" + columnNameX + "` HAVING COUNT(*) > 1");

            ArrayList<ChartSetItem> setItems = new ArrayList<>();

            while(result.next())
            {            	
            	ChartSetItem newSetItem = new ChartSetItem(result.getDouble(1), result.getDouble(2), result.getDouble(3));
            	setItems.add(newSetItem);
            }            
          
            statement.close();            

            return setItems;
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory", it probably means no database file is found
            Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
            return null;
        }
        finally {
            connection.close();
        }
    }
    //endregion

	/**
	 * return path to the db file
	 * @return return path variable
	 * @author Alan Uecker
	 */
    public String getPath()
    {
        return path;
    }
}