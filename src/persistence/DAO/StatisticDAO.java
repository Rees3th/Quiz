package persistence.DAO;

import java.util.List;
import quizLogic.QuizStatistic;

/**
 * Interface defining data access object (DAO) methods for managing quiz
 * statistics. This contract specifies operations for inserting and retrieving
 * quiz attempt records.
 * 
 * Implementations of this interface should provide database-specific logic.
 * 
 * Responsibilities:
 * <ul>
 * <li>Insert new quiz attempt statistics into persistence storage.</li>
 * <li>Retrieve all existing quiz statistics.</li>
 * <li>Retrieve quiz statistics associated with a specific question.</li>
 * </ul>
 * 
 * @author Oleg Kapirulya
 */
public interface StatisticDAO {

	/**
	 * Inserts a new quiz statistic record into the database.
	 * 
	 * @param statistic the QuizStatistic object representing a quiz attempt to
	 *                  save.
	 * @return true if the insertion was successful, false otherwise.
	 */
	boolean insert(QuizStatistic statistic);

	/**
	 * Retrieves all quiz statistic records from the database.
	 * 
	 * @return a list of all quiz statistics, or an empty list if none exist.
	 */
	List<QuizStatistic> findAll();

	/**
	 * Retrieves quiz statistic records associated with a specific question ID.
	 * 
	 * @param questionId the ID of the question whose statistics should be
	 *                   retrieved.
	 * @return a list of quiz statistics for the specified question, or empty if
	 *         none found.
	 */
	List<QuizStatistic> findByQuestionId(int questionId);
}
