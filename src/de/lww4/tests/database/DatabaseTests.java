package de.lww4.tests.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.junit.Test;

import de.lww4.logic.Importer;
import de.lww4.logic.handler.DatabaseHandler;
import de.lww4.logic.models.CSVTable;
import de.lww4.logic.models.Dashboard;
import de.lww4.logic.models.chart.Chart;
import de.lww4.logic.models.enums.ChartType;
import de.lww4.logic.models.enums.DelimiterType;
import de.lww4.logic.models.scale.Scale;
import de.lww4.logic.models.scale.ScaleItem;
import javafx.scene.paint.Color;

/**
 * @author max
 */
public class DatabaseTests
{
    /**
     * checks if database file was created
     */
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

    /**
     * @param allColumns contains all column names
     * @param searchArray contains the words that need to be contained
     * @return true, if all words are contained
     */
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

    /**
     * checks if csv was saved in database
     */
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

    /**
     *
     * @return Scale with some predefined values
     */
    private Scale getTestScale()
    {
        HashMap<Double, String> testScaleHashMap = new HashMap<>();
        testScaleHashMap.put(1.0, "Männlich");
        testScaleHashMap.put(2.0, "Weiblich");
        testScaleHashMap.put(3.0, "Keine Angabe");
        Scale scale = new Scale(-1, "TestScale", testScaleHashMap);
        return scale;
    }

