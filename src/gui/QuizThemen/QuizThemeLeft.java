package gui.QuizThemen;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import gui.Panels.QuizThemenLeftLayout;
import quizLogic.Theme;

/**
 * {@code QuizThemeLeft} is the left panel in the quiz theme management UI.
 * <p>
 * It contains input fields for entering or editing the title and description
 * of a quiz theme. The layout and UI elements are built via
 * {@link QuizThemenLeftLayout}.
 * </p>
 * 
 * <p>
 * This panel is typically paired with {@link QuizThemeRight}
 * to form a two-column layout for theme selection and editing.
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public class QuizThemeLeft extends JPanel {

    /** Serial version UID for serialization compatibility. */
    private static final long serialVersionUID = 1L;

    /** Text field for entering the theme's title. */
    private JTextField titelField;

    /** Text area for entering additional theme information/description. */
    private JTextArea infoArea;

    /**
     * Constructs a new {@code QuizThemeLeft} panel.
     * <ul>
     *   <li>Initializes the panel layout</li>
     *   <li>Creates UI components</li>
     *   <li>Delegates building the full layout to {@link QuizThemenLeftLayout}</li>
     * </ul>
     */
    public QuizThemeLeft() {
        super();
        initPanelLayout();
        initComponents();
        QuizThemenLeftLayout.build(this, titelField, infoArea);
    }

    /**
     * Initializes the panel's layout settings, including spacing and dimensions.
     */
    private void initPanelLayout() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(350, 500));
    }

    /**
     * Creates and configures the input components for title and info text.
     */
    private void initComponents() {
        titelField = new JTextField();
        titelField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        titelField.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoArea = new JTextArea(6, 30);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
    }

    /**
     * Sets the theme's data into the input fields.
     *
     * @param thema The {@link Theme} whose data should be displayed;
     *              if {@code null}, the fields will be cleared.
     */
    public void setThema(Theme thema) {
        if (thema != null) {
            titelField.setText(thema.getTitle());
            infoArea.setText(thema.getText());
        } else {
            clearFields();
        }
    }

    /**
     * Clears both the title and information input fields.
     */
    public void clearFields() {
        titelField.setText("");
        infoArea.setText("");
    }

    /**
     * Optional setter for linking the right-hand theme panel.
     * <p>
     * Currently a placeholder for potential future interaction between panels.
     * </p>
     *
     * @param quizThemenRight The {@link QuizThemeRight} panel to associate.
     */
    public void setPanelRight(QuizThemeRight quizThemenRight) {
        // Future implementation: link functionality with right panel if needed.
    }

    /**
     * Gets the title input field.
     *
     * @return the {@link JTextField} for entering the title.
     */
    public JTextField getTitelField() {
        return titelField;
    }

    /**
     * Gets the info text area.
     *
     * @return the {@link JTextArea} for entering theme information.
     */
    public JTextArea getInfoArea() {
        return infoArea;
    }
}
