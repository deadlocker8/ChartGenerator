package de.lww4.tests.importer;

import de.lww4.logic.DelimiterType;
import de.lww4.logic.Importer;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * @author max
 */
public class EdgecaseCSVTest
{
    protected final File file = new File("src/de/lww4/tests/importer/data/edgecase.csv");
    protected final String fillValue = "0";
    protected final Importer importer = new Importer(file, DelimiterType.COMMA, fillValue, "edgecase.csv");

    @Test
    public void UmlautTest()
    {
        assertTrue(importer.getColumnNames().get(0).equals("TöstDütä0"));
    }

    @Test
    public void SpecialCharsTest()
    {
        assertTrue(importer.getColumnNames().get(1).equals("TestD&ta1"));
    }

    @Test
    public void OtherCharsetTest()
    {
        assertTrue(importer.getColumnNames().get(3).equals("Test汉字ata3"));
    }

    @Test
    public void EmptySpaceTest()
    {
        assertTrue(importer.getColumnNames().get(4).equals("Test data"));
    }
}
