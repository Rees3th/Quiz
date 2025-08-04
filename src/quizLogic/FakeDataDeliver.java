package quizLogic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Diese Klasse generiert und liefert gefälschte Daten für Themen, Fragen und
 * Antworten. Sie dient hauptsächlich zu Testzwecken und zur Demonstration der
 * Funktionsweise.
 */

public class FakeDataDeliver {

	private Map<Integer, Thema> themaMap = new HashMap<>();
	private int themaIdCounter = 0;
	private int questionIdCounter = 0;
	private int answerIdCounter = 0;

	public FakeDataDeliver() {
		createThemes(5);

	}

	/**
	 * Konstruktor, der eine bestimmte Anzahl an Themen mit Fragen und Antworten
	 * generiert.
	 * 
	 * @param count Anzahl der zu erstellenden Themen
	 */

	private void createThemes(int count) {
		for (int i = 0; i < count; i++) {
			Thema th = new Thema();
			th.setId(themaIdCounter++);
			th.setTitle("Title of Thema " + th.getId());
			th.setText("Beschreibung zu Thema " + th.getId());

			createQuestionsFor(th);

			themaMap.put(th.getId(), th);
		}
	}

	/**
	 * Erstellt 6 Fragen für ein gegebenes Thema und fügt jeweils 4 Antworten hinzu.
	 * 
	 * @param thema Das Thema, für das die Fragen erstellt werden sollen
	 */

	private void createQuestionsFor(Thema thema) {
		for (int i = 0; i < 6; i++) {
			Question q = new Question(thema);
			q.setId(questionIdCounter++);
			q.setTitle("Frage zu " + q.getId() + " zu " + thema.getTitle());
			q.setText("Dies ist der Fragetext für Frage " + q.getId());

			createAnswersFor(q);

			thema.addQuestion(q);
		}
	}

	/**
	 * Erstellt 4 Antworten für eine gegebene Frage. Die erste Antwort ist immer
	 * korrekt.
	 * 
	 * @param question Die Frage, für die die Antworten erstellt werden sollen
	 */

	private void createAnswersFor(Question question) {
		for (int i = 0; i < 4; i++) {
			Answer a = new Answer(question);
			a.setId(getNextAnswerId());
			a.setText("Antwort " + a.getId() + " auf " + question.getTitle());
			a.setCorrect(i == 0);

			question.addAnswer(a);
		}
	}

	/**
	 * Gibt alle Themen zurück.
	 */
	public Collection<Thema> getAllThemen() {
		return themaMap.values();
	}

	/**
	 * Gibt ein Thema anhand der ID zurück.
	 */
	public Thema getThemaById(int id) {
		return themaMap.get(id);
	}

	/**
	 * Fügt ein neues Thema hinzu.
	 */
	public void addThema(Thema thema) {
		if (thema != null) {
			themaMap.put(thema.getId(), thema);
		}
	}

	/**
	 * Entfernt ein Thema anhand der ID.
	 */
	public void removeThema(int themaId) {
		themaMap.remove(themaId);
	}

	/**
	 * Vergibt eine neue eindeutige Themen-ID.
	 */
	public int getNextThemaId() {
		return themaIdCounter++;
	}

	/**
	 * Vergibt eine neue eindeutige Frage-ID.
	 */
	public int getNextQuestionId() {
		return questionIdCounter++;
	}

	/**
	 * Vergibt eine neue eindeutige Antwort-ID.
	 */
	public int getNextAnswerId() {
		return answerIdCounter++;
	}

	/**
	 * Löscht eine Frage anhand der ID aus allen Themen.
	 */
	public void deleteQuestion(int questionId) {
		for (Thema thema : themaMap.values()) {
			if (thema.getAllQuestions().stream().anyMatch(q -> q.getId() == questionId)) {
				thema.removeQuestionById(questionId);
				break;
			}
		}
	}

	/**
	 * Liefert eine zufällige Frage aus den vorhandenen Themen. TODO: Überprüfen
	 */
	public Question getRandomQuestion() {
		Random random = new Random();

		if (themaMap.isEmpty())
			return null;

		Object[] themenArray = themaMap.values().toArray();
		Thema thema = (Thema) themenArray[random.nextInt(themenArray.length)];
		if (thema.getAllQuestions().isEmpty())
			return null;

		Object[] fragenArray = thema.getAllQuestions().toArray();
		return (Question) fragenArray[random.nextInt(fragenArray.length)];
	}
}
