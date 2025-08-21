package gui.Statistic;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import persistence.DBDataManager;
import quizLogic.QuizStatistic;
import quizLogic.Theme;

/**
 * StatisticsContainerPanel manages three different statistic views for the quiz
 * app: - Basic statistics with detailed per-week, per-question charts - Trend
 * analysis showing accuracy trends over calendar weeks - Theme accuracy showing
 * accuracy summarized by quiz theme in a table
 * 
 * This panel provides buttons to switch between these views and handles their
 * data updating accordingly. It acts as a high-level container coordinating the
 * subpanels.
 * 
 * Usage: - Construct with a DBDataManager instance - Call {@link #refresh()} to
 * trigger data reloading
 * 
 * The subpanels are: - {@link StatisticPanel} for basic detailed stats, -
 * {@link TrendChartPanel} for weekly trend chart, - {@link ThemeAccuracyPanel}
 * for theme accuracy table.
 * 
 * Buttons allow the user to switch visible panel and keep data updated.
 * 
 * All data fetching / calculations are delegated to the StatisticPanel.
 * 
 * @author Oleg Kapirulya
 */
public class StatisticsContainerPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final StatisticPanel basicStatisticPanel;
	private final TrendChartPanel trendChartPanel;
	private final ThemeAccuracyPanel themeAccuracyPanel;
	private final DBDataManager dm;

	/**
	 * Constructs the container panel with subpanels and navigation buttons.
	 * 
	 * @param dm the DBDataManager instance for data access
	 */
	public StatisticsContainerPanel(DBDataManager dm) {
		super(new BorderLayout());
		this.dm = dm;

		// Create subpanels
		basicStatisticPanel = new StatisticPanel(dm);
		trendChartPanel = new TrendChartPanel();
		themeAccuracyPanel = new ThemeAccuracyPanel();

		// Create buttons panel
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton btnBasic = new JButton("Grundstatistik");
		JButton btnTrend = new JButton("Trend Analyse");
		JButton btnThemeAcc = new JButton("Genauigkeit je Thema");

		buttonPanel.add(btnBasic);
		buttonPanel.add(btnTrend);
		buttonPanel.add(btnThemeAcc);

		add(buttonPanel, BorderLayout.NORTH);
		add(basicStatisticPanel, BorderLayout.CENTER);

		// Button action listeners to toggle views and update data accordingly
		btnBasic.addActionListener(e -> switchPanel(basicStatisticPanel));

		btnTrend.addActionListener(e -> {
			switchPanel(trendChartPanel);
			updateTrendPanel();
		});

		btnThemeAcc.addActionListener(e -> {
			switchPanel(themeAccuracyPanel);
			updateThemeAccuracyPanel();
		});
	}

	/**
	 * Switches the visible central panel to the specified one.
	 * 
	 * @param panelToShow the panel to display in the center
	 */
	private void switchPanel(JPanel panelToShow) {
		if (getComponentCount() > 1) {
			remove(1); // remove current center panel (index 1 after buttons)
		}
		add(panelToShow, BorderLayout.CENTER);
		revalidate();
		repaint();
	}

	/**
	 * Updates the trend chart panel data based on current basic statistics
	 * selection.
	 */
	private void updateTrendPanel() {
		List<QuizStatistic> stats = basicStatisticPanel.collectStatisticsForTrend();
		var weeklyAccuracy = basicStatisticPanel.calculateWeeklyAccuracy(stats);
		trendChartPanel.setWeeklyAccuracyData(weeklyAccuracy);
	}

	/**
	 * Updates the theme accuracy table using all themes from the DB and the basic
	 * statistics calculations.
	 */
	private void updateThemeAccuracyPanel() {
		List<Theme> themes = dm.findAllThemes();
		var themeAcc = basicStatisticPanel.calculateThemeAccuracy(themes, dm);
		themeAccuracyPanel.setThemeAccuracyData(themeAcc);
	}

	/**
	 * Refreshes data by delegating to the basic statistics panel. Call this after
	 * data changes or on tab focus to reload combo boxes.
	 */
	public void refresh() {
		basicStatisticPanel.fetchThemes();
	}
}
