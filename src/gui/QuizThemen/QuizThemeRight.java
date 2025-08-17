package gui.QuizThemen;

import java.awt.Dimension;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import gui.Panels.ThemaPanel;
import quizLogic.Theme;

/**
 * {@code QuizThemeRight} is the right-hand panel in the Quiz Theme Management
 * view.
 * <p>
 * It displays a list of available quiz themes and allows the user to select a
 * theme. The selected theme's details are passed to the left panel
 * ({@link QuizThemeLeft}) for editing or display.
 * </p>
 * 
 * <p>
 * This panel uses a {@link ThemaPanel} to handle the theme list rendering and
 * uses a {@code ListSelectionListener} to notify the left panel whenever the
 * selection changes.
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public class QuizThemeRight extends JPanel {

	/** Serial version UID for serialization compatibility. */
	private static final long serialVersionUID = 1L;

	/** Reference to the left panel for updating when the selected theme changes. */
	private QuizThemeLeft quizThemenLeft;

	/** The panel containing the list of quiz themes. */
	private ThemaPanel themaPanel;

	/**
	 * Constructs a new {@code QuizThemeRight} panel.
	 *
	 * @param themen the collection of quiz themes to display in the list.
	 */
	public QuizThemeRight(Collection<Theme> themen) {
		super();
		initPanelLayout();
		initThemaPanel(themen);
	}

	/**
	 * Initializes the panel's layout settings.
	 * <p>
	 * Sets {@link BoxLayout} orientation, border spacing, and preferred/maximum
	 * sizes.
	 * </p>
	 */
	private void initPanelLayout() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setMaximumSize(new Dimension(500, 500));
		setPreferredSize(new Dimension(350, 500));
	}

	/**
	 * Initializes the {@link ThemaPanel} and sets up a listener to update the
	 * linked left panel when the user selects a theme.
	 *
	 * @param themen the collection of quiz themes to display
	 */
	private void initThemaPanel(Collection<Theme> themen) {
		themaPanel = new ThemaPanel(themen);
		add(themaPanel);

		themaPanel.getThemenList().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				Theme selected = themaPanel.getThemenList().getSelectedValue();
				if (quizThemenLeft != null) {
					quizThemenLeft.setThema(selected);
				}
			}
		});
	}

	/**
	 * Links this right-hand panel to the left-hand theme panel.
	 * <p>
	 * This connection allows the left panel to refresh its content when the
	 * selected theme changes.
	 * </p>
	 *
	 * @param quizThemenLeft the left theme panel to associate with this panel
	 */
	public void setPanelLeft(QuizThemeLeft quizThemenLeft) {
		this.quizThemenLeft = quizThemenLeft;
	}

	/**
	 * Updates the list of themes displayed in the {@link ThemaPanel}.
	 *
	 * @param themen the new collection of themes to display
	 */
	public void setThemen(Collection<Theme> themen) {
		themaPanel.setThemen(themen);
	}

	/**
	 * Gets the {@link ThemaPanel} instance used by this right panel.
	 *
	 * @return the {@link ThemaPanel} containing the theme list
	 */
	public ThemaPanel getThemaPanel() {
		return themaPanel;
	}
}
