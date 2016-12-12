package de.lww4.logic.models;


import java.util.HashMap;

public class Scale
{
    private HashMap<String, Double> scaleHashMap;
    private int id;
    private String name;

    public Scale(int id, String name)
    {
        scaleHashMap = new HashMap<>();
        this.id = id;
        this.name = name;
    }

    public HashMap<String, Double> getScaleHashMap()
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


}
