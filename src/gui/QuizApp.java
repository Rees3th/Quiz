package gui;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.sql.SQLException;

import javax.swing.JFrame;

import gui.Panels.TabPanel;
import gui.Quiz.QuizPanel;
import gui.QuizQuestion.QuizQuestionPanel;
import gui.QuizThemes.QuizThemePanel;
import gui.Statistic.StatisticsContainerPanel;
import persistence.DBDataManager;

/**
 * {@code QuizApp} is the main entry point of the Quiz application.
 *
 * <p>
 * This class sets up the main application window (a {@link JFrame}) and
 * organizes the application into a tabbed interface consisting of core
 * functional areas:
 * </p>
 * <ul>
 * <li>{@link QuizThemePanel} – management of quiz themes/topics</li>
 * <li>{@link QuizQuestionPanel} – create, update, and delete quiz
 * questions</li>
 * <li>{@link QuizPanel} – gameplay for taking quizzes</li>
 * </ul>
 *
 * <p>
 * Additionally, a "Statistics" tab hosts future extensions for data analytics.
 * </p>
 *
 * <p>
 * The {@link DBDataManager} is instantiated here and shared among all panels to
 * provide centralized data access and persistence.
 * </p>
 *
 * <p>
 * <b>Usage:</b>
 * </p>
 * 
 * <pre>{@code
 * public static void main(String[] args) throws SQLException {
 * 	new QuizApp();
 * }
 * }</pre>
 *
 * <p>
 * Responsibilities are primarily UI composition and wiring, delegating business
 * logic and data handling to underlying panels and the data manager.
 * </p>
 *
 * @author Oleg Kapirulya
 */
public class QuizApp extends JFrame {

	private static final long serialVersionUID = 1L;

	/** Initial X position of the application window on screen */
	private static final int FRAME_X = 600;
	/** Initial Y position of the application window on screen */
	private static final int FRAME_Y = 240;
	/** Width of the application window */
	private static final int FRAME_WIDTH = 800;
	/** Height of the application window */
	private static final int FRAME_HEIGHT = 600;

	/**
	 * Constructs and initializes the main application window and its primary
	 * functional panels.
	 * 
	 * @throws HeadlessException if the system does not support a display, keyboard,
	 *                           or mouse
	 * @throws SQLException      if initialization of the database connection fails
	 */
	public QuizApp() throws HeadlessException, SQLException {

		// Create the central data access manager
		DBDataManager dm = new DBDataManager();

		// Configure JFrame properties
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(FRAME_X, FRAME_Y, FRAME_WIDTH, FRAME_HEIGHT);
		setTitle("Quiz App");
		setResizable(true);
		setMinimumSize(new Dimension(650, 500));


		// Instantiate core UI panels, all sharing the same DBDataManager instance
		QuizThemePanel quizThemePanel = new QuizThemePanel(dm);
		QuizQuestionPanel quizQuestionPanel = new QuizQuestionPanel(dm);
		quizThemePanel.setQuizQuestionPanel(quizQuestionPanel); // link theme and questions panels for syncing

		QuizPanel quizPanel = new QuizPanel(dm); // quiz gameplay panel
		StatisticsContainerPanel statisticsContainer = new StatisticsContainerPanel(dm); // stats panel

		// Create tab container and add all panels as tabs
		TabPanel tabPanel = new TabPanel(quizThemePanel, quizQuestionPanel, quizPanel, statisticsContainer);

		tabPanel.addTab("Add Theme", quizThemePanel);
		tabPanel.addTab("Add Question", quizQuestionPanel);
		tabPanel.addTab("Quiz", quizPanel);
		tabPanel.addTab("Statistics", statisticsContainer);

		add(tabPanel);

		// Display the window
		setVisible(true);
	}

	/**
	 * Main method serving as application entry point.
	 * 
	 * @param args command-line arguments (not used)
	 * @throws SQLException      if database initialization fails
	 * @throws HeadlessException if the environment lacks display or input devices
	 */
	public static void main(String[] args) throws SQLException, HeadlessException {
		new QuizApp();
	}
}
