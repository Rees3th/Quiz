package gui.QuizFragen;

import java.awt.Dimension;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;


import gui.Panels.AnswerHeaderPanel;
import gui.Panels.AnswerRowPanel;
import gui.Panels.FragePanel;
import gui.Panels.LabelFieldPanel;
import persistence.serialization.QuizDataManager;
import quizLogic.Answer;
import quizLogic.Question;
import quizLogic.Theme;

/**
 * {@code QuizQuestionLeft} is the left-side panel in the quiz question management view.
 * <p>
 * This panel is responsible for displaying and editing the details of a single quiz question,
 * including:
 * <ul>
 *     <li>The associated theme</li>
 *     <li>Question title</li>
 *     <li>Question text</li>
 *     <li>Up to four possible answers, each of which may be marked as correct</li>
 * </ul>
 * </p>
 * 
 * <p><b>Main functions:</b></p>
 * <ul>
 *     <li>Enter a new question</li>
 *     <li>Edit an existing question</li>
 *     <li>Convert form data into a {@link Question} object</li>
 * </ul>
 * 
 * @author Oleg Kapirulya
 */
public class QuizQuestionLeft extends JPanel {

    /** Serial version UID for serialization compatibility. */
    private static final long serialVersionUID = 1L;

    /** Text field showing the selected theme (read-only). */
    private JTextField themaField;

    /** Field for entering the question title. */
    private JTextField titelField;

    /** Text area for entering the question text. */
    private JTextArea frageArea;

    /** Fields for entering answer text. */
    private JTextField[] answerFields = new JTextField[4];

    /** Checkboxes for marking whether each answer is correct. */
    private JCheckBox[] checkboxes = new JCheckBox[4];

    /** Data manager providing access to quiz data. */
    private final QuizDataManager dm;

    /**
     * Creates a new panel for entering and editing quiz questions.
     *
     * @param dm the {@link QuizDataManager} used for data access
     */
    public QuizQuestionLeft(QuizDataManager dm) {
        this.dm = dm;
        initPanel();
        initComponents();
        layoutComponents();
    }

    // ------------------- Initialization -------------------

    /**
     * Initializes basic layout settings for this panel.
     * <p>Uses a vertical {@link BoxLayout} and sets preferred sizes.</p>
     */
    private void initPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setMaximumSize(new Dimension(500, 500));
        setPreferredSize(new Dimension(450, 500));
    }

    /**
     * Creates all UI components (text fields, checkboxes, text area).
     */
    private void initComponents() {
        themaField = new JTextField(27);
        themaField.setEditable(false);

        titelField = new JTextField(27);

        frageArea = new JTextArea(6, 24);

        for (int i = 0; i < 4; i++) {
            answerFields[i] = new JTextField(23);
            checkboxes[i] = new JCheckBox();
        }
    }

    /**
     * Adds all created components to the panel in a logical order.
     * <p>
     * Structure:
     * <ol>
     *   <li>Theme</li>
     *   <li>Title</li>
     *   <li>Question text</li>
     *   <li>Answers with correct-marking checkboxes</li>
     * </ol>
     * </p>
     */
    private void layoutComponents() {
        add(new LabelFieldPanel("Theme:", themaField));
        add(Box.createVerticalStrut(10));
        add(new LabelFieldPanel("Title:", titelField));
        add(Box.createVerticalStrut(10));
        add(new FragePanel(frageArea));
        add(Box.createVerticalStrut(15));
        add(new AnswerHeaderPanel());
        add(Box.createVerticalStrut(8));
        for (int i = 0; i < 4; i++) {
            add(new AnswerRowPanel(i + 1, answerFields[i], checkboxes[i]));
            add(Box.createVerticalStrut(8));
        }
    }

    // ------------------- Public API -------------------

    /**
     * Sets the currently selected theme in the form.
     *
     * @param t the selected {@link Theme}, or {@code null} to clear the theme field
     */
    public void setThema(Theme t) {
        themaField.setText(t != null ? t.getTitle() : "");
    }

    /**
     * Fills the form with the data of an existing question.
     * <p>If the question is {@code null}, the form is cleared.</p>
     *
     * @param q the {@link Question} to display, or {@code null} to clear the form
     */
    public void setFrage(Question q) {
        if (q == null) {
            clearFields();
            return;
        }

        titelField.setText(q.getTitle());
        frageArea.setText(q.getText());

        List<Answer> answers = q.getAnswers();
        for (int i = 0; i < answerFields.length; i++) {
            if (i < answers.size()) {
                answerFields[i].setText(answers.get(i).getText());
                checkboxes[i].setSelected(answers.get(i).isCorrect());
            } else {
                answerFields[i].setText("");
                checkboxes[i].setSelected(false);
            }
        }
    }

    /**
     * Creates a new {@link Question} object from the current form inputs.
     * <p>
     * Only non-empty answers are included.
     * Each answer can be marked as correct.
     * </p>
     *
     * @param selectedThema the currently selected {@link Theme}
     * @return a new {@link Question} populated with the form data
     */
    public Question getSelectedQuestion(Theme selectedThema) {
        Question q = new Question(selectedThema);
        q.setTitle(titelField.getText());
        q.setText(frageArea.getText());

        for (int i = 0; i < answerFields.length; i++) {
            String text = answerFields[i].getText().trim();
            if (!text.isEmpty()) {
                Answer a = new Answer(q);
                a.setText(text);
                a.setCorrect(checkboxes[i].isSelected());
                a.setId(i);
                q.addAnswer(a);
            }
        }
        return q;
    }

    // ------------------- Private Helpers -------------------

    /**
     * Clears all input fields in the form.
     */
    private void clearFields() {
        themaField.setText("");
        titelField.setText("");
        frageArea.setText("");
        for (int i = 0; i < answerFields.length; i++) {
            answerFields[i].setText("");
            checkboxes[i].setSelected(false);
        }
    }

    /**
     * Optional setter linking this panel to the right-side question panel.
     * Currently unused, but reserved for future functionality.
     *
     * @param quizFragenRight the {@link QuizQuestionRight} panel to link
     */
    public void setPanelRight(QuizQuestionRight quizFragenRight) {
        // Placeholder for potential future linkage
    }
}
