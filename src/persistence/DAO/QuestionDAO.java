package persistence.DAO;

import java.util.List;
import quizLogic.Question;
import quizLogic.Theme;

/**
 * {@code QuestionDAO} defines the contract for persistence operations related
 * to {@link Question} entities.
 *
 * <p>
 * Responsibilities:
 * </p>
 * <ul>
 * <li>Retrieve a question by its ID</li>
 * <li>Retrieve all questions belonging to a specific {@link Theme}</li>
 * <li>Insert new questions</li>
 * <li>Update existing questions</li>
 * <li>Delete questions by ID</li>
 * </ul>
 *
 * <p>
 * Implementations (such as {@code MariaDBQuestionDAO}) define how these
 * operations are executed against a specific database.
 * </p>
 */
public interface QuestionDAO {

	/**
	 * Finds a question by its unique ID.
	 *
	 * @param id the ID of the question
	 * @return the {@link Question} instance if found, or {@code null} if not found
	 */
	Question findById(int id);

	/**
	 * Retrieves all questions that belong to the given theme.
	 *
	 * @param theme the {@link Theme} whose associated questions should be retrieved
	 * @return a list of {@link Question} objects, possibly empty but never
	 *         {@code null}
	 */
	List<Question> findByTheme(Theme theme);

	/**
	 * Inserts a new question into the database.
	 *
	 * @param question the {@link Question} to insert (must be linked to a valid
	 *                 theme)
	 * @return {@code true} if insertion succeeded, {@code false} otherwise
	 */
	boolean insert(Question question);

	/**
	 * Updates an existing question.
	 *
	 * @param question the {@link Question} with updated fields (must have a valid
	 *                 ID)
	 * @return {@code true} if update succeeded, {@code false} otherwise
	 */
	boolean update(Question question);

	/**
	 * Deletes a question by its ID. Any answers linked to this question should also
	 * be removed, typically via <code>ON DELETE CASCADE</code> constraints or
	 * explicit DAO calls.
	 *
	 * @param id the ID of the question
	 * @return {@code true} if deletion succeeded, {@code false} otherwise
	 */
	boolean delete(int id);
}
