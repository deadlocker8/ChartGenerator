package de.lww4.logic.utils;

import javafx.scene.paint.Color;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

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
}