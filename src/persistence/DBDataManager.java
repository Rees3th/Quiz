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
import persistence.DataBase.DBAnswerDAO;
import persistence.DataBase.DBQuestionDAO;
import persistence.DataBase.DBStatisticDAO;
import persistence.DataBase.DBThemeDAO;
import quizLogic.Answer;
import quizLogic.Question;
import quizLogic.QuizStatistic;
import quizLogic.Theme;

/**
 * {@code DBDataManager} is the central persistence manager managing database 
 * interactions related to quiz data in the application.
 *
 * <p>
 * This class establishes and holds a JDBC connection to a MariaDB (MySQL) database,
 * sets up tables if necessary, and exposes methods to perform CRUD operations on
 * main quiz entities: themes, questions, answers, and statistics.
 * </p>
 *
 * <p>
 * Concrete database operations are delegated to individual DAO instances:
 * <ul>
 *  <li>{@link ThemeDAO} for Theme objects.</li>
 *  <li>{@link QuestionDAO} for Question objects.</li>
 *  <li>{@link AnswerDAO} for Answer objects.</li>
 *  <li>{@link StatisticDAO} for Quiz statistics.</li>
 * </ul>
 * </p>
 *
 * <p>
 * This class acts as a centralized service, providing a simplified API for 
 * upper layers (UI, logic) to interact with persistent data without managing direct SQL code.
 * </p>
 *
 * <p><b>Responsibilities include:</b></p>
 * <ul>
 *   <li>Initialize database connection.</li>
 *   <li>Ensure required tables exist.</li>
 *   <li>Load, save and delete quiz themes.</li>
 *   <li>Load, save and delete quiz questions, with answers fully synchronized.</li>
 *   <li>Retrieve quiz answers, statistics, and support deletion cascading.</li>
 * </ul>
 *
 * @author
 */
public class DBDataManager {

    /** The persistent database connection used throughout the application. */
    private final Connection conn;

    /** DAO handling theme-related database operations. */
    private final ThemeDAO themeDAO;

    /** DAO handling question-related database operations. */
    private final QuestionDAO questionDAO;

    /** DAO handling answer-related database operations. */
    private final AnswerDAO answerDAO;

    /** DAO handling statistics-related database operations. */
    private final StatisticDAO statisticDAO;

    /**
     * Constructs the data manager, sets up the database connection,
     * initializes DAOs, and creates necessary tables if missing.
     *
     * @throws SQLException if database setup or connection fails.
     */
    public DBDataManager() throws SQLException {
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quizdb", "root", "");

        createTables();

        themeDAO = new DBThemeDAO(conn);
        questionDAO = new DBQuestionDAO(conn);
        answerDAO = new DBAnswerDAO(conn);
        statisticDAO = new DBStatisticDAO(conn);
    }

