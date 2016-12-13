package de.lww4.logic.models.Scale;


public class ScaleItem
{
    public ScaleItem(Double key, String value)
    {
        this.key = key;
        this.value = value;
    }

    private Double key;
    private String value;

    public Double getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}
