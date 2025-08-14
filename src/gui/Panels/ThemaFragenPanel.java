package gui.Panels;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Collection;

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

import quizLogic.Question;
import quizLogic.Theme;

/**
 * {@code ThemaFragenPanel} is a reusable UI component that displays:
 * <ul>
 *     <li>A {@link JComboBox} for selecting a quiz theme</li>
 *     <li>A {@link JList} of questions for the selected theme</li>
 *     <li>An optional information view showing the selected theme's description</li>
 *     <li>An optional feedback view (e.g., for showing correct answers)</li>
 * </ul>
 *
 * <p>
 * It uses a {@link CardLayout} to toggle between different center views:
 * <ul>
 *     <li>"FRAGEN" – question list view</li>
 *     <li>"INFO" – theme info view</li>
 *     <li>"FEEDBACK" – feedback message view</li>
 * </ul>
 * </p>
 *
 * <p>
 * The first combo box item is the special constant {@link #ALLE_THEMEN}, representing "All themes".
 * Selecting it displays questions from all available themes.
 * </p>
 * 
 * <p>
 * This panel is commonly used in theme management, question management, and quiz gameplay UIs.
 * </p>
 * 
 * @author 
 *         Oleg Kapirulya
 */
public class ThemaFragenPanel extends JPanel {

    /** Serial version UID for serialization compatibility. */
    private static final long serialVersionUID = 1L;

    /** Combo box for selecting a theme. */
    private JComboBox<Theme> themaComboBox;

    /** JList displaying the questions for the selected theme. */
    private JList<Question> fragenList;

    /** List model backing the question list. */
    private DefaultListModel<Question> fragenModel;

    /** Collection of all available themes. */
    private Collection<Theme> allThemen;

    /** Button to toggle between theme info and question list. */
    private JButton themaInfoButton;

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
    private JLabel fragenLabel;

    /** Feedback panel shown for displaying correct answers or messages. */
    private JPanel feedbackPanel;

    /** Label inside the feedback panel. */
    private JLabel feedbackLabel;

    /**
     * Special constant theme representing "All themes" in the combo box.
     */
    public static final Theme ALLE_THEMEN = new Theme() {
        @Override
        public String toString() {
            return "Alle Themen";
        }
    };

