package de.lww4.main;

public class Chart 
{
	private int id;
	private ChartType type;
	private String title;
	//?
	private String x, y;
	private String tableUUID;
	private String color;
	
	public Chart(int id, ChartType type, String title, String x, String y, String tableUUID, String color) 
	{
		this.id =id;
		this.type=type;
		this.title=title;
		this.x=x;
		this.y=y;
		this.tableUUID=tableUUID;
		this.color=color;
	}

	
	//Auto-generated Getter/Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ChartType getType() {
		return type;
	}

	public void setType(ChartType type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

	public String getTableUUID() {
		return tableUUID;
	}

	public void setTableUUID(String tableUUID) {
		this.tableUUID = tableUUID;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}


}
