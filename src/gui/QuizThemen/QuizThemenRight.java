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
 * Themen an und ermöglicht deren Auswahl. Das ausgewählte Thema wird an das
 * linke Panel weitergegeben.
 */
public class QuizThemenRight extends JPanel {

	private static final long serialVersionUID = 1L;

	private QuizThemenLeft quizThemenLeft;
	private ThemaPanel themaPanel;

	/**
	 * Konstruktor, der eine Liste von Themen für das Panel erwartet.
	 * 
	 * @param themen Sammlung von anzuzeigenden Themen.
	 */
	public QuizThemenRight(Collection<Thema> themen) {
		super();
		initPanelLayout();
		initThemaPanel(themen);
	}

	/** Initialisiert das Layout des Panels. */
	private void initPanelLayout() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setMaximumSize(new Dimension(500, 500));
		setPreferredSize(new Dimension(350, 500));
	}

	/**
	 * Initialisiert das ThemaPanel, fügt Listener zur Weitergabe der Auswahl ans
	 * linke Panel hinzu.
	 */
	private void initThemaPanel(Collection<Thema> themen) {
		themaPanel = new ThemaPanel(themen);
		add(themaPanel);
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
	 * Verknüpft das linke Panel, um die Anzeige beim Themawechsel zu aktualisieren.
	 * 
	 * @param quizThemenLeft das linke Themen-Panel.
	 */
	public void setPanelLeft(QuizThemenLeft quizThemenLeft) {
		this.quizThemenLeft = quizThemenLeft;
	}

	/**
	 * Aktualisiert die angezeigten Themen im ThemaPanel.
	 * 
	 * @param themen neue Sammlung von Themen.
	 */
	public void setThemen(Collection<Thema> themen) {
		themaPanel.setThemen(themen);
	}

	/**
	 * Getter für das ThemaPanel (enthält die Liste).
	 * 
	 * @return ThemaPanel
	 */
	public ThemaPanel getThemaPanel() {
		return themaPanel;
	}
}
