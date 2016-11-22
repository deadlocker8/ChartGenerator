package de.lww4.logic;


import java.util.ArrayList;

public enum ErrorType
{
    NO_FILE("Datei","W�hlen Sie eine CSV-Datei aus."),
    NO_FILLER("F�llwert","Bitte geben Sie einen F�llwert an."),
    NO_CHARTNAME("Name","Bitte vergeben Sie einen Namen f�r die importierte Datei");

    private String errorMessage;
    private String errorShortMessage;

    ErrorType(String errorShortMessage, String errorMessage)
    {
        this.errorMessage = errorMessage;
        this.errorShortMessage = errorShortMessage;
    }

    public static String getErrorMessage(ArrayList<ErrorType> errorTypes)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Es fehlen einige Eingaben.\nBitte f�llen Sie die folgenden Felder aus:\n");
        for(ErrorType currentType : errorTypes)
        {
            stringBuilder.append(currentType.getErrorShortMessage()+"\n");
        }
        return stringBuilder.toString();
    }

    public String getErrorShortMessage()
    {
        return errorShortMessage;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }
}