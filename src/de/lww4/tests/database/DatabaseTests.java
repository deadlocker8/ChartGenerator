package de.lww4.tests.database;

import de.lww4.logic.CSVTable;
import de.lww4.logic.DatabaseHandler;
import de.lww4.logic.DelimiterType;
import de.lww4.logic.Importer;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class DatabaseTests
{
    @Test
    public void DatabaseCreatedTest()
    {
        try
        {
            DatabaseHandler databaseHandler = new DatabaseHandler();
            File file = new File(databaseHandler.getPath());
            if(file.exists())
            {
                assertTrue(true);
            }
            else
            {
                assertTrue(false);
            }
        }
        catch (Exception e)
        {
            assertTrue(false);
        }
    }

    @Test
    public void CSVTableSavedInDataBaseTest()
    {
        try
        {
            DatabaseHandler databaseHandler = new DatabaseHandler();
            Importer importer = new Importer(new File("src/de/lww4/tests/importer/data/valid.csv"),
                    DelimiterType.COMMA,"0", "valid.csv");
            databaseHandler.saveCSVTable(importer);
            ArrayList<CSVTable> csvTableArrayList = databaseHandler.getAllCSVTables();
            for(CSVTable currentTable : csvTableArrayList)
            {
                for(String columnName : currentTable.getColumnNames())
                {
                    if(columnName.equals("TestData3"))
                    {
                        assertTrue(true);
                        return;
                    }
                }
            }
        }
        catch (Exception e)
        {
            assertTrue(false);
        }
        assertTrue(false);
    }
}
