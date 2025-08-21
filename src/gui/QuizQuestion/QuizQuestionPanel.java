package gui.QuizQuestion;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JPanel;

import persistence.DBDataManager;
import quizLogic.Question;
import quizLogic.QuestionValidator;
import quizLogic.Theme;

/**
 * {@code QuizQuestionPanel} is the main container panel for managing quiz
 * questions (create, edit, delete).
 *
 * <p>
 * It combines three sub-panels:
 * </p>
 * <ul>
 * <li>{@link QuizQuestionLeft} – left-side editor form</li>
 * <li>{@link QuizQuestionRight} – right-side theme and question list</li>
 * <li>{@link QuizQuestionBottom} – bottom action bar (Save/New/Delete)</li>
 * </ul>
 *
 * <p>
 * <b>Responsibilities:</b>
 * </p>
 * <ul>
 * <li>Coordinating interaction between left form and right list</li>
 * <li>Handling persistence via {@link DBDataManager}</li>
 * <li>Acting as a {@link QuizQuestionDelegate} for button actions</li>
 * </ul>
 * 
 * @author Oleg Kapirulya
 */
public class QuizQuestionPanel extends JPanel implements QuizQuestionDelegate {
	private static final long serialVersionUID = 1L;

	/** Left-side editor form for a single question. */
	private QuizQuestionLeft quizQuestionLeft;

	/** Right-side panel with theme and question list. */
	private QuizQuestionRight quizQuestionRight;

	/** Bottom control/action bar. */
	private QuizQuestionBottom quizQuestionBottom;

	/** Database manager for loading/saving/deleting questions and themes. */
	private final DBDataManager dm;

	/**
	 * Constructs a new {@code QuizQuestionPanel}.
	 *
	 * @param dm the {@link DBDataManager} to use for persistence operations
	 */
	public QuizQuestionPanel(DBDataManager dm) {
		this.dm = dm;
		initLayout();
		initComponents();
		linkComponents();
		setDelegate();
		reloadThemes(); // load initial data
	}

	/** Configure the panel layout. */
	private void initLayout() {
		setLayout(new BorderLayout(10, 10));
	}

	/** Instantiate sub-panels. */
	private void initComponents() {
		quizQuestionLeft = new QuizQuestionLeft(dm);
		quizQuestionRight = new QuizQuestionRight(dm);
		quizQuestionBottom = new QuizQuestionBottom();
	}

	/**
	 * Link left and right panels, and arrange all sub-components.
	 */
	private void linkComponents() {
		quizQuestionRight.setPanelLeft(quizQuestionLeft);
		quizQuestionLeft.setPanelRight(quizQuestionRight);

		add(quizQuestionLeft, BorderLayout.WEST);
		add(quizQuestionRight, BorderLayout.EAST);
		add(quizQuestionBottom, BorderLayout.SOUTH);
	}

	/** Register this panel as delegate for bottom control bar. */
	private void setDelegate() {
		quizQuestionBottom.setDelegate(this);
	}

	/**
	 * Saves the current question entered in the left form.
	 * <p>
	 * Workflow:
	 * </p>
	 * <ol>
	 * <li>Get selected theme + form data</li>
	 * <li>Validate with {@link QuestionValidator}</li>
	 * <li>If valid → save in DB via {@link DBDataManager}</li>
	 * <li>Refresh question list and clear form</li>
	 * </ol>
	 */
	@Override
	public void onSaveQuestion() {
		Theme selectedThema = (Theme) quizQuestionRight.getQuizQuestionRightLayout().getThemaComboBox().getSelectedItem();

		Question q = quizQuestionLeft.getSelectedQuestion(selectedThema);

		// Validate input data
		String validationError = QuestionValidator.validate(q, selectedThema, q);
		if (validationError != null) {
			quizQuestionBottom.getMessagePanel().setText(validationError);
			return;
		}

		// Save in DB
		String result = dm.saveQuestion(q);
		if (result != null) {
			quizQuestionBottom.getMessagePanel().setText(QuestionValidator.MSG_SAVE_ERROR_PREFIX + result);
			return;
		}

		// On success: refresh and clear
		reloadQuestionsForTheme(q.getThema());
		quizQuestionLeft.setQuestion(null);
		quizQuestionBottom.getMessagePanel().setText(QuestionValidator.MSG_SAVE_SUCCESS);
	}

	/**
	 * Deletes the currently selected question from DB. Shows error messages if
	 * selection is invalid or deletion fails.
	 */
	@Override
	public void onDeleteQuestion() {
		Question q = quizQuestionRight.getSelectedQuestion();

		if (q == null || q.getThema() == null) {
			quizQuestionBottom.getMessagePanel().setText(QuestionValidator.MSG_DELETE_INVALID_SELECTION);
			return;
		}

		String result = dm.deleteQuestion(q);
		if (result != null) {
			quizQuestionBottom.getMessagePanel().setText(QuestionValidator.MSG_DELETE_ERROR_PREFIX + result);
		} else {
			reloadQuestionsForTheme(q.getThema());
			quizQuestionLeft.setQuestion(null);
		}
	}

	/**
	 * Prepares form for a brand new question by clearing both left form and right
	 * selection.
	 */
	@Override
	public void onNewQuestion() {
		quizQuestionLeft.setQuestion(null);
		quizQuestionRight.getQuizQuestionRightLayout().getQuestionList().clearSelection();
	}

	/**
	 * Reloads all themes from DB and refreshes the right panel.
	 */
	public void reloadThemes() {
		List<Theme> themes = dm.getAllThemes();
		quizQuestionRight.setThemen(themes);
	}

	/**
	 * Reload all questions for a given theme from DB and update right panel.
	 * 
	 * @param theme the theme whose questions to reload
	 */
	public void reloadQuestionsForTheme(Theme theme) {
		if (theme == null) {
			quizQuestionRight.setQuestion(List.of());
			return;
		}
		List<Question> question = dm.getQuestionsFor(theme);
		quizQuestionRight.setQuestion(question);
	}

	/**
	 * Directly set the list of questions in the right panel. Useful for external
	 * updates.
	 *
	 * @param questions questions to show
	 */
	public void setQuestions(List<Question> questions) {
		quizQuestionRight.setQuestion(questions);
	}
}
