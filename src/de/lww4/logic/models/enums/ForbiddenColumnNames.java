package de.lww4.logic.models.enums;

/**
 * ForbiddenColumnNames
 * checks if word is forbidden as a column name
 * @author max
 */
public enum ForbiddenColumnNames
{
    ID("id"),
    NAME("name"),
    DATE("date");

    private String word;

    ForbiddenColumnNames(String word)
    {
        this.word = word;
    }

    /**
     * Compares given word with all forbidden words in this enum. If it's contained, true is returned
     * @param wordToCheck given word
     * @return boolean isForbiddenWord
     */
    public static boolean isForbidden(String wordToCheck)
    {
        for (ForbiddenColumnNames forbiddenColumnName : ForbiddenColumnNames.values())
        {
            if (forbiddenColumnName.word.toLowerCase().equals(wordToCheck.toLowerCase()))
            {
                return true;
            }
        }
        return false;
    }
}
