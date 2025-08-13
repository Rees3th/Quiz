package gui.Quiz;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;

import persistence.serialization.QuizDataManager;
import quizLogic.Answer;
import quizLogic.Question;
import quizLogic.Thema;

/**
 * Das Haupt-Panel für das Quiz-Interface.
 * 
 * Diese Klasse verwaltet die drei Haupt-Panels: links (Frage/Antwort), rechts (Themen/Fragen-Liste) 
 * und unten (Steuerung mit Buttons).
 * 
 * Sie steuert die Steuerungslogik des Quiz:
 * - Laden und Auswählen von Fragen (einschließlich zufälliger Auswahl neuer Fragen)
 * - Anzeige der richtigen Antwort
 * - Sperren der Benutzereingaben nach Speicherung
 * - Kommunikation mit dem QuizDataManager für Daten im Hintergrund
 * 
 * Implementiert das Interface {@link QuizDelegate} für die Button-Callbacks.
 */
public class QuizPanel extends JPanel implements QuizDelegate {

    private static final long serialVersionUID = 1L;

    /** Linkes Panel mit Frage- und Antwort-Eingaben */
    private QuizPanelLeft quizPanelLeft;

    /** Rechtes Panel mit Themen- und Fragenauswahl */
    private QuizPanelRight quizPanelRight;

    /** Unteres Panel mit Steuerungsbuttons ("Nächste Frage", "Antwort zeigen", "Antwort speichern") */
    private QuizPanelBottom quizButtonPanel;

    /** Datenmanager zur Verwaltung der Themen und Fragen */
    private QuizDataManager dm;

    /** Zufallsgenerator für die Zufallsauswahl der Fragen */
    private final Random random = new Random();

    /**
     * Konstruktor.
     * 
     * Initialisiert die drei Sub-Panels, richtet das Layout ein und startet mit der ersten zufälligen Frage.
     * 
     * @param dm Instanz des {@link QuizDataManager} für das Datenmanagement
     */
    public QuizPanel(QuizDataManager dm) {
        super();
        this.dm = dm;

        // Layout mit Abstand zwischen Ländern
        setLayout(new BorderLayout(10, 10));

        // Panels erzeugen
        quizPanelLeft = new QuizPanelLeft(dm);
        quizPanelRight = new QuizPanelRight(dm);
        quizButtonPanel = new QuizPanelBottom();

        // Verknüpfungen: QuizPanelRight weiß von QuizPanelLeft
        quizPanelRight.setPanelLeft(quizPanelLeft);

        // Buttons-Panel bekommt Delegat für Callback-Methoden (onShowAnswer(), onSaveAnswer() etc.)
        quizButtonPanel.setDelegate(this);

        // Panels ins Layout setzen
        add(quizPanelLeft, BorderLayout.WEST);
        add(quizPanelRight, BorderLayout.EAST);
        add(quizButtonPanel, BorderLayout.SOUTH);

        // Startfrage laden - ruft onNewQuestion() auf

    }

    /**
     * Callback-Methode bei Klick auf "Antwort zeigen".
     * 
     * Ermittelt die aktuell ausgewählte Frage und zeigt die korrekte Antwort zentral im Fragenpanel an.
     * Die Fragen-Liste wird dabei ausgeblendet (über CardLayout im ThemaFragenPanel).
     * Nachrichtenfelder links werden geleert.
     * 
     * Wird keine Frage ausgewählt, wird eine passende Fehlermeldung angezeigt.
     */
    @Override
    public void onShowAnswer() {
        // Ausgewählte Frage aus der Fragenliste rechts abfragen
        Question currentQuestion = quizPanelRight.getThemaFragenPanel().getFragenList().getSelectedValue();
        if (currentQuestion == null) {
            // Keine Frage ausgewählt → Fehlermeldung links
            quizPanelLeft.getMessageField().setText("Keine Frage ausgewählt.");
            return;
        }

        // Antwortenliste der aktuellen Frage holen
        List<Answer> answers = currentQuestion.getAnswers();

        // Indizes der richtigen Antwort ermitteln (erster Treffer)
        int correctIndex = -1;
        for (int i = 0; i < answers.size(); i++) {
            if (answers.get(i).isCorrect()) {
                correctIndex = i + 1; // Nummerierung ab 1 für Anzeige
                break;
            }
        }

        // Wenn richtige Antwort gefunden wurde, Feedback anzeigen
        if (correctIndex != -1) {
            String msg = "<html><center><b>Die richtige Antwort ist:<br>" 
                + correctIndex  + "</b></center></html>";

            // Rückmeldung im Fragenpanel anzeigen (Fragenliste wird ausgeblendet)
            quizPanelRight.getThemaFragenPanel().showFeedbackMessage(msg);
        }

        // Linkes Nachrichtenfeld leeren, da der Hinweis nun mittig im Panel steht
        quizPanelLeft.getMessageField().setText("");

        // Frage als beantwortet markieren (optional, z.B. für UI-Status)
        quizPanelRight.markAnswered(currentQuestion.getId());
    }

