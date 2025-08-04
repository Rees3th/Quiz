package gui.QuizThemen;
	
/** Interface für die Kommunikation zwischen der QuizThemen-Ansicht und der QuizThemen-Logik.
 * Diese Schnittstelle definiert Methoden, die aufgerufen werden, wenn ein Thema gelöscht,
 * gespeichert oder neu erstellt wird.
 */

public interface QuizThemenDelegate {
	void onDeleteTheme();
	void onSaveTheme();
	void onNewTheme();
}
