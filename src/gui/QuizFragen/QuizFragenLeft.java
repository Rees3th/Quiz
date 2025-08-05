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
import quizLogic.Answer;
import quizLogic.FakeDataDeliver;
import quizLogic.Question;
import quizLogic.Thema;

/**
 * Linke Seite des Quiz-Fragen-Editors, mit Eingabefeldern für Frage &
 * Antworten.
 */
public class QuizFragenLeft extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextField themaField;
	private JTextField titelField;
	private JTextArea frageArea;
	private JTextField[] answerFields = new JTextField[4];
	private MyCheckBox[] checkboxes = new MyCheckBox[4];
	private JTextField messageField;
	private FakeDataDeliver fdd;
	private Map<Integer, Answer> answerMap = new HashMap<>();

	/**
	 * Konstruktor für das linke Panel des Quiz-Fragen-Editors.
	 *
	 * @param fdd FakeDataDeliver, um zufällige Fragen zu laden.
	 */
	public QuizFragenLeft(FakeDataDeliver fdd) {
		this.fdd = fdd;
		initPanel();
		initComponents();
		layoutComponents();
		if (fdd != null) {
			fillWithData(fdd.getRandomQuestion());
		}
	}

	/** Initialisiert das Layout des Panels. */
	private void initPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setMaximumSize(new Dimension(500, 500));
		setPreferredSize(new Dimension(450, 500));
	}

	/** Initialisiert die Komponenten des Panels. */
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

		messageField = new JTextField(34);
		messageField.setEditable(false);
	}

	/** Layoutet die Komponenten im Panel. */
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
		add(new MessagePanel(messageField));
		add(Box.createVerticalStrut(15));
	}

	/** Fügt die Überschrift für die Antwortfelder hinzu. */
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

	/** Fügt die Antwortzeilen zum Panel hinzu. */
	private void addAnswerRows() {
		for (int i = 0; i < 4; i++) {
			add(new AnswerRowPanel(i + 1, answerFields[i], checkboxes[i]));
			add(Box.createVerticalStrut(8));
		}
	}

	/**
	 * Füllt die Eingabefelder mit den Daten der angegebenen Frage.
	 *
	 * @param q die Frage, deren Daten angezeigt werden sollen.
	 */
	public void fillWithData(Question q) {
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

	/** Löscht alle Eingabefelder im Panel. */
	public void clearFields() {
		themaField.setText("");
		titelField.setText("");
		frageArea.setText("");
		for (int i = 0; i < answerFields.length; i++) {
			answerFields[i].setText("");
			checkboxes[i].setSelected(false);
		}
	}

	/** Setzt das Thema-Feld basierend auf dem angegebenen Thema. */
	public void setThema(Thema t) {
		if (t != null)
			themaField.setText(t.getTitle());
	}

	/** Setzt die Frage-Felder basierend auf der angegebenen Frage. */
	public void setFrage(Question q) {
		fillWithData(q);
	}

	/** Erstellt eine neue Frage basierend auf den Eingabefeldern. */
	public JTextField getMessageField() {
		return messageField;
	}
}
