package gui.Statistic;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import persistence.DBDataManager;
import quizLogic.QuizStatistic;
import quizLogic.Theme;

/**
 * The {@code StatisticsContainerPanel} class represents the main container
 * panel for displaying different quiz statistics views in a Swing UI
 * application.
 * 
 * It contains three main subpanels:
 * <ul>
 * <li>{@link StatisticPanel} for basic quiz statistics and detailed
 * selection</li>
 * <li>{@link TrendChartPanel} for displaying the quiz accuracy trend over
 * time</li>
 * <li>{@link ThemeAccuracyPanel} for showing accuracy summarized by quiz
 * theme</li>
 * </ul>
 * 
 * The panel also provides a toolbar with buttons to switch between these views
 * and a combo box to select the trend display mode (daily or weekly) when the
 * trend chart is visible.
 * 
 * This class manages updating of the displayed data and forwarding user
 * selections to the respective subpanels.
 * 
 * It relies on a {@link DBDataManager} interface to fetch quiz data from the
 * database.
 * 
 * Usage example:
 * 
 * <pre>
 * DBDataManager dm = ...; // Obtain database manager instance
 * StatisticsContainerPanel panel = new StatisticsContainerPanel(dm);
 * // Add panel to JFrame or other container
 * </pre>
 * 
 * @author Oleg Kapirulya
 */
public class StatisticsContainerPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/** Panel showing basic detailed statistics with filtering */
	private final StatisticPanel basicStatisticPanel;

	/** Panel rendering trend charts (daily or weekly) */
	private final TrendChartPanel trendChartPanel;

	/** Panel showing accuracy per theme as a table or summary */
	private final ThemeAccuracyPanel themeAccuracyPanel;

	/** Database manager to access quiz data */
	private final DBDataManager dm;

	/** Combo box to select trend chart mode: "daily" or "weekly" */
	private JComboBox<String> trendModeSelector;

	/**
	 * Constructs the {@code StatisticsContainerPanel} with all subpanels and
	 * controls. Initializes buttons for navigation and the trend mode selector
	 * combo box.
	 * 
	 * @param dm the database manager instance used for fetching quiz data
	 */
	public StatisticsContainerPanel(DBDataManager dm) {
		super(new BorderLayout());
		this.dm = dm;

		// Initialize subpanels with the provided DBDataManager
		basicStatisticPanel = new StatisticPanel(dm);
		trendChartPanel = new TrendChartPanel();
		themeAccuracyPanel = new ThemeAccuracyPanel();

		// Create the button panel to switch between views
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton btnBasic = new JButton("Grundstatistik");
		JButton btnTrend = new JButton("Trend Analyse");
		JButton btnThemeAcc = new JButton("Genauigkeit je Thema");

		buttonPanel.add(btnBasic);
		buttonPanel.add(btnTrend);
		buttonPanel.add(btnThemeAcc);

		// Initialize the trend mode selector combo box, hidden by default
		trendModeSelector = new JComboBox<>(new String[] { "Täglicher Trend", "Wöchentlicher Trend" });
		trendModeSelector.setVisible(false); // Show only when trend chart is active
		buttonPanel.add(trendModeSelector);

		// Add components to container
		add(buttonPanel, BorderLayout.NORTH);
		add(basicStatisticPanel, BorderLayout.CENTER);

		// Action listener: Show basic statistics panel, hide trend mode selector
		btnBasic.addActionListener(e -> {
			switchPanel(basicStatisticPanel);
			trendModeSelector.setVisible(false);
		});

		// Action listener: Show trend chart panel, show trend mode selector, update
		// data
		btnTrend.addActionListener(e -> {
			switchPanel(trendChartPanel);
			trendModeSelector.setVisible(true);
			updateTrendPanel();
		});

		// Action listener: Show theme accuracy panel and hide trend mode selector
		btnThemeAcc.addActionListener(e -> {
			switchPanel(themeAccuracyPanel);
			trendModeSelector.setVisible(false);
			updateThemeAccuracyPanel();
		});

		// Update trend chart when user changes trend mode selection
		trendModeSelector.addActionListener(e -> updateTrendPanel());
	}

	/**
	 * Helper method to switch the currently displayed central panel. Removes the
	 * existing center component (if any) and adds the specified panel. Calls
	 * revalidate() and repaint() to refresh the container.
	 * 
	 * @param panelToShow the JPanel to display in the center region
	 */
	private void switchPanel(JPanel panelToShow) {
		if (getComponentCount() > 1) {
			remove(1);
		}
		add(panelToShow, BorderLayout.CENTER);
		revalidate();
		repaint();
	}

	/**
	 * Updates the trend chart panel according to current user selections and mode.
	 * It collects quiz statistics from the basic statistic panel and calculates
	 * accuracy data either daily or weekly.
	 * 
	 * The calculated accuracy data and mode string are then passed to the trend
	 * chart panel for rendering.
	 */
	private void updateTrendPanel() {
		List<QuizStatistic> stats = basicStatisticPanel.collectStatisticsForTrend();
		// Determine mode based on selector index: 0 = daily, 1 = weekly
		String mode = trendModeSelector.getSelectedIndex() == 1 ? "weekly" : "daily";
		Map<String, Double> data;
		if (mode.equals("weekly")) {
			data = basicStatisticPanel.calculateWeeklyAccuracy(stats);
		} else {
			data = basicStatisticPanel.calculateDailyAccuracy(stats);
		}
		trendChartPanel.setTrendData(data, mode);
	}

	/**
	 * Updates the theme accuracy panel by fetching all themes and calculating their
	 * respective accuracies via the basic statistic panel.
	 */
	private void updateThemeAccuracyPanel() {
		List<Theme> themes = dm.findAllThemes();
		Map<String, Double> themeAcc = basicStatisticPanel.calculateThemeAccuracy(themes, dm);
		themeAccuracyPanel.setThemeAccuracyData(themeAcc);
	}

	/**
	 * Refreshes the data in the basic statistic panel, primarily to reload combo
	 * boxes such as theme selection after data changes.
	 */
	public void refresh() {
		basicStatisticPanel.fetchThemes();
	}
}
