package gui.QuizThemen;

import java.awt.Dimension;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import gui.Panels.ThemaPanel;
import quizLogic.Thema;

/**
 * Panel für die rechte Seite der Quiz-Themen-Ansicht. Zeigt eine Liste von
 * Themen an und ermöglicht die Auswahl eines Themas. Das ausgewählte Thema wird
 * an das linke Panel (QuizThemenLeft) weitergegeben.
 */
public class QuizThemenRight extends JPanel {
	private static final long serialVersionUID = 1L;
	private QuizThemenLeft quizThemenLeft;
	private ThemaPanel themaPanel;

	/**
	 * Konstruktor, der eine Liste von Themen erwartet.
	 * 
	 * @param themen Sammlung von Themen, die angezeigt werden sollen
	 */
	
	public QuizThemenRight(Collection<Thema> themen) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setMaximumSize(new Dimension(500, 500));
		setPreferredSize(new Dimension(350, 500));

		themaPanel = new ThemaPanel(themen);
		add(themaPanel);

		// Listener für die Themenliste, um das ausgewählte Thema zu aktualisieren
		themaPanel.getThemenList().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				Thema selected = themaPanel.getThemenList().getSelectedValue();
				if (quizThemenLeft != null) {
					quizThemenLeft.setThema(selected);
				}
			}
		});
	}

	/**
	 * Setzt das linke Panel, um das ausgewählte Thema anzuzeigen.
	 * 
	 * @param quizThemenLeft das linke Panel, das die Themen anzeigt
	 */

	public void setPanelLeft(QuizThemenLeft quizThemenLeft) {
		this.quizThemenLeft = quizThemenLeft;
	}

	/**
	 * Ermöglicht das Setzen der Themen für das ThemaPanel.
	 * 
	 * @param themen Sammlung von Themen, die im ThemaPanel angezeigt werden sollen
	 */

	public void setThemen(Collection<Thema> themen) {
		themaPanel.setThemen(themen);
	}

	// Getter für das ThemaPanel, um darauf zuzugreifen
	public ThemaPanel getThemaPanel() {
		return themaPanel;
	}
}
