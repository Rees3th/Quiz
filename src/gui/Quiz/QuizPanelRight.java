package gui.Quiz;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import gui.Panels.QuizQuestionRightLayout;
import persistence.DBDataManager;
import quizLogic.Question;
import quizLogic.Theme;

/**
 * {@code QuizPanelRight} represents the right-side panel of the quiz gameplay
 * view.
 *
 * <p>
 * This panel manages the **theme selection and question list**. It uses a
 * {@link QuizQuestionRightLayout} internally, which bundles:
 * </p>
 * <ul>
 * <li>A combo box for selecting a theme</li>
 * <li>A list of questions belonging to the chosen theme</li>
 * </ul>
 *
 * <p>
 * Responsibilities of this panel include:
 * </p>
 * <ul>
 * <li>Fetching themes and corresponding questions via
 * {@link DBDataManager}</li>
 * <li>Updating the question list whenever the theme changes</li>
 * <li>Notifying the linked {@link QuizPanelLeft} when a question is selected,
 * so its details and answers can be shown there</li>
 * <li>Tracking whether a selected question has been answered</li>
 * </ul>
 *
 * <p>
 * By default, the panel initializes with all available themes (including an
 * "All themes" pseudo-entry) and automatically populates its question list.
 * </p>
 *
 * @author Oleg Kapirulya
 */
public class QuizPanelRight extends JPanel {
	/** Serial version UID for serialization compatibility. */
	private static final long serialVersionUID = 1L;

	/** Combined UI element for theme selection and question list. */
	private QuizQuestionRightLayout quizQuestionRightLayout;

	/** Data manager for retrieving themes and questions from the database. */
	private final DBDataManager dm;

	/**
	 * Reference to the left-side panel, used to display selected question details.
	 */
	private QuizPanelLeft quizPanelLeft;

	/**
	 * ID of the currently shown (answered) question, or {@code null} if none is
	 * marked.
	 */
	private Integer actualQuestionId = null;

	/**
	 * Constructs a new {@code QuizPanelRight}.
	 *
	 * <p>
	 * Steps:
	 * </p>
	 * <ol>
	 * <li>Initialize the layout and padding</li>
	 * <li>Load all available themes from {@link DBDataManager}</li>
	 * <li>Create a {@link QuizQuestionRightLayout} with those themes</li>
	 * <li>Register event listeners for theme and question selection</li>
	 * <li>Select the first theme (default "All themes") and populate the question
	 * list</li>
	 * </ol>
	 *
	 * @param dm the {@link DBDataManager} used for accessing themes and questions
	 */
	public QuizPanelRight(DBDataManager dm) {
		this.dm = dm;

		// Use a vertical box layout for stacking components
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new EmptyBorder(10, 10, 10, 10));

		// Load initial themes from database
		List<Theme> themenListe = new ArrayList<>(dm.getAllThemes());

		// Build internal UI component
		quizQuestionRightLayout = new QuizQuestionRightLayout(dm, themenListe);
		add(quizQuestionRightLayout);

		// Hide UI elements not needed in this gameplay context
		quizQuestionRightLayout.getThemaInfoButton().setVisible(false);
		quizQuestionRightLayout.getInfoTitelLbl().setVisible(false);
		quizQuestionRightLayout.getQuestionLabel().setVisible(false);

		add(Box.createVerticalStrut(15)); // spacing between UI elements

		// Link event listeners (theme change, question select)
		setupEvents();

