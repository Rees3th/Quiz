package gui.Panels;

import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import persistence.DBDataManager;
import quizLogic.Question;
import quizLogic.QuizStatistic;

/**
 * {@code StatisticPanel} displays quiz statistics in a table format.
 * 
 * <p>
 * It shows each quiz question along with whether it was answered correctly and
 * the timestamp of when the answer was recorded.
 * </p>
 * 
 * <p>
 * Data is loaded from the database via {@link DBDataManager} and its associated
 * DAOs.
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public class StatisticPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/** Table component showing quiz statistics. */
	private JTable statsTable;

	/** Data model backing the {@link #statsTable} rows and columns. */
	private DefaultTableModel tableModel;

	/**
	 * Creates a new statistics panel and initializes the table layout.
	 * 
	 * @param dm the {@link DBDataManager} to retrieve quiz statistics and questions
	 *           from
	 */
	public StatisticPanel(DBDataManager dm) {
		// Table columns
		String[] cols = { "Frage", "Richtig", "Zeitpunkt" };
		tableModel = new DefaultTableModel(cols, 0);
		statsTable = new JTable(tableModel);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(new JScrollPane(statsTable));

		fillWithData(dm);
	}

	/**
	 * Populates the statistics table with data retrieved from the database.
	 * 
	 * <p>
	 * Clears existing rows before re-adding all quiz statistics. Each row displays
	 * the question text, correctness state, and timestamp.
	 * </p>
	 * 
	 * @param dm the {@link DBDataManager} used to fetch statistics and questions
	 */
	public void fillWithData(DBDataManager dm) {
		tableModel.setRowCount(0);
		List<QuizStatistic> stats = dm.getStatisticDAO().findAll();
		for (QuizStatistic stat : stats) {
			String frageText = "--";
			Question q = dm.getQuestionDAO().findById(stat.getQuestionId());
			if (q != null)
				frageText = q.getText();
			tableModel.addRow(new Object[] { frageText, stat.isCorrect() ? "Ja" : "Nein", stat.getDate().toString() });
		}
	}
}
