package gui.Panels;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JTabbedPane;

import gui.Quiz.QuizPanel;
import gui.QuizFragen.QuizFragenPanel;
import gui.QuizThemen.QuizThemenPanel;

/**
 * Diese Klasse erstellt ein TabPanel, welches die verschiedenen Panels für die
 * Quiz-Anwendung enthält. Es ermöglicht das Wechseln zwischen den Panels und
 * aktualisiert das QuizPanel bei Bedarf.
 */

public class TabPanel extends JTabbedPane {

	private static final long serialVersionUID = 1L;
	public static final Color BG_COLOR = Color.WHITE;
	private static final Font FONT_TAB = new Font("Helvetica", Font.ITALIC, 16);
	

	/**
	 * Konstruktor für das TabPanel, der das QuizPanel übergibt. Adds einen
	 * ChangeListener hinzu, der bei Tab-Wechseln das QuizPanel aktualisiert.
	 * 
	 * @param quizPanel Das QuizPanel, das im TabPanel verwendet wird.
	 */

	// In TabPanel.java
	public TabPanel(QuizThemenPanel quizThemenPanel, QuizFragenPanel quizFragenPanel, QuizPanel quizPanel) {
	    super(JTabbedPane.TOP);
	    setFont(FONT_TAB);
	    setBackground(BG_COLOR);

	    addChangeListener(e -> {
	        int tab = getSelectedIndex();
	        if (tab == 1) { // Quiz Fragen Tab
	            quizFragenPanel.reloadThemen();
	        } else if (tab == 2) { // Quiz Tab
	            quizPanel.getQuizPanelRight().reloadAllThemenUndFragen();
	        }
	    });
	}

}
