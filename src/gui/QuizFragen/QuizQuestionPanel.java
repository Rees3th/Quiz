package gui.QuizFragen;

import java.awt.BorderLayout;
import java.util.ArrayList;
import javax.swing.JPanel;

import persistence.serialization.QuizDataManager;
import quizLogic.Question;
import quizLogic.QuestionValidator;
import quizLogic.Theme;

/**
 * {@code QuizQuestionPanel} is the main panel for managing quiz questions.
 * <p>
 * This panel combines:
 * <ul>
 *     <li>{@link QuizQuestionLeft} – left form for editing a question</li>
 *     <li>{@link QuizQuestionRight} – right selection area for themes and questions</li>
 *     <li>{@link QuizQuestionBottom} – bottom action bar with save, new, and delete buttons</li>
 * </ul>
 * It coordinates loading, saving, and deleting questions via the {@link QuizDataManager}
 * and updates the display accordingly.
 * </p>
 * <p>
 * Implements {@link QuizQuestionDelegate} to respond to user actions.
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public class QuizQuestionPanel extends JPanel implements QuizQuestionDelegate {

    /** Serial version UID for serialization compatibility. */
    private static final long serialVersionUID = 1L;

    /** Left form panel for question editing. */
    private QuizQuestionLeft quizFragenLeft;

    /** Right panel for selecting themes and questions. */
    private QuizQuestionRight quizFragenRight;

    /** Bottom panel with control buttons and message display. */
    private QuizQuestionBottom quizFragenBottom;

    /** Data manager for loading and saving questions and themes. */
    private final QuizDataManager dm;

    /**
     * Creates a new {@code QuizQuestionPanel}.
     *
     * @param dm the {@link QuizDataManager} used for data manipulation
     */
    public QuizQuestionPanel(QuizDataManager dm) {
        this.dm = dm;
        initLayout();
        initComponents();
        linkComponents();
        setDelegate();
    }

    /**
     * Initializes the panel's layout.
     * <p>
     * Sets a {@link BorderLayout} with 10-pixel gaps.
     * </p>
     */
    private void initLayout() {
        setLayout(new BorderLayout(10, 10));
    }

    /**
     * Creates the main sub-panels (left, right, bottom).
     */
    private void initComponents() {
        quizFragenLeft = new QuizQuestionLeft(dm);
        quizFragenRight = new QuizQuestionRight(dm);
        quizFragenBottom = new QuizQuestionBottom();
    }

    /**
     * Links the left and right panels so they can update each other
     * and adds all sub-panels to the main layout.
     */
    private void linkComponents() {
        quizFragenRight.setPanelLeft(quizFragenLeft);
        quizFragenLeft.setPanelRight(quizFragenRight);
        add(quizFragenLeft, BorderLayout.WEST);
        add(quizFragenRight, BorderLayout.EAST);
        add(quizFragenBottom, BorderLayout.SOUTH);
    }

    /**
     * Sets this panel as the current {@link QuizQuestionDelegate} for the bottom panel,
     * so button events (save, new, delete) are handled here.
     */
    private void setDelegate() {
        quizFragenBottom.setDelegate(this);
    }

    // ------------------- Public API -------------------

    /**
     * {@inheritDoc}
     * Saves the question currently entered in the form.
     * <p>
     * Workflow:
     * <ol>
     *     <li>Retrieve the currently selected theme and form data</li>
     *     <li>Validate the question using {@link QuestionValidator}</li>
     *     <li>Save the question via {@link QuizDataManager#saveQuestion(Question)}</li>
     *     <li>Reload the question list for the selected theme</li>
     * </ol>
     * </p>
     */
    @Override
    public void onSaveQuestion() {
        Theme selectedThema = (Theme) quizFragenRight.getThemaFragenPanel()
                                                     .getThemaComboBox()
                                                     .getSelectedItem();

        Question q = quizFragenLeft.getSelectedQuestion(selectedThema);

        // Perform business logic validation
        String validationError = QuestionValidator.validate(q, selectedThema, q);
        if (validationError != null) {
            quizFragenBottom.getMessagePanel().setText(validationError);
            return;
        }

        String err = dm.saveQuestion(q);
        if (err != null) {
            quizFragenBottom.getMessagePanel()
                            .setText(QuestionValidator.MSG_SAVE_ERROR_PREFIX + err);
            return;
        }

        reloadFragenForThema(selectedThema);
        quizFragenBottom.getMessagePanel()
                        .setText(QuestionValidator.MSG_SAVE_SUCCESS);
    }

    /**
     * {@inheritDoc}
     * Deletes the currently selected question from the selected theme.
     * <p>
     * If no valid question/theme is selected, a validation message is displayed.
     * </p>
     */
    @Override
    public void onDeleteQuestion() {
        Question q = quizFragenRight.getSelectedQuestion();
        if (q == null || q.getThema() == null) {
            quizFragenBottom.getMessagePanel()
                            .setText(QuestionValidator.MSG_DELETE_INVALID_SELECTION);
            return;
        }

        String result = dm.deleteQuestion(q);
        if (result != null) {
            quizFragenBottom.getMessagePanel()
                            .setText(QuestionValidator.MSG_DELETE_ERROR_PREFIX + result);
        } else {
            reloadFragenForThema(q.getThema());
            quizFragenLeft.setFrage(null);
        }
    }

    /**
     * {@inheritDoc}
     * Prepares the creation of a new question by clearing the left form
     * and resetting the right panel's selection.
     */
    @Override
    public void onNewQuestion() {
        quizFragenLeft.setFrage(null);
        quizFragenRight.getThemaFragenPanel().getFragenList().clearSelection();
    }

    /**
     * Reloads the list of available themes in the right panel.
     */
    public void reloadThemen() {
        ArrayList<Theme> themen = dm.getAllThemen();
        quizFragenRight.reloadThemen(themen);
    }

    /**
     * Reloads all questions for a given {@link Theme} and displays them in the right panel.
     *
     * @param thema the theme for which to refresh the question list
     */
    private void reloadFragenForThema(Theme thema) {
        if (thema == null) {
            return;
        }
        ArrayList<Question> fragen = dm.getQuestionsFor(thema);
        quizFragenRight.reloadFragen(fragen);
    }
}
