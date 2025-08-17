package persistence.DAO;

import java.util.List;
import quizLogic.Answer;
import quizLogic.Question;

/**
 * {@code AnswerDAO} defines the data access contract for {@link Answer}
 * entities.
 *
 * <p>
 * Responsibilities:
 * </p>
 * <ul>
 * <li>Retrieve all answers belonging to a given {@link Question}</li>
 * <li>Insert new answers</li>
 * <li>Update existing answers</li>
 * <li>Delete all answers for a question (by question ID)</li>
 * </ul>
 *
 * <p>
 * Implementations (e.g. {@link persistence.MariaDB.MariaDBAnswerDAO}) provide
 * the actual persistence logic against a specific database.
 * </p>
 */
public interface AnswerDAO {

	/**
	 * Returns all answers linked to the given question.
	 *
	 * @param question the {@link Question} whose answers to fetch
	 * @return a list of {@link Answer} instances, possibly empty but never
	 *         {@code null}
	 */
	List<Answer> findByQuestion(Question question);

	/**
	 * Inserts a new {@link Answer} into the database.
	 *
	 * @param answer the answer to insert (must be linked to a valid question)
	 * @return {@code true} if the insert was successful, {@code false} otherwise
	 */
	boolean insert(Answer answer);

	/**
	 * Updates an existing {@link Answer}.
	 *
	 * @param answer the answer with updated fields (must already exist in DB and
	 *               have a valid ID)
	 * @return {@code true} if update was successful, {@code false} otherwise
	 */
	boolean update(Answer answer);

	/**
	 * Deletes all answers belonging to a given question. Typically used before
	 * re-inserting updated answers.
	 *
	 * @param questionId the ID of the question whose answers should be deleted
	 * @return {@code true} if deletion succeeded, {@code false} otherwise
	 */
	boolean deleteByQuestionId(int questionId);
}
