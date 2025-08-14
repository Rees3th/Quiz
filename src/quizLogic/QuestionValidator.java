package quizLogic;

public class QuestionValidator {

    // Validation errors
    public static final String MSG_NO_THEME = "Bitte wählen Sie ein Thema aus.";
    public static final String MSG_INVALID_QUESTION = "Ungültige Frage oder Thema.";
    public static final String MSG_NO_TITLE = "Bitte geben Sie einen Titel ein.";
    public static final String MSG_NO_TEXT = "Bitte geben Sie den Fragetext ein.";
    public static final String MSG_NO_ANSWER = "Bitte geben Sie mindestens eine Antwort ein.";
    public static final String MSG_NO_CORRECT = "Bitte markieren Sie mindestens eine richtige Antwort.";
    public static final String MSG_DUPLICATE_TITLE = "Es existiert bereits eine andere Frage mit diesem Titel im gewählten Thema.";

    // Actions & Results
    public static final String MSG_SAVE_ERROR_PREFIX = "Fehler beim Speichern: ";
    public static final String MSG_SAVE_SUCCESS = "Frage erfolgreich gespeichert.";
    public static final String MSG_DELETE_ERROR_PREFIX = "Fehler beim Löschen der Frage: ";
    public static final String MSG_DELETE_INVALID_SELECTION = "Keine Frage ausgewählt oder Thema ungültig.";

    public static String validate(Question question, Theme thema, Question excludeQuestion) {
        if (thema == null) return MSG_NO_THEME;
        if (question == null || question.getThema() == null) return MSG_INVALID_QUESTION;
        if (isEmpty(question.getTitle())) return MSG_NO_TITLE;
        if (isEmpty(question.getText())) return MSG_NO_TEXT;
        if (question.getAnswers() == null || question.getAnswers().isEmpty()) return MSG_NO_ANSWER;
        boolean hasCorrect = question.getAnswers().stream().anyMatch(Answer::isCorrect);
        if (!hasCorrect) return MSG_NO_CORRECT;

        String normalized = question.getTitle().trim().toLowerCase();
        if (thema.getAllQuestions() != null) {
            for (Question existing : thema.getAllQuestions()) {
                if (existing != excludeQuestion &&
                    existing.getTitle() != null &&
                    normalized.equals(existing.getTitle().trim().toLowerCase())) {
                    return MSG_DUPLICATE_TITLE;
                }
            }
        }
        return null;
    }

    private static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
