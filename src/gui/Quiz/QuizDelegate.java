package gui.Quiz;

/**
 * {@code QuizDelegate} defines the contract for handling user actions from the
 * quiz gameplay control panel.
 * <p>
 * Any class implementing this interface will respond to events triggered by the
 * bottom control bar or other UI components in the quiz view, such as:
 * <ul>
 * <li>Showing the correct answer for the current question</li>
 * <li>Saving the user’s selected answer</li>
 * <li>Loading a new question</li>
 * </ul>
 * </p>
 * 
 * <p>
 * This interface is typically implemented by {@link QuizPanel}, which
 * coordinates updates between the left and right quiz panels and manages the
 * quiz flow.
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public interface QuizDelegate {

	/**
	 * Triggered when the user requests to reveal the correct answer for the
	 * currently selected question.
	 * <p>
	 * The implementing class should handle retrieving and displaying the answer in
	 * the UI.
	 * </p>
	 */
	void onShowAnswer();

	/**
	 * Triggered when the user saves their selected answer to the current question.
	 * <p>
	 * The implementing class should handle storing the answer state and providing
	 * feedback to the user.
	 * </p>
	 */
	void onSaveAnswer();

	/**
	 * Triggered when the user requests a new question.
	 * <p>
	 * The implementing class should handle selecting and displaying a new question,
	 * either randomly or from the selected theme.
	 * </p>
	 */
	void onNewQuestion();
}
