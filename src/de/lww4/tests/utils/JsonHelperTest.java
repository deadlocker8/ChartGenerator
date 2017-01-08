package de.lww4.tests.utils;


import de.lww4.logic.utils.JsonHelper;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;

/**
 * @author max
 *
 * verifies functionality of the JsonHelper class
 *
 */
public class JsonHelperTest
{
    @Test
    public void JsonToScaleHashMapTest()
    {
        HashMap<Double, String> hashMap = new HashMap<>();
        hashMap.put(1.0, "Männlich");
        hashMap.put(2.0, "Weiblich");

        String jsonString = "{" +
                "\"1.0\":" + "\"Männlich\""+ ","+
                "\"2.0\":" + "\"Weiblich\""+
                "}";
        assertEquals(JsonHelper.getScaleHashMapFromJson(jsonString), hashMap);
    }

    @Test
    public void ScaleHashMapToJsonTest()
    {
        HashMap<Double, String> hashMap = new HashMap<>();
        hashMap.put(1.0, "Männlich");
        hashMap.put(2.0, "Weiblich");

        String jsonString = "{" +
                "\"1.0\":" + "\"Männlich\""+ ","+
                "\"2.0\":" + "\"Weiblich\""+
                "}";
        assertEquals(JsonHelper.convertScaleHashMapToJson(hashMap), jsonString);
    }
}
