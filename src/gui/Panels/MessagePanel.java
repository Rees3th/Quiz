package gui.Panels;

import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * {@code MessagePanel} is a simple reusable UI component for displaying a
 * single line of text (messages, status updates, or feedback) to the user.
 * <p>
 * It contains a single, always-visible {@link JLabel} which can be updated via
 * {@link #setText(String)}. The text is displayed in a fixed-size label to
 * maintain consistent alignment within parent panels.
 * </p>
 *
 * <p>
 * Typical use cases:
 * <ul>
 * <li>Displaying validation messages in form panels</li>
 * <li>Showing success or error messages in action bars</li>
 * <li>Acting as a status label in control panels</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Layout:
 * <ul>
 * <li>Horizontal {@link BoxLayout} to align neatly with other controls</li>
 * <li>Fixed preferred size to maintain consistent UI appearance</li>
 * </ul>
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public class MessagePanel extends JPanel {

	/** Serial version UID for serialization compatibility. */
	private static final long serialVersionUID = 1L;

	/** Always-visible label used for displaying messages. */
	private final JLabel messageLabel;

	/**
	 * Constructs a {@code MessagePanel} with an empty message label.
	 * <p>
	 * The label is horizontally aligned and given a fixed preferred size for
	 * predictable layout within parent containers.
	 * </p>
	 */
	public MessagePanel() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		messageLabel = new JLabel();
		messageLabel.setPreferredSize(new Dimension(350, 28));
		add(messageLabel);
	}

	/**
	 * Updates the message text displayed in the label.
	 * <p>
	 * If {@code string} is {@code null}, the label will be set to an empty string.
	 * </p>
	 *
	 * @param string the message text to display; may be {@code null}
	 */
	public void setText(String string) {
		messageLabel.setText(string != null ? string : "");
	}

	/**
	 * Returns the underlying {@link JLabel} used to display the message.
	 * <p>
	 * This can be used for customization such as changing text color or font.
	 * </p>
	 *
	 * @return the {@link JLabel} displaying the message
	 */
	public JLabel getMessageLabel() {
		return messageLabel;
	}
}
