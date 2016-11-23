package de.lww4.tests.importer;


import de.lww4.logic.DelimiterType;
import de.lww4.logic.Importer;

import java.io.File;

public class AbstractCSVTests
{
    protected final File file = new File("src/de/lww4/tests/data/valid.csv");
    protected final String fillValue = "0";
    protected final Importer importer = new Importer(file, DelimiterType.COMMA, fillValue, "valid.csv");

}
