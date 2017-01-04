package de.lww4.logic.models;

import java.util.ArrayList;

public class Dashboard 
{
	private int ID;
	private String name;
	private ArrayList<Integer> cells;
	
	public Dashboard(String name) 
	{
		this.ID = -1;
		this.name = name;
		cells = new ArrayList<Integer>();
		for(int i = 0; i < 6; i++)
		{
			cells.add(-1);
		}	
	}

	public Dashboard(int ID, String name, int cell1, int cell2, int cell3, int cell4, int cell5, int cell6)
	{
		this.ID = ID;
		this.name = name;
		
		cells= new ArrayList<Integer>();
		cells.add(cell1);
		cells.add(cell2);
		cells.add(cell3);
		cells.add(cell4);
		cells.add(cell5);
		cells.add(cell6);
	}

    public void setID(int ID)
    {
        this.ID = ID;
    }

    public int getID()
	{
		return ID;
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
	
	public String toString()
	{
		return "Dashboard [ID=" + ID + ", name=" + name + ", cells=" + cells + "]";
	}
}