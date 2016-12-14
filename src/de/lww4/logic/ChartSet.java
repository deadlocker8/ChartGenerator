package de.lww4.logic;

import java.util.ArrayList;

public class ChartSet
{
	private double setName;
	private ArrayList<ChartSetItem> scaleItems;
	
	public ChartSet(double setName)
	{
		this.setName = setName;
		this.scaleItems = new ArrayList<>();
	}

	public double getSetName()
	{
		return setName;
	}

	public ArrayList<ChartSetItem> getScaleItems()
	{
		return scaleItems;
	}

	@Override
	public String toString()
	{
		return "ChartSet [setName=" + setName + ", scaleItems=" + scaleItems + "]";
	}
}