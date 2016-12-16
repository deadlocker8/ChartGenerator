package de.lww4.logic.models.Scale;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Scale class
 *
 * @author max
 */
public class Scale
{
    private HashMap<Double, String> scaleHashMap;
    private int id;
    private String name;

    public Scale(int id, String name, HashMap<Double, String> hashMap)
    {
    	if(hashMap != null)
    	{
    		scaleHashMap = hashMap;
    	}
    	else
    	{
    		scaleHashMap = new HashMap<Double, String>();
    	}
        this.id = id;
        this.name = name;
    }

    public Scale(int id, String name)
    {   
    	this.id = id;
        this.name = name;
    	scaleHashMap = new HashMap<Double, String>();
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

    public void setScaleItems(ArrayList<ScaleItem> scaleItems)
    {
        scaleHashMap = new HashMap<>();
        for(ScaleItem currentItem : scaleItems)
        {
            scaleHashMap.put(currentItem.getKey(), currentItem.getValue());
        }
    }

	@Override
	/**
	 * needed for comboxbox to display correct name for selected item
	 */
	public String toString()
	{
		return name;
	}
}