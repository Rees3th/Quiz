package gui.Panels;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JTabbedPane;

import gui.Quiz.QuizPanel;
import gui.QuizFragen.QuizQuestionPanel;
import gui.QuizThemen.QuizThemePanel;

/**
 * {@code TabPanel} is a customized {@link JTabbedPane} that contains the main
 * sections (tabs) of the quiz application.
 * <p>
 * It holds:
 * <ul>
 * <li>The {@link QuizThemePanel} for managing quiz themes</li>
 * <li>The {@link QuizQuestionPanel} for managing quiz questions</li>
 * <li>The {@link QuizPanel} for playing the quiz</li>
 * <li>Additional panels such as a statistics tab</li>
 * </ul>
 * 
 * <p>
 * The panel listens for tab change events and triggers data reloads in certain
 * tabs to ensure their content is up to date:
 * <ul>
 * <li>When switching to the "Quiz Questions" tab, it reloads the list of
 * themes.</li>
 * <li>When switching to the "Quiz" tab, it reloads all themes and
 * questions.</li>
 * </ul>
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public class TabPanel extends JTabbedPane {

	/** Serial version UID for serialization compatibility. */
	private static final long serialVersionUID = 1L;

	/** Background color for the tab bar. */
	public static final Color BG_COLOR = Color.WHITE;

	/** Font for the tab titles. */
	private static final Font FONT_TAB = new Font("Helvetica", Font.ITALIC, 16);

	/**
	 * Constructs a new {@code TabPanel} and sets up tab change handling.
	 * <p>
	 * When the tab selection changes, the relevant panel is refreshed.
	 * </p>
	 *
	 * @param quizThemenPanel the {@link QuizThemePanel} used in the "Quiz Themes"
	 *                        tab
	 * @param quizFragenPanel the {@link QuizQuestionPanel} used in the "Quiz
	 *                        Questions" tab
	 * @param quizPanel       the {@link QuizPanel} used in the "Quiz" tab
	 */
	public TabPanel(QuizThemePanel quizThemenPanel, QuizQuestionPanel quizFragenPanel, QuizPanel quizPanel) {

		super(JTabbedPane.TOP);

		setFont(FONT_TAB);
		setBackground(BG_COLOR);

		// Listen for tab changes and refresh data where necessary
		addChangeListener(e -> {
			int tab = getSelectedIndex();
			if (tab == 1) { // "Quiz Questions" tab
				quizFragenPanel.reloadThemen();
			} else if (tab == 2) { // "Quiz" tab
				quizPanel.getQuizPanelRight().reloadAllThemenUndFragen();
			}
		});
	}
}
