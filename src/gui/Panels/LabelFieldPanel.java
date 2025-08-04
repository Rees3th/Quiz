package gui.Panels;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Diese Klasse stellt ein Panel mit einem Label und einem Textfeld dar. Es wird
 * verwendet, um eine klare Beschriftung für Eingabefelder zu bieten.
 */
public class LabelFieldPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Konstruktor, der ein Label und ein Textfeld erstellt.
	 * 
	 * @param label Der Text, der im Label angezeigt wird.
	 */

	public LabelFieldPanel(String Label) {

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		JLabel lbl = new JLabel(Label);
		lbl.setPreferredSize(new Dimension(80, 16));
		add(lbl);
	}

	/**
	 * Konstruktor, der ein Label und ein Textfeld erstellt.
	 * 
	 * @param label Der Text, der im Label angezeigt wird.
	 * @param field Das Textfeld, das neben dem Label angezeigt wird.
	 */

	public LabelFieldPanel(String label, JTextField field) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		JLabel lbl = new JLabel(label);
		lbl.setPreferredSize(new Dimension(80, 16));
		add(lbl);
		add(field);
	}
}
