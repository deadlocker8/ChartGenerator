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
        initDumpScales();
        //TODO add empty object that user can choose if he doesn't want to use a scale anymore
    }

    //TODO REMOVE THIS TEST METHOD!
    private void initDumpScales()
    {
        //Init dump scale
        Scale scale = new Scale(0, "Geschlechter", null);
        scale.getScaleHashMap().put(0.0, "Männlich");
        scale.getScaleHashMap().put(1.0, "Weiblich");
        scales.add(scale);
    }

    public ArrayList<Scale> getScales()
    {
        return scales;
    }

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