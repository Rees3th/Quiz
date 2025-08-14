package gui;

import java.awt.HeadlessException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import gui.Panels.TabPanel;
import gui.Quiz.QuizPanel;
import gui.QuizFragen.QuizQuestionPanel;
import gui.QuizThemen.QuizThemePanel;

/**
 * {@code QuizApp} is the main entry point of the Quiz application.
 * <p>
 * This class creates the main application window (a {@link JFrame}),
 * initializes all main panels (Quiz themes, Quiz questions, and Quiz run
 * panel), and organizes them into a tabbed interface using {@link TabPanel}.
 * </p>
 * 
 * <p>
 * Usage:
 * 
 * <pre>
 * public static void main(String[] args) {
 * 	new QuizApp();
 * }
 * </pre>
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public class QuizApp extends JFrame {

	/** Serialization ID for this JFrame class. */
	private static final long serialVersionUID = 1L;

	/** X position of the application window on the screen. */
	private static final int FRAME_X = 600;

	/** Y position of the application window on the screen. */
	private static final int FRAME_Y = 240;

	/** Width of the application window. */
	private static final int FRAME_WIDTH = 800;

	/** Height of the application window. */
	private static final int FRAME_HEIGHT = 600;

	/**
	 * Constructs a new {@code QuizApp} frame.
	 * <ul>
	 * <li>Initializes window properties (size, location, title, close
	 * operation)</li>
	 * <li>Creates and wires the main panels: {@link QuizThemePanel},
	 * {@link QuizQuestionPanel}, {@link QuizPanel}</li>
	 * <li>Adds all panels to a {@link TabPanel} for tabbed navigation</li>
	 * </ul>
	 *
	 * @throws HeadlessException if the environment does not support a display,
	 *                           keyboard, or mouse
	 */
	public QuizApp() throws HeadlessException {
		// ---------------- Frame setup ----------------
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(FRAME_X, FRAME_Y, FRAME_WIDTH, FRAME_HEIGHT);
		setTitle("Quiz App");
		setResizable(false);

		// ---------------- Create Panels ----------------
		/**
		 * Creates the QuizThemePanel which manages quiz topics (themes).
		 */
		QuizThemePanel quizThemenPanel = new QuizThemePanel();

		/**
		 * Create QuizQuestionPanel which manages quiz questions. It requires
		 * DataManager from quizThemenPanel for data access.
		 */
		QuizQuestionPanel quizFragenPanel = new QuizQuestionPanel(quizThemenPanel.getDataManager());

		// Wire QuizThemePanel with its QuizQuestionPanel
		quizThemenPanel.setQuizFragenPanel(quizFragenPanel);

		/**
		 * Create QuizPanel which runs the actual quiz gameplay, also requires
		 * DataManager from quizThemenPanel.
		 */
		QuizPanel quizPanel = new QuizPanel(quizThemenPanel.getDataManager());

		// ---------------- Create Tab Panel ----------------
		/**
		 * A TabPanel organizes all parts of the application into tabs.
		 */
		TabPanel tabPanel = new TabPanel(quizThemenPanel, quizFragenPanel, quizPanel);

		// Add tabs with descriptive names
		tabPanel.addTab("Quiz Themen", quizThemenPanel); // Manage quiz topics
		tabPanel.addTab("Quiz Fragen", quizFragenPanel); // Manage quiz questions
		tabPanel.addTab("Quiz", quizPanel); // Play the quiz
		tabPanel.addTab("Statistik", new JPanel()); // Placeholder for future stats panel

		// ---------------- Add TabPanel to frame ----------------
		add(tabPanel);

		// ---------------- Display the frame ----------------
		setVisible(true);
	}

	/**
	 * Application entry point.
	 *
	 * @param args the command-line arguments (not used)
	 */
	public static void main(String[] args) {
		new QuizApp();
	}
}
