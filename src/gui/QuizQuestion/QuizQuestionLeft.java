package gui.QuizQuestion;

import java.awt.Dimension;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import gui.Panels.AnswerHeaderPanel;
import gui.Panels.AnswerRowPanel;
import gui.Panels.questionPanel;
import gui.Panels.LabelFieldPanel;
import persistence.DBDataManager;
import quizLogic.Answer;
import quizLogic.Question;
import quizLogic.Theme;

/**
 * {@code QuizQuestionLeft} is the left-side panel in the quiz **question
 * management** view (different from quiz gameplay).
 *
 * <p>
 * This panel provides both display and editing capabilities for a single quiz
 * question. It represents a form with:
 * </p>
 * <ul>
 * <li>Associated theme (read-only, injected externally)</li>
 * <li>Question title input</li>
 * <li>Question text input</li>
 * <li>Up to four possible answers, each with a "correct" checkbox</li>
 * </ul>
 *
 * <p>
 * <b>Primary responsibilities:</b>
 * </p>
 * <ul>
 * <li>Creating new questions by filling out the form</li>
 * <li>Editing and updating existing questions</li>
 * <li>Converting user-entered form data into a {@link Question} object</li>
 * <li>Clearing or resetting the form when needed</li>
 * </ul>
 *
 * <p>
 * UI layout is structured using {@link BoxLayout} with helper classes like
 * {@link LabelFieldPanel}, {@link questionPanel}, {@link AnswerHeaderPanel},
 * and {@link AnswerRowPanel}.
 * </p>
 *
 * <p>
 * Data persistence and retrieval are handled externally through
 * {@link DBDataManager}.
 * </p>
 *
 * @author Oleg Kapirulya
 */
public class QuizQuestionLeft extends JPanel {

	/** Serial version UID for serialization compatibility. */
	private static final long serialVersionUID = 1L;

	/** Data manager for persistence and DB access. */
	private final DBDataManager dm;

	/** Text field showing the selected theme (read-only). */
	private JTextField themeField;

	/** Field for entering the question title. */
	private JTextField titelField;

	/** Text area for entering the main question text. */
	private JTextArea questionArea;

	/** Input fields for up to 4 possible answer texts. */
	private JTextField[] answerFields = new JTextField[4];

	/** Checkboxes for marking whether each corresponding answer is correct. */
	private JCheckBox[] checkboxes = new JCheckBox[4];

	/**
	 * Creates a new panel for entering and editing quiz questions.
	 *
	 * @param dm the {@link DBDataManager} instance used for data access
	 */
	public QuizQuestionLeft(DBDataManager dm) {
		this.dm = dm;
		initPanel();
		initComponents();
		layoutComponents();
	}

	/**
	 * Initializes base panel properties and layout strategy.
	 * <p>
	 * Uses a vertical {@link BoxLayout}, defines padding and size constraints.
	 * </p>
	 */
	private void initPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setMaximumSize(new Dimension(500, 500));
		setPreferredSize(new Dimension(450, 500));
	}

	/**
	 * Creates all UI components required for this panel. - Theme field (read-only,
	 * 1 line) - Title field (editable, 1 line) - Question text area (multi-line
	 * input) - Four answer rows (editable text + checkbox each)
	 */
	private void initComponents() {
		themeField = new JTextField(27);
		themeField.setEditable(false);

		titelField = new JTextField(27);
		questionArea = new JTextArea(6, 24);

		for (int i = 0; i < 4; i++) {
			answerFields[i] = new JTextField(23);
			checkboxes[i] = new JCheckBox();
		}
	}

	/**
	 * Adds all created components into the panel in logical, user-friendly order.
	 *
	 * <ol>
	 * <li>Theme field</li>
	 * <li>Title field</li>
	 * <li>Question text area</li>
	 * <li>Answer rows (with checkbox for "correct")</li>
	 * </ol>
	 */
	private void layoutComponents() {
		add(new LabelFieldPanel("Theme:", themeField));
		add(Box.createVerticalStrut(10));
		add(new LabelFieldPanel("Title:", titelField));
		add(Box.createVerticalStrut(10));
		add(new questionPanel(questionArea));
		add(Box.createVerticalStrut(15));
		add(new AnswerHeaderPanel());
		add(Box.createVerticalStrut(8));
		for (int i = 0; i < 4; i++) {
			add(new AnswerRowPanel(i + 1, answerFields[i], checkboxes[i]));
			add(Box.createVerticalStrut(8));
		}
	}

	/**
	 * Sets the theme displayed in the form.
	 * <p>
	 * Intended to be called by the outer context (quiz question manager) when a
	 * theme is currently selected. Cannot be manually edited here.
	 * </p>
	 *
	 * @param t the selected {@link Theme}, or {@code null} to clear the theme field
	 */
	public void setThema(Theme t) {
		if (t != null) {
			themeField.setText(t.getTitle());
		}
	}

	/**
	 * Fills the form with an existing question for editing.
	 *
	 * <ul>
	 * <li>If the given question is {@code null}, the form is cleared.</li>
	 * <li>Otherwise, its theme, title, text, and answers are shown.</li>
	 * </ul>
	 *
	 * @param q the {@link Question} to load into the form, or {@code null} for
	 *          reset
	 */
	public void setQuestion(Question q) {
		if (q == null) {
			clearFields();
			return;
		}
		setThema(q.getThema());
		titelField.setText(q.getTitle());
		questionArea.setText(q.getText());
		List<Answer> answers = q.getAnswers();
		for (int i = 0; i < answerFields.length; i++) {
			if (i < answers.size()) {
				answerFields[i].setText(answers.get(i).getText());
				checkboxes[i].setSelected(answers.get(i).isCorrect());
			} else {
				answerFields[i].setText("");
				checkboxes[i].setSelected(false);
			}
		}
	}

	/**
	 * Builds a new {@link Question} object based on the current form inputs. - Only
	 * includes answers with non-empty text - Associates each created {@link Answer}
	 * with its "correct" flag
	 *
	 * @param selectedThema the currently selected {@link Theme} (context of the
	 *                      question)
	 * @return a new {@link Question} populated with the user’s form data
	 */
	public Question getSelectedQuestion(Theme selectedThema) {
		Question q = new Question(selectedThema);
		q.setTitle(titelField.getText());
		q.setText(questionArea.getText());
		for (int i = 0; i < answerFields.length; i++) {
			String text = answerFields[i].getText().trim();
			if (!text.isEmpty()) {
				Answer a = new Answer(q);
				a.setText(text);
				a.setCorrect(checkboxes[i].isSelected());
				a.setId(i);
				q.addAnswer(a);
			}
		}
		return q;
	}

	/**
	 * Clears all form fields (theme, title, text, and answers).
	 */
	private void clearFields() {
		themeField.setText("");
		titelField.setText("");
		questionArea.setText("");
		for (int i = 0; i < answerFields.length; i++) {
			answerFields[i].setText("");
			checkboxes[i].setSelected(false);
		}
	}

	/**
	 * Placeholder linking method to connect with a right-hand management panel.
	 * Currently unused, but allows for future inter-panel communication.
	 *
	 * @param quizFragenRight the {@link QuizQuestionRight} reference
	 */
	public void setPanelRight(QuizQuestionRight quizFragenRight) {
		// reserved for future linkage
	}
}
