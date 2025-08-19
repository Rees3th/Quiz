package gui;

import java.awt.HeadlessException;
import java.sql.SQLException;

import javax.swing.JFrame;

import gui.Panels.StatisticPanel;
import gui.Panels.TabPanel;
import gui.Quiz.QuizPanel;
import gui.QuizQuestion.QuizQuestionPanel;
import gui.QuizThemes.QuizThemePanel;
import persistence.DBDataManager;

/**
 * {@code QuizApp} is the main entry point of the Quiz application.
 *
 * <p>
 * This class sets up the main application window (a {@link JFrame}) and
 * organizes the three main functional areas into a tabbed interface:
 * </p>
 * <ul>
 * <li>{@link QuizThemePanel} – management of quiz topics (themes)</li>
 * <li>{@link QuizQuestionPanel} – management of quiz questions</li>
 * <li>{@link QuizPanel} – running the quiz gameplay</li>
 * </ul>
 *
 * <p>
 * A generic tab ("Statistics") is also included as placeholder for future
 * features.
 * </p>
 *
 * <p>
 * <b>Usage:</b>
 * </p>
 * 
 * <pre>{@code
 * public static void main(String[] args) {
 * 	new QuizApp();
 * }
 * }</pre>
 *
 * <p>
 * This class primarily handles UI orchestration. Data loading and persistence
 * are delegated to {@link DBDataManager} and the respective panels.
 * </p>
 *
 * @author Oleg Kapirulya
 */
public class QuizApp extends JFrame {
	/** Serialization ID for this JFrame class. */
	private static final long serialVersionUID = 1L;

	/** X position of the window on the screen. */
	private static final int FRAME_X = 600;
	/** Y position of the window on the screen. */
	private static final int FRAME_Y = 240;
	/** Width of the application window. */
	private static final int FRAME_WIDTH = 800;
	/** Height of the application window. */
	private static final int FRAME_HEIGHT = 600;

	/**
	 * Constructs and initializes the main quiz application window.
	 *
	 * <p>
	 * Main responsibilities:
	 * </p>
	 * <ol>
	 * <li>Configure JFrame properties</li>
	 * <li>Create and initialize the main panels with a shared
	 * {@link DBDataManager}</li>
	 * <li>Wire dependent panels where necessary (Theme ↔ Question)</li>
	 * <li>Add all components into a {@link TabPanel}</li>
	 * <li>Show the application window</li>
	 * </ol>
	 *
	 * @throws HeadlessException if no display, keyboard, or mouse is available
	 * @throws SQLException      if database initialization fails
	 */
	public QuizApp() throws HeadlessException, SQLException {
		// ---------------- Data Manager ----------------
		DBDataManager dm = new DBDataManager();

		// ---------------- Frame setup ----------------
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(FRAME_X, FRAME_Y, FRAME_WIDTH, FRAME_HEIGHT);
		setTitle("Quiz App");
		setResizable(false);

		// ---------------- Create Panels ----------------
		// Panel to manage quiz topics (themes)
		QuizThemePanel quizThemenPanel = new QuizThemePanel(dm);

		// Panel to manage quiz questions (CRUD operations)
		QuizQuestionPanel quizQuestionPanel = new QuizQuestionPanel(dm);

		// Connect themes panel with questions panel so updates are synced
		quizThemenPanel.setQuizQuestionPanel(quizQuestionPanel);

		// Panel to run the actual quiz gameplay
		QuizPanel quizPanel = new QuizPanel(dm);

		StatisticPanel statisticPanel = new StatisticPanel(dm);
		// ---------------- Create Tab Panel ----------------
		TabPanel tabPanel = new TabPanel(quizThemenPanel, quizQuestionPanel, quizPanel, statisticPanel);

		// Add tabs with user-facing names
		tabPanel.addTab("Add Theme", quizThemenPanel); // Manage quiz topics
		tabPanel.addTab("Add Question", quizQuestionPanel); // Manage quiz questions
		tabPanel.addTab("Quiz", quizPanel); // Play the quiz
		tabPanel.addTab("Statistics", statisticPanel); 

		// ---------------- Add TabPanel to Frame ----------------
		add(tabPanel);

		// ---------------- Show Frame ----------------
		setVisible(true);
	}

	/**
	 * Application entry point.
	 *
	 * @param args not used
	 * @throws SQLException      if the database cannot be initialized
	 * @throws HeadlessException if no display environment exists
	 */
	public static void main(String[] args) throws HeadlessException, SQLException {
		new QuizApp();
	}
}
