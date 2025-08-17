package quizLogic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import persistence.DataAccessObject;

/**
 * {@code Question} represents a single quiz question.
 *
 * <p>
 * Responsibilities:
 * </p>
 * <ul>
 * <li>Stores metadata such as <b>title</b> and <b>text</b> of the question</li>
 * <li>Is associated with a parent {@link Theme}</li>
 * <li>Manages its possible {@link Answer}s using an internal map keyed by
 * answer ID</li>
 * </ul>
 *
 * <p>
 * Since it extends {@link DataAccessObject}, every question has a unique ID.
 * The ID is also used in {@link #equals(Object)} and {@link #hashCode()} for
 * identity comparison.
 * </p>
 */
public class Question extends DataAccessObject {

	/** Serialization compatibility ID. */
	private static final long serialVersionUID = 1L;

	/** Short title/label for the question. */
	private String title;

	/** Full text of the question. */
	private String text;

	/** Parent theme this question belongs to. */
	private Theme thema;

	/**
	 * Internal map of answers keyed by answer IDs, ensuring stable order
	 * (LinkedHashMap preserves insertion order).
	 */
	private Map<Integer, Answer> answerMap = new LinkedHashMap<>();

	/**
	 * Constructs a new question associated with a given theme.
	 *
	 * @param thema the {@link Theme} this question belongs to
	 */
	public Question(Theme thema) {
		super();
		this.thema = thema;
	}

	/**
	 * Returns the question's title (short description).
	 *
	 * @return the question title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the question's title.
	 *
	 * @param title the new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns the main text of the question.
	 *
	 * @return the full question text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the main question text.
	 *
	 * @param text the new question text
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Returns the parent theme this question belongs to.
	 *
	 * @return the {@link Theme} of this question
	 */
	public Theme getThema() {
		return thema;
	}

	/**
	 * Assigns this question to a different theme.
	 *
	 * @param thema the new parent {@link Theme}
	 */
	public void setThema(Theme thema) {
		this.thema = thema;
	}

	/**
	 * Adds an answer to this question.
	 * <p>
	 * The answer is stored in an internal map keyed by its {@code id}. If an answer
	 * with the same ID already exists, it will be replaced.
	 * </p>
	 *
	 * @param a the {@link Answer} to add
	 */
	public void addAnswer(Answer a) {
		if (a != null) {
			answerMap.put(a.getId(), a);
		}
	}

	/**
	 * Returns all answers of this question.
	 *
	 * @return a new {@link List} containing all {@link Answer}s
	 */
	public List<Answer> getAnswers() {
		return new ArrayList<>(answerMap.values());
	}

	/**
	 * Removes all answers from this question.
	 */
	public void clearAnswers() {
		answerMap.clear();
	}

	/**
	 * Returns a string representation of this question.
	 * <p>
	 * Currently returns only the title.
	 * </p>
	 *
	 * @return the title of this question
	 */
	@Override
	public String toString() {
		return getTitle();
	}

	/**
	 * Two {@code Question} objects are considered equal if their IDs are identical.
	 *
	 * @param obj the object to compare
	 * @return {@code true} if both represent the same database entity
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Question))
			return false;
		Question other = (Question) obj;
		return getId() == other.getId();
	}

	/**
	 * Hash code is based solely on the persistence ID.
	 *
	 * @return hash code derived from {@code id}
	 */
	@Override
	public int hashCode() {
		return Integer.hashCode(getId());
	}
}
