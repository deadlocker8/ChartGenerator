package de.lww4.tests.importer;


import de.lww4.logic.DelimiterType;
import de.lww4.logic.Importer;
import java.io.File;
import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author max
 */
public class InvalidCSVTest
{
    protected final File file = new File("src/de/lww4/tests/importer/data/invalid.csv");
    protected final String fillValue = "0";
    protected final Importer importer = new Importer(file, DelimiterType.COMMA, fillValue, "invalid.csv");

    @Test(expected = NumberFormatException.class)
    public void LetterError()
    {
        double testDouble = Double.parseDouble(importer.getData().get(0).get(0));
    }

    /**
     * checks for invalid separator
     */
    @Test
    public void InvalidSeparator()
    {
        for(ArrayList<String> row : importer.getData())
        {
            for(String value : row)
            {
                if(value.contains(";"))
                {
                    assertTrue(true);
                    return;
                }
            }
        }

        assertTrue(false);
    }
}
