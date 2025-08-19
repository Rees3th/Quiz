package persistence.MariaDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import persistence.DAO.StatisticDAO;
import quizLogic.QuizStatistic;

public class MariaDBStatisticDAO implements StatisticDAO {
    private final Connection conn;

    public MariaDBStatisticDAO(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean insert(QuizStatistic statistic) {
        String sql = "INSERT INTO statistic (question_id, correct, date) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, statistic.getQuestionId());
            ps.setBoolean(2, statistic.isCorrect());
            ps.setTimestamp(3, new Timestamp(statistic.getDate().getTime()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<QuizStatistic> findAll() {
        List<QuizStatistic> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM statistic")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                QuizStatistic stat = new QuizStatistic();
                stat.setId(rs.getInt("id"));
                stat.setQuestionId(rs.getInt("question_id"));
                stat.setCorrect(rs.getBoolean("correct"));
                stat.setDate(new Date(rs.getTimestamp("date").getTime()));
                list.add(stat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<QuizStatistic> findByQuestionId(int questionId) {
        List<QuizStatistic> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM statistic WHERE question_id=?")) {
            ps.setInt(1, questionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                QuizStatistic stat = new QuizStatistic();
                stat.setId(rs.getInt("id"));
                stat.setQuestionId(rs.getInt("question_id"));
                stat.setCorrect(rs.getBoolean("correct"));
                stat.setDate(new Date(rs.getTimestamp("date").getTime()));
                list.add(stat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
