package de.lww4.logic.utils;

import com.google.gson.*;

import java.util.HashMap;
import java.util.Map;

/**
 * JsonHelper class
 *
 * @author Max
 */

public class JsonHelper
{
    public static HashMap<Double, String> getScaleHashMapFromJson(String jsonContent)
    {
        HashMap<Double, String> scaleHashMap = new HashMap<>();
        JsonParser jsonParser = new JsonParser();
        JsonElement root = jsonParser.parse(jsonContent);

        for(Map.Entry entry : root.getAsJsonObject().entrySet())
        {
            scaleHashMap.put(Double.valueOf(entry.getKey().toString()), entry.getValue().toString());
        }
        return scaleHashMap;
    }

    public static String convertScaleHashMapToJson(HashMap<Double, String> hashMap)
    {
        Gson gson = new Gson();
        return gson.toJson(hashMap);
    }

    public static void main(String args[])
    {
        HashMap<Double, String> hashMap = new HashMap<>();
        hashMap.put(12.5, "Test");
        hashMap.put(142.5, "dsf");
        String hashMapString = convertScaleHashMapToJson(hashMap);
        System.out.println(hashMapString);
        System.out.println(getScaleHashMapFromJson(hashMapString));
    }
}
