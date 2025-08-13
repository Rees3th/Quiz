package gui;

import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import gui.Panels.TabPanel;
import gui.Quiz.QuizPanel;
import gui.QuizFragen.QuizFragenPanel;
import gui.QuizThemen.QuizThemenPanel;

/**
 * QUizApp ist die Hauptklasse der Quiz-Anwendung. Sie initialisiert das
 * Hauptfenster und fügt die verschiedenen Panels hinzu.
 * 
 */
public class QuizApp extends JFrame {

	private static final long serialVersionUID = 1L;
	final int FRAME_X = 600;
	final int FRAME_Y = 240;
	final int FRAME_WIDTH = 800;
	final int FRAME_HEIGHT = 600;

	/**
	 * Konstruktor der QuizApp-Klasse. Initialisiert das JFrame und fügt die
	 * verschiedenen Panels hinzu.
	 * 
	 * @throws HeadlessException
	 */
	public QuizApp() throws HeadlessException {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(FRAME_X, FRAME_Y, FRAME_WIDTH, FRAME_HEIGHT);
		setTitle("Quiz App");
		setResizable(false);
		
		// Erstellen der QuizThemenPanel
		QuizThemenPanel quizThemenPanel = new QuizThemenPanel();
		add(quizThemenPanel);
		
		// QuizFragenPanel benötigt DataDeliver von quizThemenPanel
		QuizFragenPanel quizFragenPanel = new QuizFragenPanel(quizThemenPanel.getDataManager());
		quizThemenPanel.setQuizFragenPanel(quizFragenPanel);
		add(quizFragenPanel);
		
		// QuizPanel benötigt DataDeliver von quizThemenPanel
		QuizPanel quizPanel = new QuizPanel(quizThemenPanel.getDataDeliver());
		
		// Erstellen des TabPanels und Hinzufügen der einzelnen Panels
		TabPanel tabPanel = new TabPanel(quizThemenPanel,quizFragenPanel , quizPanel);
		tabPanel.addTab("Quiz Themen", quizThemenPanel);
		tabPanel.addTab("Quiz Fragen", quizFragenPanel);
		tabPanel.addTab("Quiz", quizPanel);
		tabPanel.addTab("Statistik", new JPanel());
		add(tabPanel);

		setVisible(true);

	}

	public static void main(String[] args) {
		new QuizApp();

	}

}


