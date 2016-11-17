package de.lww4.main;

public enum ChartType 
{	
	BAR_HORIZONTAL("Balkendiagramm"), 
	BAR_VERTICAL("Säulendiagramm"),
	PI("Tortendiagramm");
	
	private String name;
	
	private ChartType(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
}