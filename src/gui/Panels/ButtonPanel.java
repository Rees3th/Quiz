package gui.Panels;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Diese Klasse stellt ein Panel mit drei Buttons dar. Es wird in der QuizApp
 * verwendet, um die Buttons anzuzeigen.
 */

public class ButtonPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Konstruktor der Klasse ButtonPanel.
	 * 
	 * @param b1 Erster Button
	 * @param b2 Zweiter Button
	 * @param b3 Dritter Button
	 */

	public ButtonPanel(JButton b1, JButton b2, JButton b3) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 15));

		add(b1);
		add(Box.createHorizontalStrut(150));
		add(b2);
		add(Box.createHorizontalGlue());
		add(b3);

	}

}
