package gui.QuizFragen;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import quizLogic.FakeDataDeliver;
import quizLogic.Question;
import quizLogic.Thema;

/**
 * Panel für die Verwaltung von Quizfragen. Enthält ein linkes Panel für die
 * Frageingabe, ein rechtes Panel für die Themen und Fragenliste sowie ein
 * unteres Panel für Aktionen wie Speichern und Löschen von Fragen.
 */

public class QuizFragenPanel extends JPanel implements QuizFragenDelegate {

	private static final long serialVersionUID = 1L;

	private QuizFragenLeft quizFragenLeft;
	private QuizFragenRight quizFragenRight;
	private QuizFragenBottom quizFragenBottom;
	private FakeDataDeliver fdd;
	private QuizFragenDelegate quizFragenPanel;

	/**
	 * Konstruktor für das QuizFragenPanel.
	 * 
	 * @param fdd FakeDataDeliver, der die Daten liefert (Themen und Fragen).
	 */
	public QuizFragenPanel(FakeDataDeliver fdd) {
		super();
		this.fdd = fdd;
		setLayout(new BorderLayout(10, 10));

		quizFragenLeft = new QuizFragenLeft(fdd);
		quizFragenRight = new QuizFragenRight(fdd);
		quizFragenRight.setPanelLeft(quizFragenLeft);
		quizFragenLeft.setPanelRight(quizFragenRight);

		quizFragenBottom = new QuizFragenBottom();

		add(quizFragenLeft, BorderLayout.WEST);
		add(quizFragenRight, BorderLayout.EAST);
		add(quizFragenBottom, BorderLayout.SOUTH);

		quizFragenBottom.setDelegate(this);

	}

	/**
	 * Methode, die löscht, wenn eine Frage ausgewählt wird. Aktualisiert das linke
	 * Panel mit den Details der ausgewählten Frage.
	 */
	@Override
	public void onDeleteQuestion() {
		DefaultListModel<Question> model = (DefaultListModel<Question>) quizFragenRight.getThemaFragenPanel()
				.getFragenList().getModel();
		int selectedIndex = quizFragenRight.getThemaFragenPanel().getFragenList().getSelectedIndex();
		if (selectedIndex != -1) {
			model.remove(selectedIndex);
			quizFragenLeft.setFrage(null);
		}
	}

	/**
	 * Methode, die aufgerufen wird, wenn eine Frage gespeichert wird. Speichert die
	 * aktuelle Frage in der Liste oder aktualisiert sie, wenn sie bereits
	 * existiert.
	 */
	@Override
	public void onSaveQuestion() {
		DefaultListModel<Question> model = (DefaultListModel<Question>) quizFragenRight.getThemaFragenPanel()
				.getFragenList().getModel();
		int selectedIndex = quizFragenRight.getThemaFragenPanel().getFragenList().getSelectedIndex();

		String titel = quizFragenLeft.getTitelField().getText().trim();
		Thema aktuelleThema = (Thema) quizFragenRight.getThemaFragenPanel().getThemaComboBox().getSelectedItem();

		Question current = (selectedIndex != -1) ? model.getElementAt(selectedIndex) : null;

		if (frageTitelExists(aktuelleThema, titel, current)) {
			JOptionPane.showMessageDialog(this, "Es existiert bereits eine Frage mit diesem Titel in diesem Thema!",
					"Fehler", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (selectedIndex != -1) {
			Question original = model.getElementAt(selectedIndex);
			quizFragenLeft.updateQuestionFromFields(original);
			model.set(selectedIndex, original);
		} else {
			Question newQuestion = quizFragenLeft.getNewQuestionFromFields();
			newQuestion.setThema(aktuelleThema);
			if (aktuelleThema != null) {
				aktuelleThema.addQuestion(newQuestion);
			}
			model.addElement(newQuestion);
			quizFragenRight.getThemaFragenPanel().getFragenList().setSelectedValue(newQuestion, true);
		}
	}

	/**
	 * Überprüft, ob eine Frage mit dem gleichen Titel im Thema bereits existiert.
	 * 
	 * @param thema   Das Thema, in dem nach der Frage gesucht wird.
	 * @param titel   Der Titel der zu überprüfenden Frage.
	 * @param exclude Die Frage, die ausgeschlossen werden soll (z.B. beim
	 *                Aktualisieren).
	 * @return true, wenn eine Frage mit dem gleichen Titel existiert, sonst false.
	 */

	private boolean frageTitelExists(Thema thema, String titel, Question exclude) {
		titel = titel.trim().toLowerCase();
		if (thema == null || thema.getAllQuestions() == null)
			return false;
		for (Question q : thema.getAllQuestions()) {
			if (q == exclude)
				continue;
			if (q.getTitle().trim().toLowerCase().equals(titel)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Methode, die aufgerufen wird, wenn eine neue Frage erstellt wird. Setzt die
	 * Felder im linken Panel zurück und leert die Auswahl im rechten Panel.
	 */
	@Override
	public void onNewQuestion() {
		quizFragenLeft.setFrage(null); // Setzt alle Felder zurück
		quizFragenRight.getThemaFragenPanel().getFragenList().clearSelection();
	}

	/**
	 * Methode, die aufgerufen wird, wenn die Themen neu geladen werden.
	 * Aktualisiert die Themenliste im rechten Panel und setzt die Auswahl zurück.
	 */

	public void reloadThemen() {
		quizFragenRight.reloadThemen(fdd.getAllThemen());
	}

	// Setter
	public void setQuizFragenPanel(QuizFragenPanel quizFragenPanel) {
		this.quizFragenPanel = quizFragenPanel;
	}

}
