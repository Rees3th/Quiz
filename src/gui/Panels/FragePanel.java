package gui.Panels;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Diese Klasse stellt ein Panel dar, das eine Frage anzeigt. Es enthält ein
 * Label und ein TextArea, in dem die Frage angezeigt wird. Das Panel verwendet
 * ein BoxLayout, um die Komponenten horizontal anzuordnen.
 */
public class FragePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Konstruktor der Klasse FragePanel.
	 * 
	 * @param textArea Das JTextArea, in dem die Frage angezeigt wird.
	 */

	public FragePanel(JTextArea textArea) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		JLabel frageLabel = new JLabel("Frage:");
		frageLabel.setPreferredSize(new Dimension(80, 16));
		add(frageLabel);
		JScrollPane scroll = new JScrollPane(textArea);
		scroll.setPreferredSize(new Dimension(350, 90));
		add(scroll);
	}
}
