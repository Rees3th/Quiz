package persistence;

import java.util.ArrayList;

import quizLogic.Answer;
import quizLogic.Question;
import quizLogic.Thema;

public interface QuizDataInterface {

	public Question getRandomQuestion();

	public ArrayList<Thema> getAllThemen();

	public ArrayList<Question> getQuestionsFor(Thema th);

	public ArrayList<Answer> getAnswersFor(Question q);
	
	
	public String saveTheme(Thema th);
	public String deleteTheme(Thema th);
	
	public String saveQuestion(Question q);
	public String deleteQuestion(Question q);

}