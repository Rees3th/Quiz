package gui.QuizQuestion;

/**
 * {@code QuizQuestionDelegate} defines the contract for delegating actions
 * within the quiz question management component.
 * <p>
 * Classes implementing this interface handle user-triggered actions from the
 * {@link QuizQuestionBottom} control panel, such as:
 * <ul>
 * <li>Deleting the currently selected question</li>
 * <li>Saving a new or edited question</li>
 * <li>Preparing the creation of a new question</li>
 * </ul>
 * </p>
 * <p>
 * This interface is typically implemented by the main {@link QuizQuestionPanel}
 * which coordinates between the left form panel and the right list panel.
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public interface QuizQuestionDelegate {

	/**
	 * Triggered when the user requests deletion of the currently selected question.
	 * <p>
	 * The implementing class should handle validation, removal from storage, and UI
	 * updates.
	 * </p>
	 */
	void onDeleteQuestion();

	/**
	 * Triggered when the user requests saving of the current question.
	 * <p>
	 * This may involve creating a new question or updating an existing one.
	 * Validation and persistence should be performed by the implementing class.
	 * </p>
	 */
	void onSaveQuestion();

	/**
	 * Triggered when the user requests creation of a new question.
	 * <p>
	 * The implementing class should reset UI fields and selections to prepare for
	 * entering a new question.
	 * </p>
	 */
	void onNewQuestion();
}
