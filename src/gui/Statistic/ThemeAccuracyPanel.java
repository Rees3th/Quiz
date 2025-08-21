package gui.Statistic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * ThemeAccuracyPanel is a Swing component that displays the accuracy statistics
 * for different quiz themes in a sortable and scrollable table.
 * <p>
 * It shows two columns: "Theme" and "Accuracy (%)" with color-coded accuracy
 * values. High accuracy is shown in green, low accuracy in red, and others in
 * gray.
 * </p>
 * This component expects accuracy data to be set via
 * {@link #setThemeAccuracyData(Map)}.
 * 
 * @author Oleg Kapirulya
 */
public class ThemeAccuracyPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final JTable table;
	private final DefaultTableModel tableModel;

	/**
	 * Constructs the ThemeAccuracyPanel and initializes the UI components.
	 */
	public ThemeAccuracyPanel() {
		super(new BorderLayout()); // Use BorderLayout

		// Initialize table model with columns: "Theme", "Accuracy (%)"
		tableModel = new DefaultTableModel(new Object[] { "Theme", "Accuracy (%)" }, 0) {
			// Disable cell editing
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		// Initialize table with model
		table = new JTable(tableModel);

		// Configure column widths for better visual balance
		table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		table.getColumnModel().getColumn(0).setPreferredWidth(200); // Theme name column wider
		table.getColumnModel().getColumn(1).setPreferredWidth(80); // Accuracy column narrower

		// Set a custom cell renderer to color-code accuracy percentages:
		// Green for accuracy >= 75%
		// Red for accuracy < 40%
		// Gray for others
		table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (column == 1 && value instanceof String) {
					try {
						double acc = Double.parseDouble((String) value);
						if (acc >= 75.0) {
							c.setForeground(new Color(34, 139, 34)); // Dark green
						} else if (acc < 40.0) {
							c.setForeground(Color.RED);
						} else {
							c.setForeground(Color.DARK_GRAY);
						}
					} catch (NumberFormatException e) {
						c.setForeground(Color.BLACK);
					}
				} else {
					c.setForeground(Color.BLACK);
				}
				return c;
			}
		});

		// Title label above table
		JLabel title = new JLabel("Theme Accuracy (sortable)", SwingConstants.CENTER);
		title.setFont(title.getFont().deriveFont(16f).deriveFont(Font.BOLD));

		// Add title and table with scroll pane to panel
		add(title, BorderLayout.NORTH);
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, BorderLayout.CENTER);
	}

	/**
	 * Updates the table data with the provided accuracy map. The map keys are theme
	 * names, values are accuracy percentages. Data is sorted by accuracy in
	 * descending order before display.
	 * 
	 * @param data Map of theme names to accuracy percentages.
	 */
	public void setThemeAccuracyData(Map<String, Double> data) {
		tableModel.setRowCount(0); // Clear existing rows

		// Sort entries by accuracy descending
		List<Map.Entry<String, Double>> sortedEntries = new ArrayList<>(data.entrySet());
		sortedEntries.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));

		// Add sorted data rows to table
		for (Map.Entry<String, Double> entry : sortedEntries) {
			String themeName = entry.getKey();
			String accuracyStr = String.format("%.1f", entry.getValue());
			tableModel.addRow(new Object[] { themeName, accuracyStr });
		}
	}
}
