package persistence.DataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import persistence.DAO.QuestionDAO;
import quizLogic.Question;
import quizLogic.Theme;

/**
 * {@code DBQuestionDAO} is the MariaDB/MySQL implementation of
 * {@link QuestionDAO}.
 *
 * <p>
 * Responsibilities:
 * </p>
 * <ul>
 * <li>Find questions by ID or by theme</li>
 * <li>Insert new questions into the database</li>
 * <li>Update existing questions</li>
 * <li>Delete questions by ID</li>
 * </ul>
 *
 * <p>
 * This DAO does <b>not</b> automatically load answers for questions. Associated
 * answers must be loaded via {@link persistence.DataBase.DBAnswerDAO}.
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public class DBQuestionDAO implements QuestionDAO {

	/** Active DB connection, managed by {@link persistence.DBDataManager}. */
	private final Connection conn;

	/**
	 * Creates a new Question DAO with an open connection.
	 *
	 * @param conn active JDBC {@link Connection}
	 */
	public DBQuestionDAO(Connection conn) {
		this.conn = conn;
	}

	/**
	 * Finds a question by its primary key ID.
	 *
	 * <p>
	 * Note: Only question fields are filled. Theme reference only contains the
	 * theme ID (no title/text). Use {@link persistence.DataBase.DBThemeDAO} if full
	 * theme details are needed.
	 * </p>
	 *
	 * @param id the question ID
	 * @return a {@link Question} object, or {@code null} if not found
	 */
	@Override
	public Question findById(int id) {
		String sql = "SELECT * FROM question WHERE id=?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					int themeId = rs.getInt("theme_id");
					Theme theme = new Theme();
					theme.setId(themeId);

					Question q = new Question(theme);
					q.setId(rs.getInt("id"));
					q.setTitle(rs.getString("title"));
					q.setText(rs.getString("text"));
					return q;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Finds all questions that belong to a given theme.
	 *
	 * @param theme the {@link Theme} whose questions to retrieve
	 * @return list of {@link Question} objects (without answers)
	 */
	@Override
	public List<Question> findByTheme(Theme theme) {
		String sql = "SELECT * FROM question WHERE theme_id=?";
		List<Question> questions = new ArrayList<>();

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, theme.getId());

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Question q = new Question(theme); // link back to parent theme
					q.setId(rs.getInt("id"));
					q.setTitle(rs.getString("title"));
					q.setText(rs.getString("text"));
					questions.add(q);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return questions;
	}

	/**
	 * Inserts a new question into the database.
	 *
	 * - Theme reference must point to a valid theme (with ID). - Retrieves and sets
	 * the generated question ID on success.
	 *
	 * @param question the {@link Question} to insert
	 * @return {@code true} if insertion succeeded, else {@code false}
	 */
	@Override
	public boolean insert(Question question) {
		String sql = "INSERT INTO question (title, text, theme_id) VALUES (?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, question.getTitle());
			ps.setString(2, question.getText());
			ps.setInt(3, question.getThema().getId());

			int rowsAffected = ps.executeUpdate();
			if (rowsAffected > 0) {
				// retrieve auto-generated ID
				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next()) {
						question.setId(rs.getInt(1));
					}
				}
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Updates an existing question in the database.
	 *
	 * @param question the {@link Question} with updated data (must have valid id)
	 * @return {@code true} if update succeeded, else {@code false}
	 */
	public boolean update(Question question) {
		String sql = "UPDATE question SET title=?, text=?, theme_id=? WHERE id=?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, question.getTitle());
			ps.setString(2, question.getText());
			ps.setInt(3, question.getThema().getId());
			ps.setInt(4, question.getId());

			int rowsAffected = ps.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Deletes a question by its ID.
	 * 
	 * <p>
	 * Associated answers are automatically deleted due to
	 * <code>ON DELETE CASCADE</code> in the schema.
	 * </p>
	 *
	 * @param id the question ID
	 * @return {@code true} if deletion succeeded, else {@code false}
	 */
	@Override
	public boolean delete(int id) {
		String sql = "DELETE FROM question WHERE id=?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			int rowsAffected = ps.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
