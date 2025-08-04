package gui.Quiz;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
 * Diese Klasse repräsentiert das linke Panel im Quiz-Interface, welches die
 * Details zu einer Frage anzeigt, inklusive Thema, Titel, Frage-Text und
 * mögliche Antworten.
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
	 * Konstruktor, der ein FakeDataDeliver-Objekt entgegennimmt, um initial mit
	 * einer zufälligen Frage gefüllt zu werden.
	 */

	public QuizPanelLeft(FakeDataDeliver fdd) {
		this.fdd = fdd;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setMaximumSize(new Dimension(500, 500));
		setPreferredSize(new Dimension(450, 500));

		// Panel: Thema
		themaField = new JTextField(27);
		themaField.setEditable(false);
		add(new LabelFieldPanel("Thema:", themaField));
		add(Box.createVerticalStrut(10));

		// Panel: Titel
		titelField = new JTextField(27);
		titelField.setEditable(false);
		add(new LabelFieldPanel("Titel:", titelField));
		add(Box.createVerticalStrut(10));

		// Panel: Frage
		frageArea = new JTextArea(6, 24);
		frageArea.setEditable(false);
		add(new FragePanel(frageArea));
		add(Box.createVerticalStrut(15));

		// Header für Antworten
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
		add(Box.createVerticalStrut(8));

		// 4 Antwortzeilen
		for (int i = 0; i < 4; i++) {
			answerFields[i] = new JTextField(23);
			answerFields[i].setEditable(false);
			checkboxes[i] = new MyCheckBox();

			add(new AnswerRowPanel(i + 1, answerFields[i], checkboxes[i]));
			add(Box.createVerticalStrut(8));
		}

		// Panel: Nachricht
		messageField = new JTextField(34);
		messageField.setEditable(false);
		add(new MessagePanel(messageField));
		add(Box.createVerticalStrut(15));

		if (fdd != null) {
			fillWithData(fdd.getRandomQuestion());
		}
	}

	/** Befüllt die Felder mit Daten aus einem Question-Objekt */
	private void fillWithData(Question q) {
		if (q != null) {
			themaField.setText(q.getThema().getTitle());
			titelField.setText(q.getTitle());
			frageArea.setText(q.getText());

			List<Answer> answers = new ArrayList<>(q.getAnswers());
			for (int i = 0; i < answers.size() && i < 4; i++) {
				answerFields[i].setText(answers.get(i).getText());
				checkboxes[i].setSelected(false);
			}

		}
	}

	/** Setzt das Thema-Feld basierend auf einem Thema-Objekt */
	public void setThema(Thema t) {
		if (t != null)
			themaField.setText(t.getTitle());
	}

	/** Setzt die Frage-Felder basierend auf einem Question-Objekt */
	public void setFrage(Question q) {
		if (q != null) {
			titelField.setText(q.getTitle());
			frageArea.setText(q.getText());

			// Antworten sortieren und in die Felder eintragen
			List<Answer> answers = new ArrayList<>(q.getAnswers());
			answers.sort((a1, a2) -> Integer.compare(a1.getId(), a2.getId()));

			int i = 0;
			for (; i < answers.size() && i < answerFields.length; i++) {
				answerFields[i].setText(answers.get(i).getText());
				checkboxes[i].setSelected(false);
			}

			for (; i < answerFields.length; i++) {
				answerFields[i].setText("");
				checkboxes[i].setSelected(false);
			}

		} else {

			titelField.setText("");
			frageArea.setText("");
			for (int i = 0; i < answerFields.length; i++) {
				answerFields[i].setText("");
				checkboxes[i].setSelected(false);
			}

		}
	}

	/// Setzt die Nachricht im Nachrichtenfeld
	public JTextField getMessageField() {
		return messageField;
	}

	/// Setzt das QuizPanelRight-Objekt, um auf Änderungen reagieren zu können
	public void setPanelRight(QuizPanelRight quizPanelRight) {
		this.quizPanelRight = quizPanelRight;
	}

}
