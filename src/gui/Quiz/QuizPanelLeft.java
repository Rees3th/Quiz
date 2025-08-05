package gui.Quiz;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
 * Linkes Panel im Quiz: zeigt Details (Thema, Titel, Frage,
 * Antwortmöglichkeiten) an.
 */
public class QuizPanelLeft extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextField themaField;
	private JTextField titelField;
	private JTextArea frageArea;
	private JTextField[] answerFields = new JTextField[4];
	private MyCheckBox[] checkboxes = new MyCheckBox[4];
	private JTextField messageField;
	private FakeDataDeliver fdd;
	private QuizPanelRight quizPanelRight;

	/**
	 * Konstruktor: initialisiert GUI und füllt eine Zufallsfrage.
	 */
	public QuizPanelLeft(FakeDataDeliver fdd) {
		this.fdd = fdd;
		initPanel();
		initComponents();
		layoutComponents();
		if (fdd != null) {
			fillWithData(fdd.getRandomQuestion());
		}
	}

	/** Initialisiert das Layout des Panels */
	private void initPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setMaximumSize(new Dimension(500, 500));
		setPreferredSize(new Dimension(450, 500));
	}

	/** Initialisiert die GUI-Komponenten */
	private void initComponents() {
		themaField = new JTextField(27);
		themaField.setEditable(false);

		titelField = new JTextField(27);
		titelField.setEditable(false);

		frageArea = new JTextArea(6, 24);
		frageArea.setEditable(false);

		for (int i = 0; i < 4; i++) {
			answerFields[i] = new JTextField(23);
			answerFields[i].setEditable(false);
			checkboxes[i] = new MyCheckBox();
		}

		messageField = new JTextField(34);
		messageField.setEditable(false);
	}

	/** Layoutet die Komponenten im Panel */
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

	/** Fügt die Überschrift für die Antwortmöglichkeiten hinzu */
	private void addAnswersHeader() {
		JPanel answersHeader = new JPanel();
		answersHeader.setLayout(new BoxLayout(answersHeader, BoxLayout.X_AXIS));
		javax.swing.JLabel label = new javax.swing.JLabel("Mögliche Antwortwahl");
		label.setPreferredSize(new Dimension(170, 16));
		answersHeader.add(label);
		answersHeader.add(Box.createHorizontalGlue());
		javax.swing.JLabel label2 = new javax.swing.JLabel("Richtig");
		label2.setPreferredSize(new Dimension(50, 16));
		answersHeader.add(label2);
		add(answersHeader);
	}

	/** Fügt die Antwortzeilen hinzu */
	private void addAnswerRows() {
		for (int i = 0; i < 4; i++) {
			add(new AnswerRowPanel(i + 1, answerFields[i], checkboxes[i]));
			add(Box.createVerticalStrut(8));
		}
	}

	/** Befüllt die Felder mit einer Frage oder leert sie, falls null */
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

	/** Leert alle Eingabefelder */
	private void clearFields() {
		themaField.setText("");
		titelField.setText("");
		frageArea.setText("");
		for (int i = 0; i < answerFields.length; i++) {
			answerFields[i].setText("");
			checkboxes[i].setSelected(false);
		}
	}

	/** Setzt das dargestellte Thema */
	public void setThema(Thema t) {
		if (t != null)
			themaField.setText(t.getTitle());
	}

	/** Setzt die dargestellte Frage inkl. Antworten */
	public void setFrage(Question q) {
		fillWithData(q);
	}

	/** Getter: Nachrichtentextfeld */
	public JTextField getMessageField() {
		return messageField;
	}

	/** Setter: Referenz auf das rechte Panel */
	public void setPanelRight(QuizPanelRight quizPanelRight) {
		this.quizPanelRight = quizPanelRight;
	}
}
