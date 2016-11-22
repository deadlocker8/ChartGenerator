package de.lww4.logic;

public enum DelimiterType
{	
	SEMICOLON(';', "Semikolon"),
	COMMA(',', "Komma"),	
	DOT('.', "Punkt"),
	SPACE(' ', "Leerzeichen"),
	TABULATOR('\t', "Tabulator");	
	
	private char delimiter;
	private String name;
	
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
	
	@Override
	public String toString()
	{
		return name;
	}
}