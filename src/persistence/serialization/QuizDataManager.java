package persistence.serialization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import persistence.QuizDataInterface;
import quizLogic.Answer;
import quizLogic.Question;
import quizLogic.Thema;

public class QuizDataManager implements QuizDataInterface {

//	private static final String FOLDER = "./quizData"; // Ordner für die Daten
//	private static final String FILE_PREFIX = FOLDER + "/Theme.";
	
	private static final String FOLDER = "C:\\Users\\OlegKapirulya\\eclipse-workspace2\\Quiz";
	private static final String FILE = FOLDER + "\\Theme.";

	private int nextQuestionId = 1;
	private int nextAnswerId = 1;

	public QuizDataManager() {
		// Sicherstellen, dass Ordner existiert
		File folder = new File(FOLDER);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		syncNextIds(); // IDs synchronisieren, um die nächsten freien IDs zu ermitteln
	}

	@Override
	public ArrayList<Thema> getAllThemen() {
		ArrayList<Thema> themen = new ArrayList<Thema>();
		
		File folder = new File(FOLDER);
		File[] files = folder.listFiles((dir, name) -> name.startsWith("Theme."));
		if (files != null) {
			for (File file : files) {
				try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
					Thema thema = (Thema) ois.readObject();
					themen.add(thema);
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return themen;
	}

	@Override
	public String saveTheme(Thema th) {
		if (th == null)
			return "Kein Thema zum Speichern";

		// ID generieren, falls nicht vergeben
		if (th.getId() == -1) {
			th.setId(createNewThemeId());
		}

		File file = new File(FILE+ th.getId());

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
			oos.writeObject(th);
			oos.flush();
			return null; // null bedeutet Erfolg
		} catch (IOException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	@Override
	public String deleteTheme(Thema th) {
		if (th == null || th.getId() == -1)
			return "Ungültiges Thema";

		File file = new File(FILE + th.getId());
		if (file.exists()) {
			if (file.delete()) {
				return null;
			} else {
				return "Löschen fehlgeschlagen";
			}
		} else {
			return "Thema-Datei nicht gefunden";
		}
	}

	/**
	 * Erstellt eine neue ID für ein Thema, indem es die höchste existierende ID
	 * ermittelt und 1 addiert.
	 * 
	 * @return Neue ID für das Thema
	 */
	private int createNewThemeId() {
		File folder = new File(FOLDER);
		File[] files = folder.listFiles((dir, name) -> name.startsWith("Theme."));
		int maxId = 0;
		if (files != null) {
			for (File file : files) {
				try {
					String name = file.getName(); // z.B. Theme.5
					String idStr = name.substring(name.indexOf('.') + 1);
					int id = Integer.parseInt(idStr);
					if (id > maxId)
						maxId = id;
				} catch (NumberFormatException e) {

				}
			}
		}
		return maxId + 1;
	}
	
	public static Thema readById(int id) {
		Thema theme = null;
		try {
			FileInputStream fileInputStream = new FileInputStream(FILE + id);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			theme = (Thema) objectInputStream.readObject();
			objectInputStream.close();
			return theme;
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return theme;
	}

	@Override
	public Question getRandomQuestion() {
		return null;
	}

	public ArrayList<Question> getQuestionsFor(Thema th) {
		if (th != null)
			return new ArrayList<>(th.getAllQuestions());
		return new ArrayList<>();
	}

	@Override
	public ArrayList<Answer> getAnswersFor(Question q) {
		if (q != null) {
			return new ArrayList<>(q.getAnswers());
		}
		return new ArrayList<>();
	}

	// Speichern einer Frage: Wir fügen sie dem Thema hinzu und speichern das Thema
	// neu
	@Override
	public String saveQuestion(Question q) {
		if (q == null || q.getThema() == null)
			return "Frage oder Thema ungültig";

		Thema thema = q.getThema();

		if (q.getId() == -1) {
			q.setId(getNextQuestionId());
		}

		for (Answer a : q.getAnswers()) {
			if (a.getId() == -1) {
				a.setId(getNextAnswerId());
			}
		}

		// Erst entfernen, dann neu hinzufügen
		thema.removeQuestionById(q.getId());
		thema.addQuestion(q);

		// Debug-Ausgabe vor speichern
		System.out.println("Frage '" + q.getTitle() + "' wird mit Antworten gespeichert:");
		for (Answer a : q.getAnswers()) {
			System.out.println(" - " + a.getText() + " (ID: " + a.getId() + ", korrekt: " + a.isCorrect() + ")");
		}

		return saveTheme(thema);
	}

	private void syncNextIds() {
		File folder = new File(FOLDER);
		File[] files = folder.listFiles((dir, name) -> name.startsWith("Theme."));
		int maxQ = 0;
		int maxA = 0;
		if (files != null) {
			for (File file : files) {
				try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
					Thema thema = (Thema) ois.readObject();
					for (Question q : thema.getAllQuestions()) {
						if (q.getId() > maxQ)
							maxQ = q.getId();
						for (Answer a : q.getAnswers()) {
							if (a.getId() > maxA)
								maxA = a.getId();
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		nextQuestionId = maxQ + 1;
		nextAnswerId = maxA + 1;
	}

	// Beim Löschen der Frage entfernen wir sie vom Thema und speichern neu
	@Override
	public String deleteQuestion(Question q) {
		if (q != null && q.getThema() != null) {
			Thema thema = q.getThema();
			thema.removeQuestionById(q.getId());
			return saveTheme(thema);
		}
		return "Frage oder Thema ungültig";
	}

	public int getNextQuestionId() {
		return nextQuestionId++;
	}

	public int getNextAnswerId() {
		return nextAnswerId++;
	}

}
