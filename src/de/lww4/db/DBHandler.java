package de.lww4.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;
import tools.PathUtils;

public class DBHandler {

    private String path = PathUtils.getOSindependentPath() + "ChartGenerator/db.sqlite";

    public DBHandler() throws Exception {
        File db = new File(path);
        if(!db.exists()){
            createDB();
        }
    }

    public void createDB() throws Exception
    {
        Connection connection = null;
        try
        {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            //create chart and dashboard table
            statement.executeUpdate("CREATE TABLE Chart (ID INT PRIMARY KEY, type VARCHAR, title VARCHAR, x VARCHAR, y VARCHAR, uuid VARCHAR UNIQUE, color VARCHAR);");
            statement.executeUpdate("CREATE TABLE Dashboard (ID INT PRIMARY KEY, cell_1_1 INT REFERENCES Chart (ID), cell_1_2 INT REFERENCES Chart (ID), cell_1_3 INT REFERENCES Chart (ID), cell_2_1 INT REFERENCES Chart (ID), cell_2_2 INT REFERENCES Chart (ID), cell_2_3 INT REFERENCES Chart (ID));");

            connection.close();
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory", it probably means no database file is found
            System.err.println(e.getMessage());
        }
    }
}
