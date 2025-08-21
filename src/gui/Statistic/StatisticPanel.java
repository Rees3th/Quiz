package gui.Statistic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import persistence.DBDataManager;
import quizLogic.Question;
import quizLogic.QuizStatistic;
import quizLogic.Theme;

/**
 * StatisticPanel displays quiz statistics with selectable themes, questions, and weeks.
 * It shows a bar chart visualizing the number of correct and incorrect answers per day within the 
 * selected calendar week, plus a label with the calculated accuracy percentage.
 * <p>
 * Components:
 * <ul>
 *   <li>ComboBoxes for theme, question, and week selection.</li>
 *   <li>Bar chart panel for visualization of daily results.</li>
 *   <li>Label showing accuracy summary.</li>
 * </ul>
 * <p>
 * Data is loaded dynamically when selections change.
 * 
 * Usage:
 * - User selects theme, question, and week.
 * - Panel gathers statistics and updates bar chart and accuracy label.
 */
public class StatisticPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    // ComboBoxes for selecting theme, question, and calendar week
    private JComboBox<Theme> themeComboBox;
    private JComboBox<Object> questionComboBox; // includes "All Questions" option
    private JComboBox<String> weekComboBox;

    // Label to display accuracy percentage
    private JLabel accuracyLabel;

    private final DBDataManager dm; // Data manager for database access

    private JPanel barChartPanel; // Panel where bar chart is drawn

    // Maps week label strings to lists of quiz statistics for that week
    private Map<String, List<QuizStatistic>> weekStatsMap = new LinkedHashMap<>();

    /**
     * Constructor initializes UI elements and sets up event handling.
     * @param dm database manager for quiz data access
     */
    public StatisticPanel(DBDataManager dm) {
        this.dm = dm;
        setLayout(new BorderLayout(8, 8));

        // Create control panel with horizontal box layout
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.X_AXIS));
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Add labeled combo boxes vertically stacked
        controlsPanel.add(createControlPanel("Select Theme", themeComboBox = new JComboBox<>()));
        controlsPanel.add(Box.createHorizontalStrut(20));
        controlsPanel.add(createControlPanel("Select Question", questionComboBox = new JComboBox<>()));
        controlsPanel.add(Box.createHorizontalStrut(20));
        controlsPanel.add(createControlPanel("Select Week", weekComboBox = new JComboBox<>()));

        add(controlsPanel, BorderLayout.NORTH);

        // Center area: Bar chart panel with custom painting
        barChartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawChart(g);
            }
        };
        barChartPanel.setPreferredSize(new Dimension(700, 300));
        add(barChartPanel, BorderLayout.CENTER);

        // Bottom label shows accuracy summary
        accuracyLabel = new JLabel("Genauigkeit: 0%", SwingConstants.CENTER);
        accuracyLabel.setFont(accuracyLabel.getFont().deriveFont(Font.BOLD, 16f));
        add(accuracyLabel, BorderLayout.SOUTH);

        // Set preferred sizes for combo boxes for consistent layout
        themeComboBox.setPreferredSize(new Dimension(200, 24));
        questionComboBox.setPreferredSize(new Dimension(200, 24));
        weekComboBox.setPreferredSize(new Dimension(150, 24));

        // Event listeners to update questions and weeks on selection change
        themeComboBox.addActionListener(e -> fetchQuestions());
        questionComboBox.addActionListener(e -> fetchWeeks());
        weekComboBox.addActionListener(e -> updateChart());

        // Initially populate themes combo box
        fetchThemes();
    }

    /**
     * Creates a vertical panel containing a label and a combo box.
     * @param labelText text for the label
     * @param comboBox combo box to be added below the label
     * @return JPanel with vertical layout containing label and combo box
     */
    private JPanel createControlPanel(String labelText, JComboBox<?> comboBox) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel label = new JLabel(labelText);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        comboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(label);
        p.add(Box.createVerticalStrut(4)); 
        p.add(comboBox);
        return p;
    }

    /**
     * Populates the theme combo box including an option for "All Themes" represented by null.
     */
    public void fetchThemes() {
        themeComboBox.removeAllItems();
        themeComboBox.addItem((Theme) null); // Represents "All Themes" option
        for (Theme t : dm.getAllThemes()) {
            themeComboBox.addItem(t);
        }
        themeComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                if (value == null)
                    value = "Alle Themen";
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        if (themeComboBox.getItemCount() > 0)
            themeComboBox.setSelectedIndex(0);
    }

    /**
     * Populates the questions combo box based on the selected theme.
     * Supports aggregation when "All Themes" (null) is selected.
     */
    private void fetchQuestions() {
        questionComboBox.removeAllItems();
        Theme selectedTheme = (Theme) themeComboBox.getSelectedItem();
        if (selectedTheme == null) { // All themes selected
            List<Question> allQuestions = new ArrayList<>();
            for (Theme t : dm.getAllThemes()) {
                allQuestions.addAll(dm.findQuestionsByTheme(t));
            }
            questionComboBox.addItem("Alle Fragen");
            for (Question q : allQuestions) {
                questionComboBox.addItem(q);
            }
            if (questionComboBox.getItemCount() > 0)
                questionComboBox.setSelectedIndex(0);
        } else {
            questionComboBox.addItem("Alle Fragen");
            for (Question q : dm.findQuestionsByTheme(selectedTheme)) {
                questionComboBox.addItem(q);
            }
            if (questionComboBox.getItemCount() > 0)
                questionComboBox.setSelectedIndex(0);
        }
        fetchWeeks();
    }

    /**
     * Populates the weeks combo box based on gathered statistics.
     * Selects first available week or updates chart if none available.
     */
    private void fetchWeeks() {
        weekComboBox.removeAllItems();
        List<QuizStatistic> stats = gatherStats();
        weekComboBox.setEnabled(false);

        weekStatsMap = groupStatsByWeek(stats);

        for (String week : weekStatsMap.keySet()) {
            weekComboBox.addItem(week);
        }

        if (weekComboBox.getItemCount() > 0) {
            weekComboBox.setSelectedIndex(0);
            weekComboBox.setEnabled(true);
        } else {
            updateChart();
        }
    }

    /**
     * Gathers statistics based on current question and theme selection.
     * Returns all relevant QuizStatistic objects for the current filter.
     * @return List of QuizStatistic objects matching filters
     */
    private List<QuizStatistic> gatherStats() {
        Object selectedQ = questionComboBox.getSelectedItem();
        if (selectedQ == null) {
            return Collections.emptyList();
        }

        if ("Alle Fragen".equals(selectedQ)) {
            Theme selectedTheme = (Theme) themeComboBox.getSelectedItem();

            List<QuizStatistic> allStats = new ArrayList<>();

            if (selectedTheme == null) { // All themes
                for (Theme t : dm.getAllThemes()) {
                    for (Question q : dm.findQuestionsByTheme(t)) {
                        List<QuizStatistic> stats = dm.findStatisticsByQuestionId(q.getId());
                        allStats.addAll(stats);
                    }
                }
            } else {
                for (Question q : dm.findQuestionsByTheme(selectedTheme)) {
                    List<QuizStatistic> stats = dm.findStatisticsByQuestionId(q.getId());
                    allStats.addAll(stats);
                }
            }

            return allStats;
        } else if (selectedQ instanceof Question) {
            return dm.findStatisticsByQuestionId(((Question) selectedQ).getId());
        }

        return Collections.emptyList();
    }

    /**
     * Groups given statistics by year-week label.
     * @param stats List of QuizStatistic objects to group.
     * @return Map with keys in format "year-KWweekNumber" and values list of stats.
     */
    private Map<String, List<QuizStatistic>> groupStatsByWeek(List<QuizStatistic> stats) {
        Map<String, List<QuizStatistic>> grouped = new LinkedHashMap<>();
        WeekFields wf = WeekFields.of(Locale.getDefault());
        for (QuizStatistic stat : stats) {
            LocalDate d = stat.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int year = d.getYear();
            int week = d.get(wf.weekOfYear());
            String key = year + "-KW" + week;
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(stat);
        }
        return grouped;
    }

    /**
     * Updates the chart and accuracy label according to the current week selection.
     */
    private void updateChart() {
        String week = (String) weekComboBox.getSelectedItem();
        if (week == null) 
            return;

        List<QuizStatistic> stats = weekStatsMap.getOrDefault(week, Collections.emptyList());

        int correct = (int) stats.stream().filter(QuizStatistic::isCorrect).count();
        int total = stats.size();
        double accuracy = total > 0 ? 100.0 * correct / total : 0.0;

        accuracyLabel.setText(String.format("Genauigkeit: %.1f%% (%d/%d)", accuracy, correct, total));
        barChartPanel.repaint();
    }

    /**
     * Draws the stacked bar chart visualizing right/wrong answers per day in selected week.
     * @param g Graphics context.
     */
    private void drawChart(Graphics g) {
        String week = (String) weekComboBox.getSelectedItem();
        if (week == null)
            return;

        int year, weekNum;
        try {
            String[] parts = week.split("-KW");
            year = Integer.parseInt(parts[0]);
            weekNum = Integer.parseInt(parts[1]);
        } catch (Exception e) {
            return;
        }

        WeekFields wf = WeekFields.of(Locale.getDefault());
        LocalDate firstDay = LocalDate.of(year, 1, 4)
                                    .with(wf.weekOfYear(), weekNum)
                                    .with(wf.dayOfWeek(), 1);

        Map<LocalDate, int[]> dayCounts = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            dayCounts.put(firstDay.plusDays(i), new int[2]); // [rightCount, wrongCount]
        }

        List<QuizStatistic> stats = weekStatsMap.getOrDefault(week, Collections.emptyList());
        for (QuizStatistic stat : stats) {
            LocalDate d = stat.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int[] count = dayCounts.getOrDefault(d, new int[2]);
            if (stat.isCorrect())
                count[0]++;
            else
                count[1]++;
            dayCounts.put(d, count);
        }

        int width = barChartPanel.getWidth();
        int height = barChartPanel.getHeight();

        int marginLeft = 60, marginBottom = 35, marginTop = 18, marginRight = 30;
        int plotWidth = width - marginLeft - marginRight;
        int plotHeight = height - marginTop - marginBottom;

        int barCount = dayCounts.size();
        int barWidth = Math.max(14, plotWidth * 3 / 4 / Math.max(1, barCount));
        int gap = Math.max(7, plotWidth / 4 / Math.max(1, barCount + 1));

        int maxTotal = 1;
        for (int[] value : dayCounts.values()) {
            int sum = value[0] + value[1];
            if (sum > maxTotal)
                maxTotal = sum;
        }

        int x = marginLeft + gap;
        FontMetrics fm = g.getFontMetrics();
        Font font = g.getFont().deriveFont(Font.PLAIN, 14f);
        g.setFont(font);
        int yBase = height - marginBottom;

        for (Map.Entry<LocalDate, int[]> entry : dayCounts.entrySet()) {
            int right = entry.getValue()[0];
            int wrong = entry.getValue()[1];
            int total = right + wrong;
            if (total == 0) {
                x += barWidth + gap;
                continue;
            }

            int totalBarHeight = (int)(plotHeight * total / (double) maxTotal);
            int rightBarHeight = (int)(plotHeight * right / (double) maxTotal);

            // Draw correct (green) part
            g.setColor(new Color(34, 139, 34));
            g.fillRect(x, yBase - rightBarHeight, barWidth, rightBarHeight);
            g.setColor(Color.BLACK);
            g.drawRect(x, yBase - rightBarHeight, barWidth, rightBarHeight);
            if (right > 0)
                g.drawString("Right", x + 3, yBase - rightBarHeight + 15);

            // Draw incorrect (red) part stacked on top
            g.setColor(new Color(220, 20, 60));
            g.fillRect(x, yBase - totalBarHeight, barWidth, totalBarHeight - rightBarHeight);
            g.setColor(Color.BLACK);
            g.drawRect(x, yBase - totalBarHeight, barWidth, totalBarHeight - rightBarHeight);
            if (wrong > 0)
                g.drawString("Wrong", x + 3, yBase - totalBarHeight + 15);

            // Draw total number above bar
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(total), x + barWidth / 2 - 8, yBase - totalBarHeight - 3);

            // Draw short day label below bar
            String dayLabel = entry.getKey().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault());
            int labelWidth = fm.stringWidth(dayLabel);
            g.drawString(dayLabel, x + (barWidth - labelWidth) / 2, height - marginBottom + 15);

            x += barWidth + gap;
        }

        // Y axis label
        g.setFont(g.getFont().deriveFont(Font.BOLD, 14f));
        g.drawString("Number of questions", marginLeft - 50, 20);
        // X axis label
        g.drawString("Day", width / 2 - 20, height - 10);
    }
}
