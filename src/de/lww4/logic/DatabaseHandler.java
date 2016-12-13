package de.lww4.logic;

import de.lww4.logic.models.Scale.Scale;
import javafx.scene.paint.Color;
import logger.LogLevel;
import logger.Logger;
import tools.PathUtils;

import java.io.File;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

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
			// create chart and dashboard table
			statement.executeUpdate("PRAGMA foreign_keys = ON");
			statement.executeUpdate("CREATE TABLE Chart (ID INTEGER PRIMARY KEY AUTOINCREMENT, type INTEGER, title VARCHAR, x VARCHAR, y VARCHAR, uuid VARCHAR, color VARCHAR, scale INTEGER REFERENCES Scale (ID));");
			statement.executeUpdate("CREATE TABLE Dashboard (ID INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, cell_1_1 INTEGER REFERENCES Chart (ID), cell_1_2 INTEGER REFERENCES Chart (ID), cell_1_3 INTEGER REFERENCES Chart (ID), cell_2_1 INTEGER REFERENCES Chart (ID), cell_2_2 INTEGER REFERENCES Chart (ID), cell_2_3 INTEGER REFERENCES Chart (ID));");
			statement.executeUpdate("CREATE TABLE Scale (ID INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, data TEXT);");
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

	//region Dashboard
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
			ResultSet result = statement.executeQuery("SELECT * FROM Dashboard ORDER BY ID");

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
	 * @param dashboard
	 * @throws Exception
	 */
	public int saveDashboard(Dashboard dashboard) throws Exception
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
			// id, cell_1_1, cell_1_2, cell_1_3, cell_2_1, cell_2_2, cell_2_3,
			statement.executeUpdate("INSERT INTO Dashboard VALUES(NULL,'" + dashboard.getName() + "'," + cells.get(0) + "," + cells.get(1) + "," + cells.get(2) + "," + cells.get(3) + "," + cells.get(4) + "," + cells.get(5) + ")");

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
			ResultSet result = statement.executeQuery("SELECT * FROM Chart WHERE ID = " + ID);			

			Color color = Color.web(result.getString("color"));
			ChartType type = ChartType.valueOf(result.getInt("type"));
			
			int scaleID = result.getInt("scale");
			Scale scale = null;
			if(scaleID != -1)
			{
				scale = getScale(scaleID);
			}			

			Chart chart = new Chart(result.getInt("ID"), type, result.getString("title"), result.getString("x"), result.getString("y"), result.getString("uuid"), color, scale);
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
			// id, type, title, x, y, uuid, color
			statement.executeUpdate("INSERT INTO Chart VALUES( NULL,'" + chart.getType().getID() + "','" + chart.getTitle() + "','" + chart.getX() + "','" + chart.getY() + "','" + chart.getTableUUID() + "','" + chart.getColor().toString() + "'," + chart.getScale().getID() + ")");
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
			// id, type, title, x, y, uuid, color
			statement.executeUpdate("UPDATE Chart SET type = '" + chart.getType().getID() + "', title ='" + chart.getTitle() + "', x = '" + chart.getX() + "', y = '" + chart.getY() + "', uuid = '" + chart.getTableUUID() + "', color = '" + chart.getColor() + "', scale = " + chart.getScale().getID() + " WHERE ID = " + chart.getID());
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
			statement.executeUpdate("DELETE FROM Chart WHERE ID = " + ID + ")");
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
            ResultSet result = statement.executeQuery("SELECT " + columnName + " FROM '" + uuid + "'");
			
			ArrayList<Double> column = new ArrayList<Double>();

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
			ResultSet result = connection.getMetaData().getTables(null, null, "%", new String[]{"TABLE"});
			ArrayList<CSVTable> tables = new ArrayList<CSVTable>();

			while(result.next())
			{
                String name = result.getString("TABLE_NAME");
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
     * @param uuid
     * @return
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
			statement.setQueryTimeout(30); // set timeout to 30 sec.
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
			sqlCreateTable = "CREATE TABLE '" + uuid + "'(ID INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, date VARCHAR";
			for(int i = 0; i < columnNamesSize; i++)
			{
				sqlCreateTable += ", '" + columnNames.get(i) + "' VARCHAR";
			}
			sqlCreateTable += ");";

			sqlMetaData = "INSERT INTO '" + uuid + "' (name, date) VALUES('" + name + "', '" + dateString + "');";

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
			// id, type, title, x, y, uuid, color
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

	public int saveScale(Scale scale) throws Exception{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			// id, name, data
			statement.executeUpdate("INSERT INTO Scale VALUES( NULL,'" + scale.getName() + "','" + scale.getData() + "')");
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

	private ArrayList<Scale> extractScales(ResultSet result) throws Exception
	{
		ArrayList<Scale> scales = new ArrayList<Scale>();
		while(result.next())
		{
			int ID = result.getInt("ID");
			String name = result.getString("name");
			String data = result.getString("data");

			Scale current = new Scale(ID, name, data);
			scales.add(current);
		}
		return scales;
	}

	public Scale getScale(int ID) throws Exception{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM Scale WHERE ID = " + ID);

			Scale scale = new Scale(ID, result.getString("name"), result.getString("data"));
			statement.close();

			return scale;
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

	public void updateScale(Scale scale) throws Exception
	{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			// id, name, data
			statement.executeUpdate("UPDATE Scale SET name = '" + scale.getName() + "', data ='" + scale.getData() + "' WHERE ID = " + scale.getID());
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

	public void deleteScaleFromDB(int ID) throws Exception
	{
		Connection connection = null;
		try
		{
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement statement = connection.createStatement();
			statement.executeUpdate("DELETE FROM Scale WHERE ID = " + ID + ")");
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
    public ArrayList<ArrayList<Double>> getData(String uuid, String columnNameX) throws Exception{
        Connection connection = null;
        try
        {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT COUNT(*), " + columnNameX + " FROM '" + uuid + "' GROUP BY " + columnNameX + " HAVING COUNT(*) > 1");

            ArrayList<Double> count = new ArrayList<Double>();
            ArrayList<Double> label = new ArrayList<Double>();

            while(result.next())
            {
                count.add(result.getDouble(1));
                label.add(result.getDouble(2));

            }

            statement.close();

            ArrayList<ArrayList<Double>> data = new ArrayList<>();
            data.add(count);
            data.add(label);

            return data;
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

    public ArrayList<ArrayList<Double>> getData(String uuid, String columnNameX, String columnNameY) throws Exception{
        Connection connection = null;
        try
        {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT " + columnNameY + ", COUNT(*), " + columnNameX + " FROM '" + uuid + "' GROUP BY " + columnNameY + ", " + columnNameX + " HAVING COUNT(*) > 1");

            ArrayList<Double> count = new ArrayList<Double>();
            ArrayList<Double> label = new ArrayList<Double>();
            ArrayList<Double> set = new ArrayList<Double>();

            while(result.next())
            {
                set.add(result.getDouble(1));
                count.add(result.getDouble(2));
                label.add(result.getDouble(3));
            }

            statement.close();

            ArrayList<ArrayList<Double>> data = new ArrayList<>();
            data.add(set);
            data.add(count);
            data.add(label);

            return data;
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

    public String getPath()
    {
        return path;
    }
}