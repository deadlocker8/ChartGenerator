package de.lww4.logic.models.Scale;


/**
 * ScaleItem class
 * ScaleHashMapItem as class for the controller to work with
 * @author max
 */
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

	public void setKey(Double key)
	{
		this.key = key;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	@Override
	public String toString()
	{
		return "ScaleItem [key=" + key + ", value=" + value + "]";
	}
}