package de.lww4.tests.database;

import de.lww4.logic.CSVTable;
import de.lww4.logic.DatabaseHandler;
import de.lww4.logic.DelimiterType;
import de.lww4.logic.Importer;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class DatabaseTests
{
    @Test
    public void DatabaseCreatedTest()
    {
        try
        {
            DatabaseHandler databaseHandler = new DatabaseHandler();
            File file = new File(databaseHandler.getPath());
            assertTrue(file.exists());
        }
        catch (Exception e)
        {
            assertTrue(false);
        }
    }

    private boolean containsAllMultiple(ArrayList<String> allColumns, String[] searchArray)
    {
        for(String searchName : searchArray)
        {
            if(!allColumns.contains(searchName))
            {
                return false;
            }
        }
        return true;
    }

    @Test
    public void CSVTableSavedInDataBaseTest()
    {
        ArrayList<String> stringArrayList = new ArrayList<>();
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
                    stringArrayList.add(columnName);
                }
            }
        }
        catch (Exception e)
        {
            assertTrue(false);
        }

        assertTrue(containsAllMultiple(stringArrayList,new String[]{"TestData0", "TestData1", "TestData2", "TestData3"}));

    }
}
