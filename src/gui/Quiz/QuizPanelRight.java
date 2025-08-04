package gui.Quiz;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultListCellRenderer;

import gui.Panels.ThemaFragenPanel;
import quizLogic.Answer;
import quizLogic.FakeDataDeliver;
import quizLogic.Question;
import quizLogic.Thema;

/**
 * Das rechte Panel im Quiz-Bereich, das die Themen und Fragen anzeigt. Es
 * enthält eine Liste von Fragen, die nach Thema gefiltert werden können. Die
 * Antworten der aktuell ausgewählten Frage werden angezeigt, wenn der "Antwort
 * zeigen"-Button gedrückt wird.
 */
public class QuizPanelRight extends JPanel {

	private static final long serialVersionUID = 1L;

	private ThemaFragenPanel themaFragenPanel;
	private FakeDataDeliver fdd;
	private QuizPanelLeft quizPanelLeft;
	private Integer aktuellGezeigteFrageId = null;

	public QuizPanelRight(FakeDataDeliver fdd) {
		this.fdd = fdd;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new EmptyBorder(10, 10, 10, 10));

		// Themenliste mit "Alle Themen" als erste Option
		List<Thema> alleThemenListe = new ArrayList<>();
		alleThemenListe.add(ThemaFragenPanel.ALLE_THEMEN);
		alleThemenListe.addAll(fdd.getAllThemen());
		themaFragenPanel = new ThemaFragenPanel(alleThemenListe);

		// Setze CellRenderer für die Fragenliste
		themaFragenPanel.getFragenList().setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {

				JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
						cellHasFocus);

				if (value instanceof Question) {
					Question q = (Question) value;
					String text = q.getTitle();
					// TODO: überprüfen
					if (aktuellGezeigteFrageId != null && q.getId() == aktuellGezeigteFrageId) {
						StringBuilder sb = new StringBuilder("<html><b>" + text + "</b><br>");
						List<Answer> answers = new ArrayList<>(q.getAnswers());
						answers.sort(Comparator.comparingInt(Answer::getId));
						int nr = 1;
						for (Answer a : answers) {
							sb.append("&nbsp;&nbsp;").append(nr++).append(". ").append(a.getText());
							if (a.isCorrect()) {
								sb.append(" <b>(richtig)</b>");
							}
							sb.append("<br>");
						}
						sb.append("</html>");
						label.setText(sb.toString());
					} else {
						label.setText(text);
					}
				} else {
					label.setText("");
				}

				label.setVerticalTextPosition(SwingConstants.TOP);
				return label;
			}
		});

		add(themaFragenPanel);
		themaFragenPanel.getThemaInfoButton().setVisible(false);
		themaFragenPanel.getInfoTitelLbl().setVisible(false);
		themaFragenPanel.getFragenLabel().setVisible(false);
		add(Box.createVerticalStrut(15));

		setupEvents();

		themaFragenPanel.getThemaComboBox().setSelectedIndex(0);
		updateFragenList(ThemaFragenPanel.ALLE_THEMEN);
	}

	/**
	 * Initialisiert die Ereignisse für das Thema- und Fragenpanel. Hier werden die
	 * Listener für die ComboBox und die Fragenliste gesetzt.
	 */

	private void setupEvents() {
		themaFragenPanel.getThemaComboBox().addActionListener(e -> {
			Thema selected = (Thema) themaFragenPanel.getThemaComboBox().getSelectedItem();

			updateFragenList(selected);

			if (quizPanelLeft != null && selected != null) {
				quizPanelLeft.setThema(selected);
			}

			// Markierung zurücksetzen, wenn Thema wechselt
			resetGezeigteAntworten();

			// Liste neu zeichnen für korrekten Zustand
			themaFragenPanel.getFragenList().repaint();
		});

		// TODO: überprüfen
		// Listener für die Fragenliste, um die ausgewählte Frage im linken Panel
		// anzuzeigen
		themaFragenPanel.getFragenList().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				Question selectedQuestion = themaFragenPanel.getFragenList().getSelectedValue();
				if (quizPanelLeft != null && selectedQuestion != null) {
					quizPanelLeft.setFrage(selectedQuestion);
				}
			}
		});
	}

	/**
	 * Aktualisiert die Liste der Fragen basierend auf dem ausgewählten Thema. Wenn
	 * "Alle Themen" ausgewählt ist, werden alle Fragen aus allen Themen angezeigt.
	 * 
	 * @param selectedThema Das aktuell ausgewählte Thema
	 */

	private void updateFragenList(Thema selectedThema) {
		DefaultListModel<Question> model = (DefaultListModel<Question>) themaFragenPanel.getFragenList().getModel();
		model.clear();

		if (selectedThema != null && "Alle Themen".equals(selectedThema.toString())) {
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
	 * Lädt alle Themen und Fragen neu und setzt die ComboBox auf "Alle Themen".
	 * Diese Methode wird aufgerufen, wenn die Daten aktualisiert wurden.
	 */

	public void reloadAllThemenUndFragen() {
		List<Thema> alleThemenListe = new ArrayList<>();
		alleThemenListe.add(ThemaFragenPanel.ALLE_THEMEN);
		alleThemenListe.addAll(fdd.getAllThemen());

		// Setze Themen in ThemaFragenPanel
		themaFragenPanel.setThemen(alleThemenListe);

		// ComboBox auf "Alle Themen" setzen
		themaFragenPanel.getThemaComboBox().setSelectedIndex(0);

		// Liste mit Fragen befüllen
		updateFragenList(ThemaFragenPanel.ALLE_THEMEN);

		// Antwortenfreischaltung zurücksetzen
		resetGezeigteAntworten();

		// Liste neu zeichnen
		themaFragenPanel.getFragenList().repaint();
	}

	// Setzt das linke Panel, um die Fragen anzuzeigen
	public void setPanelLeft(QuizPanelLeft panel) {
		this.quizPanelLeft = panel;
	}

	// Getter für das ThemaFragenPanel
	public ThemaFragenPanel getThemaFragenPanel() {
		return themaFragenPanel;
	}

	// Getter für FakeDataDeliver, um auf die Daten zuzugreifen
	public FakeDataDeliver getFdd() {
		return fdd;
	}

	// Markiert eine Frage als beantwortet, um die Antworten anzuzeigen
	public void markAnswered(int questionId) {
		aktuellGezeigteFrageId = questionId;
	}

	// Setzt die aktuell angezeigte Frage zurück, um die Antworten nicht mehr
	// anzuzeigen
	public void resetGezeigteAntworten() {
		aktuellGezeigteFrageId = null;
	}
}
