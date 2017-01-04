package de.lww4.logic.models.enums;

public enum DelimiterType
{	
	SEMICOLON(';', "Semikolon"),
	COMMA(',', "Komma"),	
	DOT('.', "Punkt"),
	SPACE(' ', "Leerzeichen"),
	TABULATOR('\t', "Tabulator");	
	
	private char delimiter;
	private String name;
    private static DelimiterType[] delimiterTypes = values();

    private DelimiterType(char delimiter, String name)
	{
		this.delimiter = delimiter;
		this.name = name;
	}

	public char getDelimiter()
	{
		return delimiter;
	}

	public String getName()
	{
		return name;
	}

    public static String getPossibleDelimiterString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < delimiterTypes.length; i++)
        {
            DelimiterType currentType = delimiterTypes[i];
            stringBuilder.append(currentType.getName());
            if (i < delimiterTypes.length - 1)
            {
                stringBuilder.append(", ");
            }

        }
        return stringBuilder.toString();
    }

	@Override
	public String toString()
	{
		return name;
	}
}