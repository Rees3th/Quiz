package quizLogic;

/** Diese Klasse dient zum Testen der FakeDataDeliver-Klasse.
 * Sie gibt alle Themen und die zugehörigen Fragen in der Konsole aus.
 */

public class FakeDataMain {

	/** Hauptmethode zum Testen der FakeDataDeliver-Klasse.
	 * @param args 
	 */
	public static void main(String[] args) {

		FakeDataDeliver fdd = new FakeDataDeliver();
		// Ausgabe aller Themen
		for (Thema thema : fdd.getAllThemen()) {
			System.out.println(thema.toString());

			// Ausgabe der Fragen zu jedem Thema
			for (Question q : thema.getAllQuestions()) {
				System.out.println("  " + q.toString());
			}
		}
	}
}
