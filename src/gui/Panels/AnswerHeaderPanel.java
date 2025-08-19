package gui.Panels;

import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * {@code AnswerHeaderPanel} is a small header row panel used in quiz question
 * views to label the answer section.
 * <p>
 * It contains two labels:
 * <ul>
 * <li><b>"Possible Answer"</b> – indicating the possible answer
 * options</li>
 * <li><b>"Correct"</b> – indicating the column where the correct answer
 * checkbox will appear</li>
 * </ul>
 * </p>
 *
 * <p>
 * The panel uses a horizontal {@link BoxLayout} to align the two labels with a
 * flexible space between them, so they fit well above the answer rows.
 * </p>
 *
 * <p>
 * Typical usage:
 * <ul>
 * <li>Placed at the top of a series of answer input rows</li>
 * <li>Gives context to both the answer text fields and their correctness
 * checkboxes</li>
 * </ul>
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public class AnswerHeaderPanel extends JPanel {

	/** Serial version UID for serialization compatibility. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an {@code AnswerHeaderPanel} with two column labels:
	 * <ul>
	 * <li>"Mögliche Antwortwahl"</li>
	 * <li>"Correct"</li>
	 * </ul>
	 */
	public AnswerHeaderPanel() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		// Label for the answer text column
		JLabel label = new JLabel("Possible Answer");
		label.setPreferredSize(new Dimension(170, 16));
		add(label);

		// Spacer between columns
		add(Box.createHorizontalGlue());

		// Label for the correct-answer checkbox column
		JLabel label2 = new JLabel("Correct");
		label2.setPreferredSize(new Dimension(50, 16));
		add(label2);
	}
}
