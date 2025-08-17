package persistence.DAO;

import java.util.List;
import quizLogic.Theme;

/**
 * {@code ThemeDAO} defines the data access contract for {@link Theme} entities.
 *
 * <p>
 * Responsibilities:
 * </p>
 * <ul>
 * <li>Retrieve themes (by ID or all)</li>
 * <li>Insert new themes</li>
 * <li>Update existing themes</li>
 * <li>Delete themes by ID</li>
 * </ul>
 *
 * <p>
 * Implementations (e.g., {@code MariaDBThemeDAO}) handle the actual persistence
 * logic using a specific database technology.
 * </p>
 */
public interface ThemeDAO {

	/**
	 * Finds a theme by its unique ID.
	 *
	 * @param id the ID of the theme
	 * @return a {@link Theme} instance if found, otherwise {@code null}
	 */
	Theme findById(int id);

	/**
	 * Retrieves all themes from the persistence layer.
	 *
	 * @return a list of {@link Theme} objects (may be empty if none exist)
	 */
	List<Theme> findAll();

	/**
	 * Inserts a new theme into the DB.
	 *
	 * @param theme the {@link Theme} to insert
	 * @return {@code true} if insertion succeeded, {@code false} otherwise
	 */
	boolean insert(Theme theme);

	/**
	 * Updates an existing theme in the DB.
	 *
	 * @param theme the {@link Theme} containing updated fields (must have a valid
	 *              ID)
	 * @return {@code true} if update succeeded, {@code false} otherwise
	 */
	boolean update(Theme theme);

	/**
	 * Deletes a theme by its ID.
	 *
	 * @param id the ID of the theme to delete
	 * @return {@code true} if deletion succeeded, {@code false} otherwise
	 */
	boolean delete(int id);
}
