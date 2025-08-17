package gui.QuizFragen;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import gui.Panels.ButtonPanel;
import gui.Panels.MessagePanel;

/**
 * {@code QuizQuestionBottom} is the bottom control bar of the
 * {@link QuizQuestionPanel} in the quiz question management view.
 * <p>
 * It contains:
 * <ul>
 * <li>Action buttons for deleting, saving, and creating new questions</li>
 * <li>A {@link MessagePanel} for displaying feedback or error messages</li>
 * </ul>
 * <p>
 * This panel uses the delegate pattern via {@link QuizQuestionDelegate} so that
 * button actions are forwarded to the parent panel or controller, keeping the
 * UI and business logic separate.
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public class QuizQuestionBottom extends JPanel {

	/** Serial version UID for serialization compatibility. */
	private static final long serialVersionUID = 1L;

	/** Button for deleting the currently selected question. */
	private JButton deleteButton;

	/** Button for saving the currently edited question. */
	private JButton saveButton;

	/** Button for creating a new question. */
	private JButton newQuestionButton;

	/** Delegate that will handle user actions triggered from this panel. */
	private QuizQuestionDelegate delegate;

	/** Panel for displaying feedback and error messages to the user. */
	private MessagePanel messagePanel;

	/**
	 * Constructs a bottom control panel for quiz question management.
	 * <p>
	 * The panel consists of:
	 * <ul>
	 * <li>A {@link ButtonPanel} containing the "Delete", "Save", and "New Question"
	 * buttons</li>
	 * <li>A {@link MessagePanel} above the buttons for displaying status
	 * messages</li>
	 * </ul>
	 * <p>
	 * When a button is clicked, the corresponding method on the current delegate
	 * (if set) is invoked:
	 * <ul>
	 * <li><b>Delete</b> → {@link QuizQuestionDelegate#onDeleteQuestion()}</li>
	 * <li><b>Save</b> → {@link QuizQuestionDelegate#onSaveQuestion()}</li>
	 * <li><b>New Question</b> → {@link QuizQuestionDelegate#onNewQuestion()}</li>
	 * </ul>
	 * </p>
	 */
	public QuizQuestionBottom() {
		setLayout(new BorderLayout());

		// Create action buttons
		deleteButton = new JButton("Delete");
		saveButton = new JButton("Save");
		newQuestionButton = new JButton("New Question");

		// Group the buttons in a common panel
		ButtonPanel bp = new ButtonPanel(deleteButton, saveButton, newQuestionButton);
		add(bp, BorderLayout.CENTER);

		// Create and add the message panel above the buttons
		messagePanel = new MessagePanel();
		add(messagePanel, BorderLayout.NORTH);

		// Wire button actions to their respective delegate methods
		saveButton.addActionListener(e -> {
			if (delegate != null) {
				delegate.onSaveQuestion();
			}
		});

		deleteButton.addActionListener(e -> {
			if (delegate != null) {
				delegate.onDeleteQuestion();
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
	 * Returns the "Delete" button instance.
	 *
	 * @return the delete button
	 */
	public JButton getDeleteButton() {
		return deleteButton;
	}

	/**
	 * Returns the "Save" button instance.
	 *
	 * @return the save button
	 */
	public JButton getSaveButton() {
		return saveButton;
	}

	/**
	 * Returns the "New Question" button instance.
	 *
	 * @return the new question button
	 */
	public JButton getNewQuestionButton() {
		return newQuestionButton;
	}

	/**
	 * Returns the current delegate handling the button actions.
	 *
	 * @return the current {@link QuizQuestionDelegate}
	 */
	public QuizQuestionDelegate getDelegate() {
		return delegate;
	}

	/**
	 * Sets the delegate that will process the actions triggered by this panel’s
	 * buttons.
	 *
	 * @param delegate the {@link QuizQuestionDelegate} to assign
	 */
	public void setDelegate(QuizQuestionDelegate delegate) {
		this.delegate = delegate;
	}

	/**
	 * Returns the {@link MessagePanel} used to display feedback or error messages.
	 *
	 * @return the message panel instance
	 */
	public MessagePanel getMessagePanel() {
		return messagePanel;
	}
}
