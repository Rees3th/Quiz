package persistence.DataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import persistence.DAO.StatisticDAO;
import quizLogic.QuizStatistic;

/**
 * DBStatisticDAO provides an implementation of the {@link StatisticDAO}
 * interface for managing quiz statistics persistence using a relational
 * database.
 * <p>
 * This DAO uses JDBC to perform create and read operations on the 'statistic'
 * table, which records quiz attempts including answer correctness and
 * timestamps.
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public class DBStatisticDAO implements StatisticDAO {
	private final Connection conn;

	/**
	 * Constructs the DAO with an active JDBC {@link Connection}.
	 * 
	 * @param conn active database connection for executing queries
	 */
	public DBStatisticDAO(Connection conn) {
		this.conn = conn;
	}

	/**
	 * Inserts a new QuizStatistic record in the database.
	 * 
	 * @param statistic the {@link QuizStatistic} to insert; must have valid
	 *                  question ID and timestamp
	 * @return true if insertion was successful; false otherwise
	 */
	@Override
	public boolean insert(QuizStatistic statistic) {
		String sql = "INSERT INTO statistic (question_id, correct, date) VALUES (?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, statistic.getQuestionId());
			ps.setBoolean(2, statistic.isCorrect());
			ps.setTimestamp(3, new Timestamp(statistic.getDate().getTime()));
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Retrieves all quiz statistics from the database.
	 * 
	 * @return a list containing all {@link QuizStatistic} records; empty list if
	 *         none found or error occurs
	 */
	@Override
	public List<QuizStatistic> findAll() {
		List<QuizStatistic> list = new ArrayList<>();
		String sql = "SELECT * FROM statistic";
		try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				QuizStatistic stat = new QuizStatistic();
				stat.setId(rs.getInt("id"));
				stat.setQuestionId(rs.getInt("question_id"));
				stat.setCorrect(rs.getBoolean("correct"));
				stat.setDate(new Date(rs.getTimestamp("date").getTime()));
				list.add(stat);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Retrieves all quiz statistics associated with a specific question ID.
	 * 
	 * @param questionId the ID of the question whose statistics to fetch
	 * @return list of {@link QuizStatistic} for the given question; empty list if
	 *         none or on error
	 */
	@Override
	public List<QuizStatistic> findByQuestionId(int questionId) {
		List<QuizStatistic> list = new ArrayList<>();
		String sql = "SELECT * FROM statistic WHERE question_id=?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, questionId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					QuizStatistic stat = new QuizStatistic();
					stat.setId(rs.getInt("id"));
					stat.setQuestionId(rs.getInt("question_id"));
					stat.setCorrect(rs.getBoolean("correct"));
					stat.setDate(new Date(rs.getTimestamp("date").getTime()));
					list.add(stat);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
}
