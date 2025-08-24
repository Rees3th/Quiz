package gui.Statistic;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

/**
 * The {@code TrendChartPanel} class is a Swing JPanel component that displays a
 * line chart illustrating quiz accuracy over time.
 * 
 * <p>
 * This panel supports two display modes:
 * <ul>
 * <li>"daily" - accuracy per day</li>
 * <li>"weekly" - accuracy per calendar week</li>
 * </ul>
 * The mode controls the interpretation and labeling of the X-axis data.
 * </p>
 * 
 * <p>
 * Data points are plotted as red circles connected by blue lines. The Y-axis
 * runs from 0% to 100% accuracy, with grid lines and labels. The X-axis labels
 * reflect either dates or week labels depending on the mode.
 * </p>
 * 
 * Usage:
 * 
 * <pre>
 * TrendChartPanel panel = new TrendChartPanel();
 * panel.setTrendData(dataMap, "daily"); // or "weekly"
 * 
 * // Add panel to UI container
 * </pre>
 * 
 * @author Oleg Kapirulya
 */
public class TrendChartPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	/** Mapping of x-axis labels (dates or weeks) to accuracy percentages */
	private Map<String, Double> chartData = new LinkedHashMap<>();

	/** Current display mode: "daily" or "weekly" */
	private String chartMode = "daily";

	/**
	 * Sets the data and display mode for the trend chart.
	 * 
	 * @param data the map of string labels (days or weeks) to accuracy values
	 *             (percentage)
	 * @param mode the mode of display, must be "daily" or "weekly"
	 */
	public void setTrendData(Map<String, Double> data, String mode) {
		if (data == null) {
			this.chartData = Collections.emptyMap();
		} else {
			this.chartData = data;
		}
		if (mode != null && (mode.equals("daily") || mode.equals("weekly"))) {
			this.chartMode = mode;
		}
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (chartData == null || chartData.isEmpty())
			return;

		int width = getWidth();
		int height = getHeight();

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int padding = 50; // padding around chart area
		int labelPadding = 30; // space for y-axis labels

		double maxAccuracy = 100.0;
		double minAccuracy = 0.0;
		int pointCount = chartData.size();
		if (pointCount < 2)
			return;
		int availableWidth = width - 2 * padding - labelPadding;
		int stepX = pointCount > 1 ? availableWidth / (pointCount - 1) : availableWidth;

		List<String> xLabels = new ArrayList<>(chartData.keySet());
		List<Double> accuracies = new ArrayList<>(chartData.values());

		// Draw Y axis line
		g2.setColor(Color.BLACK);
		g2.drawLine(padding + labelPadding, padding, padding + labelPadding, height - padding);
		// Draw X axis line
		g2.drawLine(padding + labelPadding, height - padding, width - padding, height - padding);

		// Draw Y axis labels and horizontal grid lines
		for (int i = 0; i <= 5; i++) {
			int y = height - padding - (int) ((height - 2 * padding) * i / 5.0);
			int labelValue = (int) (minAccuracy + (maxAccuracy - minAccuracy) * i / 5);
			g2.setColor(Color.BLACK);
			g2.drawString(labelValue + "%", padding + 5, y + 5);

			g2.setColor(new Color(200, 200, 200)); // light gray for grid
			g2.drawLine(padding + labelPadding, y, width - padding, y);
		}

		// Draw data points and lines connecting them
		int prevX = padding + labelPadding;
		int prevY = height - padding
				- (int) ((accuracies.get(0) - minAccuracy) / (maxAccuracy - minAccuracy) * (height - 2 * padding));
		for (int i = 0; i < pointCount; i++) {
			int x = padding + labelPadding + i * stepX;
			int y = height - padding
					- (int) ((accuracies.get(i) - minAccuracy) / (maxAccuracy - minAccuracy) * (height - 2 * padding));
			if (i > 0) {
				g2.setColor(Color.BLUE);
				g2.drawLine(prevX, prevY, x, y);
			}
			g2.setColor(Color.RED);
			g2.fillOval(x - 3, y - 3, 6, 6); // red dot
			g2.setColor(Color.BLACK);

			// Draw X axis label (date or week string) centered below the dot
			String label = xLabels.get(i);
			int strWidth = g2.getFontMetrics().stringWidth(label);
			g2.drawString(label, x - strWidth / 2, height - padding + 20);

			prevX = x;
			prevY = y;
		}

		// Draw chart title and axis labels
		g2.setFont(new Font("SansSerif", Font.BOLD, 14));
		g2.setColor(Color.BLACK);
		g2.drawString("Accuracy " + (chartMode.equals("weekly") ? "per Week" : "per Day"), padding, padding - 25);
		g2.drawString(chartMode.equals("weekly") ? "Week" : "Day", width / 2, height - 5);

		// Draw rotated Y-axis label "Accuracy (%)"
		Graphics2D g2d = (Graphics2D) g2.create();
		g2d.rotate(-Math.PI / 2);
		g2d.drawString("Accuracy (%)", -height / 2, padding - 10);
		g2d.dispose();
	}
}
