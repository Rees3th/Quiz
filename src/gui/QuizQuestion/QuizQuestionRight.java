package gui.QuizQuestion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import gui.Panels.QuizQuestionRightLayout;
import persistence.DBDataManager;
import quizLogic.Question;
import quizLogic.Theme;

/**
 * {@code QuizQuestionRight} is the right-hand panel in the quiz question
 * management view.
 *
 * <p>
 * It displays both:
 * </p>
 * <ul>
 * <li>A combo box containing available quiz themes</li>
 * <li>A list of questions belonging to the selected theme</li>
 * </ul>
 *
 * <p>
 * <b>Main responsibilities:</b>
 * </p>
 * <ul>
 * <li>Loading themes/questions from {@link DBDataManager}</li>
 * <li>Reacting to theme selection changes</li>
 * <li>Updating the left form panel ({@link QuizQuestionLeft}) when the user
 * picks a question</li>
 * </ul>
 *
 * <p>
 * This panel keeps the editor panel and data source synchronized, so users can
 * efficiently browse and edit quiz questions.
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public class QuizQuestionRight extends JPanel {

	/** Serial version UID for Swing serialization compatibility. */
	private static final long serialVersionUID = 1L;

	/** Combined sub-panel showing both theme combo box and question list. */
	private QuizQuestionRightLayout quizQuestionRightLayout;

	/** Reference to the left question editor panel. */
	private QuizQuestionLeft quizQuestionLeft;

	/** Data manager for retrieving themes and questions from the DB. */
	private final DBDataManager dm;

	/**
	 * Constructs a new right-hand management panel.
	 *
	 * @param dm the {@link DBDataManager} used for data access
	 */
	public QuizQuestionRight(DBDataManager dm) {
		this.dm = dm;
		initPanel();
		initThemaQuestionPanel();
		setupEvents();
		initializeSelection();
	}

	/**
	 * Initialize layout with vertical box structure and basic padding.
	 */
	private void initPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}

	/**
	 * Create and configure a {@link QuizQuestionRightLayout}, initially filled with
	 * themes from the database.
	 */
	private void initThemaQuestionPanel() {
		List<Theme> filteredThemes = getFilteredThemen();
		quizQuestionRightLayout = new QuizQuestionRightLayout(dm, filteredThemes);
		add(quizQuestionRightLayout);
		add(Box.createVerticalStrut(15));
		// Add special "All themes" entry
		filteredThemes.add(QuizQuestionRightLayout.ALL_THEMES);
	}

	/**
	 * Fetches all real themes from DB and excludes the special "All themes"
	 * placeholder (added separately).
	 *
	 * @return list of valid {@link Theme} objects
	 */
	private List<Theme> getFilteredThemen() {
		List<Theme> list = new ArrayList<>();
		for (Theme t : dm.getAllThemes()) {
			if (!"Alle Themen".equals(t.toString())) {
				list.add(t);
			}
		}
		return list;
	}

	/**
	 * Automatically selects the first theme in the combo box, if available.
	 */
	private void initializeSelection() {
		if (quizQuestionRightLayout.getThemaComboBox().getItemCount() > 0) {
			quizQuestionRightLayout.getThemaComboBox().setSelectedIndex(0);
		}
	}

	/**
	 * Setup event listeners for:
	 * <ul>
	 * <li><b>Theme selection:</b> reload questions for chosen theme</li>
	 * <li><b>Question selection:</b> show details in left editor panel</li>
	 * </ul>
	 */
	private void setupEvents() {
		// Theme selector updates questions in the list
		quizQuestionRightLayout.getThemaComboBox().addActionListener(e -> {
			Theme selected = (Theme) quizQuestionRightLayout.getThemaComboBox().getSelectedItem();
			if (selected != null) {
				handleThemeSelection(selected);
			}
		});

		// Question selection updates the left form
		quizQuestionRightLayout.getQuestionList().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting() && quizQuestionLeft != null) {
				Question selectedQ = quizQuestionRightLayout.getQuestionList().getSelectedValue();
				quizQuestionLeft.setQuestion(selectedQ);
			}
		});
	}

	/**
	 * Replaces the set of available themes and automatically reloads the questions
	 * for the first entry.
	 *
	 * @param themen collection of themes to display.
	 */
	public void setThemen(Collection<Theme> themen) {
		quizQuestionRightLayout.setThemes(themen);
		if (themen != null && !themen.isEmpty()) {
			quizQuestionRightLayout.getThemaComboBox().setSelectedIndex(0);
			Theme first = (Theme) quizQuestionRightLayout.getThemaComboBox().getSelectedItem();
			if (first != null) {
				handleThemeSelection(first);
			}
		} else {
			quizQuestionRightLayout.setQuestion(null);
		}
	}

	/**
	 * Replaces the question list with a given set of questions.
	 *
	 * @param question list of {@link Question} objects (may be null)
	 */
	public void setQuestion(List<Question> question) {
		quizQuestionRightLayout.setQuestion(question);
	}

	/**
	 * Returns the question currently selected in the UI list.
	 *
	 * @return the selected {@link Question}, or {@code null} if nothing chosen
	 */
	public Question getSelectedQuestion() {
		if (quizQuestionRightLayout != null && quizQuestionRightLayout.getQuestionList() != null) {
			return quizQuestionRightLayout.getQuestionList().getSelectedValue();
		}
		return null;
	}

	/**
	 * Links this right-hand panel with the left editing panel, so user selections
	 * can populate its form.
	 *
	 * @param quiz the {@link QuizQuestionLeft} reference
	 */
	public void setPanelLeft(QuizQuestionLeft quizQuestionLeft) {
		this.quizQuestionLeft = quizQuestionLeft;
	}

	/**
	 * Provides access to the underlying {@link QuizQuestionRightLayout} (useful for
	 * advanced customization).
	 *
	 * @return the {@link QuizQuestionRightLayout} instance
	 */
	public QuizQuestionRightLayout getQuizQuestionRightLayout() {
		return quizQuestionRightLayout;
	}

	/**
	 * Utility helper: loads questions for a given theme, including the special "All
	 * themes" case. Additionally, resets the left panel to show the chosen theme
	 * and clears the selected question.
	 *
	 * @param selected the chosen {@link Theme}, may be {@code ALLE_THEMEN} or null
	 */
	private void handleThemeSelection(Theme selected) {
		loadQuestionsForSelection(selected);

		// Reset left form if linked
		if (quizQuestionLeft != null) {
			quizQuestionLeft.setThema(selected == quizQuestionRightLayout.ALL_THEMES ? null : selected);
			quizQuestionLeft.setQuestion(null);
		}
	}

	/**
	 * Loads questions from DB for the chosen theme.
	 *
	 * @param selected the chosen {@link Theme}, may be {@code ALLE_THEMEN} or null
	 */
	private void loadQuestionsForSelection(Theme selected) {
		if (selected == null) {
			setQuestion(new ArrayList<>());
			return;
		}
		List<Question> question;
		if (selected == quizQuestionRightLayout.ALL_THEMES) {
			question = new ArrayList<>();
			for (Theme t : dm.getAllThemes()) {
				question.addAll(dm.getQuestionsFor(t));
			}
		} else {
			question = dm.getQuestionsFor(selected);
		}
		setQuestion(question);
	}
}
