package de.lww4.main;

import java.util.ArrayList;

public class Dashboard 
{
	private int id;
	private String name;
	private ArrayList<Integer> columns;
	
	public Dashboard() 
	{
		columns= new ArrayList<Integer>();
	}

	public Dashboard(int id, String name, int col1, int col2, int col3, int col4, int col5, int col6)
	{
		this.id = id;
		this.name = name;
		
		columns= new ArrayList<Integer>();
		columns.add(col1);
		columns.add(col2);
		columns.add(col3);
		columns.add(col4);
		columns.add(col5);
		columns.add(col6);
	}
	
	public int getId()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	public ArrayList<Integer> getColumns()
	{
		return columns;
	}
}
