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
	private JTextField messageField;
	private FakeDataDeliver fdd;
	private Map<Integer, Answer> answerMap = new HashMap<>();

	/** Konstruktor, der ein FakeDataDeliver-Objekt entgegennimmt */

	public QuizFragenLeft(FakeDataDeliver fdd) {
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
		titelField.setEditable(true);
		add(new LabelFieldPanel("Titel:", titelField));
		add(Box.createVerticalStrut(10));

		// Panel: Frage
		frageArea = new JTextArea(6, 24);
		frageArea.setEditable(true);
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
			answerFields[i].setEditable(true);
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

	/** Konstruktor, der eine Frage entgegennimmt und die Felder damit füllt */
	private void fillWithData(Question q) {
		if (q != null) {
			themaField.setText(q.getThema().getTitle());
			titelField.setText(q.getTitle());
			frageArea.setText(q.getText());

			Collection<Answer> answersCollection = q.getAnswers();
			List<Answer> answers = new ArrayList<>(q.getAnswers());

			for (int i = 0; i < answers.size() && i < 4; i++) {
				answerFields[i].setText(answers.get(i).getText());
				checkboxes[i].setSelected(answers.get(i).isCorrect());
			}
		}
	}

	/** Setzt das Thema-Feld auf den Titel des übergebenen Themas */
	public void setThema(Thema t) {
		if (t != null)
			themaField.setText(t.getTitle());
	}

	/** Setzt die Frage-Felder basierend auf der übergebenen Frage */
	public void setFrage(Question q) {
		if (q != null) {
			themaField.setText(q.getThema() != null ? q.getThema().getTitle() : "");
			titelField.setText(q.getTitle());
			frageArea.setText(q.getText());

			int i = 0;
			for (Answer a : q.getAnswers()) {
				if (i >= answerFields.length)
					break;
				answerFields[i].setText(a.getText());
				checkboxes[i].setSelected(a.isCorrect());
				i++;
			}
			for (; i < answerFields.length; i++) {
				answerFields[i].setText("");
				checkboxes[i].setSelected(false);
			}
		} else {
			themaField.setText("");
			titelField.setText("");
			frageArea.setText("");
			for (int i = 0; i < answerFields.length; i++) {
				answerFields[i].setText("");
				checkboxes[i].setSelected(false);
			}
		}
	}

	/** Gibt die aktuell eingegebene Frage zurück */
	public Question getSelectedQuestion() {
		Question q = new Question(null);
		q.setTitle(titelField.getText());
		q.setText(frageArea.getText());

		Thema thema = new Thema();
		thema.setTitle(themaField.getText());
		q.setThema(thema);

		for (int i = 0; i < answerFields.length; i++) {
			if (!answerFields[i].getText().isEmpty()) {
				Answer a = new Answer(q);
				a.setText(answerFields[i].getText());
				a.setCorrect(checkboxes[i].isSelected());
				q.addAnswer(a);
			}
		}

		return q;
	}

	/** Aktualisiert die übergebene Frage mit den aktuellen Eingabefeldern */
	public void updateQuestionFromFields(Question q) {
		if (q == null)
			return;

		q.setTitle(titelField.getText());
		q.setText(frageArea.getText());

		// Antworten löschen und neu setzen
		q.getAnswers().clear();

		for (int i = 0; i < answerFields.length; i++) {
			String txt = answerFields[i].getText();
			if (txt != null && !txt.trim().isEmpty()) {
				Answer a = new Answer(q);
				a.setId(fdd.getNextAnswerId());
				a.setText(txt);
				a.setCorrect(checkboxes[i].isSelected());
				q.addAnswer(a);
			}
		}
	}

	/** Erstellt eine neue Frage basierend auf den Eingabefeldern */
	public Question getNewQuestionFromFields() {
		Question q = new Question(null);
		q.setId(fdd.getNextQuestionId());

		q.setTitle(titelField.getText());
		q.setText(frageArea.getText());

		for (int i = 0; i < answerFields.length; i++) {
			String text = answerFields[i].getText();
			if (text != null && !text.trim().isEmpty()) {
				Answer a = new Answer(q);
				a.setId(fdd.getNextAnswerId());
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

	// Getter für Message
	public JTextField getMessageField() {
		return messageField;
	}

	// Setter
	public void setPanelRight(QuizFragenRight quizFragenRight) {

	}

	public Collection<Answer> getAnswers() {
		return answerMap.values();
	}

}
