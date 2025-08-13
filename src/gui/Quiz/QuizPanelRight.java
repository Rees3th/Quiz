package gui.Quiz;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import gui.Panels.ThemaFragenPanel;
import persistence.serialization.QuizDataManager;
import quizLogic.Answer;
import quizLogic.Question;
import quizLogic.Thema;

/**
 * Rechtes Panel im Quizbereich, zeigt Themen und Fragen als Listen. Verwaltung
 * und Anzeige der Fragen erfolgt über ThemaFragenPanel.
 */
public class QuizPanelRight extends JPanel {
	private static final long serialVersionUID = 1L;

	private ThemaFragenPanel themaFragenPanel;
	private QuizDataManager dm;
	private QuizPanelLeft quizPanelLeft;
	private Integer aktuellGezeigteFrageId = null;
	
	private Integer feedbackFrageId = null;
	private String feedbackMsg = null;

	/**
	 * Konstruktor, baut das Panel mit Themenselektion und Fragenliste auf.
	 */
	public QuizPanelRight(QuizDataManager dm) {
		this.dm = dm;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new EmptyBorder(10, 10, 10, 10));

		List<Thema> themenListe = new ArrayList<>(dm.getAllThemen());
		themaFragenPanel = new ThemaFragenPanel(themenListe);

		// Renderer: Zeigt die richtige Antwort nach Klick auf "Antwort zeigen"
		themaFragenPanel.getFragenList().setCellRenderer(new DefaultListCellRenderer() {
		    @Override
		    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
		            boolean cellHasFocus) {
		        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		        if (value instanceof Question) {
		            Question q = (Question) value;
		            String text = q.getTitle();
		            if (feedbackFrageId != null && q.getId() == feedbackFrageId && feedbackMsg != null) {
		                StringBuilder sb = new StringBuilder("<html>");
		                sb.append("<b>").append(text).append("</b><br>");
		                List<Answer> answers = new ArrayList<>(q.getAnswers());
		                int nr = 1;
		                for (Answer a : answers) {
		                    sb.append(nr++).append(". ").append(a.getText());
		                    if (a.isCorrect()) sb.append(" (richtig)");
		                    sb.append("<br>");
		                }
		                sb.append("<b><span style='color:").append(feedbackMsg.contains("Richtig") ? "green" : "red").append(";'>")
		                  .append(feedbackMsg).append("</span></b></html>");
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

	/** Setzt Eventlistener für ComboBox und Fragenliste. */
	private void setupEvents() {
		themaFragenPanel.getThemaComboBox().addActionListener(e -> {
			Thema selected = (Thema) themaFragenPanel.getThemaComboBox().getSelectedItem();
			updateFragenList(selected);
			if (quizPanelLeft != null && selected != null) {
				quizPanelLeft.setThema(selected);
			}
			resetGezeigteAntworten();
			themaFragenPanel.getFragenList().repaint();
		});

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
	 * Aktualisiert die Liste der Fragen im Panel je nach ausgewähltem Thema.
	 */
	private void updateFragenList(Thema selectedThema) {
		DefaultListModel<Question> model = (DefaultListModel<Question>) themaFragenPanel.getFragenList().getModel();
		model.clear();
		if (selectedThema != null && "Alle Themen".equals(selectedThema.toString())) {
			for (Thema thema : dm.getAllThemen()) {
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
	 * Lädt alle Themen und Fragen neu und setzt auf "Alle Themen".
	 */
	public void reloadAllThemenUndFragen() {
	    List<Thema> alleThemenListe = new ArrayList<>(dm.getAllThemen());
	    alleThemenListe.removeIf(t -> t == ThemaFragenPanel.ALLE_THEMEN);
	    themaFragenPanel.setThemen(alleThemenListe);
	    themaFragenPanel.getThemaComboBox().setSelectedIndex(0);
	    updateFragenList(ThemaFragenPanel.ALLE_THEMEN);
	    resetGezeigteAntworten();
	    themaFragenPanel.getFragenList().repaint();
	}

	
	public void setFeedback(int frageId, String msg) {
	    feedbackFrageId = frageId;
	    feedbackMsg = msg;
	    themaFragenPanel.getFragenList().repaint();
	}

	// Verbindung mit dem linken Panel
	public void setPanelLeft(QuizPanelLeft panel) {
		this.quizPanelLeft = panel;
	}

	// Getter für das ThemaFragenPanel
	public ThemaFragenPanel getThemaFragenPanel() {
		return themaFragenPanel;
	}


	// Markiert eine Frage als beantwortet, um die Antworten einzublenden
	public void markAnswered(int questionId) {
		aktuellGezeigteFrageId = questionId;
	}

	// Entfernt eine Markierung, sodass keine Antworten mehr angezeigt werden
	public void resetGezeigteAntworten() {
		aktuellGezeigteFrageId = null;
	}
	public void clearFeedback() {
	    feedbackFrageId = null;
	    feedbackMsg = null;
	}
}
