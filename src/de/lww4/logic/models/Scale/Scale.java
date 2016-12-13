package de.lww4.logic.models.Scale;


import java.util.HashMap;

public class Scale
{
    private HashMap<Double, String> scaleHashMap;
    private int id;
    private String name;

    public Scale(int id, String name, String data)
    {
        scaleHashMap = new HashMap<>();
        this.id = id;
        this.name = name;
        //TODO use data
    }

    public HashMap<Double, String> getScaleHashMap()
    {
        return scaleHashMap;
    }

    public int getID()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
    
    //TODO return data
    public String getData()
    {
    	return "";
    }
}