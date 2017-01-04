package de.lww4.logic.utils;

import javafx.scene.paint.Color;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import de.lww4.logic.ChartSet;
import de.lww4.logic.ChartSetItem;

/**
 * general utils
 * @author Robert
 */
public class Utils
{
	public static String toRGBHex(Color color)
	{		
		return String.format("#%02X%02X%02X%02X", (int)(color.getRed() * 255), (int)(color.getGreen() * 255), (int)(color.getBlue() * 255), (int)(color.getOpacity()* 255));
	}

    public static String getContentsFromInputStream(InputStream inputStream)
    {
        try
        {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream));
            String line = "";
            StringBuilder text = new StringBuilder();

            while (line != null)
            {
                line = bufferedReader.readLine();
                if (line != null)
                    text.append(line);
            }
            return text.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * splits given list of ChartSetItems into CharSets
     * @param items - ArrayList<ChartSetItem>
     * @return ArrayList<ChartSet> - chartsets
     */
    public static ArrayList<ChartSet> splitIntoChartSets(ArrayList<ChartSetItem> items)
    {
    	ArrayList<ChartSet> sets = new ArrayList<>();
    	
    	Set<Double> possibleSetNames = new HashSet<>();
    	for(ChartSetItem currentItem : items)
    	{
    		possibleSetNames.add(currentItem.getSet());
    	}
    	
    	for(Double currentSetName : possibleSetNames)
    	{
    		sets.add(new ChartSet(currentSetName));
    	}
    	
    	for(ChartSetItem currentItem : items)
    	{
    		for(ChartSet currentSet : sets)
    		{
    			if(Math.abs(currentItem.getSet() - currentSet.getSetName()) < 0.0000001)
    			{
    				currentSet.getScaleItems().add(currentItem);
    			}
    		}
    	}    	
    	
    	return sets;    	
    }
}