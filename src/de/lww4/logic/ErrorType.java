package de.lww4.logic;


import java.util.ArrayList;

/**
 * ErrorType class
 *
 * @author max
 */
public enum ErrorType
{
    NO_FILE("Datei","Wählen Sie eine CSV-Datei aus."),
    NO_FILLER("Füllwert","Bitte geben Sie einen Füllwert an."),
    NO_CHARTNAME("Name","Bitte vergeben Sie einen Namen für die importierte Datei"),
    INVALID_FILLER("Füllwert", "Bitte geben Sie eine gültige Zahl als Füllwert an");

    private String errorMessage;
    private String errorShortMessage;

    private ErrorType(String errorShortMessage, String errorMessage)
    {
        this.errorMessage = errorMessage;
        this.errorShortMessage = errorShortMessage;
    }

    public static String getErrorMessage(ArrayList<ErrorType> errorTypes)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Es fehlen einige Eingaben oder sind fehlerhaft.\nBitte füllen Sie die folgenden Felder richtig aus:\n");
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