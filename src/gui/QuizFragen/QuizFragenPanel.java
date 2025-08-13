package gui.QuizFragen;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import persistence.serialization.QuizDataManager;
import quizLogic.Answer;
import quizLogic.Question;
import quizLogic.Thema;

public class QuizFragenPanel extends JPanel implements QuizFragenDelegate {

	private static final long serialVersionUID = 1L;

	private QuizFragenLeft quizFragenLeft;
	private QuizFragenRight quizFragenRight;
	private QuizFragenBottom quizFragenBottom;
	private QuizDataManager dm;

	/**
	 * Konstruktor für das QuizFragenPanel.
	 * 
	 * @param dm QuizDataManager für Themen und Fragen.
	 */
	public QuizFragenPanel(QuizDataManager dm) {
		super();
		this.dm = dm;

		initLayout();
		initComponents();
		linkComponents();
		setDelegate();
	}

	private void initLayout() {
		setLayout(new BorderLayout(10, 10));
	}

	private void initComponents() {
		quizFragenLeft = new QuizFragenLeft(dm);
		quizFragenRight = new QuizFragenRight(dm);
		quizFragenBottom = new QuizFragenBottom();
	}

	private void linkComponents() {
		quizFragenRight.setPanelLeft(quizFragenLeft);
		quizFragenLeft.setPanelRight(quizFragenRight);

		add(quizFragenLeft, BorderLayout.WEST);
		add(quizFragenRight, BorderLayout.EAST);
		add(quizFragenBottom, BorderLayout.SOUTH);
	}

	private void setDelegate() {
		quizFragenBottom.setDelegate(this);
	}

	@Override
	public void onSaveQuestion() {
	    Thema selectedThema = (Thema) quizFragenRight.getThemaFragenPanel()
	            .getThemaComboBox().getSelectedItem();

	    if (!quizFragenRight.hasSelectedThema()) {
	        quizFragenBottom.getMessagePanel().setText("Bitte wählen Sie ein Thema aus.");
	        return;
	    }

	    Question q = quizFragenLeft.getSelectedQuestion(selectedThema);

	    if (q == null || q.getThema() == null) {
	        quizFragenBottom.getMessagePanel().setText("Ungültige Frage oder Thema.");
	        return;
	    }

	    // ✅ NEU: Eingabevalidierung
	    if (q.getTitle() == null || q.getTitle().trim().isEmpty()) {
	        quizFragenBottom.getMessagePanel().setText("Bitte geben Sie einen Titel ein.");
	        return;
	    }

	    if (q.getText() == null || q.getText().trim().isEmpty()) {
	        quizFragenBottom.getMessagePanel().setText("Bitte geben Sie den Fragetext ein.");
	        return;
	    }

	    if (q.getAnswers() == null || q.getAnswers().isEmpty()) {
	        quizFragenBottom.getMessagePanel().setText("Bitte geben Sie mindestens eine Antwort ein.");
	        return;
	    }

	    boolean hatKorrekteAntwort = q.getAnswers().stream().anyMatch(Answer::isCorrect);
	    if (!hatKorrekteAntwort) {
	        quizFragenBottom.getMessagePanel().setText("Bitte markieren Sie mindestens eine richtige Antwort.");
	        return;
	    }

	    // Prüfen, ob der Titel im Thema schon existiert
	    if (frageTitelExists(selectedThema, q.getTitle(), q)) {
	        quizFragenBottom.getMessagePanel().setText(
	            "Es existiert bereits eine andere Frage mit diesem Titel im gewählten Thema."
	        );
	        return;
	    }

	    String error = dm.saveQuestion(q);
	    if (error != null) {
	        quizFragenBottom.getMessagePanel().setText("Fehler beim Speichern: " + error);
	        return;
	    }

	    reloadFragenForThema(selectedThema);
	    quizFragenBottom.getMessagePanel().setText("Frage erfolgreich gespeichert.");
	}


	@Override
	public void onDeleteQuestion() {
		Question q = quizFragenRight.getSelectedQuestion();
		if (q == null || q.getThema() == null) {
			quizFragenBottom.getMessagePanel().setText("Keine Frage ausgewählt oder Thema ungültig.");
			return;
		}

		String result = dm.deleteQuestion(q);
		if (result != null) {
			quizFragenBottom.getMessagePanel().setText("Fehler beim Löschen der Frage: " + result);
		} else {
			reloadFragenForThema(q.getThema());
			quizFragenLeft.setFrage(null);
		}
	}

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
	
	

	@Override
	public void onNewQuestion() {
		quizFragenLeft.setFrage(null);
		quizFragenRight.getThemaFragenPanel().getFragenList().clearSelection();
	}

	private void reloadFragenForThema(Thema thema) {
		if (thema == null)
			return;
		ArrayList<Question> fragen = dm.getQuestionsFor(thema);
		quizFragenRight.reloadFragen(fragen);
	}

	public void reloadThemen() {
		ArrayList<Thema> themen = dm.getAllThemen();
		quizFragenRight.reloadThemen(themen);
	}
}
