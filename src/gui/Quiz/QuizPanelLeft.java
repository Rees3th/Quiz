package gui.Quiz;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import gui.Panels.QuizPanelLeftLayout;
import persistence.DBDataManager;
import quizLogic.Answer;
import quizLogic.Question;
import quizLogic.Theme;

/**
 * {@code QuizPanelLeft} represents the left-hand side of the quiz UI.
 *
 * <p>
 * This panel is responsible for displaying the detailed information about a
 * single quiz question, including:
 * </p>
 * <ul>
 * <li>The associated theme</li>
 * <li>The question title</li>
 * <li>The question text</li>
 * <li>Possible answers along with checkboxes</li>
 * </ul>
 *
 * <p>
 * The UI components are primarily <b>read-only</b>, serving as a display rather
 * than user input form. The panel ensures that any currently displayed question
 * is rendered consistently with its answers and checkboxes.
 * </p>
 *
 * <p>
 * Layout and positioning of components are handled by
 * {@link QuizPanelLeftLayout}, while this class focuses on data population and
 * state updates.
 * </p>
 *
 * <p>
 * Data is retrieved and managed through {@link DBDataManager}, which interacts
 * with the persistence/database layer.
 * </p>
 *
 * @author Oleg Kapirulya
 */
public class QuizPanelLeft extends JPanel {
	/** Serialization version UID (recommended for Swing panels). */
	private static final long serialVersionUID = 1L;

	/** Text field displaying the title of the theme (read-only). */
	private JTextField themaField;

	/** Text field displaying the question's title (read-only). */
	private JTextField titelField;

	/** Text area showing the main question text (read-only, word-wrapped). */
	private JTextArea frageArea;

	/** Text fields for showing up to 4 possible answers (read-only). */
	private JTextField[] answerFields = new JTextField[4];

	/** Checkboxes for each of the possible answers. */
	private JCheckBox[] checkboxes = new JCheckBox[4];

	/** Field for displaying messages, status, or feedback. */
	private JTextField messageField;

	/** Data manager to retrieve question/answer/theme details from DB. */
	private final DBDataManager dm;

	/** Optional link back to the right-side quiz panel. */
	private QuizPanelRight quizPanelRight;

	/**
	 * Constructs a new {@code QuizPanelLeft}.
	 *
	 * <p>
	 * The panel initializes all UI components, builds the layout via
	 * {@link QuizPanelLeftLayout}, and clears the display initially.
	 * </p>
	 *
	 * @param dm the {@link DBDataManager} used for data retrieval
	 */
	public QuizPanelLeft(DBDataManager dm) {
		this.dm = dm;
		initComponents();

		// Delegate layout arrangement to a dedicated layout helper
		QuizPanelLeftLayout.build(this, themaField, titelField, frageArea, answerFields, checkboxes, messageField);

		// Initially, nothing is selected, so fields are cleared
		fillWithData(null);
	}

	/**
	 * Initializes all Swing UI components for this panel and configures them as
	 * read-only where appropriate.
	 */
	private void initComponents() {
		themaField = new JTextField(27);
		themaField.setEditable(false);

		titelField = new JTextField(27);
		titelField.setEditable(false);

		frageArea = new JTextArea(6, 24);
		frageArea.setEditable(false);
		frageArea.setLineWrap(true);
		frageArea.setWrapStyleWord(true);

		for (int i = 0; i < 4; i++) {
			answerFields[i] = new JTextField(23);
			answerFields[i].setEditable(false);

			checkboxes[i] = new JCheckBox();
		}

		messageField = new JTextField(34);
		messageField.setEditable(false);
	}

	/**
	 * Populates this panel with data from the given {@link Question}.
	 * <ul>
	 * <li>If the question is non-null, its title, text, theme, and answers will be
	 * shown.</li>
	 * <li>If {@code null} is passed, all fields are cleared.</li>
	 * </ul>
	 *
	 * @param q the {@link Question} to display, or {@code null} to reset fields
	 */
	void fillWithData(Question q) {
		if (q == null) {
			clearFields();
			return;
		}

		themaField.setText(q.getThema() != null ? q.getThema().getTitle() : "");
		titelField.setText(q.getTitle());
		frageArea.setText(q.getText());

		// Copy answers into local array (limit 4)
		List<Answer> answers = new ArrayList<>(q.getAnswers());
		for (int i = 0; i < answerFields.length; i++) {
			if (i < answers.size()) {
				answerFields[i].setText(answers.get(i).getText());
				checkboxes[i].setSelected(false); // always reset as initial state
			} else {
				answerFields[i].setText("");
				checkboxes[i].setSelected(false);
			}
		}
	}

	/**
	 * Returns the text from the first answer field.
	 *
	 * <p>
	 * This is a helper method – primarily useful when only one answer field is
	 * relevant. In multi-answer scenarios, direct access to checkboxes/answers
	 * should be used.
	 * </p>
	 *
	 * @return the text of the first answer field, or {@code null} if none exist
	 */
	public String getUserAnswerText() {
		if (answerFields != null && answerFields.length > 0) {
			return answerFields[0].getText();
		}
		return null;
	}

	/**
	 * Enables or disables all answer checkboxes.
	 *
	 * @param enabled {@code true} to enable; {@code false} to disable
	 */
	public void setCheckboxesEnabled(boolean enabled) {
		for (JCheckBox cb : checkboxes) {
			cb.setEnabled(enabled);
		}
	}

	/**
	 * Clears all fields in the panel, including theme, question, answers, and
	 * status message.
	 */
	private void clearFields() {
		themaField.setText("");
		titelField.setText("");
		frageArea.setText("");

		for (int i = 0; i < answerFields.length; i++) {
			answerFields[i].setText("");
			checkboxes[i].setSelected(false);
		}

		messageField.setText("");
	}

	/**
	 * Sets the theme field to display the given {@link Theme}.
	 *
	 * @param t the theme to display; ignored if {@code null}
	 */
	public void setThema(Theme t) {
		if (t != null) {
			themaField.setText(t.getTitle());
		}
	}

	/**
	 * Sets the displayed question along with its answers.
	 *
	 * @param q the {@link Question} to show
	 */
	public void setFrage(Question q) {
		fillWithData(q);
	}

	/**
	 * Returns the dedicated message field, which can be used to show feedback,
	 * status information, or prompts to the user.
	 *
	 * @return the message {@link JTextField}
	 */
	public JTextField getMessageField() {
		return messageField;
	}

	/**
	 * Links this panel to the right-side quiz panel.
	 * <p>
	 * This allows inter-panel communication if the right panel needs to reference
	 * or update this one.
	 * </p>
	 *
	 * @param quizPanelRight the {@link QuizPanelRight} reference
	 */
	public void setPanelRight(QuizPanelRight quizPanelRight) {
		this.quizPanelRight = quizPanelRight;
	}
}
