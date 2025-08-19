package gui.Panels;

import java.awt.Dimension;
import javax.swing.*;

/**
 * {@code QuizPanelLeftLayout} is a layout builder utility for the left panel of
 * the quiz gameplay view.
 * <p>
 * It structures the panel to display:
 * <ul>
 * <li>Theme and question title fields</li>
 * <li>Question text area</li>
 * <li>Answer section with a header and multiple answer rows</li>
 * <li>A message/status field at the bottom</li>
 * </ul>
 * </p>
 *
 * <p>
 * This class follows the same approach as other *Layout* builder classes in the
 * application: it is purely responsible for adding and arranging components,
 * leaving all event handling and data binding to the parent panel logic.
 * </p>
 *
 * <p>
 * Layout details:
 * <ul>
 * <li>Uses a vertical {@link BoxLayout} to stack elements</li>
 * <li>Includes fixed spacing between groups of components via
 * {@link Box#createVerticalStrut(int)}</li>
 * <li>Applies padding and fixed preferred/max sizes for consistent
 * dimensioning</li>
 * </ul>
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public class QuizPanelLeftLayout {

	/**
	 * Builds the left quiz panel by adding and arranging all necessary components.
	 *
	 * @param panel        the parent {@link JPanel} to which UI elements will be
	 *                     added
	 * @param themaField   the text field for displaying the theme title
	 * @param titelField   the text field for displaying the question title
	 * @param frageArea    the text area for the question text
	 * @param answerFields an array of text fields for the possible answers
	 * @param checkboxes   an array of checkboxes for marking answers as correct
	 * @param messageField the text field for displaying feedback or status messages
	 */
	public static void build(JPanel panel, JTextField themaField, JTextField titelField, JTextArea frageArea,
			JTextField[] answerFields, JCheckBox[] checkboxes, JTextField messageField) {

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.setMaximumSize(new Dimension(500, 500));
		panel.setPreferredSize(new Dimension(450, 500));

		// --- Theme & Title ---
		panel.add(new LabelFieldPanel("Thema:", themaField));
		panel.add(Box.createVerticalStrut(10));
		panel.add(new LabelFieldPanel("Titel:", titelField));
		panel.add(Box.createVerticalStrut(10));

		// --- Question ---
		panel.add(new questionPanel(frageArea));
		panel.add(Box.createVerticalStrut(15));

		// --- Answer header ---
		panel.add(new AnswerHeaderPanel());
		panel.add(Box.createVerticalStrut(8));

		// --- Answer rows ---
		for (int i = 0; i < answerFields.length; i++) {
			panel.add(new AnswerRowPanel(i + 1, answerFields[i], checkboxes[i]));
			panel.add(Box.createVerticalStrut(8));
		}
		panel.add(Box.createVerticalStrut(15));

		// --- Message field ---
		panel.add(new MessagePanel());
		panel.add(Box.createVerticalStrut(15));
	}
}
