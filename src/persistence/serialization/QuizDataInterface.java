package persistence.serialization;

import java.util.ArrayList;

import persistence.DBDataManager;
import quizLogic.Answer;
import quizLogic.Question;
import quizLogic.Theme;

/**
 * {@code QuizDataInterface} defines a unified contract for all
 * persistence-related operations in the quiz application.
 *
 * <p>
 * It abstracts away the underlying storage mechanism (e.g. file-based
 * serialization via {@link persistence.serialization.QuizDataManager} or
 * relational database access via {@link persistence.DBDataManager}).
 * </p>
 *
 * <p>
 * <b>Responsibilities:</b>
 * </p>
 * <ul>
 * <li>Provide methods to retrieve, save, and delete {@link Theme},
 * {@link Question}, and {@link Answer}</li>
 * <li>Offer a unified API so that the UI and business logic don’t care about
 * whether data comes from files or a database</li>
 * </ul>
 */
public interface QuizDataInterface {

	/**
	 * Returns a random {@link Question} from the data source.
	 *
	 * @return a random {@link Question}, or {@code null} if none exist
	 */
	Question getRandomQuestion();

	/**
	 * Retrieves all available quiz themes.
	 *
	 * @return an {@link ArrayList} of {@link Theme} objects (possibly empty)
	 */
	ArrayList<Theme> getAllThemes();

	/**
	 * Retrieves all questions for a given theme.
	 *
	 * @param th the {@link Theme} whose questions should be loaded
	 * @return an {@link ArrayList} containing all related {@link Question} objects,
	 *         or an empty list if none exist or parameter is {@code null}
	 */
	ArrayList<Question> getQuestionsFor(Theme th);

	/**
	 * Retrieves all possible answers for the given question.
	 *
	 * @param q the {@link Question} whose answers to retrieve
	 * @return an {@link ArrayList} of {@link Answer} objects, possibly empty but
	 *         never {@code null}
	 */
	ArrayList<Answer> getAnswersFor(Question q);

	/**
	 * Saves the given theme (insert or update).
	 *
	 * @param th the {@link Theme} to save
	 * @return {@code null} if operation succeeded, otherwise an error message
	 *         string
	 */
	String saveTheme(Theme th);

	/**
	 * Deletes the given theme.
	 *
	 * @param th the {@link Theme} to delete
	 * @return {@code null} if deletion succeeded, otherwise an error message string
	 */
	String deleteTheme(Theme th);

	/**
	 * Saves the given question (insert or update).
	 *
	 * @param q the {@link Question} to save
	 * @return {@code null} if operation succeeded, otherwise an error message
	 *         string
	 */
	String saveQuestion(Question q);

	/**
	 * Deletes the given question.
	 *
	 * @param q the {@link Question} to delete
	 * @return {@code null} if deletion succeeded, otherwise an error message string
	 */
	String deleteQuestion(Question q);
}
