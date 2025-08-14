package gui.Quiz;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import gui.Panels.ButtonPanel;

/**
 * {@code QuizPanelBottom} represents the bottom control bar in the quiz
 * gameplay interface.
 * <p>
 * It contains three action buttons:
 * <ul>
 * <li><b>Show answer</b> – to reveal the correct answer to the current
 * question</li>
 * <li><b>Save your answer</b> – to store the user's selected answer</li>
 * <li><b>New question</b> – to load a different question</li>
 * </ul>
 * </p>
 *
 * <p>
 * This panel uses a {@link QuizDelegate} to forward user actions to the
 * controlling logic (usually {@link QuizPanel}), keeping the UI and gameplay
 * logic decoupled.
 * </p>
 * 
 * <p>
 * The buttons are grouped inside a reusable {@link ButtonPanel}.
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public class QuizPanelBottom extends JPanel {

	/** Serial version UID for serialization compatibility. */
	private static final long serialVersionUID = 1L;

	/** Button for showing the correct answer. */
	private JButton answerButton;

	/** Button for saving the user's selected answer. */
	private JButton saveButton;

	/** Button for loading a new question. */
	private JButton newQuestionButton;

	/** Delegate responsible for handling the actions triggered by this panel. */
	private QuizDelegate delegate;

	/**
	 * Creates a {@code QuizPanelBottom} with action buttons for gameplay.
	 * <p>
	 * The panel layout is a {@link BorderLayout}, with the three buttons grouped in
	 * a {@link ButtonPanel} in the center. Button clicks are forwarded to the
	 * corresponding methods on the {@link QuizDelegate} (if set).
	 * </p>
	 *
	 * <ul>
	 * <li>"Save your answer" → {@link QuizDelegate#onSaveAnswer()}</li>
	 * <li>"Show answer" → {@link QuizDelegate#onShowAnswer()}</li>
	 * <li>"New question" → {@link QuizDelegate#onNewQuestion()}</li>
	 * </ul>
	 */
	public QuizPanelBottom() {
		setLayout(new BorderLayout());

		answerButton = new JButton("Show answer");
		saveButton = new JButton("Save your answer");
		newQuestionButton = new JButton("New question");

		// Group all buttons inside a reusable container
		ButtonPanel bp = new ButtonPanel(answerButton, saveButton, newQuestionButton);
		add(bp, BorderLayout.CENTER);

		// Wire button clicks to delegate methods
		saveButton.addActionListener(e -> {
			if (delegate != null) {
				delegate.onSaveAnswer();
			}
		});

		answerButton.addActionListener(e -> {
			if (delegate != null) {
				delegate.onShowAnswer();
			}
		});

		newQuestionButton.addActionListener(e -> {
			if (delegate != null) {
				delegate.onNewQuestion();
			}
		});
	}

	// ---------- Public API (Getters & Setters) ----------

	/**
	 * Returns the "Show answer" button instance.
	 *
	 * @return the answer button
	 */
	public JButton antwortenButton() {
		return answerButton;
	}

	/**
	 * Returns the "Save your answer" button instance.
	 *
	 * @return the save button
	 */
	public JButton speicherButton() {
		return saveButton;
	}

	/**
	 * Returns the "New question" button instance.
	 *
	 * @return the new question button
	 */
	public JButton neueFrageButton() {
		return newQuestionButton;
	}

	/**
	 * Returns the current delegate handling this panel's button actions.
	 *
	 * @return the {@link QuizDelegate} instance, or {@code null} if none set
	 */
	public QuizDelegate getDelegate() {
		return delegate;
	}

	/**
	 * Sets the delegate responsible for handling this panel's button actions.
	 *
	 * @param delegate the {@link QuizDelegate} to assign
	 */
	public void setDelegate(QuizDelegate delegate) {
		this.delegate = delegate;
	}
}
