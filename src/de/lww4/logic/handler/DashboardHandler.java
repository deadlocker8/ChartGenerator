package de.lww4.logic.handler;

import java.util.ArrayList;

import de.lww4.logic.models.Dashboard;

public class DashboardHandler
{
	private ArrayList<Dashboard> dashboards;

	public DashboardHandler(ArrayList<Dashboard> dashboards)
	{
		this.dashboards = dashboards;
	}

	public ArrayList<Dashboard> getDashboards()
	{
		return dashboards;
	}

	public boolean isNameAlreadyInUse(String name)
	{
		for(Dashboard currentDashboard : dashboards)
		{
			if(currentDashboard.getName().equals(name))
			{
				return true;
			}
		}
		
		return false;
	}
}