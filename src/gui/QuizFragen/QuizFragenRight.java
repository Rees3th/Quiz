package gui.QuizFragen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;

import gui.Panels.ThemaFragenPanel;
import quizLogic.FakeDataDeliver;
import quizLogic.Question;
import quizLogic.Thema;

/**
 * Das rechte Panel im Quiz, das die Themen und Fragen anzeigt.
 * Es ermöglicht die Auswahl eines Themas und zeigt die zugehörigen Fragen an.
 */
public class QuizFragenRight extends JPanel {

    private static final long serialVersionUID = 1L;

    private ThemaFragenPanel themaFragenPanel;
    private FakeDataDeliver fdd;
    private QuizFragenLeft quizFragenLeft;

    /**
     * Konstruktor für das QuizFragenRight Panel.
     * 
     * @param fdd Die FakeDataDeliver Instanz, die die Themen und Fragen liefert.
     */
    public QuizFragenRight(FakeDataDeliver fdd) {
        this.fdd = fdd;
        initPanel();
        initThemaFragenPanel();
        setupEvents();
        initializeSelection();
    }

    /**
     * Initialisiert das Hauptpanel mit Layout und Rand.
     */
    private void initPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    /**
     * Initialisiert das ThemaFragenPanel mit Themen und fügt es dem Panel hinzu.
     */
    private void initThemaFragenPanel() {
        List<Thema> alleThemenListe = getFilteredThemen();

        themaFragenPanel = new ThemaFragenPanel(alleThemenListe);
        add(themaFragenPanel);
        add(Box.createVerticalStrut(15));

        // "Alle Themen" zum Ende der Liste hinzufügen
        alleThemenListe.add(ThemaFragenPanel.ALLE_THEMEN);
    }

    /**
     * Filtert die Themenliste, sodass "Alle Themen" nicht doppelt vorkommt.
     * 
     * @return gefilterte Themenliste ohne "Alle Themen"
     */
    private List<Thema> getFilteredThemen() {
        List<Thema> alleThemenListe = new ArrayList<>();
        for (Thema t : fdd.getAllThemen()) {
            if (!"Alle Themen".equals(t.toString())) {
                alleThemenListe.add(t);
            }
        }
        return alleThemenListe;
    }

    /**
     * Setzt die Anfangsauswahl der ComboBox und der Fragenliste.
     */
    private void initializeSelection() {
        if (themaFragenPanel.getThemaComboBox().getItemCount() > 0) {
            themaFragenPanel.getThemaComboBox().setSelectedIndex(0);
        }
    }

    /**
     * Initialisiert die Ereignisse für das ThemaFragenPanel.
     * Aktualisiert die Fragenliste, wenn ein Thema ausgewählt wird.
     * Setzt die ausgewählte Frage im QuizFragenLeft Panel, wenn eine Frage ausgewählt wird.
     */
    private void setupEvents() {
        themaFragenPanel.getThemaComboBox().addActionListener(e -> {
            Thema selected = (Thema) themaFragenPanel.getThemaComboBox().getSelectedItem();
            updateFragenList(selected);
            if (quizFragenLeft != null && selected != null) {
                quizFragenLeft.setThema(selected);
            }
        });

        themaFragenPanel.getFragenList().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Question selectedQuestion = themaFragenPanel.getFragenList().getSelectedValue();
                if (quizFragenLeft != null && selectedQuestion != null) {
                    quizFragenLeft.setFrage(selectedQuestion);
                }
            }
        });
    }

    /**
     * Aktualisiert die Liste der Fragen im ThemaFragenPanel basierend auf dem ausgewählten Thema.
     * 
     * @param selectedThema Das ausgewählte Thema, dessen Fragen angezeigt werden sollen.
     */
    private void updateFragenList(Thema selectedThema) {
        DefaultListModel<Question> model = (DefaultListModel<Question>) themaFragenPanel.getFragenList().getModel();
        model.clear();

        if (selectedThema == ThemaFragenPanel.ALLE_THEMEN) {
            for (Thema thema : fdd.getAllThemen()) {
                if (thema.getAllQuestions() != null) {
                    for (Question q : thema.getAllQuestions()) {
                        model.addElement(q);
                    }
                }
            }
        } else if (selectedThema != null && selectedThema.getAllQuestions() != null) {
            for (Question q : selectedThema.getAllQuestions()) {
                model.addElement(q);
            }
        }
    }

    /**
     * Lädt die Themen neu und aktualisiert die ComboBox im ThemaFragenPanel.
     * 
     * @param neueThemen Die neuen Themen, die geladen werden sollen.
     */
    public void reloadThemen(Collection<Thema> neueThemen) {
        themaFragenPanel.setThemen(neueThemen);
        themaFragenPanel.getThemaComboBox().setSelectedIndex(0);
    }

    /**
     * Setter für das verknüpfte QuizFragenLeft Panel.
     * 
     * @param quizFragenLeft Das linke Quiz-Fragen Panel.
     */
    public void setPanelLeft(QuizFragenLeft quizFragenLeft) {
        this.quizFragenLeft = quizFragenLeft;
    }

    /**
     * Getter für das ThemaFragenPanel.
     * 
     * @return das ThemaFragenPanel
     */
    public ThemaFragenPanel getThemaFragenPanel() {
        return themaFragenPanel;
    }
}
