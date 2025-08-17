package quizLogic;

/**
 * {@code QuizValidator} is a utility class that defines standardized
 * user-facing messages for quiz gameplay.
 *
 * <p>
 * Differences to {@link QuestionValidator} and {@link ThemeValidator}:
 * </p>
 * <ul>
 * <li>{@link QuestionValidator} and {@link ThemeValidator} validate data when
 * creating/editing content</li>
 * <li>{@code QuizValidator} provides status/feedback messages during the actual
 * quiz execution</li>
 * </ul>
 *
 * <p>
 * These constants are used in the quiz UI (e.g. in {@code QuizPanel},
 * {@code QuizPanelLeft}, {@code QuizPanelRight}, etc.) to provide consistent,
 * localized feedback to the user.
 * </p>
 */
public class QuizValidator {

	/** Message shown if no question was selected. */
	public static final String MSG_NO_QUESTION_SELECTED = "Keine Frage ausgewählt.";

	/** Message shown when the user saves an answer. */
	public static final String MSG_ANSWER_SAVED = "Antwort gespeichert!";

	/**
	 * Message shown when no questions are available for the current theme/category.
	 */
	public static final String MSG_NO_QUESTIONS_AVAILABLE = "Keine Fragen vorhanden.";

	/**
	 * Prefix for displaying the correct answer (actual answer text is appended).
	 */
	public static final String MSG_CORRECT_ANSWER_IS = "Die richtige Antwort ist: ";
}
