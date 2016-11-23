package de.lww4.logic;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import javafx.scene.paint.Color;
import logger.LogLevel;
import logger.Logger;
import tools.PathUtils;

public class DatabaseHandler
{
	private String path = PathUtils.getOSindependentPath() + "ChartGenerator/db.sqlite";

    /**
     * checks if the sqlite file is there and creates a new one if needed
     * @throws Exception
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
     */
	private void createDB() throws Exception
	{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			// create chart and dashboard table
			statement.executeUpdate("PRAGMA foreign_keys = ON");
			statement.executeUpdate("CREATE TABLE Chart (ID INTEGER PRIMARY KEY AUTOINCREMENT, type INTEGER, title VARCHAR, x VARCHAR, y VARCHAR, uuid VARCHAR, color VARCHAR);");
			statement.executeUpdate("CREATE TABLE Dashboard (ID INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, cell_1_1 INT REFERENCES Chart (ID), cell_1_2 INT REFERENCES Chart (ID), cell_1_3 INT REFERENCES Chart (ID), cell_2_1 INT REFERENCES Chart (ID), cell_2_2 INT REFERENCES Chart (ID), cell_2_3 INT REFERENCES Chart (ID));");
			statement.executeUpdate("CREATE TABLE Settings (ID VARCHAR, value INT REFERENCES Dashboard(ID));");
			statement.executeUpdate("INSERT INTO Settings(ID) VALUES('lastDashboard');");
			connection.close();
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
		}
	}

    /**
     * returns all the dashboards in the table
     * @return
     * @throws Exception
     */
	public ArrayList<Dashboard> getAllDashboards() throws Exception
	{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			// statement.setQueryTimeout(30); // set timeout to 30 sec.
			ResultSet result = statement.executeQuery("SELECT * FROM Dashboard ORDER BY ID");

			ArrayList<Dashboard> dashboards = extractDashboards(result);

			connection.close();

			return dashboards;
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
			return null;
		}
	}

    /**
     * takes the result set and creates a Dashboard object for each row
     * @param result
     * @return
     * @throws Exception
     */
	private ArrayList<Dashboard> extractDashboards(ResultSet result) throws Exception
	{
		ArrayList<Dashboard> dashboards = new ArrayList<Dashboard>();
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
     * @param ID
     * @return
     * @throws Exception
     */
	public Dashboard getDashboard(int ID) throws Exception
	{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			ResultSet result = statement.executeQuery("SELECT * FROM Dashboard WHERE ID = " + ID);
			connection.close();

			Dashboard dashboard = new Dashboard(result.getInt("ID"), result.getString("name"), result.getInt("cell_1_1"), result.getInt("cell_1_2"), result.getInt("cell_1_3"), result.getInt("cell_2_1"), result.getInt("cell_2_2"), result.getInt("cell_2_3"));

			return dashboard;
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
			return null;
		}
	}

    /**
     * get data for one chart
     * @param ID
     * @return
     * @throws Exception
     */
	public Chart getChart(int ID) throws Exception
	{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			ResultSet result = statement.executeQuery("SELECT * FROM Chart WHERE ID = " + ID);			

			Color color = Color.web(result.getString("color"));
			ChartType type = ChartType.valueOf(result.getInt("type"));

			Chart chart = new Chart(result.getInt("ID"), type, result.getString("title"), result.getString("x"), result.getString("y"), result.getString("uuid"), color);

			connection.close();
			return chart;
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
			return null;
		}
	}

    /**
     * save new chart in the table
     * @param chart
     * @throws Exception
     */
	public int saveChart(Chart chart) throws Exception
	{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			// id, type, title, x, y, uuid, color
			statement.executeUpdate("INSERT INTO Chart VALUES( NULL,'" + chart.getType().getID() + "','" + chart.getTitle() + "','" + chart.getX() + "','" + chart.getY() + "','" + chart.getTableUUID() + "','" + chart.getColor().toString() + "')");
			ResultSet result = statement.executeQuery("SELECT max(ID) FROM Chart");
						
			int id = result.getInt(1);
			connection.close();
			return id;		
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
			return -1;
		}
	}

    /**
     * save new Dashboard in the table
     * @param dashboard
     * @throws Exception
     */
	public void saveDashboard(Dashboard dashboard) throws Exception
	{
		Connection connection = null;
		try
		{
			ArrayList<Integer> cells = dashboard.getCells();
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
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			// id, cell_1_1, cell_1_2, cell_1_3, cell_2_1, cell_2_2, cell_2_3,
			statement.executeUpdate("INSERT INTO Dashboard VALUES(NULL,\"" + dashboard.getName() + "\"," + cells.get(0) + "," + cells.get(1) + "," + cells.get(2) + "," + cells.get(3) + "," + cells.get(4) + "," + cells.get(5) + ")");
			connection.close();
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
		}
	}

    /**
     * update all data of a chart
     * @param chart
     * @throws Exception
     */
	public void updateChart(Chart chart) throws Exception
	{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			// id, type, title, x, y, uuid, color
			statement.executeUpdate("UPDATE Chart SET type = '" + chart.getType() + "', title ='" + chart.getTitle() + "', x = '" + chart.getX() + "', y = '" + chart.getY() + "', uuid = '" + chart.getTableUUID() + "', color = '" + chart.getColor() + "' WHERE ID = " + chart.getID());
			connection.close();
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
		}
	}

    /**
     * update the cells of a dashboard
     * @param dashboard
     * @throws Exception
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
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			// id, cell_1_1, cell_1_2, cell_1_3, cell_2_1, cell_2_2, cell_2_3,
			statement.executeUpdate("UPDATE Dashboard SET cell_1_1 = " + cells.get(0) + ", cell_1_2 = " + cells.get(1) + ", cell_1_3 = " + cells.get(2) + ", cell_2_1 = " + cells.get(3) + ", cell_2_2 = " + cells.get(4) + ", cell_2_3 = " + cells.get(5) + " WHERE ID = " + dashboard.getID());
			connection.close();
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
		}
	}

    /**
     * delete a chart from the table
     * @param ID
     * @throws Exception
     */
	public void deleteChartFromDB(int ID) throws Exception
	{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			statement.executeUpdate("DELETE FROM Chart WHERE ID = " + ID + ")");
			connection.close();
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
		}
	}

    /**
     * delete a dashboard from the table
     * @param ID
     * @throws Exception
     */
	public void deleteDashboard(int ID) throws Exception
	{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			statement.executeUpdate("DELETE FROM Dashboard WHERE ID=" + ID);
			connection.close();
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
		}
	}

    /**
     * get the data from one column in a csv table
     * @param uuid
     * @param columnName
     * @return
     * @throws Exception
     */
	public ArrayList<Double> getCSVColumn(String uuid, String columnName) throws Exception
	{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			ResultSet result = statement.executeQuery("SELECT " + columnName + " FROM " + uuid);
			
			ArrayList<Double> column = new ArrayList<Double>();

			while(result.next())
			{
				column.add(result.getDouble(1));
			}
			
			connection.close();

			return column;
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
			return null;
		}
	}

    /**
     * get all saved csv tables in the DB
     * @return
     * @throws Exception
     */
	public ArrayList<CSVTable> getAllCSVTables() throws Exception
	{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			// TODO: exclude label table
//			ResultSet result = statement.executeQuery("SELECT name FROM sqlite_master WHERE type = 'table' and name != 'Chart' and name != 'Dashboard' and name != 'sqlite_sequence'");
			ResultSet result = connection.getMetaData().getTables(null, null, "%", new String[]{"TABLE"});
			
			ArrayList<CSVTable> tables = new ArrayList<CSVTable>();

			while(result.next())
			{
				String name = result.getString("TABLE_NAME");
				//TODO add settings table
				if(!name.equals("Chart") && !name.equals("Dashboard") && !name.equals("sqlite_sequence") && !name.equals("Settings"))
				{					
					tables.add(getCSVTable(name));
				}
			}
			
			connection.close();

			return tables;
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
			return null;
		}
	}

    /**
     * get information for one CSV Table and return a CSVTable object
     * @param uuid
     * @return
     */
	private CSVTable getCSVTable(String uuid)
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
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			ResultSet result = statement.executeQuery("SELECT * FROM " + uuid);
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
			
			result = statement.executeQuery("SELECT name, date FROM " + uuid);
			name = result.getString("name");
			date = result.getString("date");
			connection.close();

			return new CSVTable(uuid, name, date, columnNames);
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
			return null;
		}
	}

	/**
	 * save CSV from importer to database
	 * 
	 * @param importer
	 * @throws Exception
	 */
	public void saveCSVTable(Importer importer) throws Exception
	{
		Connection connection = null;
		try
		{
			String sqlCreateTable;
			String sqlMetaData;
			String sqlData;

			String name = importer.getName();
			String uuid = UUID.randomUUID().toString();

			DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			Date date = new Date();
			String dateString = sdf.format(date);

			ArrayList<String> columnNames = importer.getColumnNames();
			int columnNamesSize = columnNames.size();
			ArrayList<ArrayList<String>> data = importer.getData();

			sqlCreateTable = "CREATE TABLE " + uuid + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, date VARCHAR";
			for(int i = 0; i < columnNamesSize; i++)
			{
				sqlCreateTable += ", " + columnNames.get(i) + " VARCHAR";
			}
			sqlCreateTable += ");";

			sqlMetaData = "INSERT INTO " + uuid + "(name, date) VALUES('" + name + "', '" + dateString + "');";

			sqlData = "INSERT INTO " + uuid + "(";

			for(int i = 0; i < columnNamesSize; i++)
			{
				if(i > 0)
				{
					sqlData += ",";
				}
				sqlData += columnNames.get(i);
			}

			sqlData += ")";

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

            System.out.println(sqlData);
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(60); // set timeout to 60 sec.
			statement.executeUpdate(sqlCreateTable);
			statement.executeUpdate(sqlMetaData);
			statement.executeUpdate(sqlData);
			System.out.println(statement.getWarnings());

			connection.close();
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
		}
	}

    /**
     * update the id for the last open dashboard
     * @param lastID
     * @throws Exception
     */
	public void updateLastDashboard(int lastID) throws Exception{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			// id, type, title, x, y, uuid, color
			statement.executeUpdate("UPDATE Settings SET value = " + lastID + " WHERE ID = 'lastDashboard'");
			connection.close();
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
		}
	}

    /**
     * get the id of the last open dashboard
     * @return
     * @throws Exception
     */
	public int getLastDashboard() throws Exception{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			ResultSet result = statement.executeQuery("SELECT value FROM Settings WHERE ID = 'lastDashboard'");
			connection.close();

			return result.getInt("value");
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
			return 0;
		}
	}
}