package de.lww4.logic;


import de.lww4.logic.models.Scale.Scale;

import java.util.ArrayList;

/**
 * ScaleHandler
 *
 * @author max
 */
public class ScaleHandler
{
    public ArrayList<Scale> scales;

    public ScaleHandler()
    {
        scales = new ArrayList<>();        
        //TODO add empty object that user can choose if he doesn't want to use a scale anymore
    }
    
    public ScaleHandler(ArrayList<Scale> scales)
    {
        this.scales = scales;       
    }
    
    public ArrayList<Scale> getScales()
    {
        return scales;
    }

    /**
     * delete scale with certain id from ArrayList
     * @param id certain id
     */
    public void deleteScale(int id)
    {
        for (int i = 0; i < scales.size(); i++)
        {
            if (scales.get(i).getID() == id)
            {
                scales.remove(i);
            }
        }
    }
}