package de.lww4.logic.models;

import java.util.ArrayList;

/**
 * represents an imported csv table in the database
 * @author Robert
 *
 */
public class CSVTable
{
	private String uuid;
	private String name;
	private String creationDate;
	private ArrayList<String> columnNames;	
	
	public CSVTable(String uuid, String name, String creationDate, ArrayList<String> columnNames)
	{	
		this.uuid = uuid;
		this.name = name;
		this.creationDate = creationDate;
		this.columnNames = columnNames;
	}
	
	public String getUuid()
	{
		return uuid;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getCreationDate()
	{
		return creationDate;
	}
	
	public ArrayList<String> getColumnNames()
	{
		return columnNames;
	}

	public String toString()
	{
		return "CSVTable [uuid=" + uuid + ", name=" + name + ", creationDate=" + creationDate + ", columnNames=" + columnNames + "]";
	}	
}