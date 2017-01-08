package de.lww4.logic.handler;


import java.util.ArrayList;

import de.lww4.logic.models.scale.Scale;

/**
 * holds all available scales
 *
 * @author max
 */
public class ScaleHandler
{
    public ArrayList<Scale> scales;

    public ScaleHandler()
    {
        scales = new ArrayList<>();     
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
     * @param id id
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