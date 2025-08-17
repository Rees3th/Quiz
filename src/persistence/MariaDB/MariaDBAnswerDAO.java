package persistence.MariaDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import persistence.DAO.AnswerDAO;
import quizLogic.Answer;
import quizLogic.Question;

/**
 * {@code MariaDBAnswerDAO} is the MariaDB/MySQL implementation of
 * {@link AnswerDAO}.
 *
 * <p>
 * Responsibilities:
 * </p>
 * <ul>
 * <li>Fetch all answers belonging to a given {@link Question}</li>
 * <li>Insert a new {@link Answer} into the database</li>
 * <li>Update an existing {@link Answer}</li>
 * <li>Delete all answers belonging to a given question ID</li>
 * </ul>
 *
 * <p>
 * This class uses plain JDBC ({@link PreparedStatement}, {@link ResultSet}) for
 * interacting with the database. Exception handling is kept simple (printing
 * stack traces) but can be enhanced for production use.
 * </p>
 */
public class MariaDBAnswerDAO implements AnswerDAO {

	/** Active DB connection, provided by {@link persistence.DBDataManager}. */
	private final Connection conn;

	/**
	 * Constructs a new MariaDBAnswerDAO with an active database connection.
	 *
	 * @param conn an open JDBC {@link Connection}
	 */
	public MariaDBAnswerDAO(Connection conn) {
		this.conn = conn;
	}

	/**
	 * Finds all answers for the given {@link Question}.
	 *
	 * @param question the parent question whose answers to load
	 * @return list of {@link Answer} objects (empty if none found)
	 */
	@Override
	public List<Answer> findByQuestion(Question question) {
		String sql = "SELECT * FROM answer WHERE question_id=?";
		List<Answer> answers = new ArrayList<>();

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, question.getId());

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Answer a = new Answer(question);
					a.setId(rs.getInt("id"));
					a.setText(rs.getString("text"));
					a.setCorrect(rs.getBoolean("is_correct"));
					answers.add(a);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return answers;
	}

	/**
	 * Inserts a new answer into the database.
	 *
	 * <p>
	 * The question reference must already exist in the database.
	 * </p>
	 *
	 * @param answer the {@link Answer} to insert
	 * @return {@code true} if insert succeeded, {@code false} otherwise
	 */
	@Override
	public boolean insert(Answer answer) {
		String sql = "INSERT INTO answer (question_id, text, is_correct) VALUES (?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, answer.getQuestion().getId());
			ps.setString(2, answer.getText());
			ps.setBoolean(3, answer.isCorrect());

			int rowsAffected = ps.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Updates an existing answer in the database.
	 *
	 * @param answer the updated {@link Answer} object (must have a valid id)
	 * @return {@code true} if update succeeded, {@code false} otherwise
	 */
	@Override
	public boolean update(Answer answer) {
		String sql = "UPDATE answer SET text=?, is_correct=? WHERE id=?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, answer.getText());
			ps.setBoolean(2, answer.isCorrect());
			ps.setInt(3, answer.getId());

			int rowsAffected = ps.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Deletes all answers belonging to a question by its ID.
	 *
	 * <p>
	 * This is typically used before re-saving updated answers.
	 * </p>
	 *
	 * @param questionId the parent question ID
	 * @return {@code true} if delete succeeded, {@code false} otherwise
	 */
	@Override
	public boolean deleteByQuestionId(int questionId) {
		String sql = "DELETE FROM answer WHERE question_id=?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, questionId);
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
