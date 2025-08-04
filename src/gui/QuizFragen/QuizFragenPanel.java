package gui.QuizFragen;

import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import quizLogic.FakeDataDeliver;
import quizLogic.Question;
import quizLogic.Thema;

public class QuizFragenPanel extends JPanel implements QuizFragenDelegate {

	private static final long serialVersionUID = 1L;

	private QuizFragenLeft quizFragenLeft;
	private QuizFragenRight quizFragenRight;
	private QuizFragenBottom quizFragenBottom;
	private FakeDataDeliver fdd;

	/**
	 * Konstruktor für das QuizFragenPanel.
	 * 
	 * @param fdd FakeDataDeliver für Themen und Fragen.
	 */
	public QuizFragenPanel(FakeDataDeliver fdd) {
		super();
		this.fdd = fdd;
		initLayout();
		initComponents();
		linkComponents();
		setDelegate();
	}

	/**
	 * Initialisiert das Layout des Panels.
	 */
	private void initLayout() {
		setLayout(new BorderLayout(10, 10));
	}

	/**
	 * Initialisiert die Komponenten des Panels.
	 */
	private void initComponents() {
		quizFragenLeft = new QuizFragenLeft(fdd);
		quizFragenRight = new QuizFragenRight(fdd);
		quizFragenBottom = new QuizFragenBottom();
	}

	/**
	 * Verknüpft die Komponenten des Panels miteinander. Diese Methode stellt
	 * sicher, dass die linken und rechten Panels korrekt miteinander kommunizieren
	 * können.
	 */
	private void linkComponents() {
		quizFragenRight.setPanelLeft(quizFragenLeft);
		quizFragenLeft.setPanelRight(quizFragenRight);

		add(quizFragenLeft, BorderLayout.WEST);
		add(quizFragenRight, BorderLayout.EAST);
		add(quizFragenBottom, BorderLayout.SOUTH);
	}

	/**
	 * Setzt den Delegate für die QuizFragenBottom-Komponente. Diese Methode
	 * ermöglicht es dem Bottom-Panel, Ereignisse an das QuizFragenPanel
	 * weiterzuleiten.
	 */
	private void setDelegate() {
		quizFragenBottom.setDelegate(this);
	}

	/**
	 * Löscht die aktuell ausgewählte Frage aus der Liste. Entfernt die Frage aus
	 * dem Modell und leert die Eingabefelder im linken Panel.
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
	 * Speichert eine neue oder aktualisierte Frage. Prüft zunächst, ob eine Frage
	 * mit gleichem Titel im Thema existiert. Bei Aktualisierung wird die
	 * ausgewählte Frage überschrieben, bei neuer Frage wird diese dem Thema und
	 * Modell hinzugefügt.
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
	 * Prüft, ob in einem bestimmten Thema eine Frage mit einem bestimmten Titel
	 * bereits existiert. Die aktuell bearbeitete Frage (exclude) wird ignoriert, um
	 * Doppelmeldungen bei Update zu vermeiden.
	 *
	 * @param thema   Das Thema, in dem gesucht wird.
	 * @param titel   Der Titel, der geprüft wird.
	 * @param exclude Die Frage, die bei der Suche ausgeschlossen wird.
	 * @return true, falls eine andere Frage den Titel verwendet, sonst false.
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
	 * Bereitet die Benutzeroberfläche auf eine neue Frage vor. Setzt alle
	 * Eingabefelder im linken Panel zurück und entfernt die Markierung in der
	 * Fragenliste im rechten Panel.
	 */
	@Override
	public void onNewQuestion() {
		quizFragenLeft.setFrage(null); // Felder zurücksetzen
		quizFragenRight.getThemaFragenPanel().getFragenList().clearSelection(); // Auswahl löschen
	}

	/**
	 * Lädt die Themen neu und aktualisiert Themenliste des rechten Panels.
	 */
	public void reloadThemen() {
		Collection<Thema> alleThemen = fdd.getAllThemen();
		quizFragenRight.reloadThemen(alleThemen);
	}
}
