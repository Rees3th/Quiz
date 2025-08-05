package gui.Quiz;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;

import quizLogic.Answer;
import quizLogic.FakeDataDeliver;
import quizLogic.Question;
import quizLogic.Thema;

/**
 * Hauptpanel für die Quiz-Anwendung. Besteht aus dem linken, rechten und
 * unteren Panel für die Anzeige und Steuerung.
 */
public class QuizPanel extends JPanel implements QuizDelegate {
	private static final long serialVersionUID = 1L;

	private QuizPanelLeft quizPanelLeft;
	private QuizPanelRight quizPanelRight;
	private QuizPanelBottom quizButtonPanel;
	private FakeDataDeliver fdd;

	/**
	 * Konstruktor für das Haupt-Quiz-Panel.
	 */
	public QuizPanel(FakeDataDeliver fdd) {
		super();
		this.fdd = fdd;
		setLayout(new BorderLayout(10, 10));
		initPanels();
		SetPanels();
		buildLayout();
	}

	/** Initialisiert und erzeugt die Unterpanels. */
	private void initPanels() {
		quizPanelLeft = new QuizPanelLeft(fdd);
		quizPanelRight = new QuizPanelRight(fdd);
		quizButtonPanel = new QuizPanelBottom();
	}

	/**
	 * Verknüpft die Panels.
	 */
	private void SetPanels() {
		quizPanelRight.setPanelLeft(quizPanelLeft);
		quizPanelLeft.setPanelRight(quizPanelRight);
		quizButtonPanel.setDelegate(this);
	}

	/** Bauen des Layouts mit den Panels. */
	private void buildLayout() {
		add(quizPanelLeft, BorderLayout.WEST);
		add(quizPanelRight, BorderLayout.EAST);
		add(quizButtonPanel, BorderLayout.SOUTH);
	}

	/**
	 * Zeigt die richtige Antwort für die aktuell ausgewählte Frage an.
	 * TODO: Überprüfen.
	 */
	@Override
	public void onShowAnswer() {
		Question currentQuestion = quizPanelRight.getThemaFragenPanel().getFragenList().getSelectedValue();
		if (currentQuestion == null)
			return;

		quizPanelRight.markAnswered(currentQuestion.getId());
		quizPanelRight.getThemaFragenPanel().getFragenList().repaint();

		Answer richtigeAntwort = null;
		for (Answer a : currentQuestion.getAnswers()) {
			if (a.isCorrect()) {
				richtigeAntwort = a;
				break;
			}
		}

		String info = (richtigeAntwort == null) ? "Bei dieser Frage ist keine richtige Antwort markiert."
				: "Die richtige Antwort ist: " + richtigeAntwort.getText();

		quizPanelLeft.getMessageField().setText(info);
	}

	/**
	 * Platzhalter für Speichern der Antwort.
	 */
	@Override
	public void onSaveAnswer() {
		System.out.println("Antwort gespeichert");
	}

	/**
	 * Wählt zufällig eine Frage aus dem aktuellen Thema oder aus allen Themen.
	 * Setzt die Auswahl und zeigt ggf. eine Meldung, falls keine Fragen vorhanden
	 * sind.
	 */
	@Override
	public void onNewQuestion() {
		Thema selectedThema = (Thema) quizPanelRight.getThemaFragenPanel().getThemaComboBox().getSelectedItem();

		List<Question> alleFragen = new ArrayList<>();
		if (selectedThema != null && selectedThema.getAllQuestions() != null
				&& !selectedThema.getAllQuestions().isEmpty()) {
			alleFragen.addAll(selectedThema.getAllQuestions());
		} else {
			for (Thema t : quizPanelRight.getFdd().getAllThemen()) {
				if (t.getAllQuestions() != null) {
					alleFragen.addAll(t.getAllQuestions());
				}
			}
		}

		if (alleFragen.isEmpty()) {
			quizPanelLeft.getMessageField().setText("Keine Fragen vorhanden.");
			return;
		}

		int idx = (int) (Math.random() * alleFragen.size());
		Question randomQuestion = alleFragen.get(idx);

		DefaultListModel<Question> model = (DefaultListModel<Question>) quizPanelRight.getThemaFragenPanel()
				.getFragenList().getModel();
		for (int i = 0; i < model.size(); i++) {
			if (model.getElementAt(i).equals(randomQuestion)) {
				quizPanelRight.getThemaFragenPanel().getFragenList().setSelectedIndex(i);
				quizPanelLeft.setFrage(randomQuestion);
				break;
			}
		}
		quizPanelLeft.setFrage(randomQuestion);
		quizPanelLeft.getMessageField().setText("");
	}

	// Getter für die Subpanels
	public QuizPanelLeft getQuizPanelLeft() {
		return quizPanelLeft;
	}

	public QuizPanelRight getQuizPanelRight() {
		return quizPanelRight;
	}

	public QuizPanelBottom getQuizButtonPanel() {
		return quizButtonPanel;
	}
}
