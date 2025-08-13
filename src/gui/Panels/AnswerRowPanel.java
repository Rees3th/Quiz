package gui.Panels;

import javax.swing.BoxLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * AnswerRowPanel ist eine Klasse, die ein Panel für eine Antwortzeile in einem
 * Quiz darstellt. Es enthält ein Label, ein Textfeld für die Antwort und eine
 * Checkbox, um anzugeben, ob die Antwort korrekt ist. Die Klasse erweitert
 * JPanel und verwendet BoxLayout für die Anordnung der Komponenten.
 */
public class AnswerRowPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Konstruktor der Klasse AnswerRowPanel.
	 * 
	 * @param idx   Der Index der Antwort, der im Label angezeigt wird.
	 * @param field Das Textfeld für die Eingabe der Antwort.
	 * @param box   Die Checkbox, um anzugeben, ob die Antwort korrekt ist.
	 */

	public AnswerRowPanel(int idx, JTextField field, JCheckBox box) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		JLabel label = new JLabel("Antwort " + idx + ":");
		label.setPreferredSize(new java.awt.Dimension(80, 36));
		add(label);
		field.setPreferredSize(new java.awt.Dimension(400, 25));
		add(field);
		add(javax.swing.Box.createHorizontalStrut(30));
		add(box);
	}
}
