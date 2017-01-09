package de.lww4.logic;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import de.lww4.logic.models.enums.DelimiterType;

import java.io.*;
import java.util.ArrayList;

/**
 * this class has methods for reading and parsing csv files and returns their column rows and their data
 * from the filesystem
 * @author max
 */
public class Importer
{
    private File file;
    private DelimiterType delimiter;
    private ArrayList<ArrayList<String>> data;
    private ArrayList<String> columnNamesArrayList;
    private String fillValue;
    private String name;

    public Importer(File file, DelimiterType delimiter, String fillValue, String name)
    {
        this.fillValue = fillValue;
        columnNamesArrayList = new ArrayList<>();
        data = new ArrayList<>();
        this.file = file;
        this.name = name;
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
            CSVParser parser = new CSVParser(inputStreamReader, CSVFormat.newFormat(delimiter.getDelimiter()));

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
            fillEmptyCells();
        }
        catch (IOException io)
        {
            io.printStackTrace();
        }
    }

    /**
     * finds the longest row size
     * @return int longest row size of all data rows
     */
    public int getLongestRowSize()
    {
        int length = 0;
        for(ArrayList<String> row : data)
        {
            if(row.size() > length)
            {
                length = row.size();
            }
        }
        return length;
    }

    /**
     * fill empty cells with given fillValue
     */
    private void fillEmptyCells()
    {
        int longestRow = getLongestRowSize();
        for(int i=0; i < data.size(); i++)
        {
            ArrayList<String> row = data.get(i);
            while (row.size() < longestRow)
            {
                row.add(fillValue);
            }

            for(int j=0; j < row.size(); j++)
            {
                if(row.get(j).trim().equals(""))
                {
                    row.set(j, fillValue);
                }
            }
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
     * is called, if certain rows are disabled. These are then removed from the ArrayList
     * @param newColumnNamesArrayList ArrayList<String> columnNames
     */
    public void setColumnNamesArrayList(ArrayList<String> newColumnNamesArrayList)
    {
        this.columnNamesArrayList = newColumnNamesArrayList;
    }

    public void removeColumns(ArrayList<Integer> columnsToDisable)
    {
        //also remove data from data columns
        for(Integer number : columnsToDisable)
        {
            for(int i=0; i < data.size(); i++)
            {
                //remove data column
                ArrayList<String> currentRow = data.get(i);
                currentRow.set(number, null);
            }
        }
        removeNullValues();

    }

    private void removeNullValues()
    {
        for(int i=0; i < data.size(); i++)
        {
            ArrayList<String> currentRow = data.get(i);
            for(int s=0; s < currentRow.size(); s++)
            {
                if(currentRow.get(s) == null)
                {
                    currentRow.remove(s);
                }
            }
        }
    }

    /**
     *
     * @return name of the csv file
     */
    public String getName() {return name;}

    /**
     * test method for class
     * @param args arguments
     */
    public static void main(String args[])
    {
        Importer importer = new Importer(new File("test.csv"), DelimiterType.SEMICOLON, "0", "Testing");
        System.out.println(importer.getColumnNames());
        System.out.println(importer.getData());
    }
}