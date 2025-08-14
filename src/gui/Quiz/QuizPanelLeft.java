package gui.Quiz;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import gui.Panels.QuizPanelLeftLayout;
import persistence.serialization.QuizDataManager;
import quizLogic.Answer;
import quizLogic.Question;
import quizLogic.Theme;

/**
 * {@code QuizPanelLeft} represents the left panel in the quiz UI.
 * <p>
 * It displays the detailed information of the selected quiz question,
 * including:
 * <ul>
 * <li>The associated theme</li>
 * <li>The question title</li>
 * <li>The question text</li>
 * <li>The possible answers with their checkboxes</li>
 * </ul>
 * The fields are mostly read-only and used to show the current question state.
 * </p>
 * 
 * <p>
 * Layout and component positioning are delegated to
 * {@link QuizPanelLeftLayout}.
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public class QuizPanelLeft extends JPanel {

	/** Serialization version UID. */
	private static final long serialVersionUID = 1L;

	/** Text field showing the theme title (read-only). */
	private JTextField themaField;

	/** Text field showing the question title (read-only). */
	private JTextField titelField;

	/** Text area showing the question text (read-only). */
	private JTextArea frageArea;

	/** Text fields showing up to 4 answer options (read-only). */
	private JTextField[] answerFields = new JTextField[4];

	/** Checkboxes associated with each answer option. */
	private JCheckBox[] checkboxes = new JCheckBox[4];

	/** Text field to display messages or status. */
	private JTextField messageField;

	/** Data manager for accessing quiz data. */
	private QuizDataManager dm;

	/** Reference to the right-side panel (if linking needed). */
	private QuizPanelRight quizPanelRight;

	/**
	 * Constructs a new {@code QuizPanelLeft}.
	 * 
	 * @param dm the {@link QuizDataManager} for accessing quiz data
	 */
	public QuizPanelLeft(QuizDataManager dm) {
		this.dm = dm;
		initComponents();
		QuizPanelLeftLayout.build(this, themaField, titelField, frageArea, answerFields, checkboxes, messageField);
		fillWithData(null);
	}

	/**
	 * Initializes the UI components and sets them to non-editable for display.
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
	 * Fills the fields with the given question's data. If the question is null, all
	 * fields are cleared.
	 * 
	 * @param q the {@link Question} to display, or {@code null} to clear fields
	 */
	private void fillWithData(Question q) {
		if (q == null) {
			clearFields();
			return;
		}
		themaField.setText(q.getThema() != null ? q.getThema().getTitle() : "");
		titelField.setText(q.getTitle());
		frageArea.setText(q.getText());

		List<Answer> answers = new ArrayList<>(q.getAnswers());
		for (int i = 0; i < answerFields.length; i++) {
			if (i < answers.size()) {
				answerFields[i].setText(answers.get(i).getText());
				checkboxes[i].setSelected(false); // Reset checkboxes, as this panel only displays answers
			} else {
				answerFields[i].setText("");
				checkboxes[i].setSelected(false);
			}
		}
	}

	/**
	 * Returns the text from the first answer field.
	 * 
	 * @return the text of the first answer field, or {@code null} if no answers
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
	 * Clears all input and display fields.
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
	 * Sets the displayed theme title.
	 * 
	 * @param t the {@link Theme} whose title will be displayed; ignored if null
	 */
	public void setThema(Theme t) {
		if (t != null) {
			themaField.setText(t.getTitle());
		}
	}

	/**
	 * Sets the displayed question and answers.
	 * 
	 * @param q the {@link Question} to display
	 */
	public void setFrage(Question q) {
		fillWithData(q);
	}

	/**
	 * Returns the message text field used for displaying info or status messages.
	 * 
	 * @return the message {@link JTextField}
	 */
	public JTextField getMessageField() {
		return messageField;
	}

	/**
	 * Sets the reference to the right panel.
	 * 
	 * @param quizPanelRight the {@link QuizPanelRight} instance to link to
	 */
	public void setPanelRight(QuizPanelRight quizPanelRight) {
		this.quizPanelRight = quizPanelRight;
	}
}
