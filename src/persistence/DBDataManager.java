package persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import persistence.DAO.AnswerDAO;
import persistence.DAO.QuestionDAO;
import persistence.DAO.StatisticDAO;
import persistence.DAO.ThemeDAO;
import persistence.MariaDB.MariaDBAnswerDAO;
import persistence.MariaDB.MariaDBQuestionDAO;
import persistence.MariaDB.MariaDBStatisticDAO;
import persistence.MariaDB.MariaDBThemeDAO;
import quizLogic.Answer;
import quizLogic.Question;
import quizLogic.QuizStatistic;
import quizLogic.Theme;

/**
 * {@code DBDataManager} is the central persistence manager for the Quiz
 * application.
 *
 * <p>
 * <b>Responsibilities:</b>
 * </p>
 * <ul>
 * <li>Establishes a database connection (here: MariaDB/MySQL)</li>
 * <li>Creates required tables (themes, questions, answers) if they don't
 * exist</li>
 * <li>Provides CRUD operations (Create/Read/Update/Delete) for {@link Theme},
 * {@link Question}, and {@link Answer}</li>
 * <li>Keeps data objects consistent by loading/saving associated answers for
 * questions</li>
 * </ul>
 *
 * <p>
 * Concrete SQL access is delegated to DAOs:
 * <ul>
 * <li>{@link ThemeDAO} – implemented by {@link MariaDBThemeDAO}</li>
 * <li>{@link QuestionDAO} – implemented by {@link MariaDBQuestionDAO}</li>
 * <li>{@link AnswerDAO} – implemented by {@link MariaDBAnswerDAO}</li>
 * </ul>
 * </p>
 *
 * <p>
 * This class serves as the <b>single entry point</b> for higher-level
 * components (UI panels, business logic) that need to access persistence. Thus,
 * UI layers only deal with {@code DBDataManager}, not individual DAO classes.
 * </p>
 */
public class DBDataManager {

	/** The database connection (kept open for the lifetime of the app). */
	private final Connection conn;

	/** DAO for managing Themes. */
	private final ThemeDAO themeDAO;

	/** DAO for managing Questions. */
	private final QuestionDAO questionDAO;

	/** DAO for managing Answers. */
	private final AnswerDAO answerDAO;
	
	/** DAO for managing Quiz Statistics. */
	private final StatisticDAO statisticDAO;

	/**
	 * Constructs a new {@code DBDataManager}.
	 *
	 * <p>
	 * Initializes the JDBC connection with a MariaDB/MySQL instance, creates
	 * required tables, and initializes DAO implementations.
	 * </p>
	 *
	 * @throws SQLException if the connection or table creation fails
	 */
	public DBDataManager() throws SQLException {
		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quizdb", "root", "");

		createTables();

		// Initialize DAO wrappers
		themeDAO = new MariaDBThemeDAO(conn);
		questionDAO = new MariaDBQuestionDAO(conn);
		answerDAO = new MariaDBAnswerDAO(conn);
		statisticDAO = new MariaDBStatisticDAO(conn);
	}

	// ------------------- Initialization -------------------

	/**
	 * Creates the required database tables if they do not already exist.
	 *
	 * <ul>
	 * <li><b>theme</b> – stores quiz themes (id, title, text)</li>
	 * <li><b>question</b> – stores quiz questions, linked to a theme</li>
	 * <li><b>answer</b> – stores possible answers, linked to a question</li>
	 * </ul>
	 *
	 * <p>
	 * Note: Uses <code>ON DELETE CASCADE</code> to remove dependent rows
	 * automatically.
	 * </p>
	 */
	private void createTables() throws SQLException {
		try (Statement st = conn.createStatement()) {
			st.executeUpdate("CREATE TABLE IF NOT EXISTS theme (" + "id INT PRIMARY KEY AUTO_INCREMENT,"
					+ "title VARCHAR(255) NOT NULL," + "text TEXT)");

			st.executeUpdate("CREATE TABLE IF NOT EXISTS question (" + "id INT PRIMARY KEY AUTO_INCREMENT,"
					+ "theme_id INT," + "title VARCHAR(255) NOT NULL," + "text TEXT,"
					+ "FOREIGN KEY (theme_id) REFERENCES theme(id) ON DELETE CASCADE)");

			st.executeUpdate("CREATE TABLE IF NOT EXISTS answer (" + "id INT PRIMARY KEY AUTO_INCREMENT,"
					+ "question_id INT," + "text VARCHAR(255) NOT NULL," + "is_correct BOOLEAN,"
					+ "FOREIGN KEY (question_id) REFERENCES question(id) ON DELETE CASCADE)");
		}
	}

	// ------------------- Theme Operations -------------------

	/**
	 * Retrieves all themes stored in the database.
	 *
	 * @return a list of {@link Theme} objects
	 */
	public List<Theme> getAllThemes() {
		return themeDAO.findAll();
	}

