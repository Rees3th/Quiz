package gui.Panels;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Collection;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import persistence.DBDataManager;
import quizLogic.Question;
import quizLogic.Theme;

/**
 * {@code ThemaFragenPanel} is a reusable UI component that displays:
 * <ul>
 * <li>A {@link JComboBox} for selecting a quiz theme</li>
 * <li>A {@link JList} of questions for the selected theme</li>
 * <li>An optional information view showing the selected theme's
 * description</li>
 * <li>An optional feedback view (e.g., for showing correct answers or
 * messages)</li>
 * </ul>
 * 
 * <p>
 * It uses a {@link CardLayout} to toggle between different center views:
 * <ul>
 * <li>"FRAGEN" – question list view</li>
 * <li>"INFO" – theme info view</li>
 * <li>"FEEDBACK" – feedback message view</li>
 * </ul>
 * </p>
 * 
 * <p>
 * The first combo box item is the special constant {@link #ALL_THEMES},
 * representing "All themes". Selecting it displays questions from all available
 * themes.
 * </p>
 * 
 * <p>
 * This panel is commonly used in theme management, question management, and
 * quiz gameplay UIs.
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public class QuizQuestionRightLayout extends JPanel {
	/** Serial version UID for serialization compatibility. */
	private static final long serialVersionUID = 1L;

	/** Combo box for selecting a theme. */
	private JComboBox<Theme> themaComboBox;

	/** JList displaying the questions for the selected theme. */
	private JList<Question> questionList;

	/** List model backing the question list. */
	private DefaultListModel<Question> questionModel;

	/** Collection of all available themes. */
	private Collection<Theme> AllThemes;

	/** Button to toggle between theme info and question list. */
	private JButton themeInfoButton;

	/** Panel showing theme information text. */
	private JPanel infoPanel;

	/** Theme title label displayed in the info panel. */
	private JLabel infoTitelLbl;

	/** Text area containing the theme description. */
	private JTextArea infoArea;

	/** Panel containing the scrollable question list. */
	private JPanel listPanel;

	/** Center container switching between list, info, and feedback panels. */
	private JPanel centerPanel;

	/** Layout manager for switching center views. */
	private CardLayout cardLayout;

	/** Internal flag to track if the info view is being shown. */
	private boolean showingInfo = false;

	/** Label above the question list. */
	private JLabel questionLabel;

	/** Feedback panel shown for displaying correct answers or messages. */
	private JPanel feedbackPanel;

	/** Label inside the feedback panel. */
	private JLabel feedbackLabel;

	/** Reference to the database data manager. */
	private DBDataManager dm;

	/**
	 * Special constant theme representing "All themes" in the combo box. Selecting
	 * this special item means showing questions from all themes.
	 */
	public static final Theme ALL_THEMES = new Theme() {
		@Override
		public String toString() {
			return "Alle Themen";
		}
	};

	/**
	 * Constructs a new {@code ThemaFragenPanel} with the given themes loaded into
	 * the combo box.
	 *
	 * @param dm     the {@link DBDataManager} instance to fetch data from the
	 *               database
	 * @param themen collection of themes to display; may be {@code null} or empty
	 */
	public QuizQuestionRightLayout(DBDataManager dm, Collection<Theme> themen) {
		this.dm = dm;
		this.AllThemes = themen;

		// Use vertical layout for the whole panel
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

		// ---------- Header with label and toggle button ----------
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));

		// Label displayed above the question list
		setQuestionLabel(new JLabel("Fragen zum Thema"));
		headerPanel.add(getQuestionLabel());

		// Horizontal glue for spacing
		headerPanel.add(Box.createHorizontalGlue());

		// Button to toggle showing theme information
		themeInfoButton = new JButton("Thema anzeigen");
		headerPanel.add(themeInfoButton);
		headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		// Add header panel to main panel
		add(headerPanel);

		// ---------- Theme combo box panel ----------
		JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		themaComboBox = new JComboBox<>();

		// Add special "All themes" entry first
		themaComboBox.addItem(ALL_THEMES);

		// Add actual themes if available
		if (themen != null) {
			for (Theme t : themen) {
				themaComboBox.addItem(t);
			}
		}

		themaComboBox.setPreferredSize(new Dimension(260, 30));
		comboPanel.add(themaComboBox);
		comboPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		// Add combo box panel to main panel
		add(comboPanel);

		// ---------- Info panel (shows theme description text) ----------
		infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

		// Label showing theme title (currently unused, reserved for enhancement)
		infoTitelLbl = new JLabel();

		// Text area for displaying theme description, non-editable and word wrapped
		infoArea = new JTextArea(4, 26);
		infoArea.setEditable(false);
		infoArea.setLineWrap(true);
		infoArea.setWrapStyleWord(true);

		infoPanel.add(infoTitelLbl);
		infoPanel.add(Box.createVerticalStrut(5));
		infoPanel.add(new JScrollPane(infoArea));
		infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		// ---------- Question list panel ----------
		questionModel = new DefaultListModel<>();
		questionList = new JList<>(questionModel);
		questionList.setVisibleRowCount(10);

		JScrollPane scrollPane = new JScrollPane(questionList);
		scrollPane.setPreferredSize(new Dimension(260, 310));

		listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		listPanel.add(scrollPane);
		listPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		// ---------- Feedback panel (for correct answers or messages) ----------
		feedbackPanel = new JPanel();
		feedbackPanel.setLayout(new BoxLayout(feedbackPanel, BoxLayout.Y_AXIS));
		feedbackPanel.add(Box.createVerticalGlue());

		feedbackLabel = new JLabel("", SwingConstants.CENTER);
		feedbackLabel.setFont(feedbackLabel.getFont().deriveFont(18f));
		feedbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		feedbackPanel.add(feedbackLabel);
		feedbackPanel.add(Box.createVerticalGlue());

		// ---------- Center panel with CardLayout to switch views ----------
		cardLayout = new CardLayout();
		centerPanel = new JPanel(cardLayout);

		// Add the possible views to the center panel
		centerPanel.add(listPanel, "FRAGEN"); // question list view
		centerPanel.add(infoPanel, "INFO"); // theme info view
		centerPanel.add(feedbackPanel, "FEEDBACK"); // feedback message view
		centerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		add(centerPanel);

		// Load initial questions into the list
		fillQuestionList();

		// ---------- Event handlers ----------

		// When theme in combo box changes, update questions list or info view
		// accordingly
		themaComboBox.addActionListener(e -> {
			if (!showingInfo) {
				fillQuestionList(); // Update questions list for selected theme
			} else {
				Theme selected = (Theme) themaComboBox.getSelectedItem();
				updateThemeInfo(selected); // Update info text for selected theme
			}
		});

		// Button toggles between question list view and theme info view
		themeInfoButton.addActionListener(e -> toggleInfoPanel());
	}

	/**
	 * Clears and fills the question list model with questions from the currently
	 * selected theme. If "Alle Themen" is selected, questions from all themes
	 * except the special "All themes" item are shown.
	 */
	public void fillQuestionList() {
		questionModel.clear();

		Theme selected = (Theme) themaComboBox.getSelectedItem();

		if (selected == ALL_THEMES) {
			// Add questions from all themes except the special "Alle Themen" entry
			for (Theme t : AllThemes) {
				if (t == ALL_THEMES)
					continue;

				List<Question> questions = dm.getQuestionsFor(t);
				for (Question q : questions) {
					questionModel.addElement(q);
				}
			}
		} else if (selected != null) {
			// Add questions for the selected single theme
			List<Question> questions = dm.getQuestionsFor(selected);
			for (Question q : questions) {
				questionModel.addElement(q);
			}
		}
	}

	/**
	 * Updates the info area to display the text description of the given theme.
	 *
	 * @param thema the selected {@link Theme} whose description is shown; may be
	 *              {@code null}
	 */
	private void updateThemeInfo(Theme thema) {
		if (thema != null) {
			infoArea.setText(thema.getText() != null ? thema.getText() : "");
		}
	}

	/**
	 * Toggles between showing the question list and the theme info panel. Switches
	 * the card layout view and updates the toggle button text accordingly.
	 */
	private void toggleInfoPanel() {
		showingInfo = !showingInfo;

		if (showingInfo) {
			Theme selected = (Theme) themaComboBox.getSelectedItem();
			updateThemeInfo(selected);
			cardLayout.show(centerPanel, "INFO");
			themeInfoButton.setText("Liste anzeigen");
		} else {
			cardLayout.show(centerPanel, "FRAGEN");
			themeInfoButton.setText("Thema anzeigen");
		}

		revalidate();
		repaint();
	}

	/**
	 * Replaces the themes currently displayed in the combo box and refreshes the
	 * question list.
	 *
	 * @param neueThemen the collection of new {@link Theme}s to display
	 */
	public void setThemes(Collection<Theme> neueThemen) {
		themaComboBox.removeAllItems();
		themaComboBox.addItem(ALL_THEMES);

		if (neueThemen != null) {
			for (Theme t : neueThemen) {
				// Exclude the special "All themes" item if present
				if (t != ALL_THEMES)
					themaComboBox.addItem(t);
			}
		}

		this.AllThemes = neueThemen;
	}

	/**
	 * Displays a feedback message (e.g. for showing the correct answer) in the info
	 * view.
	 *
	 * @param answerText the message text to show
	 */
	public void showFeedbackAnswer(String answerText) {
		infoArea.setText(answerText);
		cardLayout.show(centerPanel, "INFO");
		themeInfoButton.setText("Zurück");
		showingInfo = true;
	}

	/**
	 * Resets the center panel to show the question list view.
	 */
	public void showQuestionList() {
		cardLayout.show(centerPanel, "FRAGEN");
		revalidate();
		repaint();
	}

	/**
	 * Replaces the question list content with a new list of questions.
	 * 
	 * @param fragen list of {@link Question}s to display; can be null or empty
	 */
	public void setQuestion(List<Question> fragen) {
		questionModel.clear();
		if (fragen != null) {
			for (Question q : fragen) {
				questionModel.addElement(q);
			}
		}
	}

	/**
	 * Removes a question from the list at the given index.
	 *
	 * @param index index of the question to remove; must be within list bounds
	 */
	public void removeQuestionAt(int index) {
		if (index >= 0 && index < questionModel.getSize()) {
			questionModel.remove(index);
		}
	}

	/**
	 * Adds a new question to the question list.
	 *
	 * @param question the {@link Question} to add
	 */
	public void addQuestion(Question question) {
		questionModel.addElement(question);
	}

	/**
	 * @return the internal {@link JList} component displaying questions
	 */
	public JList<Question> getQuestionList() {
		return questionList;
	}

	/**
	 * @return the internal {@link JComboBox} component for theme selection
	 */
	public JComboBox<Theme> getThemaComboBox() {
		return themaComboBox;
	}

	/**
	 * @return the button used to toggle theme info view
	 */
	public JButton getThemaInfoButton() {
		return themeInfoButton;
	}

	/**
	 * @return the label displaying the theme title in info view
	 */
	public JLabel getInfoTitelLbl() {
		return infoTitelLbl;
	}

	/**
	 * @return the label above the question list
	 */
	public JLabel getQuestionLabel() {
		return questionLabel;
	}

	/**
	 * Sets the label that appears above the question list.
	 *
	 * @param fragenLabel the new {@link JLabel} to set
	 */
	public void setQuestionLabel(JLabel questionLabel) {
		this.questionLabel = questionLabel;
	}
}
