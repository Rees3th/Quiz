package gui.QuizFragen;
	
/** Interface für die Delegation von Aktionen in der QuizFragen-Komponente.
 * Diese Schnittstelle definiert Methoden, die aufgerufen werden, wenn
 * eine Frage gelöscht, gespeichert oder eine neue Frage erstellt wird.
 */

public interface QuizFragenDelegate {
	
	void onDeleteQuestion();
	void onSaveQuestion();
	void onNewQuestion();

}