	/**
	 * Persists a {@link Theme} into the database.
	 * <ul>
	 * <li>If {@code id <= 0} → inserts new theme</li>
	 * <li>If {@code id > 0} → updates existing theme</li>
	 * </ul>
	 *
	 * @param theme the theme to save
	 * @return {@code null} on success, or an error message string on failure
	 */
	public String saveTheme(Theme theme) {
		if (theme.getId() <= 0) {
			return themeDAO.insert(theme) ? null : "Fehler beim Einfügen des Themas";
		} else {
			return themeDAO.update(theme) ? null : "Fehler beim Aktualisieren des Themas";
		}
	}

	/**
	 * Deletes a theme by ID.
	 *
	 * @param themeId the ID of the theme to delete
	 * @return {@code true} if deletion succeeded, otherwise {@code false}
	 */
	public boolean deleteTheme(int themeId) {
		return themeDAO.delete(themeId);
	}

	// ------------------- Question Operations -------------------

	/**
	 * Retrieves all questions for a given theme, including their answers.
	 *
	 * <p>
	 * If theme is {@code null} or invalid, returns an empty list.
	 * </p>
	 *
	 * @param theme the {@link Theme} whose questions to load
	 * @return list of {@link Question} objects with their {@link Answer}s populated
	 */
	public List<Question> getQuestionsFor(Theme theme) {
		if (theme == null || theme.getId() <= 0) {
			return new ArrayList<>();
		}

		List<Question> questions = questionDAO.findByTheme(theme);

		for (Question q : questions) {
			List<Answer> answers = answerDAO.findByQuestion(q);

			// Ensure no stale state: clear answers before adding fresh ones
			q.clearAnswers();

			for (Answer a : answers) {
				q.addAnswer(a);
			}
		}
		return questions;
	}

	/**
	 * Retrieves a full {@link Question} by its ID, including all answers.
	 *
	 * @param id the question ID
	 * @return the full question with answers, or {@code null} if not found
	 */
	public Question getFullQuestionById(int id) {
		Question q = questionDAO.findById(id);
		if (q != null) {
			List<Answer> answers = answerDAO.findByQuestion(q);

			q.clearAnswers();
			for (Answer a : answers) {
				q.addAnswer(a);
			}
			if (q.getThema() != null && q.getThema().getId() > 0) {
	            Theme fullTheme = themeDAO.findById(q.getThema().getId());
	            q.setThema(fullTheme);
	        }
		}
		return q;
	}

	/**
	 * Saves a question (insert or update) and its associated answers.
	 *
	 * <ul>
	 * <li>If the {@link Theme} is invalid, returns an error message</li>
	 * <li>If question id is {@code <= 0}, a new record is inserted</li>
	 * <li>If id is {@code > 0}, the record is updated</li>
	 * <li>Answers are always overwritten: old ones removed, current ones
	 * re-inserted</li>
	 * </ul>
	 *
	 * @param question the {@link Question} to save
	 * @return {@code null} if save succeeded, otherwise an error message
	 */
	public String saveQuestion(Question question) {
		if (question.getThema() == null || question.getThema().getId() <= 0) {
			return "Bitte wählen Sie ein gültiges Thema vor dem Speichern der Frage.";
		}

		boolean success;
		if (question.getId() <= 0) {
			success = questionDAO.insert(question);
		} else {
			success = questionDAO.update(question);
		}

		if (success) {
			saveAnswers(question);
			return null;
		} else {
			return question.getId() <= 0 ? "Fehler beim Einfügen der Frage" : "Fehler beim Aktualisieren der Frage";
		}
	}

	/**
	 * Deletes a question from the database.
	 *
	 * @param question the question to delete
	 * @return {@code null} on success, or an error message if deletion failed
	 */
	public String deleteQuestion(Question question) {
		return questionDAO.delete(question.getId()) ? null : "Fehler beim Löschen der Frage";
	}

	// ------------------- Answer Handling -------------------

	/**
	 * Saves all answers belonging to a question. Workflow:
	 * <ol>
	 * <li>Delete all old answers linked to the question</li>
	 * <li>Insert fresh answers from the given {@link Question} object</li>
	 * </ol>
	 *
	 * @param question the question whose answers to persist
	 */
	private void saveAnswers(Question question) {
		// Delete old answers for this question
		answerDAO.deleteByQuestionId(question.getId());

		// Insert all current answers
		for (Answer answer : question.getAnswers()) {
			answer.setQuestion(question); // enforce relationship
			answerDAO.insert(answer);
		}
	}

	public List<Theme> findAllThemes() {
	    return themeDAO.findAll();
	}
	public List<Question> findQuestionsByTheme(Theme t) {
	    return questionDAO.findByTheme(t);
	}
	public List<QuizStatistic> findStatisticsByQuestionId(int qid) {
	    return statisticDAO.findByQuestionId(qid);
	}
	
	public ThemeDAO getThemeDAO() {
	    return this.themeDAO;
	}

	public QuestionDAO getQuestionDAO() {
	    return this.questionDAO;
	}

	public StatisticDAO getStatisticDAO() {
	    return this.statisticDAO;
	}





}
