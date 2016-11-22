package de.lww4.logic;

import javafx.scene.paint.Color;

public class Chart
{
	private int ID;
	private ChartType type;
	private String title;
	private String x, y;
	private String tableUUID;
	private Color color;

	public Chart(int ID, ChartType type, String title, String x, String y, String tableUUID, Color color)
	{
		this.ID = ID;
		this.type = type;
		this.title = title;
		this.x = x;
		this.y = y;
		this.tableUUID = tableUUID;
		this.color = color;
	}

	public int getID()
	{
		return ID;
	}

	public void setID(int ID)
	{
		this.ID = ID;
	}

	public ChartType getType()
	{
		return type;
	}

	public void setType(ChartType type)
	{
		this.type = type;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getX()
	{
		return x;
	}

	public void setX(String x)
	{
		this.x = x;
	}

	public String getY()
	{
		return y;
	}

	public void setY(String y)
	{
		this.y = y;
	}

	public String getTableUUID()
	{
		return tableUUID;
	}

	public void setTableUUID(String tableUUID)
	{
		this.tableUUID = tableUUID;
	}

	public Color getColor()
	{
		return color;
	}

	public void setColor(Color color)
	{
		this.color = color;
	}
	
	public String toString()
	{
		return "Chart [ID=" + ID + ", type=" + type + ", title=" + title + ", x=" + x + ", y=" + y + ", tableUUID=" + tableUUID + ", color=" + color + "]";
	}	
}