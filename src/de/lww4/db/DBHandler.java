package de.lww4.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;
import java.util.ArrayList;
import tools.PathUtils;

public class DBHandler {

    private String path = PathUtils.getOSindependentPath() + "ChartGenerator/db.sqlite";

    public DBHandler() throws Exception {
        File db = new File(path);
        if(!db.exists()){
            createDB();
        }
    }

    private void createDB() throws Exception{
        Connection connection = null;
        try
        {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            //create chart and dashboard table
            statement.executeUpdate("PRAGMA foreign_keys = ON");
            statement.executeUpdate("CREATE TABLE Chart (ID INTEGER PRIMARY KEY AUTOINCREMENT, type VARCHAR, title VARCHAR, x VARCHAR, y VARCHAR, uuid VARCHAR UNIQUE, color VARCHAR);");
            statement.executeUpdate("CREATE TABLE Dashboard (ID INTEGER PRIMARY KEY AUTOINCREMENT, cell_1_1 INT REFERENCES Chart (ID), cell_1_2 INT REFERENCES Chart (ID), cell_1_3 INT REFERENCES Chart (ID), cell_2_1 INT REFERENCES Chart (ID), cell_2_2 INT REFERENCES Chart (ID), cell_2_3 INT REFERENCES Chart (ID));");
            connection.close();
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory", it probably means no database file is found
            System.err.println(e.getMessage());
        }
    }

    public ArrayList<Dashboard> getAllDashboards() throws Exception{
        Connection connection = null;
        try
        {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            ResultSet result = statement.executeQuery("SELECT * FROM Dashboard ORDER BY ID");
            connection.close();

            ArrayList<Dashboard> dashboards = extractDashboards(result);

            return dashboards;
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory", it probably means no database file is found
            System.err.println(e.getMessage());
            return null;
        }
    }

    private ArrayList<Dashboard> extractDashboards(ResultSet result) throws Exception{
        ArrayList<Dashboard> dashboards = new ArrayList<Dashboard>();
        while (result.next()){
            int ID = result.getInt("ID");
            int cell_1_1 = result.getInt("cell_1_1");
            int cell_1_2 = result.getInt("cell_1_2");
            int cell_1_3 = result.getInt("cell_1_3");
            int cell_2_1 = result.getInt("cell_2_1");
            int cell_2_2 = result.getInt("cell_2_2");
            int cell_2_3 = result.getInt("cell_2_3");

            Dashboard current = new Dashboard(ID, cell_1_1, cell_1_2, cell_1_3, cell_2_1, cell_2_2, cell_2_3);
            dashboards.add(current);
        }
        return dashboards;
    }

    public Dashboard getDashboard(int ID) throws Exception{
        Connection connection = null;
        try
        {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            ResultSet result = statement.executeQuery("SELECT * FROM Dashboard WHERE ID = " + ID);
            connection.close();

            Dashboard dashboard = new Dashboard(result.getInt("ID"), result.getInt("cell_1_1"), result.getInt("cell_1_2"), result.getInt("cell_1_3"), result.getInt("cell_2_1"), result.getInt("cell_2_2"), result.getInt("cell_2_3"));

            return dashboard;
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory", it probably means no database file is found
            System.err.println(e.getMessage());
            return null;
        }
    }

    public Chart getChart(int ID) throws Exception{
        Connection connection = null;
        try
        {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            ResultSet result = statement.executeQuery("SELECT * FROM Chart WHERE ID = " + ID);
            connection.close();

            Chart chart = new Chart(result.getInt("ID"), result.getString("type"), result.getString("title"), result.getString("x"), result.getString("y"), result.getString("uuid"), result.getString("color"));

            return chart;
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory", it probably means no database file is found
            System.err.println(e.getMessage());
            return null;
        }
    }

    public void saveChart(Chart chart) throws Exception{
        Connection connection = null;
        try
        {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            //id, type, title, x, y, uuid, color
            statement.executeUpdate("INSERT INTO Chart VALUES( NULL,'" + chart.getType() + "','" + chart.getTitle() + "','" + chart.getX() + "','" + chart.getY() + "','" + chart.getUUID() + "','" + chart.getColor() + "')");
            connection.close();
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory", it probably means no database file is found
            System.err.println(e.getMessage());
        }
    }

    public void saveDashboard(Dashboard dashboard) throws Exception{
        Connection connection = null;
        try
        {
            ArrayList colums = dashboard.getColums();
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            //id, cell_1_1, cell_1_2, cell_1_3, cell_2_1, cell_2_2, cell_2_3,
            statement.executeUpdate("INSERT INTO Dashboard VALUES( NULL," + colums.get(0) + "," + colums.get(1) + "," + colums.get(2) + "," + colums.get(3) + "," + colums.get(4) + "," + colums.get(5) + ")");
            connection.close();
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory", it probably means no database file is found
            System.err.println(e.getMessage());
        }
    }

    public void updateChart(Chart chart) throws Exception{
        Connection connection = null;
        try
        {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            //id, type, title, x, y, uuid, color
            statement.executeUpdate("UPDATE Chart SET type = '" + chart.getType() + "', title ='" + chart.getTitle() + "', x = '" + chart.getX() + "', y = '" + chart.getY() + "', uuid = '" + chart.getUUID() + "', color = '" + chart.getColor() + "' WHERE ID = " + chart.getID() + ")");
            connection.close();
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory", it probably means no database file is found
            System.err.println(e.getMessage());
        }
    }

    public void updateDashboard(Dashboard dashboard) throws Exception{
        Connection connection = null;
        try
        {
            ArrayList colums = dashboard.getColums();
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            //id, cell_1_1, cell_1_2, cell_1_3, cell_2_1, cell_2_2, cell_2_3,
            statement.executeUpdate("UPDATE Dashboard SET cell_1_1 = " + colums.get(0) + ", cell_1_2 = " + colums.get(1) + ", cell_1_3 = " + colums.get(2) + ", cell_2_1 = " + colums.get(3) + ", cell_2_2 = " + colums.get(4) + ", cell_2_3 = " + colums.get(5) + " WHERE ID = " + dashboard.getID() + ")");
            connection.close();
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory", it probably means no database file is found
            System.err.println(e.getMessage());
        }
    }

    public void deleteChartFromDB(int ID) throws Exception{
        Connection connection = null;
        try
        {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            statement.executeUpdate("DELETE FROM Chart WHERE ID = " + ID + ")");
            connection.close();
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory", it probably means no database file is found
            System.err.println(e.getMessage());
        }
    }

    public void deleteDashboard(int ID) throws Exception{
        Connection connection = null;
        try
        {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            statement.executeUpdate("DELETE FROM Dashboard WHERE ID = " + ID + ")");
            connection.close();
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory", it probably means no database file is found
            System.err.println(e.getMessage());
        }
    }

}
