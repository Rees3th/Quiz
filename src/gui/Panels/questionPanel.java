package gui.Panels;

import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * {@code FragePanel} is a reusable UI component for displaying a question label
 * and its corresponding text area.
 * <p>
 * It is typically used in quiz-related forms or views to present the question
 * content in a scrollable area alongside a descriptive label.
 * </p>
 *
 * <p>
 * Layout:
 * <ul>
 * <li>Horizontal {@link BoxLayout} placing the label on the left and the
 * scrollable text area on the right</li>
 * <li>Label has a fixed preferred width for alignment with other form
 * fields</li>
 * <li>Text area is placed inside a {@link JScrollPane} so it can display long
 * text</li>
 * </ul>
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public class questionPanel extends JPanel {

	/** Serial version UID for serialization compatibility. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a {@code FragePanel} with a label and a scrollable text area.
	 *
	 * @param textArea the {@link JTextArea} used to display or edit the question
	 *                 text
	 */
	public questionPanel(JTextArea textArea) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		// Label for the question
		JLabel questionLabel = new JLabel("Frage:");
		questionLabel.setPreferredSize(new Dimension(80, 16));
		add(questionLabel);

		// Scroll pane containing the text area
		JScrollPane scroll = new JScrollPane(textArea);
		scroll.setPreferredSize(new Dimension(350, 90));
		add(scroll);
	}
}