    /**
     * Constructs a new {@code ThemaFragenPanel} with the given themes loaded into the combo box.
     *
     * @param themen collection of themes to display; may be {@code null} or empty
     */
    public ThemaFragenPanel(Collection<Theme> themen) {
        this.allThemen = themen;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        // ---------- Header with label and toggle button ----------
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));

        setFragenLabel(new JLabel("Fragen zum Thema"));
        headerPanel.add(getFragenLabel());
        headerPanel.add(Box.createHorizontalGlue());

        themaInfoButton = new JButton("Thema anzeigen");
        headerPanel.add(themaInfoButton);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(headerPanel);

        // ---------- Theme combo box ----------
        JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        themaComboBox = new JComboBox<>();
        themaComboBox.addItem(ALLE_THEMEN);

        if (themen != null) {
            for (Theme t : themen) {
                themaComboBox.addItem(t);
            }
        }
        themaComboBox.setPreferredSize(new Dimension(260, 30));
        comboPanel.add(themaComboBox);
        comboPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(comboPanel);

        // ---------- Info panel ----------
        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoTitelLbl = new JLabel();
        infoArea = new JTextArea(4, 26);
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoPanel.add(infoTitelLbl);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(new JScrollPane(infoArea));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ---------- Question list panel ----------
        fragenModel = new DefaultListModel<>();
        fragenList = new JList<>(fragenModel);
        fragenList.setVisibleRowCount(10);
        JScrollPane scrollPane = new JScrollPane(fragenList);
        scrollPane.setPreferredSize(new Dimension(260, 310));

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.add(scrollPane);
        listPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ---------- Feedback panel ----------
        feedbackPanel = new JPanel();
        feedbackPanel.setLayout(new BoxLayout(feedbackPanel, BoxLayout.Y_AXIS));
        feedbackPanel.add(Box.createVerticalGlue());
        feedbackLabel = new JLabel("", SwingConstants.CENTER);
        feedbackLabel.setFont(feedbackLabel.getFont().deriveFont(18f));
        feedbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        feedbackPanel.add(feedbackLabel);
        feedbackPanel.add(Box.createVerticalGlue());

        // ---------- CardLayout to switch between views ----------
        cardLayout = new CardLayout();
        centerPanel = new JPanel(cardLayout);
        centerPanel.add(listPanel, "FRAGEN");
        centerPanel.add(infoPanel, "INFO");
        centerPanel.add(feedbackPanel, "FEEDBACK");
        centerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(centerPanel);

        fillFragenList();

        // ---------- Event handlers ----------
        themaComboBox.addActionListener(e -> {
            if (!showingInfo) {
                fillFragenList();
            } else {
                Theme selected = (Theme) themaComboBox.getSelectedItem();
                updateThemaInfo(selected);
            }
        });

        themaInfoButton.addActionListener(e -> toggleInfoAnzeige());
    }

    /**
     * Populates the question list based on the currently selected theme.
     * <p>
     * If {@link #ALLE_THEMEN} is selected, questions from all themes are shown.
     * Otherwise, only questions for the selected theme are displayed.
     * </p>
     */
    public void fillFragenList() {
        fragenModel.clear();
        Theme selected = (Theme) themaComboBox.getSelectedItem();

        if (selected == ALLE_THEMEN) {
            for (Theme t : allThemen) {
                if (t == ALLE_THEMEN) continue;
                if (t.getAllQuestions() != null) {
                    for (Question q : t.getAllQuestions()) {
                        fragenModel.addElement(q);
                    }
                }
            }
        } else if (selected != null && selected.getAllQuestions() != null) {
            for (Question q : selected.getAllQuestions()) {
                fragenModel.addElement(q);
            }
        }
    }

    /**
     * Updates the theme info area with the description of the selected theme.
     *
     * @param thema the {@link Theme} whose description will be shown
     */
    private void updateThemaInfo(Theme thema) {
        if (thema != null) {
            infoArea.setText(thema.getText() != null ? thema.getText() : "");
        }
    }

    /**
     * Toggles between showing the question list and the theme info view.
     */
    private void toggleInfoAnzeige() {
        showingInfo = !showingInfo;
        if (showingInfo) {
            Theme selected = (Theme) themaComboBox.getSelectedItem();
            updateThemaInfo(selected);
            cardLayout.show(centerPanel, "INFO");
            themaInfoButton.setText("Liste anzeigen");
        } else {
            cardLayout.show(centerPanel, "FRAGEN");
            themaInfoButton.setText("Thema anzeigen");
        }
        revalidate();
        repaint();
    }

    /**
     * Replaces the themes in the combo box and refreshes the question list.
     *
     * @param neueThemen the new themes to be shown in the combo box
     */
    public void setThemen(Collection<Theme> neueThemen) {
        themaComboBox.removeAllItems();
        themaComboBox.addItem(ALLE_THEMEN);

        if (neueThemen != null) {
            for (Theme t : neueThemen) {
                if (t == ALLE_THEMEN) continue;
                themaComboBox.addItem(t);
            }
        }
        this.allThemen = neueThemen;
        fillFragenList();
    }

    /**
     * Shows a feedback message (e.g., correct answer) in the info view.
     *
     * @param answerText the text to display
     */
    public void showFeedbackAnswer(String answerText) {
        infoArea.setText(answerText);
        cardLayout.show(centerPanel, "INFO");
        themaInfoButton.setText("Zurück");
        showingInfo = true;
    }

    /**
     * Switches the display back to the question list view.
     */
    public void showFragenList() {
        cardLayout.show(centerPanel, "FRAGEN");
        revalidate();
        repaint();
    }

    /**
     * Removes a question from the list at the specified index.
     *
     * @param index the index of the question to remove
     */
    public void removeQuestionAt(int index) {
        if (index >= 0 && index < fragenModel.getSize()) {
            fragenModel.remove(index);
        }
    }

    /**
     * Adds a new question to the list.
     *
     * @param question the {@link Question} to add
     */
    public void addQuestion(Question question) {
        fragenModel.addElement(question);
    }

    /** 
     * @return the question {@link JList}
     */
    public JList<Question> getFragenList() {
        return fragenList;
    }

    /** 
     * @return the theme {@link JComboBox}
     */
    public JComboBox<Theme> getThemaComboBox() {
        return themaComboBox;
    }

    /** 
     * @return the info toggle {@link JButton}
     */
    public JButton getThemaInfoButton() {
        return themaInfoButton;
    }

    /** 
     * @return the theme title {@link JLabel} used in the info view
     */
    public JLabel getInfoTitelLbl() {
        return infoTitelLbl;
    }

    /** 
     * @return the label displayed above the question list
     */
    public JLabel getFragenLabel() {
        return fragenLabel;
    }

    /**
     * Sets the label that appears above the question list.
     *
     * @param fragenLabel the new label to set
     */
    public void setFragenLabel(JLabel fragenLabel) {
        this.fragenLabel = fragenLabel;
    }
}
