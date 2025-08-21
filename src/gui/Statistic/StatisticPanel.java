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
 * StatisticPanel is a Swing component that presents detailed quiz statistics.
 * It allows users to select a quiz theme, a specific question (or all
 * questions), and a calendar week to view the performance.
 * 
 * The panel features:
 * <ul>
 * <li>Dropdown selections for Themes, Questions, and Weeks.</li>
 * <li>A graphical bar chart illustrating correct and incorrect answers per day
 * within the chosen week.</li>
 * <li>A textual display of overall accuracy for the selected week.</li>
 * </ul>
 * 
 * Functionality:
 * <ul>
 * <li>Supports selection of all themes ("All Themes") and all questions ("All
 * Questions").</li>
 * <li>Aggregates and fetches related statistics from the provided database
 * manager.</li>
 * <li>Dynamically updates available questions and weeks based on current
 * selections.</li>
 * </ul>
 * 
 * The visual bar chart differentiates correct (green) and incorrect (red)
 * answers stacked per day. Accuracy calculation takes into account all answered
 * questions within the time frame.
 * 
 * Dependencies: Requires an instance of {@link DBManager} (or
 * {@link DBManager}) to fetch quiz data.
 * 
 * Example usage:
 * 
 * <pre>
 * StatisticPanel panel = new StatisticPanel(dbManager);
 * // Add panel to a JFrame or other container
 * </pre>
 * 
 * This class implements data-driven UI updates to reflect the user's choices
 * within the quiz application.
 * 
 * @author Oleg Kapirulya
 */
public class StatisticPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/** Dropdown selector for quiz themes */
	private JComboBox<Theme> themeComboBox;

	/** Dropdown selector for quiz questions (includes 'All Questions' option) */
	private JComboBox<Object> questionComboBox;

	/** Dropdown selector for calendar weeks corresponding to quiz statistics */
	private JComboBox<String> weekComboBox;

	/** Label displaying computed accuracy for selected filters */
	private JLabel accuracyLabel;

	/**
	 * Database manager interface to access quiz themes, questions, and statistics
	 */
	private final DBDataManager dm;

	/** Panel responsible for rendering the bar chart */
	private JPanel barChartPanel;

	/** Map holding grouped quiz statistics keyed by week (e.g., "2025-34") */
	private Map<String, List<QuizStatistic>> weekStatsMap = new LinkedHashMap<>();

	/**
	 * Constructs the StatisticPanel. Initializes UI components, lays out the
	 * selectors, chart, and label. Registers event listeners for updating dependent
	 * selections and visualization.
	 * 
	 * @param dm an instance of database manager for querying themes, questions, and
	 *           stats
	 */
	public StatisticPanel(DBDataManager dm) {
		this.dm = dm;
		setLayout(new BorderLayout(8, 8));

		// Create selection controls panel (themes, questions, weeks)
		JPanel controlsPanel = new JPanel();
		controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.X_AXIS));
		controlsPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		controlsPanel.add(createControlPanel("Select Theme", themeComboBox = new JComboBox<>()));
		controlsPanel.add(Box.createHorizontalStrut(20));
		controlsPanel.add(createControlPanel("Select Question", questionComboBox = new JComboBox<>()));
		controlsPanel.add(Box.createHorizontalStrut(20));
		controlsPanel.add(createControlPanel("Select Week", weekComboBox = new JComboBox<>()));

		add(controlsPanel, BorderLayout.NORTH);

		// Chart panel for showing answer results
		barChartPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				drawChart(g);
			}
		};
		barChartPanel.setPreferredSize(new Dimension(700, 300));
		add(barChartPanel, BorderLayout.CENTER);

		// Label for displaying current accuracy metric
		accuracyLabel = new JLabel("Accuracy: 0%", SwingConstants.CENTER);
		accuracyLabel.setFont(accuracyLabel.getFont().deriveFont(Font.BOLD, 16f));
		add(accuracyLabel, BorderLayout.SOUTH);

		// Set fixed sizes for dropdown selectors
		themeComboBox.setPreferredSize(new Dimension(200, 24));
		questionComboBox.setPreferredSize(new Dimension(200, 24));
		weekComboBox.setPreferredSize(new Dimension(150, 24));

		// Register selection change listeners to update dependent controls and
		// visualization
		themeComboBox.addActionListener(e -> fetchQuestions());
		questionComboBox.addActionListener(e -> fetchWeeks());
		weekComboBox.addActionListener(e -> updateChart());

		// Initial population of themes dropdown
		fetchThemes();
	}

	/**
	 * Helper method to create a vertical panel with a label and a combo box.
	 * 
	 * @param labelText text shown above the combo box
	 * @param comboBox  the combo box to be placed below the label
	 * @return JPanel containing label and combo box, layouted vertically
	 */
	private JPanel createControlPanel(String labelText, JComboBox<?> comboBox) {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		JLabel label = new JLabel(labelText);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		comboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		p.add(label);
		p.add(Box.createVerticalStrut(4)); // small spacing between label and combo box
		p.add(comboBox);
		return p;
	}

	/**
	 * Retrieves quiz statistics relevant to current theme and question selection.
	 * If 'All Questions' is selected, aggregates data either from selected theme or
	 * all themes.
	 * 
	 * @return List of QuizStatistic matching the current selection
	 */
	public List<QuizStatistic> collectStatisticsForTrend() {
		Object selectedQuestion = questionComboBox.getSelectedItem();
		Theme selectedTheme = (Theme) themeComboBox.getSelectedItem();

		if (selectedQuestion == null)
			return Collections.emptyList();

		if (selectedQuestion instanceof String && "All Questions".equals(selectedQuestion)) {
			List<Question> allQuestions = new ArrayList<>();
			if (selectedTheme == null) { // All themes selected
				for (Theme theme : dm.getAllThemes()) {
					allQuestions.addAll(dm.findQuestionsByTheme(theme));
				}
			} else {
				allQuestions.addAll(dm.findQuestionsByTheme(selectedTheme));
			}
			List<QuizStatistic> allStats = new ArrayList<>();
			for (Question question : allQuestions) {
				allStats.addAll(dm.findStatisticsByQuestionId(question.getId()));
			}
			return allStats;
		} else if (selectedQuestion instanceof Question) {
			return dm.findStatisticsByQuestionId(((Question) selectedQuestion).getId());
		}
		return Collections.emptyList();
	}

	/**
	 * Calculates accuracy per week from a list of quiz statistics.
	 * 
	 * @param stats List of quiz statistics to calculate accuracy on.
	 * @return Map of week label (e.g. '2025-34') to accuracy value (0-100).
	 */
	public Map<String, Double> calculateWeeklyAccuracy(List<QuizStatistic> stats) {
		Map<String, List<QuizStatistic>> grouped = new LinkedHashMap<>();
		WeekFields wf = WeekFields.of(Locale.getDefault());
		for (QuizStatistic stat : stats) {
			LocalDate date = stat.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			int year = date.getYear();
			int week = date.get(wf.weekOfYear());
			String label = year + "-KW" + week;
			grouped.computeIfAbsent(label, k -> new ArrayList<>()).add(stat);
		}
		Map<String, Double> accuracy = new LinkedHashMap<>();
		for (var entry : grouped.entrySet()) {
			List<QuizStatistic> list = entry.getValue();
			long correctCount = list.stream().filter(QuizStatistic::isCorrect).count();
			accuracy.put(entry.getKey(), list.isEmpty() ? 0.0 : (100.0 * correctCount / list.size()));
		}
		return accuracy;
	}

	/**
	 * Aggregates accuracy percentages on the theme level.
	 * 
	 * @param themes List of themes to calculate accuracy for.
	 * @param dm     Database manager to fetch questions and statistics.
	 * @return Map of theme title to accuracy percentage.
	 */
	public Map<String, Double> calculateThemeAccuracy(List<Theme> themes, DBDataManager dm) {
		Map<String, Double> themeAccuracy = new LinkedHashMap<>();
		for (Theme theme : themes) {
			int total = 0;
			int correct = 0;
			for (Question q : dm.findQuestionsByTheme(theme)) {
				List<QuizStatistic> stats = dm.findStatisticsByQuestionId(q.getId());
				total += stats.size();
				correct += stats.stream().filter(QuizStatistic::isCorrect).count();
			}
			double accuracyVal = (total == 0) ? 0.0 : (100.0 * correct / total);
			themeAccuracy.put(theme.getTitle(), accuracyVal);
		}
		return themeAccuracy;
	}

	/**
	 * Populates the theme combo box including 'All Themes' represented as null. The
	 * combo box renderer displays 'All Themes' label accordingly.
	 */
	public void fetchThemes() {
		themeComboBox.removeAllItems();
		themeComboBox.addItem(null); // 'All Themes' placeholder
		for (Theme theme : dm.getAllThemes()) {
			themeComboBox.addItem(theme);
		}
		themeComboBox.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				if (value == null) {
					value = "All Themes";
				}
				return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			}
		});
		if (themeComboBox.getItemCount() > 0) {
			themeComboBox.setSelectedIndex(0);
		}
	}

	/**
	 * Populates the question combo box based on selected theme. If 'All Themes' is
	 * selected, aggregates questions from all themes. Includes 'All Questions' as
	 * first entry.
	 */
	private void fetchQuestions() {
		questionComboBox.removeAllItems();
		Theme selectedTheme = (Theme) themeComboBox.getSelectedItem();
		List<Question> questions = new ArrayList<>();

		if (selectedTheme == null) { // All themes selected
			for (Theme theme : dm.getAllThemes()) {
				questions.addAll(dm.findQuestionsByTheme(theme));
			}
		} else {
			questions.addAll(dm.findQuestionsByTheme(selectedTheme));
		}

		questionComboBox.addItem("All Questions");
		for (Question question : questions) {
			questionComboBox.addItem(question);
		}
		if (questionComboBox.getItemCount() > 0) {
			questionComboBox.setSelectedIndex(0);
		}

		fetchWeeks(); // Update weeks based on new question selection
	}

	/**
	 * Populates the week combo box for currently selected theme/question by
	 * grouping available statistics. Selects first week if available and enables
	 * the combo box. Otherwise disables it and triggers a chart update with empty
	 * data.
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
	 * Retrieves quiz statistics matching the current theme and question filters.
	 * Supports 'All Questions' and 'All Themes' aggregations.
	 *
	 * @return List of matching QuizStatistic instances; empty if no match or
	 *         selection invalid.
	 */
	private List<QuizStatistic> gatherStats() {
		Object selectedQuestion = questionComboBox.getSelectedItem();

		if (selectedQuestion == null)
			return Collections.emptyList();

		if ("All Questions".equals(selectedQuestion)) {
			Theme selectedTheme = (Theme) themeComboBox.getSelectedItem();
			List<QuizStatistic> allStats = new ArrayList<>();

			if (selectedTheme == null) { // All themes selected
				for (Theme theme : dm.getAllThemes()) {
					for (Question question : dm.findQuestionsByTheme(theme)) {
						allStats.addAll(dm.findStatisticsByQuestionId(question.getId()));
					}
				}
			} else {
				for (Question question : dm.findQuestionsByTheme(selectedTheme)) {
					allStats.addAll(dm.findStatisticsByQuestionId(question.getId()));
				}
			}
			return allStats;
		} else if (selectedQuestion instanceof Question) {
			return dm.findStatisticsByQuestionId(((Question) selectedQuestion).getId());
		}

		return Collections.emptyList();
	}

	/**
	 * Groups the provided list of QuizStatistic objects by calendar week label.
	 *
	 * @param stats list of quiz statistics to group
	 * @return map of week label (e.g. '2025-34') to list of statistics for that
	 *         week
	 */
	private Map<String, List<QuizStatistic>> groupStatsByWeek(List<QuizStatistic> stats) {
		Map<String, List<QuizStatistic>> grouped = new LinkedHashMap<>();
		WeekFields weekFields = WeekFields.of(Locale.getDefault());

		for (QuizStatistic stat : stats) {
			LocalDate date = stat.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			int year = date.getYear();
			int week = date.get(weekFields.weekOfYear());
			String key = year + "-KW" + week;
			grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(stat);
		}
		return grouped;
	}

	/**
	 * Updates the accuracy label and triggers repaints of the bar chart when the
	 * selected week changes.
	 */
	private void updateChart() {
		String selectedWeek = (String) weekComboBox.getSelectedItem();
		if (selectedWeek == null)
			return;

		List<QuizStatistic> stats = weekStatsMap.getOrDefault(selectedWeek, Collections.emptyList());
		int total = stats.size();
		int correct = (int) stats.stream().filter(QuizStatistic::isCorrect).count();
		double accuracy = (total > 0) ? 100.0 * correct / total : 0.0;

		accuracyLabel.setText(String.format("Accuracy: %.1f%% (%d/%d)", accuracy, correct, total));
		barChartPanel.repaint();
	}

	/**
	 * Paints a stacked bar chart on the barChartPanel showing correct and incorrect
	 * answers per day of the selected week. Correct answers are shown in green,
	 * incorrect in red.
	 *
	 * @param g Graphics context for drawing
	 */
	private void drawChart(Graphics g) {
		String selectedWeek = (String) weekComboBox.getSelectedItem();

		if (selectedWeek == null)
			return;

		int year, weekNumber;
		try {
			String[] parts = selectedWeek.split("-KW");
			year = Integer.parseInt(parts[0]);
			weekNumber = Integer.parseInt(parts[1]);
		} catch (Exception ex) {
			return; // unable to parse week info
		}

		WeekFields weekFields = WeekFields.of(Locale.getDefault());
		LocalDate firstDayOfWeek = LocalDate.of(year, 1, 4).with(weekFields.weekOfYear(), weekNumber)
				.with(weekFields.dayOfWeek(), 1);

		Map<LocalDate, int[]> dayCounts = new LinkedHashMap<>();
		for (int i = 0; i < 7; i++) {
			dayCounts.put(firstDayOfWeek.plusDays(i), new int[2]); // [correct, incorrect]
		}

		// count correct/wrong per day
		List<QuizStatistic> stats = weekStatsMap.getOrDefault(selectedWeek, Collections.emptyList());
		for (QuizStatistic stat : stats) {
			LocalDate date = stat.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			int[] counts = dayCounts.getOrDefault(date, new int[2]);
			if (stat.isCorrect())
				counts[0]++;
			else
				counts[1]++;
			dayCounts.put(date, counts);
		}

		int width = barChartPanel.getWidth();
		int height = barChartPanel.getHeight();

		int marginLeft = 60;
		int marginBottom = 35;
		int marginTop = 18;
		int marginRight = 30;

		int plotWidth = width - marginLeft - marginRight;
		int plotHeight = height - marginTop - marginBottom;

		int dayCount = dayCounts.size();

		int barWidth = Math.max(14, plotWidth * 3 / 4 / Math.max(1, dayCount));
		int gap = Math.max(7, plotWidth / 4 / Math.max(1, dayCount + 1));

		int maxCount = 1;
		for (int[] counts : dayCounts.values()) {
			maxCount = Math.max(maxCount, counts[0] + counts[1]);
		}

		int xPos = marginLeft + gap;
		FontMetrics fm = g.getFontMetrics();
		g.setFont(g.getFont().deriveFont(Font.PLAIN, 14f));

		int yBase = height - marginBottom;

		// draw bars and labels
		for (Map.Entry<LocalDate, int[]> entry : dayCounts.entrySet()) {
			int correct = entry.getValue()[0];
			int wrong = entry.getValue()[1];
			int total = correct + wrong;

			if (total == 0) {
				xPos += barWidth + gap;
				continue;
			}

			int totalBarHeight = (int) (plotHeight * total / (double) maxCount);
			int correctBarHeight = (int) (plotHeight * correct / (double) maxCount);

			// correct answers in green
			g.setColor(new Color(34, 139, 34));
			g.fillRect(xPos, yBase - correctBarHeight, barWidth, correctBarHeight);
			g.setColor(Color.BLACK);
			g.drawRect(xPos, yBase - correctBarHeight, barWidth, correctBarHeight);
			if (correct > 0)
				g.drawString("✓", xPos + 3, yBase - correctBarHeight + 15);

			// wrong answers in red stacked above correct
			g.setColor(new Color(220, 20, 60));
			g.fillRect(xPos, yBase - totalBarHeight, barWidth, totalBarHeight - correctBarHeight);
			g.setColor(Color.BLACK);
			g.drawRect(xPos, yBase - totalBarHeight, barWidth, totalBarHeight - correctBarHeight);
			if (wrong > 0)
				g.drawString("X", xPos + 3, yBase - totalBarHeight + 15);

			// total count label above bar
			g.setColor(Color.BLACK);
			g.drawString(String.valueOf(total), xPos + barWidth / 2 - 8, yBase - totalBarHeight - 3);

			// day label below bar
			String dayLabel = entry.getKey().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault());
			int dayLabelWidth = fm.stringWidth(dayLabel);
			g.drawString(dayLabel, xPos + (barWidth - dayLabelWidth) / 2, height - marginBottom);

			xPos += barWidth + gap;
		}

		// draw axis labels
		g.setFont(g.getFont().deriveFont(Font.BOLD, 14f));
		g.drawString("Number of questions", marginLeft - 50, 20);
		g.drawString("Day", width / 2 - 20, height - 10);
	}
}
