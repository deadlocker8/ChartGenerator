package de.lww4.logic;

import java.util.ArrayList;

public class DashboardHandler 
{
	private ArrayList<Dashboard> dashboards;
	
	public DashboardHandler() 
	{
		dashboards= new ArrayList<Dashboard>();
	}

	public ArrayList<Dashboard> getDashboards()
	{
		return dashboards;
	}	
}