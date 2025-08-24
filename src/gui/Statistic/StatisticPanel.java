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
import java.time.format.DateTimeFormatter;
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
 * The {@code StatisticPanel} class is a Swing JPanel that provides detailed
 * quiz statistics with interactive filters and visual representation.
 * 
 * <p>
 * Key features:
 * </p>
 * <ul>
 * <li>Selection of Theme, Question, and Calendar Week via combo boxes.</li>
 * <li>Aggregation and retrieval of quiz statistics from the
 * {@link DBDataManager}.</li>
 * <li>Bar chart visualization displaying the number of correct and incorrect
 * answers per day for the selected week.</li>
 * <li>Dynamic calculation and display of accuracy percentage for the selected
 * filters and week.</li>
 * </ul>
 * 
 * <p>
 * The bar chart differentiates correct answers (green bars) and incorrect
 * answers (red bars) stacked vertically for each day of the selected week. The
 * component is locale-aware and handles week calculations accordingly.
 * </p>
 * 
 * Usage example:
 * 
 * <pre>
 * DBDataManager dm = ...;
 * StatisticPanel panel = new StatisticPanel(dm);
 * // add panel to JFrame or other container
 * </pre>
 * 
 * Dependencies:
 * <ul>
 * <li>Persistence layer interface {@link DBDataManager} for queries.</li>
 * <li>Domain classes {@link Theme}, {@link Question}, and
 * {@link QuizStatistic}.</li>
 * </ul>
 * 
 * @author Oleg Kapirulya
 */
