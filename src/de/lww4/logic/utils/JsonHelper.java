package de.lww4.logic.utils;

import com.google.gson.Gson;

import java.util.HashMap;

/**
 * JsonHelper class
 *
 * @author Max
 */

public class JsonHelper
{
    public static HashMap<Double, String> getScaleHashMapFromJson(String jsonContent)
    {
        Gson gson = new Gson();
        HashMap<Double, String> hashMap = gson.fromJson(jsonContent, HashMap.class);
        return hashMap;
    }

    public static String convertScaleHashMapToJson(HashMap<Double, String> hashMap)
    {
        Gson gson = new Gson();
        return gson.toJson(hashMap);
    }
}