    /**
     * checks if scale was saved in the database
     */
    @Test
    public void ScaleSavedInDatabaseTest()
    {
        try
        {
            Scale testScale = getTestScale();
            //save scale in db
            DatabaseHandler databaseHandler = new DatabaseHandler();

            int scaleID = databaseHandler.saveScale(testScale);
             //check if scale exists
            Scale dbScale = databaseHandler.getScale(scaleID);
            assertNotNull(dbScale);
            assertTrue(dbScale.getName().equals(testScale.getName()));
            assertTrue(dbScale.getScaleHashMap().equals(testScale.getScaleHashMap()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * updates scale already in database and checks if update was successful or not
     */
    @Test
    public void ScaleUpdatedInDatabaseTest()
    {
        Scale scale = getTestScale();
        Scale updatedScale = getTestScale();
        updatedScale.setName("Updated Name");
        ArrayList<ScaleItem> scaleItems = new ArrayList<>();
        scaleItems.add(new ScaleItem(5.0, "Rot"));
        scaleItems.add(new ScaleItem(2.0, "Grün"));
        updatedScale.setScaleItems(scaleItems);
        try
        {
            //save scale in db
            DatabaseHandler databaseHandler = new DatabaseHandler();
            int scaleID = databaseHandler.saveScale(scale);
            updatedScale.setId(scaleID);
            databaseHandler.updateScale(updatedScale);
            Scale updatedDBScale = databaseHandler.getScale(scaleID);
            assertEquals(updatedScale.getName(), updatedDBScale.getName());
            assertEquals(updatedScale.getScaleHashMap(), updatedDBScale.getScaleHashMap());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            assertTrue(false);
        }
    }


    /**
     * creates a scale in the database and reports if delete was successful
     */
    @Test
    public void ScaleDeletedFromDatabaseTest()
    {
        try
        {
            Scale testScale = getTestScale();
            //save scale in db
            DatabaseHandler databaseHandler = new DatabaseHandler();
            int scaleID = databaseHandler.saveScale(testScale);

            Scale scale = databaseHandler.getScale(scaleID);
            assertNotNull(scale);

            databaseHandler.deleteScaleFromDB(scaleID);
            //check if scale exists
            Scale dbScale = databaseHandler.getScale(scaleID);
            assertNull(dbScale);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            assertTrue(false);
        }

    }

    /**
     *
     * @return Chart with some predefined values
     */
    private Chart getTestChart()
    {
        return new Chart(1, ChartType.BAR_HORIZONTAL, "Test", "XTest", "YTest", UUID.randomUUID().toString(), Color.ALICEBLUE, getTestScale(), getTestScale());
    }

    /**
     * creates a chart and saves it to the database. Checks if save was successful
     */
    @Test
    public void ChartSavedInDatabaseTest()
    {
        Chart chart = getTestChart();
        try
        {
            //save chart
            DatabaseHandler databaseHandler = new DatabaseHandler();
            int chartId = databaseHandler.saveChart(chart);

            Chart dbChart = databaseHandler.getChart(chartId);
            assertNotNull(dbChart);
            assertEquals(dbChart.getColor(), chart.getColor());
            assertEquals(dbChart.getTitle(), chart.getTitle());
            assertEquals(dbChart.getType(), chart.getType());
            assertEquals(dbChart.getTableUUID(), chart.getTableUUID());
            assertEquals(dbChart.getX(), chart.getX());
            assertEquals(dbChart.getY(), chart.getY());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * checks if chart update was successful
     */
    @Test
    public void ChartUpdatedInDatabaseTest()
    {
        Chart chart = getTestChart();
        Chart updatedChart = getTestChart();
        updatedChart.setColor(Color.AQUA);
        updatedChart.setType(ChartType.PIE);
        updatedChart.setX("Testing");
        updatedChart.setY("Update Test");
        updatedChart.setTitle("Test Update Title");
        try
        {
            //save chart
            DatabaseHandler databaseHandler = new DatabaseHandler();
            int chartId = databaseHandler.saveChart(chart);
            updatedChart.setID(chartId);
            databaseHandler.updateChart(updatedChart);

            Chart dbUpdatedChart = databaseHandler.getChart(chartId);
            assertEquals(updatedChart.getX(), dbUpdatedChart.getX());
            assertEquals(updatedChart.getColor(), dbUpdatedChart.getColor());
            assertEquals(updatedChart.getTitle(), dbUpdatedChart.getTitle());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * saves and removes chart from database. Checks if chart was really removed
     */
    @Test
    public void ChartRemovedFromDatabaseTest()
    {
        Chart chart = getTestChart();
        try
        {
            //save chart
            DatabaseHandler databaseHandler = new DatabaseHandler();
            int chartId = databaseHandler.saveChart(chart);

            Chart dbChart = databaseHandler.getChart(chartId);
            assertNotNull(dbChart);

            databaseHandler.deleteChartFromDB(chartId);
            Chart dbNullChart = databaseHandler.getChart(chartId);
            assertNull(dbNullChart);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    private Dashboard getTestDashboard()
    {
        return new Dashboard("TestDashboard");
    }

    /**
     * saves dashboard in database and checks if it was successful
     */
    @Test
    public void DashboardSavedInDatabaseTest()
    {
        try
        {
            //save dashboard
            DatabaseHandler databaseHandler = new DatabaseHandler();
            Dashboard dashboard = getTestDashboard();
            int dashboardId = databaseHandler.saveDashboard(dashboard);

            //get dashboard from db
            Dashboard dbDashboard = databaseHandler.getDashboard(dashboardId);
            assertNotNull(dbDashboard);
            assertEquals(dbDashboard.getName(), dashboard.getName());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * updates dashboard in database
     */
    @Test
    public void DashboardUpdatedInDatabaseTest()
    {
        try
        {
            //save chart
            DatabaseHandler databaseHandler = new DatabaseHandler();
            Dashboard dashboard = getTestDashboard();
            int dashboardId = databaseHandler.saveDashboard(dashboard);

            //update existing dashboard
            Dashboard updatedDashboard = new Dashboard("UpdatedDashboard");
            updatedDashboard.setID(dashboardId);
            databaseHandler.updateDashboard(updatedDashboard);

            //check if dashboard was updated
            Dashboard updatedDBDashboard = databaseHandler.getDashboard(dashboardId);
            assertNotNull(updatedDBDashboard);
            assertEquals(updatedDBDashboard.getName(), updatedDashboard.getName());
            assertNotEquals(updatedDBDashboard.getName(), dashboard.getName());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * checks if the deletion of dashboards works
     */
    @Test
    public void DashboardDeletedFromDatabaseTest()
    {
        try
        {
            //save chart
            DatabaseHandler databaseHandler = new DatabaseHandler();
            Dashboard dashboard = getTestDashboard();
            int dashboardId = databaseHandler.saveDashboard(dashboard);

            //delete dashboard
            databaseHandler.deleteDashboard(dashboardId);

            //check if null --> successfully deleted
            assertNull(databaseHandler.getDashboard(dashboardId));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
