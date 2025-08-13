package quizLogic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import persistence.DataAccessObject;

/**
 * Diese Klasse repräsentiert ein Thema in einem Quiz. Jedes Thema kann mehrere
 * Fragen enthalten.
 * 
 */
public class Thema extends DataAccessObject {

	private static final long serialVersionUID = 1L;
	private String title;
	private String text;

	private Map<Integer, Question> questionMap = new HashMap<>();

	public Thema() {
		super();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void addQuestion(Question q) {
		if (q != null && q.getId() != -1) {
			questionMap.put(q.getId(), q);
		}
	}

	public boolean removeQuestionById(int id) {
		if (questionMap == null)
			return false;
		return questionMap.remove(id) != null;
	}

	public Collection<Question> getAllQuestions() {
		return questionMap.values();
	}

	@Override
	public String toString() {
		return getTitle();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Thema other = (Thema) obj;
		return this.getId() == other.getId();
	}

	@Override
	public int hashCode() {
		return Integer.hashCode(getId());
	}

}