    /**
     * Callback-Methode bei Klick auf "Antwort speichern".
     * 
     * Sperrt danach alle Checkboxen im linken Panel, damit die Antwort nicht mehr verändert wird.
     * Zeigt optional eine kurze Bestätigungsmeldung an.
     */
    @Override
    public void onSaveAnswer() {
        // Checkboxen sperren
        quizPanelLeft.setCheckboxesEnabled(false);
        // Kleine Info im linken Nachrichtenfeld
        quizPanelLeft.getMessageField().setText("Antwort gespeichert!");
    }

    /**
     * Callback-Methode bei Klick auf "Nächste Frage".
     * 
     * Wählt zufällig eine neue Frage aus dem aktuell ausgewählten Thema oder (wenn "Alle Themen" ausgewählt ist) aus allen Fragen.
     * Stellt diese Frage sowohl im rechten Fragenpanel als auch im linken Eingabepanel dar.
     * 
     * Die Feedback-Anzeige wird zurückgesetzt (Fragenliste wird wieder sichtbar).
     * Nachrichtenfelder werden geleert.
     * Checkboxen werden wieder aktiviert, damit der Nutzer neue Antworten eingeben kann.
     */
    @Override
    public void onNewQuestion() {
        // Feedback deaktivieren, Fragenliste wieder anzeigen
        quizPanelRight.getThemaFragenPanel().showFragenList();

        // Liste aller verfügbaren Fragen (je nach Thema Auswahl) sammeln
        List<Question> alleFragen = new ArrayList<>();

        Thema selectedThema = (Thema) quizPanelRight.getThemaFragenPanel().getThemaComboBox().getSelectedItem();

        // Filter nach Thema
        if (selectedThema != null && !"Alle Themen".equals(selectedThema.toString())) {
            if (selectedThema.getAllQuestions() != null && !selectedThema.getAllQuestions().isEmpty()) {
                alleFragen.addAll(selectedThema.getAllQuestions());
            }
        } else {
            // Wenn "Alle Themen" ausgewählt → alle Fragen aller Themen nehmen
            for (Thema t : dm.getAllThemen()) {
                if (t.getAllQuestions() != null) {
                    alleFragen.addAll(t.getAllQuestions());
                }
            }
        }

        // Keine Fragen gefunden
        if (alleFragen.isEmpty()) {
            quizPanelLeft.getMessageField().setText("Keine Fragen vorhanden.");
            return;
        }

        // Zufallsfrage auswählen
        int idx = random.nextInt(alleFragen.size());
        Question randomQuestion = alleFragen.get(idx);

        // Frage als Auswahl im rechten Panel setzen (wird angezeigt)
        quizPanelRight.getThemaFragenPanel().getFragenList().setSelectedValue(randomQuestion, true);

        // Frage im linken Panel (Frage + Antworten) anzeigen
        quizPanelLeft.setFrage(randomQuestion);

        // Linkes Nachrichtenfeld leeren
        quizPanelLeft.getMessageField().setText("");

        // Checkboxen wieder aktivieren, damit Nutzer neu tippen kann
        quizPanelLeft.setCheckboxesEnabled(true);

        // Gezeigte Antworten evtl. zurücksetzen (je nach Implementierung)
        quizPanelRight.resetGezeigteAntworten();
    }

	public  QuizPanelRight getQuizPanelRight() {
		return quizPanelRight;
	}
}
