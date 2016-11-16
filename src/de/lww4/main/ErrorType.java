package de.lww4.main;


import java.util.ArrayList;

public enum ErrorType
{
    NO_FILE("Datei","Keine Datei zum oeffnen angegeben!"),
    NO_FILLER("Fuellwert","Kein Fuellwert angegeben!"),
    NO_CHARTNAME("Chartnamen","Kein Chartnamen vergeben!");

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
        stringBuilder.append("Fehlende Eingaben:\n");
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
