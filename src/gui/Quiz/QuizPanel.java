package gui.Quiz;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;

import persistence.DBDataManager;
import quizLogic.Answer;
import quizLogic.Question;
import quizLogic.QuizStatistic;
import quizLogic.QuizValidator;

/**
 * {@code QuizPanel} is the main container panel combining all quiz-related
 * sub-panels and coordinating quiz gameplay logic.
 *
 * <p>
 * It integrates:
 * </p>
 * <ul>
 * <li>{@link QuizPanelLeft} – shows the currently active question and its
 * answer options</li>
 * <li>{@link QuizPanelRight} – displays the list of themes and related
 * questions</li>
 * <li>{@link QuizPanelBottom} – contains the control buttons for interacting
 * with the quiz</li>
 * </ul>
 *
 * <p>
 * The panel is responsible for handling quiz flow events (via
 * {@link QuizDelegate}), such as:
 * </p>
 * <ul>
 * <li>Showing the correct answer for the selected question</li>
 * <li>Saving the user’s answer</li>
 * <li>Loading and displaying a new random question</li>
 * </ul>
 *
 * <p>
 * All data (themes, questions, and answers) are retrieved through
 * {@link DBDataManager}, which serves as the abstraction for persistent
 * storage.
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public class QuizPanel extends JPanel implements QuizDelegate {

	/** Serial version UID for serialization (Swing best practice). */
	private static final long serialVersionUID = 1L;

	/**
	 * Left panel responsible for showing the active question and answer checkboxes.
	 */
	private final QuizPanelLeft quizPanelLeft;

	/**
	 * Right panel that lists available quiz themes and questions.
	 */
	private final QuizPanelRight quizPanelRight;

	/**
	 * Bottom control panel with buttons (e.g., Save, Show Answer, New Question).
	 */
	private final QuizPanelBottom quizButtonPanel;

	/** Data manager for accessing quiz data from the database. */
	private final DBDataManager dm;

	/** Random generator to ensure non-predictable selection of questions. */
	private final Random random = new Random();

	// State flag: true if Show Answer was clicked before Save Answer.
	private boolean hasShownAnswer = false;

	/**
	 * Constructs a new {@code QuizPanel} and initializes all sub-panels.
	 *
	 * <p>
	 * Panels are connected in the following way:
	 * </p>
	 * <ul>
	 * <li>The right panel ({@link QuizPanelRight}) is aware of the left panel so
	 * that it can update it when a question is selected.</li>
	 * <li>The bottom panel ({@link QuizPanelBottom}) uses this class as its
	 * delegate to handle button events.</li>
	 * </ul>
	 * 
	 * @param dm the {@link DBDataManager} instance used for loading themes,
	 *           questions, and answers from the database
	 */
	public QuizPanel(DBDataManager dm) {
		// Use BorderLayout with horizontal/vertical gaps for better spacing
		super(new BorderLayout(10, 10));
		this.dm = dm;
		// Instantiate left, right, and bottom panels
		quizPanelLeft = new QuizPanelLeft(dm);
		quizPanelRight = new QuizPanelRight(dm);
		quizButtonPanel = new QuizPanelBottom();
		// Link the panels together (right panel needs to know the left one)
		quizPanelRight.setPanelLeft(quizPanelLeft);
		// Register this panel as delegate for bottom control events
		quizButtonPanel.setDelegate(this);
		// Add components to the main layout
		add(quizPanelLeft, BorderLayout.WEST);
		add(quizPanelRight, BorderLayout.EAST);
		add(quizButtonPanel, BorderLayout.SOUTH);
	}

	/**
	 * Shows the correct answer for the currently selected question.
	 *
	 * <p>
	 * - If no question is currently selected, an error message is shown. -
	 * Otherwise, the correct answer is determined, displayed in the right-side
	 * panel, and the question is marked as "answered".
	 * </p>
	 */
	@Override
	public void onShowAnswer() {
		// Get the currently selected question from the right-side question list
		Question q = quizPanelRight.getQuizQuestionRightLayout().getQuestionList().getSelectedValue();
		// If nothing is selected, inform the user and stop here
		if (q == null) {
			quizButtonPanel.getMessagePanel().setText(QuizValidator.MSG_NO_QUESTION_SELECTED);
			return;
		}
		// Collect all correct answers
		List<String> correctAnswers = new ArrayList<>();
		for (Answer a : q.getAnswers()) {
			if (a.isCorrect()) {
				correctAnswers.add(a.getText());
			}
			hasShownAnswer = true;
		}
		String correctAnswerText = String.join(", ", correctAnswers);
		// Show correct answer on the right panel as feedback
		quizPanelRight.getQuizQuestionRightLayout()
				.showFeedbackAnswer(QuizValidator.MSG_CORRECT_ANSWER_IS + correctAnswerText);
		// Clear any message on the left panel
		quizButtonPanel.getMessagePanel().setText("");
		// Mark the question as answered in the right panel (e.g., highlight state)
		quizPanelRight.markAnswered(q.getId());
	}

	/**
	 * Saves the user's selected answer for the currently displayed question.
	 *
	 * <p>
	 * - Validates if a question is selected and if any answer was chosen. -
	 * Compares user selection with correct answers. - Saves the result in quiz
	 * statistics. - Provides feedback to the user in the left panel's message
	 * field.
	 * </p>
	 */
	@Override
	public void onSaveAnswer() {
		// If the user has already shown the answer, they cannot save again
		if (hasShownAnswer) {
			quizButtonPanel.getMessagePanel().setText(QuizValidator.MSG_CANNOT_SAVE_AFTER_SHOW);
			return;
		}
		// Get currently selected question and user selection
		Question q = quizPanelRight.getQuizQuestionRightLayout().getQuestionList().getSelectedValue();
		if (q == null) {
			quizButtonPanel.getMessagePanel().setText(QuizValidator.MSG_NO_QUESTION_SELECTED);
			return;
		}
		// Check if the user has selected any answers
		boolean correct = true; // assume correct unless found otherwise
		List<Answer> answers = q.getAnswers();
		boolean answerSelected = false;
		for (int i = 0; i < answers.size(); i++) {
			boolean chosen = quizPanelLeft.getCheckboxes()[i].isSelected();
			if (chosen)
				answerSelected = true;
			if (answers.get(i).isCorrect() != chosen)
				correct = false;
		}
		// If no answer selected, show a hint
		if (!answerSelected) {
			quizButtonPanel.getMessagePanel().setText(QuizValidator.MSG_NO_SELECTION_MADE);
			return;
		}
		// Save statistics about the answer
		QuizStatistic stat = new QuizStatistic(q.getId(), correct, new Date());
		dm.getStatisticDAO().insert(stat);
		// Give feedback to user
		if (correct) {
			quizButtonPanel.getMessagePanel().setText(QuizValidator.MSG_CORRECT);
		} else {
			quizButtonPanel.getMessagePanel().setText(QuizValidator.MSG_WRONG_WITH_HINT);
		}
	}

	/**
	 * Selects a new random question from the list of available ones.
	 *
	 * <p>
	 * Logic: - Ensure that there are questions available. - Pick a random index
	 * from the list. - Retrieve the full question (including its answers) from the
	 * database. - Update the UI to show this question in the left panel and select
	 * it in the right panel. - Reset the answer feedback panel to a "fresh" state.
	 * </p>
	 */
	@Override
	public void onNewQuestion() {
		// Reset state: no answer shown yet
		hasShownAnswer = false;
		// Make sure the latest list of questions is displayed
		quizPanelRight.getQuizQuestionRightLayout().showQuestionList();
		// Retrieve the JList containing all questions
		JList<Question> fragenList = quizPanelRight.getQuizQuestionRightLayout().getQuestionList();
		DefaultListModel<Question> model = (DefaultListModel<Question>) fragenList.getModel();
		// Case 1: No questions available -> display message and clear panel
		if (model.getSize() == 0) {
			quizButtonPanel.getMessagePanel().setText(QuizValidator.MSG_NO_QUESTIONS_AVAILABLE);
			quizPanelLeft.setQuestion(null);
			return;
		}
		// Case 2: Randomly choose an index from question list
		int randomIndex = random.nextInt(model.getSize());
		Question randomQuestionModelRef = model.getElementAt(randomIndex);
		// Load the complete question including answers from DB
		Question fullQ = dm.getFullQuestionById(randomQuestionModelRef.getId());
		// Set the selection in the question list (so UI highlights it)
		fragenList.setSelectedIndex(randomIndex);
		// Display the full question (with answers) on the left panel
		quizPanelLeft.setQuestion(fullQ);
		// Clear any old messages
		quizPanelLeft.getMessageField().setText("");
		// Re-enable checkboxes so user can answer
		quizPanelLeft.setCheckboxesEnabled(true);
		// Reset shown answer feedback (right panel visual reset after switching)
		quizPanelRight.resetActualQuestion();
	}

	/**
	 * Returns the right-side quiz panel managing themes and questions list.
	 *
	 * @return the instance of {@link QuizPanelRight}
	 */
	public QuizPanelRight getQuizPanelRight() {
		return quizPanelRight;
	}

	/**
	 * Returns the data manager instance used for database access.
	 *
	 * @return the {@link DBDataManager} instance managing data retrieval and
	 *         persistence
	 */
	public DBDataManager getDm() {
		return dm;
	}
}
