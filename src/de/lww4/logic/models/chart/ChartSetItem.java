package de.lww4.logic.models.chart;

public class ChartSetItem
{
	private double set;
	private double count;
	private double label;
	
	public ChartSetItem(double set, double count, double label)
	{
		this.set = set;
		this.count = count;
		this.label = label;
	}

	public double getSet()
	{
		return set;
	}

	public double getCount()
	{
		return count;
	}

	public double getLabel()
	{
		return label;
	}

	@Override
	public String toString()
	{
		return "ChartSetItem [set=" + set + ", count=" + count + ", label=" + label + "]";
	}
}