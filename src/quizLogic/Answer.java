package quizLogic;

import persistence.DataAccessObject;

/**
 * DIese Klasse stellt eine Antwort zu einer Frage dar. Sie enthält den Text der
 * Antwort, ob sie korrekt ist und die zugehörige Frage.
 */
public class Answer extends DataAccessObject {

	private static final long serialVersionUID = 1L;
	private String text;
	private boolean isCorrect;
	private transient Question question;

	public Answer(Question question) {
		super();
		this.question = question;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isCorrect() {
		return isCorrect;
	}

	public void setCorrect(boolean correct) {
		this.isCorrect = correct;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	@Override
	public String toString() {
		return text;
	}
}
