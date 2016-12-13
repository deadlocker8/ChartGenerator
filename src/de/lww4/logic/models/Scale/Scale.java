package de.lww4.logic.models.Scale;


import java.util.HashMap;

public class Scale
{
    private HashMap<Double, String> scaleHashMap;
    private int id;
    private String name;

    public Scale(int id, String name)
    {
        scaleHashMap = new HashMap<>();
        this.id = id;
        this.name = name;
    }

    public HashMap<Double, String> getScaleHashMap()
    {
        return scaleHashMap;
    }

    public int getId()
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
}
