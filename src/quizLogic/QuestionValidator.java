package quizLogic;

/**
 * {@code QuestionValidator} provides static validation logic for
 * {@link Question} objects.
 *
 * <p>
 * Responsibilities:
 * </p>
 * <ul>
 * <li>Ensures that a question has a valid theme, title, text, and at least one
 * answer</li>
 * <li>Checks that at least one answer is marked as correct</li>
 * <li>Prevents duplicate titles within the same {@link Theme}</li>
 * <li>Provides standardized validation and persistence messages as
 * constants</li>
 * </ul>
 *
 * <p>
 * This class is used before saving or deleting questions to ensure data
 * integrity and provide user feedback.
 * </p>
 */
public class QuestionValidator {

	// ------------------- Validation Error Messages -------------------

	/** Error message: no theme selected. */
	public static final String MSG_NO_THEME = "Bitte wählen Sie ein Thema aus.";

	/** Error message: the question or theme is invalid. */
	public static final String MSG_INVALID_QUESTION = "Ungültige Frage oder Thema.";

	/** Error message: title is missing. */
	public static final String MSG_NO_TITLE = "Bitte geben Sie einen Titel ein.";

	/** Error message: question text is missing. */
	public static final String MSG_NO_TEXT = "Bitte geben Sie den Fragetext ein.";

	/** Error message: no answer options provided. */
	public static final String MSG_NO_ANSWER = "Bitte geben Sie mindestens eine Antwort ein.";

	/** Error message: at least one correct answer required. */
	public static final String MSG_NO_CORRECT = "Bitte markieren Sie mindestens eine richtige Antwort.";

	/** Error message: duplicate title in same theme. */
	public static final String MSG_DUPLICATE_TITLE = "Es existiert bereits eine andere Frage mit diesem Titel im gewählten Thema.";

	// ------------------- Action / Result Messages -------------------

	/** Prefix for save error messages. */
	public static final String MSG_SAVE_ERROR_PREFIX = "Fehler beim Speichern: ";

	/** Success message after saving a question. */
	public static final String MSG_SAVE_SUCCESS = "Frage erfolgreich gespeichert.";

	/** Prefix for delete error messages. */
	public static final String MSG_DELETE_ERROR_PREFIX = "Fehler beim Löschen der Frage: ";

	/** Error message: no valid selection for deletion. */
	public static final String MSG_DELETE_INVALID_SELECTION = "Keine Frage ausgewählt oder Thema ungültig.";

	// ------------------- Validation Logic -------------------

	/**
	 * Validates a question before persistence.
	 *
	 * <p>
	 * Checks performed:
	 * </p>
	 * <ul>
	 * <li>A theme must be selected</li>
	 * <li>The question must not be null and must have a theme</li>
	 * <li>Title and text must not be empty</li>
	 * <li>At least one answer must exist</li>
	 * <li>At least one answer must be marked as correct</li>
	 * <li>No other question in the same theme may share the same title (case
	 * insensitive), unless it is the excluded question (useful for update)</li>
	 * </ul>
	 *
	 * @param question        the {@link Question} to validate
	 * @param thema           the {@link Theme} the question belongs to
	 * @param excludeQuestion an optional question to ignore during duplicate-title
	 *                        check (e.g., the one being updated)
	 * @return {@code null} if validation succeeded, otherwise one of the static
	 *         error message constants
	 */
	public static String validate(Question question, Theme thema, Question excludeQuestion) {
		if (thema == null)
			return MSG_NO_THEME;
		if (question == null || question.getThema() == null)
			return MSG_INVALID_QUESTION;
		if (isEmpty(question.getTitle()))
			return MSG_NO_TITLE;
		if (isEmpty(question.getText()))
			return MSG_NO_TEXT;
		if (question.getAnswers() == null || question.getAnswers().isEmpty())
			return MSG_NO_ANSWER;

		// Must have at least one correct answer
		boolean hasCorrect = question.getAnswers().stream().anyMatch(Answer::isCorrect);
		if (!hasCorrect)
			return MSG_NO_CORRECT;

		// Prevent duplicate titles in the same theme
		String normalized = question.getTitle().trim().toLowerCase();
		if (thema.getAllQuestions() != null) {
			for (Question existing : thema.getAllQuestions()) {
				if (existing != excludeQuestion && existing.getTitle() != null
						&& normalized.equals(existing.getTitle().trim().toLowerCase())) {
					return MSG_DUPLICATE_TITLE;
				}
			}
		}
		return null; // success
	}

	/**
	 * Utility method to check if a string is empty or null.
	 *
	 * @param s a candidate string
	 * @return {@code true} if null or only whitespace, else {@code false}
	 */
	private static boolean isEmpty(String s) {
		return s == null || s.trim().isEmpty();
	}
}
