package quizLogic;

import java.util.Date;

public class QuizStatistic {
    private int id;
    private int questionId;
    private boolean correct;
    private Date date;

    public QuizStatistic() {}

    public QuizStatistic(int questionId, boolean correct, Date date) {
        this.questionId = questionId;
        this.correct = correct;
        this.date = date;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	public boolean isCorrect() {
		return correct;
	}

	public void setCorrect(boolean correct) {
		this.correct = correct;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
