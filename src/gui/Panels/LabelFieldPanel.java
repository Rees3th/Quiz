package gui.Panels;

import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * {@code LabelFieldPanel} is a small reusable component that displays a label
 * alongside an optional input field.
 * <p>
 * It is typically used to provide a clear caption for text fields within forms
 * or other data entry panels.
 * </p>
 *
 * <p>
 * The layout used is a horizontal {@link BoxLayout}, placing the label on the
 * left and, if provided, a {@link JTextField} to its right.
 * </p>
 * 
 * <p>
 * The label has a fixed preferred width to keep alignment consistent across
 * multiple instances of this panel.
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public class LabelFieldPanel extends JPanel {

	/** Serial version UID for serialization compatibility. */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a {@code LabelFieldPanel} containing only a label.
	 *
	 * @param label the text to display in the label
	 */
	public LabelFieldPanel(String label) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		JLabel lbl = new JLabel(label);
		lbl.setPreferredSize(new Dimension(80, 16));
		add(lbl);
	}

	/**
	 * Creates a {@code LabelFieldPanel} containing a label and a text field.
	 * <p>
	 * The label is placed to the left of the field, both in a horizontal layout.
	 * </p>
	 *
	 * @param label the text to display in the label
	 * @param field the {@link JTextField} to be placed next to the label
	 */
	public LabelFieldPanel(String label, JTextField field) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		JLabel lbl = new JLabel(label);
		lbl.setPreferredSize(new Dimension(80, 16));
		add(lbl);
		add(field);
	}
}
