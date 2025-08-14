package gui.Panels;

import java.awt.Dimension;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import quizLogic.Theme;

/**
 * {@code ThemaPanel} is a reusable UI panel that displays a list of quiz
 * themes.
 * <p>
 * It provides a scrollable {@link JList} for viewing and selecting available
 * {@link Theme} objects. The panel is intended to be embedded in larger views
 * such as theme management or quiz gameplay panels.
 * </p>
 *
 * <p>
 * <b>Main responsibilities:</b>
 * </p>
 * <ul>
 * <li>Display a themed label and a scrollable list of themes</li>
 * <li>Allow the theme list to be dynamically updated via
 * {@link #setThemen(Collection)}</li>
 * <li>Provide access to the underlying {@link JList} for event handling</li>
 * </ul>
 * 
 * <p>
 * The list contents are backed by a {@link DefaultListModel}, making it easy to
 * update or clear the displayed themes.
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public class ThemaPanel extends JPanel {

	/** Serial version UID for serialization compatibility. */
	private static final long serialVersionUID = 1L;

	/** The JList displaying the current list of themes. */
	private JList<Theme> themenList;

	/** The list model holding the theme data. */
	private DefaultListModel<Theme> themenModel;

	/**
	 * Creates a new {@code ThemaPanel} with an initially populated list of themes.
	 * <p>
	 * The layout places a label at the top and the scrollable theme list below it.
	 * </p>
	 *
	 * @param themen the initial collection of themes to display; may be
	 *               {@code null} or empty
	 */
	public ThemaPanel(Collection<Theme> themen) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// Label for the theme list
		JLabel lbl = new JLabel("Themes");
		lbl.setPreferredSize(new Dimension(80, 16));
		lbl.setAlignmentX(LEFT_ALIGNMENT);
		add(lbl);
		add(Box.createVerticalStrut(40));

		// Initialize the list and model
		themenModel = new DefaultListModel<>();
		themenList = new JList<>(themenModel);
		themenList.setVisibleRowCount(10);

		// Add the list inside a scroll pane
		JScrollPane scroll = new JScrollPane(themenList);
		scroll.setPreferredSize(new Dimension(220, 170));
		scroll.setAlignmentX(LEFT_ALIGNMENT);
		add(scroll);

		// Populate with initial themes, if any
		setThemen(themen);
	}

	/**
	 * Updates the displayed list of themes.
	 * <p>
	 * Clears any previously shown themes before adding the new collection.
	 * </p>
	 *
	 * @param themen a collection of {@link Theme} objects to display; if
	 *               {@code null}, the list is cleared
	 */
	public void setThemen(Collection<Theme> themen) {
		themenModel.clear();
		if (themen != null) {
			for (Theme t : themen) {
				themenModel.addElement(t);
			}
		}
	}

	/**
	 * Returns the JList that displays the themes.
	 * <p>
	 * This allows external components to register listeners or customize selection
	 * behavior.
	 * </p>
	 *
	 * @return the {@link JList} of {@link Theme} objects
	 */
	public JList<Theme> getThemenList() {
		return themenList;
	}
}
