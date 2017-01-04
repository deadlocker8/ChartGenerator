package de.lww4.logic.models.chart;

import de.lww4.logic.models.enums.ChartType;
import de.lww4.logic.models.scale.Scale;
import javafx.scene.paint.Color;

/**
 * model class for a chart
 * @author Maxie
 */
public class Chart
{
	private int ID;
	private ChartType type;
	private String title;
	private String x, y;
	private String tableUUID;
	private Color color;
	private Scale scale;
	private Scale legendScale;

	public Chart(int ID, ChartType type, String title, String x, String y, String tableUUID, Color color, Scale scale, Scale legendScale)
	{
		this.ID = ID;
		this.type = type;
		this.title = title;
		this.x = x;
		this.y = y;
		this.tableUUID = tableUUID;
		this.color = color;
		this.scale = scale;
		this.legendScale = legendScale;
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
	
	public Scale getScale()
	{
		return scale;
	}	

	public void setScale(Scale scale)
	{
		this.scale = scale;
	}	

	public Scale getLegendScale()
	{
		return legendScale;
	}

	public void setLegendScale(Scale legendScale)
	{
		this.legendScale = legendScale;
	}

	@Override
	public String toString()
	{
		return "Chart [ID=" + ID + ", type=" + type + ", title=" + title + ", x=" + x + ", y=" + y + ", tableUUID=" + tableUUID + ", color=" + color + ", scale=" + scale + ", legendScale=" + legendScale + "]";
	}
}