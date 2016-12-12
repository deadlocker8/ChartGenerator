package de.lww4.logic;


import de.lww4.logic.models.Scale;

import java.util.ArrayList;

public class ScaleHandler
{
    public ArrayList<Scale> scales;

    public ScaleHandler()
    {
        initDumpScales();
    }

    //TODO REMOVE THIS TEST METHOD!
    private void initDumpScales()
    {
        //Init dump scale
        Scale scale = new Scale(0, "Geschlechter");
        scale.getScaleHashMap().put("Männlich", 0.0);
        scale.getScaleHashMap().put("Weiblich", 1.0);
    }

    public ArrayList<Scale> getScales()
    {
        return scales;
    }

    public void deleteScale(int id)
    {
        for (int i = 0; i < scales.size(); i++)
        {
            if (scales.get(i).getId() == id)
            {
                scales.remove(i);
            }
        }
    }


}
