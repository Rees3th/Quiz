package quizLogic;

/**
 * {@code QuizValidator} is a utility class that defines standardized
 * user-facing messages used during the quiz gameplay.
 *
 * <p>
 * This class provides constant String messages which are displayed as feedback
 * to the user in the quiz UI components (e.g., {@code QuizPanel},
 * {@code QuizPanelLeft}, {@code QuizPanelRight}).
 * </p>
 *
 * <p>
 * Note the contrast to {@link QuestionValidator} and {@link ThemeValidator}:
 * {@code QuizValidator} deals with runtime messages during quiz play, while the
 * others focus on validating input during content creation or editing.
 * </p>
 *
 * <p>
 * Using these constants ensures consistent and localized messages across the
 * UI.
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public class QuizValidator {

	/** Message displayed when no quiz question is currently selected. */
	public static final String MSG_NO_QUESTION_SELECTED = "Keine Frage ausgewählt.";

	/**
	 * Confirmation message shown after the player’s answer has been successfully
	 * saved.
	 */
	public static final String MSG_ANSWER_SAVED = "Antwort gespeichert!";

	/**
	 * Message shown when no questions are available for the selected theme or
	 * category.
	 */
	public static final String MSG_NO_QUESTIONS_AVAILABLE = "Keine Fragen vorhanden.";

	/**
	 * Prefix for the label that appears before displaying the correct answer text.
	 */
	public static final String MSG_CORRECT_ANSWER_IS = "Die richtige Antwort ist: ";

	/** Message prompting the user to select an answer before proceeding. */
	public static final String MSG_NO_SELECTION_MADE = "Bitte wähle eine Antwort aus.";

	/** Message indicating the submitted answer was correct. */
	public static final String MSG_CORRECT = "Richtig!";

	/**
	 * Message indicating the submitted answer was incorrect, encouraging another
	 * attempt.
	 */
	public static final String MSG_WRONG_WITH_HINT = "Leider falsch. Versuche es erneut.";

	/**
	 * Warning displayed if the user attempts to save an answer after the correct
	 * answer has been revealed.
	 */
	public static final String MSG_CANNOT_SAVE_AFTER_SHOW = "Nach dem Anzeigen der Antwort kann keine Antwort mehr gespeichert werden.";
}
