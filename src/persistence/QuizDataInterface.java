package persistence;

import java.util.ArrayList;

import quizLogic.Answer;
import quizLogic.Question;
import quizLogic.Theme;

public interface QuizDataInterface {

	public Question getRandomQuestion();

	public ArrayList<Theme> getAllThemen();

	public ArrayList<Question> getQuestionsFor(Theme th);

	public ArrayList<Answer> getAnswersFor(Question q);
	
	
	public String saveTheme(Theme th);
	public String deleteTheme(Theme th);
	
	public String saveQuestion(Question q);
	public String deleteQuestion(Question q);

}