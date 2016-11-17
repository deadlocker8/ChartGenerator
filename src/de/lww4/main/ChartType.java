package de.lww4.main;

public enum ChartType
{
	BAR_HORIZONTAL(0, "Balkendiagramm"),
	BAR_VERTICAL(1, "Säulendiagramm"),
	PI(2, "Tortendiagramm");

	private int ID;
	private String name;

	private ChartType(int ID, String name)
	{
		this.ID = ID;
		this.name = name;
	}

	public int getID()
	{
		return ID;
	}

	public String getName()
	{
		return name;
	}

	public static ChartType valueOf(int ID)
	{
		for(ChartType currentType : ChartType.values())
		{
			if(ID == currentType.getID())
			{
				return currentType;
			}
		}
		return null;
	}
}