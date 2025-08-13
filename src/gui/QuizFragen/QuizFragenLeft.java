package gui.QuizFragen;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import gui.My.MyCheckBox;
import gui.Panels.AnswerRowPanel;
import gui.Panels.FragePanel;
import gui.Panels.LabelFieldPanel;
import gui.Panels.MessagePanel;
import persistence.serialization.QuizDataManager;
import quizLogic.Answer;
import quizLogic.Question;
import quizLogic.Thema;

/**
 * Linke Seite des Quiz-Fragen-Editors, die die Eingabefelder für eine Frage und
 * deren Antworten enthält.
 */
public class QuizFragenLeft extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextField themaField;
	private JTextField titelField;
	private JTextArea frageArea;
	private JTextField[] answerFields = new JTextField[4];
	private MyCheckBox[] checkboxes = new MyCheckBox[4];
	private QuizDataManager dm;
	private Map<Integer, Answer> answerMap = new HashMap<>();

	public QuizFragenLeft(QuizDataManager dm) {
		this.dm = dm;
		initPanel();
		initComponents();
		layoutComponents();

	}

	/**
	 * Initialisiert Layout und grundlegende Einstellungen des Panels
	 */
	private void initPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setMaximumSize(new Dimension(500, 500));
		setPreferredSize(new Dimension(450, 500));
	}

	/**
	 * Initialisiert alle GUI-Komponenten
	 */
	private void initComponents() {
		themaField = new JTextField(27);
		themaField.setEditable(false);

		titelField = new JTextField(27);
		titelField.setEditable(true);

		frageArea = new JTextArea(6, 24);
		frageArea.setEditable(true);

		for (int i = 0; i < 4; i++) {
			answerFields[i] = new JTextField(23);
			answerFields[i].setEditable(true);
			checkboxes[i] = new MyCheckBox();
		}

	}

	/**
	 * Baut das Layout mit den Komponenten auf
	 */
	private void layoutComponents() {
		add(new LabelFieldPanel("Thema:", themaField));
		add(Box.createVerticalStrut(10));

		add(new LabelFieldPanel("Titel:", titelField));
		add(Box.createVerticalStrut(10));

		add(new FragePanel(frageArea));
		add(Box.createVerticalStrut(15));

		addAnswersHeader();
		add(Box.createVerticalStrut(8));
		addAnswerRows();
		add(Box.createVerticalStrut(15));

		add(new MessagePanel());
		add(Box.createVerticalStrut(15));
	}

	/**
	 * Fügt die Headerzeile für die Antworten hinzu
	 */
	private void addAnswersHeader() {
		JPanel answersHeader = new JPanel();
		answersHeader.setLayout(new BoxLayout(answersHeader, BoxLayout.X_AXIS));

		JLabel label = new JLabel("Mögliche Antwortwahl");
		label.setPreferredSize(new Dimension(170, 16));
		answersHeader.add(label);

		answersHeader.add(Box.createHorizontalGlue());

		JLabel label2 = new JLabel("Richtig");
		label2.setPreferredSize(new Dimension(50, 16));
		answersHeader.add(label2);

		add(answersHeader);
	}

	/**
	 * Fügt die Antwortzeilen mit Textfeldern und Checkboxen hinzu
	 */
	private void addAnswerRows() {
		for (int i = 0; i < 4; i++) {
			add(new AnswerRowPanel(i + 1, answerFields[i], checkboxes[i]));
			add(Box.createVerticalStrut(8));
		}
	}

	/**
	 * Füllt die Eingabefelder mit den Daten einer übergebenen Frage
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
				checkboxes[i].setSelected(answers.get(i).isCorrect());
			} else {
				answerFields[i].setText("");
				checkboxes[i].setSelected(false);
			}
		}
	}

	/**
	 * Löscht alle Eingabefelder
	 */
	private void clearFields() {
		themaField.setText("");
		titelField.setText("");
		frageArea.setText("");
		for (int i = 0; i < answerFields.length; i++) {
			answerFields[i].setText("");
			checkboxes[i].setSelected(false);
		}
	}

	/**
	 * Setzt das Thema-Feld auf den Titel des übergebenen Themas
	 */
	public void setThema(Thema t) {
		if (t != null)
			themaField.setText(t.getTitle());
	}

	/**
	 * Setzt die Frage-Felder basierend auf der übergebenen Frage
	 */
	public void setFrage(Question q) {
		if (q == null) {
			titelField.setText("");
			frageArea.setText("");
			for (int i = 0; i < 4; i++) {
				answerFields[i].setText("");
				checkboxes[i].setSelected(false);
			}
			return;
		}
		titelField.setText(q.getTitle());
		frageArea.setText(q.getText());
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
	 * Gibt die aktuell eingegebene Frage zurück
	 */
	public Question getSelectedQuestion(Thema selectedThema) {
		Question q = new Question(selectedThema);
		q.setTitle(titelField.getText());
		q.setText(frageArea.getText());
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
	 * Erstellt eine neue Frage basierend auf den Eingabefeldern
	 */
	public Question getNewQuestionFromFields() {
		Question q = new Question(null);
		q.setId(-1); // Noch keine ID: signalisiert neue Frage
		q.setTitle(titelField.getText());
		q.setText(frageArea.getText());

		for (int i = 0; i < answerFields.length; i++) {
			String text = answerFields[i].getText();
			if (text != null && !text.trim().isEmpty()) {
				Answer a = new Answer(q);
				a.setId(-1); // Neu, ID bekommt Antwort erst später
				a.setText(text);
				a.setCorrect(checkboxes[i].isSelected());
				q.addAnswer(a);
			}
		}
		return q;
	}

	// Getter

	public JTextField getTitelField() {
		return titelField;
	}

	// Setter

	public void setPanelRight(QuizFragenRight quizFragenRight) {
		// Implementierung falls benötigt
	}

	public Collection<Answer> getAnswers() {
		return answerMap.values();
	}
}