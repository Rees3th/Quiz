package quizLogic;

import java.util.List;

public class ThemeValidator {

    // Input validation messages
    public static final String MSG_NO_TITLE = "Bitte einen Titel eingeben!";
    public static final String MSG_DUPLICATE_TITLE = "Es existiert bereits ein Thema mit diesem Namen!";
    public static final String MSG_NO_DESCRIPTION = "Bitte eine Beschreibung eingeben!";

    // Save/Delete result messages
    public static final String MSG_SAVE_ERROR_PREFIX = "Fehler beim Speichern: ";
    public static final String MSG_SAVE_SUCCESS = "Thema erfolgreich gespeichert.";
    public static final String MSG_DELETE_ERROR_PREFIX = "Fehler beim Löschen: ";
    public static final String MSG_DELETE_CONFIRM_PREFIX = "Thema \"%s\" wirklich löschen?";
    public static final String MSG_DELETE_CONFIRM_TITLE = "Löschen bestätigen";

    /**
     * Prüft Titel und Beschreibung und ob der Titel im System bereits existiert.
     */
    public static String validate(String titel, String description, List<Theme> allThemes, Theme exclude) {
        if (titel == null || titel.trim().isEmpty()) return MSG_NO_TITLE;
        if (description == null || description.trim().isEmpty()) return MSG_NO_DESCRIPTION;

        String lower = titel.trim().toLowerCase();
        for (Theme t : allThemes) {
            if (t != null && t != exclude && t.getTitle() != null &&
                t.getTitle().trim().toLowerCase().equals(lower)) {
                return MSG_DUPLICATE_TITLE;
            }
        }
        return null; // alles OK
    }
}
