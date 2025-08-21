//package persistence.serialization;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.util.ArrayList;
//
//import persistence.QuizDataInterface;
//import quizLogic.Answer;
//import quizLogic.Question;
//import quizLogic.Theme;
//
///**
// * {@code QuizDataManager} is a legacy persistence implementation based on
// * **Java object serialization** instead of a database.
// *
// * <p>
// * Responsibilities:
// * </p>
// * <ul>
// * <li>Serialize and deserialize {@link Theme}, {@link Question}, and
// * {@link Answer} objects to disk</li>
// * <li>Manage unique IDs for {@link Question} and {@link Answer} entities</li>
// * <li>Provide CRUD-like operations for themes and questions via serialized
// * files</li>
// * </ul>
// *
// * <p>
// * This implementation stores each {@link Theme} as a separate file named
// * <code>Theme.&lt;id&gt;</code> inside a configured folder.
// * </p>
// *
// * <p>
// * <b>Note:</b> This class is no longer used in the main application, as
// * {@link persistence.DBDataManager} with a relational DB has replaced it. It is
// * retained for reference and fallback purposes.
// * </p>
//
//@author Oleg Kapirulya
// */
//public class QuizDataManager implements QuizDataInterface {
//
//	/** Folder in which serialized theme files are stored. */
//	private static final String FOLDER = "C:\\Users\\OlegKapirulya\\eclipse-workspace2\\Quiz";
//
//	/** Filename prefix for theme serialization. */
//	private static final String FILE = FOLDER + "\\Theme.";
//
//	/** Counter for generating unique question IDs. */
//	private int nextQuestionId = 1;
//
//	/** Counter for generating unique answer IDs. */
//	private int nextAnswerId = 1;
//
//	/**
//	 * Constructs a file-based QuizDataManager.
//	 * <ul>
//	 * <li>Ensures the target folder exists</li>
//	 * <li>Synchronizes ID counters with existing serialized data</li>
//	 * </ul>
//	 */
//	public QuizDataManager() {
//		File folder = new File(FOLDER);
//		if (!folder.exists()) {
//			folder.mkdirs();
//		}
//		syncNextIds(); // Compute next available IDs based on stored files
//	}
//
//	/** {@inheritDoc} */
//	@Override
//	public ArrayList<Theme> getAllThemen() {
//		ArrayList<Theme> themen = new ArrayList<>();
//		File folder = new File(FOLDER);
//
//		File[] files = folder.listFiles((dir, name) -> name.startsWith("Theme."));
//		if (files != null) {
//			for (File file : files) {
//				try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
//					Theme thema = (Theme) ois.readObject();
//					themen.add(thema);
//				} catch (IOException | ClassNotFoundException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		return themen;
//	}
//
//	/** {@inheritDoc} */
//	@Override
//	public String saveTheme(Theme th) {
//		if (th == null) {
//			return "No theme to save";
//		}
//
//		// Generate ID if not yet assigned
//		if (th.getId() == -1) {
//			th.setId(createNewThemeId());
//		}
//
//		File file = new File(FILE + th.getId());
//
//		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
//			oos.writeObject(th);
//			oos.flush();
//			return null; // null = success
//		} catch (IOException e) {
//			e.printStackTrace();
//			return e.getMessage();
//		}
//	}
//
//	/** {@inheritDoc} */
//	@Override
//	public String deleteTheme(Theme th) {
//		if (th == null || th.getId() == -1) {
//			return "Invalid theme";
//		}
//
//		File file = new File(FILE + th.getId());
//		if (file.exists()) {
//			return file.delete() ? null : "Delete failed";
//		} else {
//			return "Theme file not found";
//		}
//	}
//
//	/**
//	 * Generates a new theme ID by scanning existing theme files and incrementing
//	 * the maximum ID found.
//	 *
//	 * @return a new unique theme ID
//	 */
//	private int createNewThemeId() {
//		File folder = new File(FOLDER);
//		File[] files = folder.listFiles((dir, name) -> name.startsWith("Theme."));
//		int maxId = 0;
//
//		if (files != null) {
//			for (File file : files) {
//				try {
//					String name = file.getName(); // e.g. Theme.5
//					String idStr = name.substring(name.indexOf('.') + 1);
//					int id = Integer.parseInt(idStr);
//					if (id > maxId) {
//						maxId = id;
//					}
//				} catch (NumberFormatException ignored) {
//				}
//			}
//		}
//		return maxId + 1;
//	}
//
//	/**
//	 * Reads a single theme by its ID from disk.
//	 *
//	 * @param id the theme ID
//	 * @return the deserialized {@link Theme}, or {@code null} if not found
//	 */
//	public static Theme readById(int id) {
//		Theme theme = null;
//		try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(FILE + id))) {
//			theme = (Theme) objectInputStream.readObject();
//			return theme;
//		} catch (ClassNotFoundException | IOException e) {
//			e.printStackTrace();
//		}
//		return theme;
//	}
//
//	/** {@inheritDoc} */
//	@Override
//	public Question getRandomQuestion() {
//		// Legacy implementation does not support random selection
//		return null;
//	}
//
//	/**
//	 * Returns all questions for the given theme (from in-memory object only).
//	 * Serialized storage only operates at theme-level.
//	 *
//	 * @param th the theme to inspect
//	 * @return list of {@link Question} contained in this theme
//	 */
//	public ArrayList<Question> getQuestionsFor(Theme th) {
//		if (th != null) {
//			return new ArrayList<>(th.getAllQuestions());
//		}
//		return new ArrayList<>();
//	}
//
//	/** {@inheritDoc} */
//	@Override
//	public ArrayList<Answer> getAnswersFor(Question q) {
//		if (q != null) {
//			return new ArrayList<>(q.getAnswers());
//		}
//		return new ArrayList<>();
//	}
//
//	/** {@inheritDoc} */
//	@Override
//	public String saveQuestion(Question q) {
//		if (q == null || q.getThema() == null) {
//			return "Invalid question or theme";
//		}
//
//		Theme thema = q.getThema();
//
//		// Assign IDs if needed
//		if (q.getId() == -1) {
//			q.setId(getNextQuestionId());
//		}
//		for (Answer a : q.getAnswers()) {
//			if (a.getId() == -1) {
//				a.setId(getNextAnswerId());
//			}
//		}
//
//		// Replace old version of question with updated one
//		thema.removeQuestionById(q.getId());
//		thema.addQuestion(q);
//
//		// Debug logging
//		System.out.println("Saving question '" + q.getTitle() + "' with answers:");
//		for (Answer a : q.getAnswers()) {
//			System.out.println(" - " + a.getText() + " (ID: " + a.getId() + ", correct: " + a.isCorrect() + ")");
//		}
//
//		return saveTheme(thema);
//	}
//
//	/**
//	 * Synchronizes ID counters for questions and answers by scanning all serialized
//	 * themes on disk.
//	 */
//	private void syncNextIds() {
//		File folder = new File(FOLDER);
//		File[] files = folder.listFiles((dir, name) -> name.startsWith("Theme."));
//		int maxQ = 0;
//		int maxA = 0;
//
//		if (files != null) {
//			for (File file : files) {
//				try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
//					Theme thema = (Theme) ois.readObject();
//					for (Question q : thema.getAllQuestions()) {
//						if (q.getId() > maxQ)
//							maxQ = q.getId();
//						for (Answer a : q.getAnswers()) {
//							if (a.getId() > maxA)
//								maxA = a.getId();
//						}
//					}
//				} catch (Exception ex) {
//					ex.printStackTrace();
//				}
//			}
//		}
//		nextQuestionId = maxQ + 1;
//		nextAnswerId = maxA + 1;
//	}
//
//	/** {@inheritDoc} */
//	@Override
//	public String deleteQuestion(Question q) {
//		if (q != null && q.getThema() != null) {
//			Theme thema = q.getThema();
//			thema.removeQuestionById(q.getId());
//			return saveTheme(thema);
//		}
//		return "Invalid question or theme";
//	}
//
//	/**
//	 * Returns and increments the next available question ID.
//	 *
//	 * @return next unique question ID
//	 */
//	public int getNextQuestionId() {
//		return nextQuestionId++;
//	}
//
//	/**
//	 * Returns and increments the next available answer ID.
//	 *
//	 * @return next unique answer ID
//	 */
//	public int getNextAnswerId() {
//		return nextAnswerId++;
//	}
//}