		// Start with first theme selected -> trigger population
		quizQuestionRightLayout.getThemaComboBox().setSelectedIndex(0);
		updateQuestionList(QuizQuestionRightLayout.ALL_THEMES);
	}

	/**
	 * Configures the event listeners for the internal components:
	 * <ul>
	 * <li><b>Theme selection:</b> updates the question list accordingly.</li>
	 * <li><b>Question selection:</b> loads the full question details (incl.
	 * answers) from DB and updates the linked {@link QuizPanelLeft}.</li>
	 * </ul>
	 */
	private void setupEvents() {
		// Theme combo box selection -> update list of questions
		quizQuestionRightLayout.getThemaComboBox().addActionListener(e -> {
			Theme selected = (Theme) quizQuestionRightLayout.getThemaComboBox().getSelectedItem();
			List<Question> fragen;
			if (selected == null) {
				fragen = new ArrayList<>();
			} else if (selected == QuizQuestionRightLayout.ALL_THEMES) {
				// Collect questions from all themes
				fragen = new ArrayList<>();
				for (Theme t : dm.getAllThemes()) {
					fragen.addAll(dm.getQuestionsFor(t));
				}
			} else {
				// Load questions for the selected theme only
				fragen = dm.getQuestionsFor(selected);
			}
			quizQuestionRightLayout.setQuestion(fragen);
		});

		// Question list selection -> update details on left panel
		quizQuestionRightLayout.getQuestionList().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				Question selected = quizQuestionRightLayout.getQuestionList().getSelectedValue();
				if (selected != null) {
					// Retrieve full version of question from DB (with answers loaded)
					Question fullQ = dm.getFullQuestionById(selected.getId());
					if (quizPanelLeft != null) {
						quizPanelLeft.fillWithData(fullQ);
					}
				} else {
					// No question selected -> clear detail fields on left panel
					if (quizPanelLeft != null) {
						quizPanelLeft.fillWithData(null);
					}
				}
			}
		});
	}

	/**
	 * Updates the list of questions shown based on the selected theme.
	 *
	 * <ul>
	 * <li>If "All themes" is selected, questions from all themes are shown.</li>
	 * <li>If a single theme is selected, only that theme's questions are
	 * displayed.</li>
	 * </ul>
	 *
	 * @param selectedThema the {@link Theme} selected in the combo box
	 */
	private void updateQuestionList(Theme selectedThema) {

		DefaultListModel<Question> model = (DefaultListModel<Question>) quizQuestionRightLayout.getQuestionList()
				.getModel();
		model.clear();

		if (selectedThema != null && "Alle Themen".equals(selectedThema.toString())) {
			// Load all questions across all themes
			for (Theme thema : dm.getAllThemes()) {
				if (thema.getAllQuestions() != null) {
					for (Question q : thema.getAllQuestions()) {
						model.addElement(q);
					}
				}
			}
		} else if (selectedThema != null && selectedThema.getAllQuestions() != null) {
			// Load only the selected theme's questions
			for (Question q : selectedThema.getAllQuestions()) {
				model.addElement(q);
			}
		}
	}

	/**
	 * Reloads all themes and questions from the database, and resets the theme
	 * selection to "All themes".
	 */
	public void reloadAllThemesAndQuestions() {
		List<Theme> themes = dm.getAllThemes();
		quizQuestionRightLayout.setThemes(themes);

		if (themes != null && !themes.isEmpty()) {
			quizQuestionRightLayout.getThemaComboBox().setSelectedIndex(0);
			Theme first = (Theme) quizQuestionRightLayout.getThemaComboBox().getSelectedItem();

			List<Question> fragen = (first == QuizQuestionRightLayout.ALL_THEMES) ? collectAllQuestions(themes)
					: dm.getQuestionsFor(first);

			quizQuestionRightLayout.setQuestion(fragen);
		} else {
			quizQuestionRightLayout.setQuestion(null);
		}
	}

	/**
	 * Helper method: collects all questions from all (real) themes.
	 *
	 * @param themes list of available themes
	 * @return list of all questions across themes
	 */
	private List<Question> collectAllQuestions(List<Theme> themes) {
		List<Question> all = new ArrayList<>();
		if (themes != null) {
			for (Theme t : themes) {
				if (t != QuizQuestionRightLayout.ALL_THEMES) {
					all.addAll(dm.getQuestionsFor(t));
				}
			}
		}
		return all;
	}

	/**
	 * Sets the link to the left panel. Enables this right panel to update the
	 * question details view whenever a question is selected.
	 *
	 * @param panel the {@link QuizPanelLeft} reference
	 */
	public void setPanelLeft(QuizPanelLeft panel) {
		this.quizPanelLeft = panel;
	}

	/**
	 * Provides access to the underlying {@link QuizQuestionRightLayout} instance
	 * for advanced customization if needed.
	 *
	 * @return the inner {@link QuizQuestionRightLayout}
	 */
	public QuizQuestionRightLayout getQuizQuestionRightLayout() {
		return quizQuestionRightLayout;
	}

	/**
	 * Marks a given question as "answered" by storing its ID. Can later be used to
	 * highlight or filter answered questions.
	 *
	 * @param questionId the ID of the answered question
	 */
	public void markAnswered(int questionId) {
		actualQuestionId = questionId;
	}

	/**
	 * Resets the answered-state of all questions. Typically called when switching
	 * to a new question.
	 */
	public void resetActualQuestion() {
		actualQuestionId = null;
	}

	/**
	 * Sets the active set of questions manually (used in special cases).
	 *
	 * @param questions list of questions to display
	 */
	public void setQuestions(List<Question> questions) {
		quizQuestionRightLayout.setQuestion(questions);
	}
}
