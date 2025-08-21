package gui.Panels;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JTabbedPane;

import gui.Quiz.QuizPanel;
import gui.QuizQuestion.QuizQuestionPanel;
import gui.QuizThemes.QuizThemePanel;
import gui.Statistic.StatisticsContainerPanel;

/**
 * {@code TabPanel} is a customized {@link JTabbedPane} serving as the main
 * navigation container of the quiz application.
 *
 * <p>
 * This tabbed pane organizes the primary sections of the application into
 * separate tabs:
 * </p>
 * <ul>
 * <li>{@link QuizThemePanel} — managing quiz themes</li>
 * <li>{@link QuizQuestionPanel} — managing quiz questions</li>
 * <li>{@link QuizPanel} — playing quiz games</li>
 * <li>{@link StatisticsContainerPanel} — viewing quiz statistics and
 * analysis</li>
 * </ul>
 *
 * <p>
 * It enhances the standard tabbed interface by listening to tab selection
 * changes and triggering appropriate data refreshes to ensure the UI reflects
 * current information:
 * </p>
 * <ul>
 * <li>When the "Quiz Questions" tab is selected, the associated panel reloads
 * available themes.</li>
 * <li>When the "Quiz" tab is selected, the quiz panel refreshes all quiz
 * questions and themes.</li>
 * <li>When the "Statistics" tab is selected, the statistics panel triggers a
 * data refresh.</li>
 * </ul>
 *
 * <p>
 * By centralizing refresh logic within this container, the application
 * maintains UI consistency and data up-to-dateness seamlessly during user
 * navigation.
 * </p>
 *
 * @author Oleg Kapirulya
 */
public class TabPanel extends JTabbedPane {

	/** Serialization version unique identifier. */
	private static final long serialVersionUID = 1L;

	/** Background color used for the tab bar area. */
	public static final Color BG_COLOR = Color.WHITE;

	/** Font used for tab titles for consistent application styling. */
	private static final Font FONT_TAB = new Font("Helvetica", Font.ITALIC, 16);

	/**
	 * Constructs a new TabPanel with the provided functional panels as tabs.
	 *
	 * @param quizThemePanel    the panel managing quiz themes, displayed under the
	 *                          "Quiz Themes" tab
	 * @param quizQuestionPanel the panel managing quiz questions, displayed under
	 *                          the "Quiz Questions" tab
	 * @param quizPanel         the panel handling the quiz gameplay, displayed
	 *                          under the "Quiz" tab
	 * @param statisticsPanel   the container panel presenting quiz statistics,
	 *                          under the "Statistics" tab
	 */
	public TabPanel(QuizThemePanel quizThemePanel, QuizQuestionPanel quizQuestionPanel, QuizPanel quizPanel,
			StatisticsContainerPanel statisticsPanel) {
		super(JTabbedPane.TOP);

		setFont(FONT_TAB);
		setBackground(BG_COLOR);

		addTab("Quiz Themes", quizThemePanel);
		addTab("Quiz Questions", quizQuestionPanel);
		addTab("Quiz", quizPanel);
		addTab("Statistics", statisticsPanel);

		// Listen to tab selection changes and refresh data accordingly
		addChangeListener(e -> {
			int selectedIndex = getSelectedIndex();
			switch (selectedIndex) {
			case 1: // Quiz Questions tab
				quizQuestionPanel.reloadThemes();
				break;
			case 2: // Quiz tab
				quizPanel.getQuizPanelRight().reloadAllThemesAndQuestions();
				break;
			case 3: // Statistics tab
				statisticsPanel.refresh();
				break;
			}
		});
	}
}