    /**
     * Creates database tables (theme, question, answer) if they don’t exist already.
     * Uses foreign keys with cascading deletes to maintain referential integrity.
     * 
     * @throws SQLException if table creation fails.
     */
    private void createTables() throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS theme (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "title VARCHAR(255) NOT NULL," +
                    "text TEXT)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS question (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "theme_id INT," +
                    "title VARCHAR(255) NOT NULL," +
                    "text TEXT," +
                    "FOREIGN KEY (theme_id) REFERENCES theme(id) ON DELETE CASCADE)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS answer (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "question_id INT," +
                    "text VARCHAR(255) NOT NULL," +
                    "is_correct BOOLEAN," +
                    "FOREIGN KEY (question_id) REFERENCES question(id) ON DELETE CASCADE)");
        }
    }

    /**
     * Fetches all themes from the database.
     *
     * @return List of all {@link Theme} objects.
     */
    public List<Theme> getAllThemes() {
        return themeDAO.findAll();
    }

    /**
     * Persists a theme to the database. Inserts new or updates existing based on the id.
     * 
     * @param theme The {@link Theme} object to save.
     * @return Null if success, or an error message string.
     */
    public String saveTheme(Theme theme) {
        if (theme.getId() <= 0) {
            return themeDAO.insert(theme) ? null : "Error inserting theme.";
        } else {
            return themeDAO.update(theme) ? null : "Error updating theme.";
        }
    }

    /**
     * Deletes a theme by its ID.
     * 
     * @param themeId The id of the theme to delete.
     * @return true if deletion was successful, false otherwise.
     */
    public boolean deleteTheme(int themeId) {
        return themeDAO.delete(themeId);
    }

    /**
     * Retrieves all questions for a given theme, with answers loaded.
     * 
     * @param theme The {@link Theme} whose questions to retrieve.
     * @return List of {@link Question} objects including their answers.
     */
    public List<Question> getQuestionsFor(Theme theme) {
        if (theme == null || theme.getId() <= 0)
            return new ArrayList<>();

        List<Question> questions = questionDAO.findByTheme(theme);
        for (Question question : questions) {
            List<Answer> answers = answerDAO.findByQuestion(question);
            question.clearAnswers();
            for (Answer answer : answers) {
                question.addAnswer(answer);
            }
        }
        return questions;
    }

    /**
     * Retrieves a question by ID with its answers.
     * 
     * @param id The question ID.
     * @return fully loaded {@link Question}, or null if not found.
     */
    public Question getFullQuestionById(int id) {
        Question question = questionDAO.findById(id);
        if (question != null) {
            List<Answer> answers = answerDAO.findByQuestion(question);
            question.clearAnswers();
            for (Answer answer : answers) {
                question.addAnswer(answer);
            }
            if (question.getThema() != null && question.getThema().getId() > 0) {
                Theme theme = themeDAO.findById(question.getThema().getId());
                question.setThema(theme);
            }
        }
        return question;
    }

    /**
     * Saves a question and its answers to the database.
     * Inserts or updates the question based on its id.
     * Answers are fully re-synced (old answers deleted, new answers inserted).
     * 
     * @param question The question to save.
     * @return Null if successful, or an error message.
     */
    public String saveQuestion(Question question) {
        if (question.getThema() == null || question.getThema().getId() <= 0)
            return "Please select a valid theme before saving the question.";

        boolean success;
        if (question.getId() <= 0)
            success = questionDAO.insert(question);
        else
            success = questionDAO.update(question);

        if (!success)
            return (question.getId() <= 0) ? "Error inserting question." : "Error updating question.";

        saveAnswers(question);
        return null;
    }

    /**
     * Deletes a question by its object reference.
     * 
     * @param question The question to delete.
     * @return Null if successful, else error message.
     */
    public String deleteQuestion(Question question) {
        return questionDAO.delete(question.getId()) ? null : "Error deleting question.";
    }

    /**
     * Persists answers for a question.
     * Deletes all existing answers and inserts current ones.
     * 
     * @param question The question whose answers to save.
     */
    private void saveAnswers(Question question) {
        // Delete old answers
        answerDAO.deleteByQuestionId(question.getId());

        // Insert new answers
        for (Answer answer : question.getAnswers()) {
            answer.setQuestion(question);
            answerDAO.insert(answer);
        }
    }

    /**
     * Returns all themes.
     * 
     * @return List of {@link Theme}.
     */
    public List<Theme> findAllThemes() {
        return themeDAO.findAll();
    }

    /**
     * Finds questions for given theme.
     * 
     * @param theme The theme to query.
     * @return List of {@link Question}.
     */
    public List<Question> findQuestionsByTheme(Theme theme) {
        return questionDAO.findByTheme(theme);
    }

    /**
     * Finds quiz statistics for the given question ID.
     * 
     * @param questionId Id of the question.
     * @return List of {@link QuizStatistic}.
     */
    public List<QuizStatistic> findStatisticsByQuestionId(int questionId) {
        return statisticDAO.findByQuestionId(questionId);
    }

    /**
     * Accessor for ThemeDAO.
     * 
     * @return {@link ThemeDAO}.
     */
    public ThemeDAO getThemeDAO() {
        return themeDAO;
    }

    /**
     * Accessor for QuestionDAO.
     * 
     * @return {@link QuestionDAO}.
     */
    public QuestionDAO getQuestionDAO() {
        return questionDAO;
    }

    /**
     * Accessor for StatisticDAO.
     * 
     * @return {@link StatisticDAO}.
     */
    public StatisticDAO getStatisticDAO() {
        return statisticDAO;
    }
}
