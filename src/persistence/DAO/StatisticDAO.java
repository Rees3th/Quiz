package persistence.DAO;

import java.util.List;
import quizLogic.QuizStatistic;

public interface StatisticDAO {
    boolean insert(QuizStatistic statistic);
    List<QuizStatistic> findAll();
    List<QuizStatistic> findByQuestionId(int questionId);
}
