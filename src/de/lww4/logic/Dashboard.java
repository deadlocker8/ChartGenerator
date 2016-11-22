package de.lww4.logic;

import java.util.ArrayList;

public class Dashboard 
{
	private int id;
	private String name;
	private ArrayList<Integer> cells;
	
	public Dashboard(String name) 
	{
		this.id = -1;
		this.name = name;
		cells = new ArrayList<Integer>();
	}

	public Dashboard(int id, String name, int cell1, int cell2, int cell3, int cell4, int cell5, int cell6)
	{
		this.id = id;
		this.name = name;
		
		cells= new ArrayList<Integer>();
		cells.add(cell1);
		cells.add(cell2);
		cells.add(cell3);
		cells.add(cell4);
		cells.add(cell5);
		cells.add(cell6);
	}
	
	public int getId()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public ArrayList<Integer> getCells()
	{
		return cells;
	}
}