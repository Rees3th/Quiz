package quizLogic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Diese Klasse repräsentiert eine Frage in einem Quiz. Sie enthält den Titel,
 * den Text der Frage, das zugehörige Thema und eine Map von Antworten, die der
 * Frage zugeordnet sind.
 */
public class Question extends QObjekt {

	private String title;
	private String text;
	private Thema thema;

	// Map von Antworten: Key = ID, Value = Answer Objekt
	private Map<Integer, Answer> answerMap = new HashMap<>();

	public Question(Thema thema) {
		super();
		this.thema = thema;
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

	public Thema getThema() {
		return thema;
	}

	public void setThema(Thema thema) {
		this.thema = thema;
	}

	/**
	 * Fügt eine Antwort zu dieser Frage hinzu.
	 * 
	 * @param a Die Antwort, die hinzugefügt werden soll.
	 */
	public void addAnswer(Answer a) {
		if (a != null) {
			answerMap.put(a.getId(), a);
		}
	}

	public Answer getAnswerById(int id) {
		return answerMap.get(id);
	}

	public void removeAnswerById(int id) {
		answerMap.remove(id);
	}

	public Collection<Answer> getAnswers() {
		return answerMap.values();
	}

	@Override
	public String toString() {
		return getTitle();
	}

	/**
	 * Gibt die Anzahl der Antworten zurück, die dieser Frage zugeordnet sind.
	 * 
	 * @return Anzahl der Antworten.
	 * TODO: Überprüfen
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Question other = (Question) obj;
		return this.getId() == other.getId(); 
	}
	
	/**
	 * Berechnet den Hashcode für diese Frage basierend auf der ID.
	 * 
	 * @return Hashcode der Frage.
	 */
	@Override
	public int hashCode() {
		return Integer.hashCode(getId());
	}

}
