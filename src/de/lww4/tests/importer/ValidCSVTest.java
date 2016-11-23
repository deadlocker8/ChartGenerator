package de.lww4.tests.importer;
import org.junit.Test;
import static org.junit.Assert.*;


public class ValidCSVTest extends AbstractCSVTests
{

    /**
     * 4 columns exist
     */
    @Test
    public void ColumnNamesTest()
    {
        assertTrue(importer.getColumnNames().size() == 4);
        assertTrue(importer.getColumnNames().get(2).equals("TestData2"));
    }

    /**
     * tests, if certain values exist
     */
    @Test
    public void ChosenValueTest()
    {
        assertTrue(importer.getData().get(0).get(0).equals("420.4059"));
        assertTrue(importer.getData().get(50).get(3).equals("633.5"));
    }

    /**
     * Tests, if value was successfully filled
     */
    @Test
    public void FillValueTest()
    {
        assertTrue(importer.getData().get(14).get(1).equals(fillValue));
        assertTrue(importer.getData().get(0).get(importer.getLongestRow()-1).equals(fillValue));
    }

}
