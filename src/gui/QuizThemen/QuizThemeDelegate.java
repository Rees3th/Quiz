package gui.QuizThemen;

/**
 * {@code QuizThemeDelegate} defines the contract for communication between
 * the Quiz Theme Management view and its underlying logic or controller.
 * <p>
 * Any class implementing this interface is responsible for handling user
 * actions triggered from the UI, such as:
 * <ul>
 *   <li>Deleting an existing quiz theme</li>
 *   <li>Saving a new or edited quiz theme</li>
 *   <li>Creating a new blank quiz theme</li>
 * </ul>
 * </p>
 * <p>
 * This interface is typically implemented by the main panel
 * ({@link QuizThemePanel}) that coordinates between the left, right,
 * and bottom sub-panels in the Theme Management view.
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public interface QuizThemeDelegate {

    /**
     * Triggered when the user requests deletion of the currently selected theme.
     * <p>
     * The implementing class should handle confirmation, removal from storage,
     * and updating the UI accordingly.
     * </p>
     */
    void onDeleteTheme();

    /**
     * Triggered when the user requests saving of the current theme.
     * <p>
     * This can mean creating a new theme or updating an existing one.
     * Validation and persistence should occur within the implementing class.
     * </p>
     */
    void onSaveTheme();

    /**
     * Triggered when the user requests creation of a new theme entry.
     * <p>
     * The implementing class should clear any current selection and
     * reset input fields to prepare for entering a new theme.
     * </p>
     */
    void onNewTheme();
}
