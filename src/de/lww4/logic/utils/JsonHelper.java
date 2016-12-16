package de.lww4.logic.utils;

import java.util.HashMap;

import com.google.gson.Gson;

/**
 * JsonHelper class
 *
 * @author Max
 */

public class JsonHelper
{
	@SuppressWarnings("unchecked")
	public static HashMap<Double, String> getScaleHashMapFromJson(String jsonContent)
	{
		Gson gson = new Gson();
		HashMap<String, String> hashMap = gson.fromJson(jsonContent, HashMap.class);
		
		HashMap<Double, String> result = new HashMap<>();
		
		for(String key : hashMap.keySet())
		{
			result.put(Double.valueOf(key), hashMap.get(key));
		}		
		
		return result;
	}

	public static String convertScaleHashMapToJson(HashMap<Double, String> hashMap)
	{
		Gson gson = new Gson();
		return gson.toJson(hashMap);
	}
}