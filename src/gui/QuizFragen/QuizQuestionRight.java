package gui.QuizFragen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import gui.Panels.ThemaFragenPanel;
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
	private ThemaFragenPanel themaFragenPanel;

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
		initThemaFragenPanel();
		setupEvents();
		initializeSelection();
	}

	// ------------------- Initialization -------------------

	/**
	 * Initialize layout with vertical box structure and basic padding.
	 */
	private void initPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}

	/**
	 * Create and configure a {@link ThemaFragenPanel}, initially filled with themes
	 * from the database.
	 */
	private void initThemaFragenPanel() {
		List<Theme> filteredThemes = getFilteredThemen();
		themaFragenPanel = new ThemaFragenPanel(dm, filteredThemes);
		add(themaFragenPanel);
		add(Box.createVerticalStrut(15));
		// Add special "All themes" entry
		filteredThemes.add(ThemaFragenPanel.ALLE_THEMEN);
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
		if (themaFragenPanel.getThemaComboBox().getItemCount() > 0) {
			themaFragenPanel.getThemaComboBox().setSelectedIndex(0);
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
		themaFragenPanel.getThemaComboBox().addActionListener(e -> {
			Theme selected = (Theme) themaFragenPanel.getThemaComboBox().getSelectedItem();
			if (selected == null)
				return;

			List<Question> fragen;
			if (selected == themaFragenPanel.ALLE_THEMEN) {
				fragen = new ArrayList<>();
				for (Theme t : dm.getAllThemes()) {
					fragen.addAll(dm.getQuestionsFor(t));
				}
			} else {
				fragen = dm.getQuestionsFor(selected);
			}

			setFragen(fragen);

			// Reset left form when theme changes
			if (quizQuestionLeft != null) {
				quizQuestionLeft.setThema(selected == themaFragenPanel.ALLE_THEMEN ? null : selected);
				quizQuestionLeft.setFrage(null);
			}
		});

		// Question selection updates the left form
		themaFragenPanel.getFragenList().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting() && quizQuestionLeft != null) {
				Question selectedQ = themaFragenPanel.getFragenList().getSelectedValue();
				quizQuestionLeft.setFrage(selectedQ);
			}
		});
	}

	// ------------------- Public API -------------------

	/**
	 * Replaces the set of available themes and automatically reloads the questions
	 * for the first entry.
	 *
	 * @param themen collection of themes to display.
	 */
	public void setThemen(Collection<Theme> themen) {
		themaFragenPanel.setThemen(themen);

		if (themen != null && !themen.isEmpty()) {
			themaFragenPanel.getThemaComboBox().setSelectedIndex(0);
			Theme first = (Theme) themaFragenPanel.getThemaComboBox().getSelectedItem();
			loadQuestionsForSelection(first);
		} else {
			themaFragenPanel.setFragen(null);
		}
	}

	/**
	 * Replaces the question list with a given set of questions.
	 *
	 * @param fragen list of {@link Question} objects (may be null)
	 */
	public void setFragen(List<Question> fragen) {
		themaFragenPanel.setFragen(fragen);
	}

	/**
	 * Returns the question currently selected in the UI list.
	 *
	 * @return the selected {@link Question}, or {@code null} if nothing chosen
	 */
	public Question getSelectedQuestion() {
		if (themaFragenPanel != null && themaFragenPanel.getFragenList() != null) {
			return themaFragenPanel.getFragenList().getSelectedValue();
		}
		return null;
	}

	/**
	 * Links this right-hand panel with the left editing panel, so user selections
	 * can populate its form.
	 *
	 * @param quizFragenLeft the {@link QuizQuestionLeft} reference
	 */
	public void setPanelLeft(QuizQuestionLeft quizFragenLeft) {
		this.quizQuestionLeft = quizFragenLeft;
	}

	/**
	 * Provides access to the underlying {@link ThemaFragenPanel} (useful for
	 * advanced customization).
	 *
	 * @return the {@link ThemaFragenPanel} instance
	 */
	public ThemaFragenPanel getThemaFragenPanel() {
		return themaFragenPanel;
	}

	// ------------------- Internal Helper -------------------

	/**
	 * Utility helper: loads questions for a given theme, including the special "All
	 * themes" case.
	 */
	private void loadQuestionsForSelection(Theme selected) {
		if (selected == null) {
			setFragen(new ArrayList<>());
			return;
		}
		List<Question> fragen;
		if (selected == themaFragenPanel.ALLE_THEMEN) {
			fragen = new ArrayList<>();
			for (Theme t : dm.getAllThemes()) {
				fragen.addAll(dm.getQuestionsFor(t));
			}
		} else {
			fragen = dm.getQuestionsFor(selected);
		}
		setFragen(fragen);
	}
}
