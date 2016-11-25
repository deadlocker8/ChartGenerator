package de.lww4.logic;

import java.io.Serializable;

public class ColumnTreeItem implements Serializable
{	
	private static final long serialVersionUID = 4856874725073524494L;
	private String tableUUID;
	private String	text;
	private boolean dragable;
	
	public ColumnTreeItem(String tableUUID, String text, boolean dragable)
	{		
		this.tableUUID = tableUUID;
		this.text = text;
		this.dragable = dragable;
	}		

	public String getTableUUID()
	{
		return tableUUID;
	}

	public String getText()
	{
		return text;
	}

	public boolean isDragable()
	{
		return dragable;
	}
	
	public String toString()
	{
		return "ColumnTreeItem [text=" + text + ", dragable=" + dragable + "]";
	}
}