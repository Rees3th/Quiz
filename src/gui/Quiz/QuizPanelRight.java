package gui.Quiz;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import gui.Panels.ThemaFragenPanel;
import persistence.serialization.QuizDataManager;
import quizLogic.Question;
import quizLogic.Theme;

/**
 * {@code QuizPanelRight} represents the right-hand panel in the quiz gameplay
 * view.
 * <p>
 * It displays a list of quiz themes in a combo box and the corresponding list
 * of questions using a {@link ThemaFragenPanel}. The user can select a theme to
 * filter the visible questions. Selecting a question updates the linked
 * {@link QuizPanelLeft} with its details.
 * </p>
 *
 * <p>
 * This panel is responsible for:
 * <ul>
 * <li>Managing the theme combo box and question list display</li>
 * <li>Synchronizing data with the left-hand panel</li>
 * <li>Reloading themes/questions when necessary</li>
 * </ul>
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public class QuizPanelRight extends JPanel {

	/** Serial version UID for serialization compatibility. */
	private static final long serialVersionUID = 1L;

	/** The combined UI component for displaying theme and question lists. */
	private ThemaFragenPanel themaFragenPanel;

	/** The data manager for accessing themes and their questions. */
	private final QuizDataManager dm;

	/**
	 * Reference to the left-hand panel to display the selected question's details.
	 */
	private QuizPanelLeft quizPanelLeft;

	/** ID of the currently shown (answered) question, or {@code null} if none. */
	private Integer aktuellGezeigteFrageId = null;

	/**
	 * Constructs a new {@code QuizPanelRight}.
	 * <p>
	 * Sets up the panel with theme selection, question list, and event listeners.
	 * </p>
	 *
	 * @param dm the {@link QuizDataManager} used for loading themes and questions
	 */
	public QuizPanelRight(QuizDataManager dm) {
		this.dm = dm;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new EmptyBorder(10, 10, 10, 10));

		List<Theme> themenListe = new ArrayList<>(dm.getAllThemen());
		themaFragenPanel = new ThemaFragenPanel(themenListe);
		add(themaFragenPanel);

		// Hide unused UI elements for quiz gameplay
		themaFragenPanel.getThemaInfoButton().setVisible(false);
		themaFragenPanel.getInfoTitelLbl().setVisible(false);
		themaFragenPanel.getFragenLabel().setVisible(false);

		add(Box.createVerticalStrut(15));

		setupEvents();

		// Select first theme and populate questions
		themaFragenPanel.getThemaComboBox().setSelectedIndex(0);
		updateFragenList(ThemaFragenPanel.ALLE_THEMEN);
	}

	/**
	 * Sets up event listeners for the theme combo box and question list.
	 * <ul>
	 * <li>Theme change → updates question list, syncs theme in left panel, and
	 * resets answers.</li>
	 * <li>Question selection → updates question details in left panel.</li>
	 * </ul>
	 */
	private void setupEvents() {
		themaFragenPanel.getThemaComboBox().addActionListener(e -> {
			Theme selected = (Theme) themaFragenPanel.getThemaComboBox().getSelectedItem();
			updateFragenList(selected);
			if (quizPanelLeft != null && selected != null) {
				quizPanelLeft.setThema(selected);
			}
			resetGezeigteAntworten();
			themaFragenPanel.getFragenList().repaint();
		});

		themaFragenPanel.getFragenList().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				Question selectedQuestion = themaFragenPanel.getFragenList().getSelectedValue();
				if (quizPanelLeft != null && selectedQuestion != null) {
					quizPanelLeft.setFrage(selectedQuestion);
				}
			}
		});
	}

	/**
	 * Updates the list of questions shown based on the selected theme.
	 * <ul>
	 * <li>If "All themes" is selected, loads questions from all themes.</li>
	 * <li>Otherwise, loads only questions from the selected theme.</li>
	 * </ul>
	 *
	 * @param selectedThema the {@link Theme} selected in the combo box
	 */
	private void updateFragenList(Theme selectedThema) {
		@SuppressWarnings("unchecked")
		DefaultListModel<Question> model = (DefaultListModel<Question>) themaFragenPanel.getFragenList().getModel();
		model.clear();

		if (selectedThema != null && "Alle Themen".equals(selectedThema.toString())) {
			for (Theme thema : dm.getAllThemen()) {
				if (thema.getAllQuestions() != null) {
					for (Question q : thema.getAllQuestions()) {
						model.addElement(q);
					}
				}
			}
		} else if (selectedThema != null && selectedThema.getAllQuestions() != null) {
			for (Question q : selectedThema.getAllQuestions()) {
				model.addElement(q);
			}
		}
	}

	/**
	 * Reloads all themes and questions and resets selection to "All themes".
	 */
	public void reloadAllThemenUndFragen() {
		List<Theme> alleThemenListe = new ArrayList<>(dm.getAllThemen());
		alleThemenListe.removeIf(t -> t == ThemaFragenPanel.ALLE_THEMEN);
		themaFragenPanel.setThemen(alleThemenListe);
		themaFragenPanel.getThemaComboBox().setSelectedIndex(0);
		updateFragenList(ThemaFragenPanel.ALLE_THEMEN);
		resetGezeigteAntworten();
		themaFragenPanel.getFragenList().repaint();
	}

	/**
	 * Sets the reference to the left panel to allow updating displayed question
	 * details.
	 *
	 * @param panel the {@link QuizPanelLeft} to link with this panel
	 */
	public void setPanelLeft(QuizPanelLeft panel) {
		this.quizPanelLeft = panel;
	}

	/**
	 * Returns the internal {@link ThemaFragenPanel} instance for further
	 * customization.
	 *
	 * @return the {@link ThemaFragenPanel} for this panel
	 */
	public ThemaFragenPanel getThemaFragenPanel() {
		return themaFragenPanel;
	}

	/**
	 * Marks a question as answered so its answers can be displayed.
	 *
	 * @param questionId the ID of the answered question
	 */
	public void markAnswered(int questionId) {
		aktuellGezeigteFrageId = questionId;
	}

	/**
	 * Resets the answered state so that answers will not be displayed anymore.
	 */
	public void resetGezeigteAntworten() {
		aktuellGezeigteFrageId = null;
	}
}
