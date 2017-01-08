package de.lww4.logic.models;

import java.io.Serializable;

/**
 * represents one entry in the TreeView in NewChartController
 * 
 * @author Robert
 *
 */
public class ColumnTreeItem implements Serializable
{	
	//required by Serializable interface
	private static final long serialVersionUID = 4856874725073524494L;
	private String tableUUID;
	private String	text;
	private boolean draggable;
	
	public ColumnTreeItem(String tableUUID, String text, boolean dragable)
	{		
		this.tableUUID = tableUUID;
		this.text = text;
		this.draggable = dragable;
	}		

	public String getTableUUID()
	{
		return tableUUID;
	}

	public String getText()
	{
		return text;
	}

	public boolean isDraggable()
	{
		return draggable;
	}
	
	public String toString()
	{
		return "ColumnTreeItem [text=" + text + ", draggable=" + draggable + "]";
	}
}