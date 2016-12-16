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
	@SuppressWarnings("rawtypes")
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
}