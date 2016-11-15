package de.lww4.main;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.ArrayList;

/**
 * this class has methods for reading and parsing csv files and returns their column rows and their data
 * from the filesystem
 */
public class Importer
{
    private File file;
    private char delimiter;
    private ArrayList<ArrayList<String>> data;
    private ArrayList<String> columnNamesArrayList;

    public Importer(File file, char delimiter)
    {
        columnNamesArrayList = new ArrayList<>();
        data = new ArrayList<>();
        this.file = file;
        this.delimiter = delimiter;
        importData();
    }

    /**
     * imports the data from the filesystem and writes it in ArrayLists
     * Called in constructor
     */
    private void importData()
    {
        try
        {
            //Read file
            FileInputStream fileInputStream = new FileInputStream(file);
            Reader inputStreamReader = new InputStreamReader(fileInputStream);
            CSVParser parser = new CSVParser(inputStreamReader, CSVFormat.newFormat(delimiter));

            //loops through all the rows in the csv file
            for (CSVRecord record : parser)
            {
                //get header row --> 1 indexed
                if(record.getRecordNumber() == 1)
                {
                    //0 indexed
                    for(String rowName : record)
                    {
                        columnNamesArrayList.add(rowName);
                    }
                }
                else
                {
                    //write the rest of the data in data ArrayList
                    ArrayList<String> dataRow = new ArrayList<>();
                    for(String rowName : record)
                    {
                        dataRow.add(rowName);
                    }
                    data.add(dataRow);
                }
            }

            //close the parser and reader
            parser.close();
            inputStreamReader.close();
        }
        catch (IOException io)
        {
            io.printStackTrace();
        }
    }

    /**
     *
     * @return ArrayList with the data in it. Doesn't contain the column names
     */
    public ArrayList<ArrayList<String>> getData()
    {
        return data;
    }

    /**
     *
     * @return column names of the csv file
     */
    public ArrayList<String> getColumnNames()
    {
        return columnNamesArrayList;
    }

    /**
     * test method for class
     * @param args
     */
    public static void main(String args[])
    {
        Importer importer = new Importer(new File("test.csv"), ';');
        System.out.println(importer.getColumnNames());
        System.out.println(importer.getData());
    }

}