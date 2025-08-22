package gui.Panels;

import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * {@code AnswerRowPanel} represents a single answer row in a quiz question UI.
 * <p>
 * Each row contains:
 * <ul>
 * <li>A label showing the answer number/index</li>
 * <li>A text field for entering or displaying the answer text</li>
 * <li>A checkbox to indicate whether the answer is correct</li>
 * </ul>
 * </p>
 *
 * <p>
 * This panel uses a horizontal {@link BoxLayout} to arrange the label, text
 * field, and checkbox from left to right. It is typically used in conjunction
 * with {@link AnswerHeaderPanel} to build a complete answer section in quiz
 * question forms or displays.
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public class AnswerRowPanel extends JPanel {

	/** Serial version UID for serialization compatibility. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an {@code AnswerRowPanel} with the given index, text field, and
	 * checkbox.
	 *
	 * @param idx   the answer index (1-based) shown in the label
	 * @param field the {@link JTextField} for entering or displaying the answer
	 *              text
	 * @param box   the {@link JCheckBox} for marking the answer as correct
	 */
	public AnswerRowPanel(int idx, JTextField field, JCheckBox box) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		// Label for the answer number
		JLabel label = new JLabel(idx + ":");
		label.setPreferredSize(new Dimension(80, 36));
		add(label);

		// Answer text field
		field.setPreferredSize(new Dimension(400, 25));
		add(field);

		// Spacer before the checkbox
		add(Box.createHorizontalStrut(30));

		// Correctness checkbox
		add(box);
	}
}
