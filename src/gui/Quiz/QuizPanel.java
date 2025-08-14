package gui.Quiz;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JPanel;

import persistence.serialization.QuizDataManager;
import quizLogic.Answer;
import quizLogic.Question;
import quizLogic.Theme;
import quizLogic.QuizValidator;

/**
 * {@code QuizPanel} is the main panel for running the quiz gameplay.
 * <p>
 * It combines:
 * <ul>
 * <li>{@link QuizPanelLeft} – left-side question display and answer selection
 * area</li>
 * <li>{@link QuizPanelRight} – right-side list of themes and their
 * questions</li>
 * <li>{@link QuizPanelBottom} – bottom control bar with action buttons</li>
 * </ul>
 * </p>
 * 
 * <p>
 * This panel coordinates the quiz flow by handling events from the bottom panel
 * through the {@link QuizDelegate} interface, such as showing the correct
 * answer, saving an answer, or loading a new random question.
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public class QuizPanel extends JPanel implements QuizDelegate {

	/** Serial version UID for serialization compatibility. */
	private static final long serialVersionUID = 1L;

	/** Left panel showing the current question and answer selection checkboxes. */
	private final QuizPanelLeft quizPanelLeft;

	/** Right panel listing available themes and questions. */
	private final QuizPanelRight quizPanelRight;

	/** Bottom panel containing gameplay control buttons. */
	private final QuizPanelBottom quizButtonPanel;

	/** Data manager for accessing persistent quiz data. */
	private final QuizDataManager dm;

	/** Random generator for selecting questions randomly. */
	private final Random random = new Random();

	/**
	 * Constructs a new {@code QuizPanel} with all sub-panels initialized and
	 * linked.
	 *
	 * @param dm the {@link QuizDataManager} used for data retrieval and question
	 *           loading
	 */
	public QuizPanel(QuizDataManager dm) {
		super(new BorderLayout(10, 10));
		this.dm = dm;

		quizPanelLeft = new QuizPanelLeft(dm);
		quizPanelRight = new QuizPanelRight(dm);
		quizButtonPanel = new QuizPanelBottom();

		// Link left and right panels
		quizPanelRight.setPanelLeft(quizPanelLeft);

		// Make this panel the delegate for the bottom control buttons
		quizButtonPanel.setDelegate(this);

		// Add panels to the layout
		add(quizPanelLeft, BorderLayout.WEST);
		add(quizPanelRight, BorderLayout.EAST);
		add(quizButtonPanel, BorderLayout.SOUTH);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Displays the correct answer for the currently selected question. The correct
	 * answer is shown via the right panel, and the left panel's message area is
	 * cleared. The question is marked as 'answered'.
	 * </p>
	 */
	@Override
	public void onShowAnswer() {
		Question q = quizPanelRight.getThemaFragenPanel().getFragenList().getSelectedValue();
		if (q == null) {
			quizPanelLeft.getMessageField().setText(QuizValidator.MSG_NO_QUESTION_SELECTED);
			return;
		}
		String correctAnswerText = "";
		for (Answer a : q.getAnswers()) {
			if (a.isCorrect()) {
				correctAnswerText = a.getText();
				break;
			}
		}
		quizPanelRight.getThemaFragenPanel()
				.showFeedbackAnswer(QuizValidator.MSG_CORRECT_ANSWER_IS + correctAnswerText);
		quizPanelLeft.getMessageField().setText("");
		quizPanelRight.markAnswered(q.getId());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Marks the current answer as saved by disabling answer checkboxes and updating
	 * the left panel message field.
	 * </p>
	 */
	@Override
	public void onSaveAnswer() {
		quizPanelLeft.setCheckboxesEnabled(false);
		quizPanelLeft.getMessageField().setText(QuizValidator.MSG_ANSWER_SAVED);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Loads a new random question from either the currently selected theme's
	 * question list or from all available themes, depending on the current theme
	 * selection. Updates the left panel to display the selected question and
	 * enables answer checkboxes.
	 * </p>
	 */
	@Override
	public void onNewQuestion() {
		quizPanelRight.getThemaFragenPanel().showFragenList();

		List<Question> alleFragen = new ArrayList<>();
		Theme selectedThema = (Theme) quizPanelRight.getThemaFragenPanel().getThemaComboBox().getSelectedItem();

		// Collect questions from a specific theme, or all themes if "All themes" is
		// selected
		if (selectedThema != null && !"Alle Themen".equals(selectedThema.toString())) {
			if (selectedThema.getAllQuestions() != null) {
				alleFragen.addAll(selectedThema.getAllQuestions());
			}
		} else {
			for (Theme t : dm.getAllThemen()) {
				if (t.getAllQuestions() != null) {
					alleFragen.addAll(t.getAllQuestions());
				}
			}
		}

		// If no questions are available, show a message
		if (alleFragen.isEmpty()) {
			quizPanelLeft.getMessageField().setText(QuizValidator.MSG_NO_QUESTIONS_AVAILABLE);
			return;
		}

		// Randomly select a question from the available pool
		Question randomQuestion = alleFragen.get(random.nextInt(alleFragen.size()));
		quizPanelRight.getThemaFragenPanel().getFragenList().setSelectedValue(randomQuestion, true);
		quizPanelLeft.setFrage(randomQuestion);
		quizPanelLeft.getMessageField().setText("");
		quizPanelLeft.setCheckboxesEnabled(true);
		quizPanelRight.resetGezeigteAntworten();
	}

	/**
	 * Returns the right-side quiz panel, which lists available themes and
	 * questions.
	 *
	 * @return the {@link QuizPanelRight} instance
	 */
	public QuizPanelRight getQuizPanelRight() {
		return quizPanelRight;
	}
}
