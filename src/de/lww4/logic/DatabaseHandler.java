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

	public DatabaseHandler() throws Exception
	{
		File db = new File(path);
		if(!db.exists())
		{
			PathUtils.checkFolder(new File(PathUtils.getOSindependentPath() + "ChartGenerator/"));
			createDB();
		}
	}

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
			statement.executeUpdate("CREATE TABLE Chart (ID INTEGER PRIMARY KEY AUTOINCREMENT, type INTEGER, title VARCHAR, x VARCHAR, y VARCHAR, uuid VARCHAR UNIQUE, color VARCHAR);");
			statement.executeUpdate(
					"CREATE TABLE Dashboard (ID INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, cell_1_1 INT REFERENCES Chart (ID), cell_1_2 INT REFERENCES Chart (ID), cell_1_3 INT REFERENCES Chart (ID), cell_2_1 INT REFERENCES Chart (ID), cell_2_2 INT REFERENCES Chart (ID), cell_2_3 INT REFERENCES Chart (ID));");
			connection.close();
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
		}
	}

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
			connection.close();

			Color color = Color.web(result.getString("color"));
			ChartType type = ChartType.valueOf(result.getInt("type"));

			Chart chart = new Chart(result.getInt("ID"), type, result.getString("title"), result.getString("x"), result.getString("y"), result.getString("uuid"), color);

			return chart;
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
			return null;
		}
	}

	public void saveChart(Chart chart) throws Exception
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
			connection.close();
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
		}
	}

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
			connection.close();

			ArrayList<Double> column = new ArrayList<Double>();

			while(result.next())
			{
				column.add(result.getDouble(0));
			}

			return column;
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
			return null;
		}
	}

	public ArrayList<CSVTable> getAllCSVTables() throws Exception
	{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			// TODO: exclude label and settings tables
//			ResultSet result = statement.executeQuery("SELECT name FROM sqlite_master WHERE type = 'table' and name != 'Chart' and name != 'Dashboard' and name != 'sqlite_sequence'");
			ResultSet result = connection.getMetaData().getTables(null, null, "%", new String[]{"TABLE"});
			
			ArrayList<CSVTable> tables = new ArrayList<CSVTable>();

			while(result.next())
			{
				String name = result.getString("TABLE_NAME");
				//TODO add settings table
				if(!name.equals("Chart") && !name.equals("Dashboard") && !name.equals("sqlite_sequence"))
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

			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(60); // set timeout to 60 sec.
			statement.executeUpdate("drop table if exists " + uuid);
			statement.executeUpdate(sqlCreateTable);
			statement.executeUpdate(sqlMetaData);
			statement.executeUpdate(sqlData);

			connection.close();

		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", it probably means no database file is found
			System.err.println(e.getMessage());
		}
	}

	/**
	 * test method for class
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception
	{
		Importer importer = new Importer(new File("test.csv"), DelimiterType.SEMICOLON, "0", "Testing");
		DatabaseHandler dbHandler = null;

		dbHandler = new DatabaseHandler();
		dbHandler.saveCSVTable(importer);
	}
}