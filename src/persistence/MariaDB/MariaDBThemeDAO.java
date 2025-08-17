package persistence.MariaDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import persistence.DAO.ThemeDAO;
import quizLogic.Theme;

/**
 * {@code MariaDBThemeDAO} is the MariaDB/MySQL implementation of
 * {@link ThemeDAO}.
 *
 * <p>
 * Responsibilities:
 * </p>
 * <ul>
 * <li>Retrieve a single theme by ID</li>
 * <li>Retrieve all themes</li>
 * <li>Insert new themes</li>
 * <li>Update existing themes</li>
 * <li>Delete themes by ID</li>
 * </ul>
 *
 * <p>
 * This DAO manages only the <b>theme table</b>. Questions and answers must be
 * handled via their respective DAOs.
 * </p>
 */
public class MariaDBThemeDAO implements ThemeDAO {

	/** Active DB connection provided by {@link persistence.DBDataManager}. */
	private final Connection conn;

	/**
	 * Constructs a new Theme DAO with an active DB connection.
	 *
	 * @param conn an open JDBC {@link Connection}
	 */
	public MariaDBThemeDAO(Connection conn) {
		this.conn = conn;
	}

	/**
	 * Finds a theme by its primary key.
	 *
	 * @param id the theme ID
	 * @return the {@link Theme} if found, otherwise {@code null}
	 */
	@Override
	public Theme findById(int id) {
		String sql = "SELECT * FROM theme WHERE id=?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Theme t = new Theme();
					t.setId(rs.getInt("id"));
					t.setTitle(rs.getString("title"));
					t.setText(rs.getString("text"));
					return t;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Retrieves all themes from the database.
	 *
	 * @return a list of all {@link Theme}s (empty if none exist)
	 */
	@Override
	public List<Theme> findAll() {
		List<Theme> list = new ArrayList<>();
		String sql = "SELECT * FROM theme";

		try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Theme t = new Theme();
				t.setId(rs.getInt("id"));
				t.setTitle(rs.getString("title"));
				t.setText(rs.getString("text"));
				list.add(t);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Inserts a new theme into the database.
	 *
	 * @param theme the {@link Theme} to insert
	 * @return {@code true} if insertion succeeded, else {@code false}
	 */
	@Override
	public boolean insert(Theme theme) {
		String sql = "INSERT INTO theme (title, text) VALUES (?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, theme.getTitle());
			ps.setString(2, theme.getText());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Updates an existing theme.
	 *
	 * @param theme the {@link Theme} with updated fields (must have valid id)
	 * @return {@code true} if update succeeded, else {@code false}
	 */
	@Override
	public boolean update(Theme theme) {
		String sql = "UPDATE theme SET title=?, text=? WHERE id=?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, theme.getTitle());
			ps.setString(2, theme.getText());
			ps.setInt(3, theme.getId());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Deletes a theme by its ID.
	 * 
	 * <p>
	 * Note: Questions linked to this theme will also be deleted if
	 * <code>ON DELETE CASCADE</code> is configured in the database schema.
	 * </p>
	 *
	 * @param id the theme ID
	 * @return {@code true} if deletion succeeded, else {@code false}
	 */
	@Override
	public boolean delete(int id) {
		String sql = "DELETE FROM theme WHERE id=?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
