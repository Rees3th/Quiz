package gui.Panels;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Diese Klasse stellt ein Panel dar, das eine Nachricht anzeigt. Es wird in der
 * Quiz-Anwendung verwendet, um dem Benutzer Anweisungen oder Informationen zu
 * geben.
 */
public class MessagePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Konstruktor der Klasse MessagePanel. Er erstellt ein JPanel mit einem
	 * JTextField, das nicht bearbeitbar ist.
	 * 
	 * @param field Das JTextField, das die Nachricht anzeigen soll.
	 */

	public MessagePanel(JTextField field) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		field.setEditable(false);
		field.setPreferredSize(new Dimension(350, 28));
		add(field);
	}

	/**
	 * Konstruktor der Klasse MessagePanel. Er erstellt ein JPanel mit einem JLabel,
	 * das eine Nachricht anzeigt.
	 * 
	 * @param label Das JLabel, das die Nachricht anzeigen soll.
	 */

	public JLabel getTextField() {
		JLabel label = new JLabel();
		label.setAlignmentX(LEFT_ALIGNMENT);
		label.setText("Bitte Thema auswählen");
		return label;
	}
}
