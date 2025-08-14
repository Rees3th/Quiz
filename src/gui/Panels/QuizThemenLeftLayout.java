package gui.Panels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import gui.QuizThemen.QuizThemeLeft;

/**
 * {@code QuizThemenLeftLayout} is a layout builder utility for the left panel
 * in the quiz theme management view ({@link QuizThemeLeft}).
 * <p>
 * It adds and arranges the following UI elements:
 * <ul>
 * <li>A title header label for creating a new theme</li>
 * <li>A label and text field for the theme title</li>
 * <li>A label and multi-line area for the theme description/information, in a
 * scroll pane</li>
 * </ul>
 * </p>
 * 
 * <p>
 * This class contains only static layout building methods. It does not handle
 * event listeners or data binding; its sole responsibility is to structure the
 * UI.
 * </p>
 * 
 * <p>
 * All components are aligned to the left and use vertical spacing for
 * separation.
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public class QuizThemenLeftLayout {

	/**
	 * Builds and adds the components for the left theme panel layout.
	 *
	 * @param panel      the {@link QuizThemeLeft} panel to build the layout on
	 * @param titelField the text field for entering the theme's title
	 * @param infoArea   the text area for entering the theme's
	 *                   description/information
	 */
	public static void build(QuizThemeLeft panel, JTextField titelField, JTextArea infoArea) {
		// ----- Header label -----
		JLabel lblNeuesThema = new JLabel("Neues Thema");
		lblNeuesThema.setFont(lblNeuesThema.getFont().deriveFont(Font.BOLD, 15f));
		lblNeuesThema.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(lblNeuesThema);
		panel.add(Box.createVerticalStrut(10));

		// ----- Title label and field -----
		JLabel lblTitel = new JLabel("Titel");
		lblTitel.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(lblTitel);
		panel.add(Box.createVerticalStrut(5));
		panel.add(titelField);
		panel.add(Box.createVerticalStrut(12));

		// ----- Info label and scrollable text area -----
		JLabel lblInfo = new JLabel("Information zum Thema");
		lblInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(lblInfo);
		panel.add(Box.createVerticalStrut(5));

		JScrollPane areaScroll = new JScrollPane(infoArea);
		areaScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
		areaScroll.setPreferredSize(new Dimension(330, 300));
		areaScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
		panel.add(areaScroll);
		panel.add(Box.createVerticalStrut(12));
	}
}
