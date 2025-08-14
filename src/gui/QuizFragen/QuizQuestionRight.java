package gui.QuizFragen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;

import gui.Panels.ThemaFragenPanel;
import persistence.serialization.QuizDataManager;
import quizLogic.Question;
import quizLogic.Theme;

/**
 * {@code QuizQuestionRight} is the right-hand panel in the quiz question management view.
 * <p>
 * It provides a {@link ThemaFragenPanel} containing:
 * <ul>
 *     <li>A dropdown list of quiz themes</li>
 *     <li>A corresponding list of questions for the selected theme</li>
 * </ul>
 * The panel coordinates with the left form panel ({@link QuizQuestionLeft}) to
 * update question details when the user changes the selection.
 * </p>
 * 
 * <p>
 * User interactions such as selecting a different theme or question are handled
 * here and propagated to the linked {@link QuizQuestionLeft}.
 * </p>
 * 
 * @author 
 */
public class QuizQuestionRight extends JPanel {

    /** Serial version UID for serialization compatibility. */
    private static final long serialVersionUID = 1L;

    /** Data manager for accessing all themes and questions. */
    private final QuizDataManager dm;

    /** UI panel for displaying both theme and question lists. */
    private ThemaFragenPanel themaFragenPanel;

    /** Reference to the left form panel for displaying the selected question's details. */
    private QuizQuestionLeft quizFragenLeft;

    /**
     * Creates a new right-hand panel for quiz question management.
     *
     * @param dm the {@link QuizDataManager} used to load themes and questions
     */
    public QuizQuestionRight(QuizDataManager dm) {
        this.dm = dm;
        initPanel();
        initThemaFragenPanel();
        setupEvents();
        initializeSelection();
    }

    // ------------------- Initialization -------------------

    /**
     * Initializes the panel layout and basic UI settings.
     * <p>Uses a vertical {@link BoxLayout} with padding.</p>
     */
    private void initPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    /**
     * Creates the {@link ThemaFragenPanel} and adds it to this panel.
     * <p>The theme list is initially loaded from the data source,
     * excluding the special "All themes" entry.</p>
     */
    private void initThemaFragenPanel() {
        List<Theme> filteredThemes = getFilteredThemen();
        themaFragenPanel = new ThemaFragenPanel(filteredThemes);
        add(themaFragenPanel);
        add(Box.createVerticalStrut(15));

        // Add special "All themes" entry to the list after initialization
        filteredThemes.add(ThemaFragenPanel.ALLE_THEMEN);
    }

    /**
     * Retrieves all themes except the special "All themes" entry.
     *
     * @return list of valid {@link Theme} objects
     */
    private List<Theme> getFilteredThemen() {
        List<Theme> list = new ArrayList<>();
        for (Theme t : dm.getAllThemen()) {
            if (!"Alle Themen".equals(t.toString())) {
                list.add(t);
            }
        }
        return list;
    }

    /**
     * Automatically selects the first theme in the dropdown,
     * if at least one theme is present.
     */
    private void initializeSelection() {
        if (themaFragenPanel.getThemaComboBox().getItemCount() > 0) {
            themaFragenPanel.getThemaComboBox().setSelectedIndex(0);
        }
    }

    /**
     * Registers listeners for the theme dropdown and question list
     * to update the linked left panel when the selection changes.
     */
    private void setupEvents() {
        // Theme selection change
        themaFragenPanel.getThemaComboBox().addActionListener(e -> {
            Theme selected = (Theme) themaFragenPanel.getThemaComboBox().getSelectedItem();
            updateFragenList(selected);
            if (quizFragenLeft != null && selected != null) {
                quizFragenLeft.setThema(selected);
            }
        });

        // Question selection change
        themaFragenPanel.getFragenList().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Question selectedQuestion = themaFragenPanel.getFragenList().getSelectedValue();
                if (quizFragenLeft != null && selectedQuestion != null) {
                    quizFragenLeft.setFrage(selectedQuestion);
                }
            }
        });
    }

    // ------------------- Public API -------------------

    /**
     * Reloads the given theme collection into the theme dropdown.
     * After reloading, the first theme is automatically selected.
     *
     * @param neueThemen the new set of themes to be shown in the dropdown
     */
    public void reloadThemen(Collection<Theme> neueThemen) {
        themaFragenPanel.setThemen(neueThemen);
        themaFragenPanel.getThemaComboBox().setSelectedIndex(0);
    }

    /**
     * Reloads the question list in the UI from the given list of questions.
     *
     * @param fragen list of questions to display (may be {@code null})
     */
    public void reloadFragen(ArrayList<Question> fragen) {
        @SuppressWarnings("unchecked")
        DefaultListModel<Question> model =
                (DefaultListModel<Question>) themaFragenPanel.getFragenList().getModel();
        model.clear();
        if (fragen != null) {
            for (Question q : fragen) {
                model.addElement(q);
            }
        }
    }

    /**
     * Checks whether a specific theme (not "All themes") is currently selected.
     *
     * @return {@code true} if a specific theme is selected, {@code false} otherwise
     */
    public boolean hasSelectedThema() {
        Object selected = themaFragenPanel.getThemaComboBox().getSelectedItem();
        return (selected instanceof Theme) && selected != ThemaFragenPanel.ALLE_THEMEN;
    }

    /**
     * Returns the question currently selected in the question list.
     *
     * @return the selected {@link Question} or {@code null} if no selection
     */
    public Question getSelectedQuestion() {
        if (themaFragenPanel != null && themaFragenPanel.getFragenList() != null) {
            return themaFragenPanel.getFragenList().getSelectedValue();
        }
        return null;
    }

    /**
     * Sets the reference to the left question form panel.
     * Required to update form data automatically when the
     * theme or question selection changes.
     *
     * @param quizFragenLeft the linked left panel
     */
    public void setPanelLeft(QuizQuestionLeft quizFragenLeft) {
        this.quizFragenLeft = quizFragenLeft;
    }

    /**
     * Returns the internal {@link ThemaFragenPanel} that displays
     * the theme dropdown and question list.
     *
     * @return the {@link ThemaFragenPanel} instance used by this panel
     */
    public ThemaFragenPanel getThemaFragenPanel() {
        return themaFragenPanel;
    }

    // ------------------- Private Helpers -------------------

    /**
     * Updates the question list when the user selects a new theme.
     * <ul>
     *     <li>If "All themes" is selected, shows questions from all themes</li>
     *     <li>Otherwise, shows questions only from the selected theme</li>
     * </ul>
     *
     * @param selectedThema the currently selected theme
     */
    private void updateFragenList(Theme selectedThema) {
        @SuppressWarnings("unchecked")
        DefaultListModel<Question> model =
                (DefaultListModel<Question>) themaFragenPanel.getFragenList().getModel();
        model.clear();

        if (selectedThema == ThemaFragenPanel.ALLE_THEMEN) {
            for (Theme thema : dm.getAllThemen()) {
                if (thema.getAllQuestions() != null) {
                    model.addAll(thema.getAllQuestions());
                }
            }
        } else if (selectedThema != null && selectedThema.getAllQuestions() != null) {
            model.addAll(selectedThema.getAllQuestions());
        }
    }
}
