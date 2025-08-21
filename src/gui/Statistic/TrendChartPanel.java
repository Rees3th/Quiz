package gui.Statistic;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

/**
 * TrendChartPanel is a Swing component that visualizes the accuracy trends over
 * calendar weeks.
 * <p>
 * It draws a line chart plotting accuracy percentages for each calendar week,
 * with axes, data points, and connecting lines.
 * </p>
 * The accuracy data is supplied as a map from week labels (e.g., "2025-34") to
 * accuracy values (percentage). Use {@link #setWeeklyAccuracyData(Map)} to
 * provide the data.
 * 
 * @author Oleg Kapirulya
 */
public class TrendChartPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private Map<String, Double> weeklyAccuracy = new LinkedHashMap<>();

	/**
	 * Sets the weekly accuracy data to be displayed.
	 * 
	 * @param data Map of week labels to accuracy percentages (0 to 100).
	 */
	public void setWeeklyAccuracyData(Map<String, Double> data) {
		this.weeklyAccuracy = data;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (weeklyAccuracy == null || weeklyAccuracy.isEmpty()) {
			return; // nothing to draw
		}

		int width = getWidth();
		int height = getHeight();

		Graphics2D g2 = (Graphics2D) g;

		// Enable anti-aliasing for smooth graphics
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Padding around the chart area
		int padding = 50;
		int labelPadding = 30;

		double maxAccuracy = 100;
		double minAccuracy = 0;

		int pointCount = weeklyAccuracy.size();

		if (pointCount < 2) {
			// Not enough points to draw lines
			return;
		}

		int availableWidth = width - 2 * padding - labelPadding;
		int stepX = availableWidth / (pointCount - 1);

		List<String> weeks = new ArrayList<>(weeklyAccuracy.keySet());
		List<Double> accuracies = new ArrayList<>(weeklyAccuracy.values());

		// Draw Y axis
		g2.drawLine(padding + labelPadding, padding, padding + labelPadding, height - padding);
		// Draw X axis
		g2.drawLine(padding + labelPadding, height - padding, width - padding, height - padding);

		// Draw Y axis labels and grid lines
		for (int i = 0; i <= 5; i++) {
			int y = padding + (int) ((height - 2 * padding) * i / 5.0);
			int yLabelPos = height - y - padding; // invert y for coordinate system

			int labelValue = (int) (minAccuracy + (maxAccuracy - minAccuracy) * i / 5);

			g2.setColor(Color.GRAY);
			g2.drawLine(padding + labelPadding - 5, yLabelPos, padding + labelPadding + 5, yLabelPos);
			g2.setColor(Color.BLACK);
			g2.drawString(labelValue + "%", padding + 5, yLabelPos + 5);

			// Optional: horizontal grid lines
			g2.setColor(new Color(200, 200, 200));
			g2.drawLine(padding + labelPadding, yLabelPos, width - padding, yLabelPos);
		}

		// Draw connecting lines and dots for data points
		g2.setColor(Color.BLUE);
		int prevX = padding + labelPadding;
		int prevY = height - padding
				- (int) ((accuracies.get(0) - minAccuracy) / (maxAccuracy - minAccuracy) * (height - 2 * padding));

		for (int i = 0; i < pointCount; i++) {
			int x = padding + labelPadding + i * stepX;
			int y = height - padding
					- (int) ((accuracies.get(i) - minAccuracy) / (maxAccuracy - minAccuracy) * (height - 2 * padding));

			// Draw line connecting previous point to current
			if (i > 0) {
				g2.drawLine(prevX, prevY, x, y);
			}

			// Draw point
			g2.setColor(Color.RED);
			g2.fillOval(x - 4, y - 4, 8, 8);
			g2.setColor(Color.BLACK);
			g2.drawOval(x - 4, y - 4, 8, 8);

			prevX = x;
			prevY = y;

			// Draw X axis labels (week names)
			String weekLabel = weeks.get(i);
			g2.setColor(Color.BLACK);
			int strWidth = g2.getFontMetrics().stringWidth(weekLabel);
			g2.drawString(weekLabel, x - strWidth / 2, height - padding + 20);
		}

		// Draw chart title
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
		g2.setColor(Color.BLACK);
		g2.drawString("Accuracy Over Calendar Weeks", padding, padding - 15);

		// Draw axis titles
		g2.drawString("Week", width / 2 - 20, height - 5);

		Graphics2D g2d = (Graphics2D) g2.create();
		g2d.rotate(-Math.PI / 2);
		g2d.drawString("Accuracy (%)", -height / 2 - 30, padding - 30);
		g2d.dispose();
	}
}
