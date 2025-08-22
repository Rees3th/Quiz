package gui.QuizThemes;

import java.awt.BorderLayout;
import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import gui.QuizQuestion.QuizQuestionPanel;
import persistence.DBDataManager;
import quizLogic.Theme;
import quizLogic.ThemeValidator;

/**
 * {@code QuizThemePanel} is the main panel for managing quiz themes (topics)
 * within the quiz application.
 * <p>
 * It contains:
 * <ul>
 * <li>{@link QuizThemeLeft} - left side input form for theme title and
 * description</li>
 * <li>{@link QuizThemeRight} - right side list displaying all existing
 * themes</li>
 * <li>{@link QuizThemeBottom} - bottom action bar with "New", "Save", "Delete"
 * buttons</li>
 * </ul>
 * Implements {@link QuizThemeDelegate} to handle actions triggered by the
 * bottom panel.
 * </p>
 * 
 * <p>
 * This panel connects to the {@link DBDataManager} to load, save, and delete
 * quiz themes. It can also notify a connected {@link QuizQuestionPanel} to
 * refresh its theme data.
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public class QuizThemePanel extends JPanel implements QuizThemeDelegate {

	/** Serial version UID for consistent serialization. */
	private static final long serialVersionUID = 1L;

	/** Right side panel displaying all quiz themes in a list. */
	private QuizThemeRight quizThemeRight;

	/** Bottom action panel with buttons (New, Save, Delete). */
	private QuizThemeBottom quizThemeBottom;

	/** Left side panel for entering or editing a quiz theme. */
	private QuizThemeLeft quizThemeLeft;

	/**
	 * Optional reference to the Quiz Questions panel for theme list
	 * synchronization.
	 */
	private QuizQuestionPanel quizQuestionPanel;

//    /** Data manager for retrieving, saving, and deleting quiz themes. */
//    private final QuizDataManager dm;

	private final DBDataManager dm;

	/**
	 * Constructs the quiz theme management panel.
	 * <ul>
	 * <li>Initializes the {@link DBDataManager}</li>
	 * <li>Sets a {@link BorderLayout}</li>
	 * <li>Creates UI components</li>
	 * </ul>
	 * 
	 * @throws SQLException
	 */
	public QuizThemePanel(DBDataManager dm) {
		super();
		setLayout(new BorderLayout(10, 10));
		this.dm = dm;
		initUI();
	}

	/**
	 * Initializes the UI structure by creating and adding the left, right, and
	 * bottom panels, and setting delegate links.
	 */
	private void initUI() {
		quizThemeLeft = new QuizThemeLeft();
		quizThemeRight = new QuizThemeRight(dm.getAllThemes());
		quizThemeBottom = new QuizThemeBottom();

		// Link the right panel with the left panel for selection updates
		quizThemeRight.setPanelLeft(quizThemeLeft);

		// Set this panel as delegate for bottom panel actions
		quizThemeBottom.setDelegate(this);

		// Add panels to layout
		add(quizThemeLeft, BorderLayout.CENTER);
		add(quizThemeRight, BorderLayout.EAST);
		add(quizThemeBottom, BorderLayout.SOUTH);
		
	}

	/**
	 * {@inheritDoc} Deletes the currently selected theme after user confirmation.
	 * <p>
	 * If deletion is successful, the themes list is reloaded and the left form is
	 * cleared.
	 * </p>
	 */
	@Override
	public void onDeleteTheme() {
		Theme selected = quizThemeRight.getThemaPanel().getThemenList().getSelectedValue();
		if (selected == null)
			return;

		// Ask user to confirm deletion
		int result = JOptionPane.showConfirmDialog(this,
				String.format(ThemeValidator.MSG_DELETE_CONFIRM_PREFIX, selected.getTitle()),
				ThemeValidator.MSG_DELETE_CONFIRM_TITLE, JOptionPane.YES_NO_OPTION);

		if (result == JOptionPane.YES_OPTION) {
			String error = dm.deleteTheme(selected.getId()) ? null : "Fehler";

			// Show error if deletion failed
			if (error != null) {
				JOptionPane.showMessageDialog(this, ThemeValidator.MSG_DELETE_ERROR_PREFIX + error, "Fehler",
						JOptionPane.ERROR_MESSAGE);
			}

			// Reload the themes list and clear the input fields
			reloadThemenUI();
			quizThemeLeft.clearFields();
		}
	}

	/**
	 * {@inheritDoc} Saves a new or edited theme after validation.
	 * <p>
	 * If a theme is selected in the list, it will be updated. Otherwise, a new
	 * theme is created. Displays success or error messages in the bottom panel.
	 * </p>
	 */
	@Override
	public void onSaveTheme() {
		String titel = quizThemeLeft.getTitelField().getText().trim();
		String info = quizThemeLeft.getInfoArea().getText();
		Theme selected = quizThemeRight.getThemaPanel().getThemenList().getSelectedValue();

		// Validate user input
		String validationError = ThemeValidator.validate(titel, info, dm.getAllThemes(), selected);
		if (validationError != null) {
			quizThemeBottom.getMessagePanel().setText(validationError);
			return;
		}

		// Update existing theme or create a new one
		if (selected != null) {
			selected.setTitle(titel);
			selected.setText(info);
		} else {
			selected = new Theme();
			selected.setTitle(titel);
			selected.setText(info);
			selected.setId(-1); // Temporary ID for new themes
		}

		// Save to the data manager
		String error = dm.saveTheme(selected);
		if (error != null) {
			quizThemeBottom.getMessagePanel().setText(ThemeValidator.MSG_SAVE_ERROR_PREFIX + error);
			return;
		}

		// Refresh view and show the saved theme as selected
		reloadThemenUI();
		quizThemeRight.getThemaPanel().getThemenList().setSelectedValue(selected, true);
		quizThemeLeft.setThema(selected);
		quizThemeBottom.getMessagePanel().setText(ThemeValidator.MSG_SAVE_SUCCESS);
	}

	/**
	 * {@inheritDoc} Clears the current theme selection and resets the input fields,
	 * preparing for the creation of a new theme.
	 */
	@Override
	public void onNewTheme() {
		quizThemeRight.getThemaPanel().getThemenList().clearSelection();
		quizThemeLeft.clearFields();
	}

	/**
	 * Reloads the theme list in the right panel and, if connected, also triggers a
	 * reload of themes in the associated questions panel.
	 */
	private void reloadThemenUI() {
		quizThemeRight.setThemen(dm.getAllThemes());
		if (quizQuestionPanel != null) {
			quizQuestionPanel.reloadThemes();
		}
	}

	// ---------- Public API (Getters & Setters) ----------

	/**
	 * Gets the theme {@link DBDataManager}.
	 *
	 * @return the data manager instance
	 */
	public DBDataManager getDataManager() {
		return dm;
	}

	/**
	 * Sets the {@link QuizQuestionPanel} reference for synchronization.
	 *
	 * @param quizFragenPanel the question panel to associate
	 */
	public void setQuizQuestionPanel(QuizQuestionPanel quizQuestionPanel) {
		this.quizQuestionPanel = quizQuestionPanel;
	}
}
