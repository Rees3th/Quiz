package gui.Panels;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * {@code ButtonPanel} is a reusable horizontal panel containing three buttons.
 * <p>
 * It is typically used in the quiz application to arrange related action
 * buttons in a single row with fixed and flexible spacing between them.
 * </p>
 *
 * <p>
 * Layout details:
 * <ul>
 * <li>Uses a horizontal {@link BoxLayout}</li>
 * <li>Applies padding around the panel via an empty border</li>
 * <li>Places the first and second button with a fixed 150-pixel space in
 * between</li>
 * <li>Places the second and third button with flexible glue space in
 * between</li>
 * </ul>
 * </p>
 * 
 * <p>
 * This ensures a consistent button arrangement across different parts of the
 * application, such as control bars for themes, questions, or quiz gameplay.
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public class ButtonPanel extends JPanel {

	/** Serial version UID for serialization compatibility. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a {@code ButtonPanel} with three horizontally arranged buttons.
	 *
	 * @param b1 the first button to add (left-most)
	 * @param b2 the second button to add (center)
	 * @param b3 the third button to add (right-most)
	 */
	public ButtonPanel(JButton b1, JButton b2, JButton b3) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 15));

		add(b1);
		add(Box.createHorizontalStrut(150)); // fixed space between first and second button
		add(b2);
		add(Box.createHorizontalGlue()); // flexible space before third button
		add(b3);
	}
}
