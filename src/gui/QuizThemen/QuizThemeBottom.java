package gui.QuizThemen;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import gui.Panels.ButtonPanel;
import gui.Panels.MessagePanel;

/**
 * {@code QuizThemeBottom} is the bottom action bar of the quiz theme management
 * view.
 * <p>
 * It contains:
 * <ul>
 * <li>A {@link MessagePanel} for displaying validation or status messages</li>
 * <li>Action buttons for deleting, saving, or creating a new quiz theme</li>
 * </ul>
 * 
 * <p>
 * This panel communicates user actions to its linked {@link QuizThemeDelegate},
 * which handles the actual logic for managing quiz themes.
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public class QuizThemeBottom extends JPanel {

	/** Serial version UID for serialization compatibility. */
	private static final long serialVersionUID = 1L;

	/** Button to delete the currently selected theme. */
	private JButton deleteButton;

	/** Button to save the current theme (new or edited). */
	private JButton saveButton;

	/** Button to create a new theme entry. */
	private JButton newThemeButton;

	/** Delegate that will handle the business logic for the button actions. */
	private QuizThemeDelegate delegate;

	/** Panel for displaying status or error messages to the user. */
	private MessagePanel messagePanel;

	/**
	 * Constructs a new {@code QuizThemeBottom} action bar.
	 * <ul>
	 * <li>Initializes the buttons and the message panel</li>
	 * <li>Arranges components in a {@link BorderLayout}</li>
	 * <li>Registers button listeners to forward actions to the delegate</li>
	 * </ul>
	 */
	public QuizThemeBottom() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// Initialize buttons
		deleteButton = new JButton("Delete");
		saveButton = new JButton("Save");
		newThemeButton = new JButton("New Theme");

		// Initialize the message panel
		messagePanel = new MessagePanel();
		messagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		// Add the message panel to the top of this panel
		add(messagePanel, BorderLayout.NORTH);

		// Create a button container and add the action buttons
		ButtonPanel bp = new ButtonPanel(deleteButton, saveButton, newThemeButton);
		add(bp, BorderLayout.CENTER);

		// Add listeners for button actions
		saveButton.addActionListener(e -> {
			if (delegate != null) {
				delegate.onSaveTheme();
			}
		});

		deleteButton.addActionListener(e -> {
			if (delegate != null) {
				delegate.onDeleteTheme();
			}
		});

		newThemeButton.addActionListener(e -> {
			if (delegate != null) {
				delegate.onNewTheme();
			}
		});
	}

	/**
	 * Returns the current delegate that handles the button actions.
	 *
	 * @return the {@link QuizThemeDelegate} instance
	 */
	public QuizThemeDelegate getDelegate() {
		return delegate;
	}

	/**
	 * Sets the delegate to handle the actions triggered by this panel.
	 *
	 * @param delegate the {@link QuizThemeDelegate} to assign
	 */
	public void setDelegate(QuizThemeDelegate delegate) {
		this.delegate = delegate;
	}

	/**
	 * Retrieves the message panel used for displaying user messages.
	 *
	 * @return the {@link MessagePanel} displaying status or validation messages
	 */
	public MessagePanel getMessagePanel() {
		return messagePanel;
	}
}