public class StatisticPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/** ComboBox for selecting the quiz theme; null means 'All Themes' */
	private JComboBox<Theme> themeComboBox;

	/** ComboBox for selecting the question; includes 'All Questions' option */
	private JComboBox<Object> questionComboBox;

	/** ComboBox for selecting a calendar week (format: yyyy-KWww) */
	private JComboBox<String> weekComboBox;

	/** Label to display the accuracy percentage and counts */
	private JLabel accuracyLabel;

	/** Database manager to fetch data */
	private final DBDataManager dm;

	/** Panel rendering the daily bar chart for the selected week */
	private JPanel barChartPanel;

	/** Map from week label (e.g. "2025-KW34") to QuizStatistic list of that week */
	private Map<String, List<QuizStatistic>> weekStatsMap = new LinkedHashMap<>();

	/**
	 * Creates a StatisticPanel initialized with the provided database manager. Sets
	 * up UI components, layouts, and event listeners.
	 * 
	 * @param dm the database manager for fetching quiz data
	 */
	public StatisticPanel(DBDataManager dm) {
		this.dm = dm;
		setLayout(new BorderLayout(8, 8));

		// Panel of combo boxes for selection filters
		JPanel controlsPanel = new JPanel();
		controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.X_AXIS));
		controlsPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		controlsPanel.add(createControlPanel("Select Theme", themeComboBox = new JComboBox<>()));
		controlsPanel.add(Box.createHorizontalStrut(20));
		controlsPanel.add(createControlPanel("Select Question", questionComboBox = new JComboBox<>()));
		controlsPanel.add(Box.createHorizontalStrut(20));
		controlsPanel.add(createControlPanel("Select Week", weekComboBox = new JComboBox<>()));

		add(controlsPanel, BorderLayout.NORTH);

		// Chart panel that paints the bar chart
		barChartPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				drawChart(g);
			}
		};
		barChartPanel.setPreferredSize(new Dimension(700, 300));
		add(barChartPanel, BorderLayout.CENTER);

		// Accuracy label under the chart
		accuracyLabel = new JLabel("Accuracy: 0%", SwingConstants.CENTER);
		accuracyLabel.setFont(accuracyLabel.getFont().deriveFont(Font.BOLD, 16f));
		add(accuracyLabel, BorderLayout.SOUTH);

		// Set fixed sizes for combo boxes
		themeComboBox.setPreferredSize(new Dimension(200, 24));
		questionComboBox.setPreferredSize(new Dimension(200, 24));
		weekComboBox.setPreferredSize(new Dimension(150, 24));

		// Event listeners to update dependent controls and chart
		themeComboBox.addActionListener(e -> fetchQuestions());
		questionComboBox.addActionListener(e -> fetchWeeks());
		weekComboBox.addActionListener(e -> updateChart());

		fetchThemes(); // load initial themes
	}

	/**
	 * Creates a vertical panel containing a label and a combo box, used for
	 * filters.
	 * 
	 * @param labelText text for the label above combo box
	 * @param comboBox  the combo box component
	 * @return JPanel containing the label and combo box, vertically aligned
	 */
	private JPanel createControlPanel(String labelText, JComboBox<?> comboBox) {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		JLabel label = new JLabel(labelText);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		comboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		p.add(label);
		p.add(Box.createVerticalStrut(4)); // spacing between label and combo box
		p.add(comboBox);
		return p;
	}

	/**
	 * Collects quiz statistics filtered by the current selected theme and question.
	 * If 'All Questions' is selected, aggregates statistics for all questions in
	 * selected or all themes.
	 * 
	 * @return a list of {@link QuizStatistic} matching the filters
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
	 * Calculates accuracy percentages grouped by calendar week in format yyyy-KWww.
	 * 
	 * @param stats list of quiz statistics to aggregate
	 * @return a map from week label (e.g. "2025-KW34") to accuracy percentage
	 *         (0-100)
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
	 * Calculates accuracy percentages grouped by calendar day (format yyyy-MM-dd).
	 * 
	 * @param stats list of quiz statistics to aggregate
	 * @return a map from date string to accuracy percentage (0-100)
	 */
	public Map<String, Double> calculateDailyAccuracy(List<QuizStatistic> stats) {
		Map<String, List<QuizStatistic>> grouped = new LinkedHashMap<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		for (QuizStatistic stat : stats) {
			LocalDate date = stat.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			String label = date.format(formatter);
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
	 * Calculates accuracy for each quiz theme aggregated over all questions.
	 * 
	 * @param themes list of quiz themes
	 * @param dm     database manager to fetch questions and statistics
	 * @return map from theme title to accuracy (0-100)
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
	 * Populates and initializes the theme combo box, including an 'All Themes'
	 * option represented by null. The combo box renderer displays 'All Themes' for
	 * null values.
	 */
	public void fetchThemes() {
		themeComboBox.removeAllItems();
		themeComboBox.addItem(null); // All themes option
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
	 * Populates the question combo box based on the selected theme. Includes an
	 * 'All Questions' option and updates week selections accordingly.
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

		fetchWeeks();
	}

	/**
	 * Fetches and populates the weekComboBox with calendar weeks derived from the
	 * filtered QuizStatistic data. Enables the week selector if there are weeks
	 * available.
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
	 * Gathers all quiz statistics based on the current filter selections of theme
	 * and question.
	 * 
	 * @return List of QuizStatistic matching the current filter or empty list
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
	 * Groups the provided statistics list by calendar week label in the format
	 * yyyy-KWww.
	 * 
	 * @param stats list of quiz statistics to group
	 * @return Map keyed by week string to list of statistics in that week
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
	 * Updates the accuracy label and repaints the bar chart for the selected week.
	 * Calculates total questions and correct answers count.
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
	 * Draws a stacked bar chart by day with counts of correct (green) and wrong
	 * (red) answers. The x-axis shows the days of the selected calendar week and
	 * the y-axis shows number of questions.
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
			return; // Unable to parse week string, exit drawing
		}

		WeekFields weekFields = WeekFields.of(Locale.getDefault());
		LocalDate firstDayOfWeek = LocalDate.of(year, 1, 4).with(weekFields.weekOfYear(), weekNumber)
				.with(weekFields.dayOfWeek(), 1); // get first day (Monday) of the week

		// Initialize map of days to counts: [correct, wrong]
		Map<LocalDate, int[]> dayCounts = new LinkedHashMap<>();
		for (int i = 0; i < 7; i++) {
			dayCounts.put(firstDayOfWeek.plusDays(i), new int[2]);
		}

		// Tally statistics for each day
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

			// Draw correct answers bar (green)
			g.setColor(new Color(34, 139, 34)); // forest green
			g.fillRect(xPos, yBase - correctBarHeight, barWidth, correctBarHeight);
			g.setColor(Color.BLACK);
			g.drawRect(xPos, yBase - correctBarHeight, barWidth, correctBarHeight);
			if (correct > 0)
				g.drawString("✓", xPos + 3, yBase - correctBarHeight + 15);

			// Draw wrong answers bar (red) stacked on top
			g.setColor(new Color(220, 20, 60)); // crimson red
			g.fillRect(xPos, yBase - totalBarHeight, barWidth, totalBarHeight - correctBarHeight);
			g.setColor(Color.BLACK);
			g.drawRect(xPos, yBase - totalBarHeight, barWidth, totalBarHeight - correctBarHeight);
			if (wrong > 0)
				g.drawString("X", xPos + 3, yBase - totalBarHeight + 15);

			// Draw total count above bar
			g.setColor(Color.BLACK);
			g.drawString(String.valueOf(total), xPos + barWidth / 2 - 8, yBase - totalBarHeight - 3);

			// Draw day label below bar (localized short day name)
			String dayLabel = entry.getKey().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault());
			int dayLabelWidth = fm.stringWidth(dayLabel);
			g.drawString(dayLabel, xPos + (barWidth - dayLabelWidth) / 2, height - marginBottom);

			xPos += barWidth + gap;
		}

		// Draw axis labels
		g.setFont(g.getFont().deriveFont(Font.BOLD, 14f));
		g.drawString("Number of questions", marginLeft - 50, 20);
		g.drawString("Day", width / 2 - 20, height - 10);
	}
}
