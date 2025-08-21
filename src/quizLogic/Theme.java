package quizLogic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import persistence.serialization.DataAccessObject;

/**
 * {@code Theme} represents a quiz topic (or category).
 *
 * <p>
 * Responsibilities:
 * </p>
 * <ul>
 * <li>Stores metadata about the theme (title and descriptive text)</li>
 * <li>Aggregates all {@link Question}s that belong to this theme</li>
 * <li>Provides methods to add, remove, and retrieve questions</li>
 * </ul>
 *
 * <p>
 * Since it extends {@link DataAccessObject}, each theme has a unique
 * persistence {@code id}, which is also used for equality.
 * </p>
 */
public class Theme extends DataAccessObject {

	/** Serialization compatibility ID. */
	private static final long serialVersionUID = 1L;

	/** The display title of this theme. */
	private String title;

	/** Additional descriptive text for the theme. */
	private String text;

	/**
	 * Internal storage of questions belonging to this theme, indexed by their
	 * unique question IDs.
	 */
	private Map<Integer, Question> questionMap = new HashMap<>();

	/**
	 * Creates a new theme with no title and text.
	 */
	public Theme() {
		super();
	}

	/**
	 * Returns the theme's title.
	 *
	 * @return the title string
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title of the theme.
	 *
	 * @param title the new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns the descriptive text for this theme.
	 *
	 * @return the description text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets a descriptive text for this theme.
	 *
	 * @param text the new description
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Adds a question to this theme.
	 * <p>
	 * The question must have a valid id ({@code != -1}), otherwise it will not be
	 * stored.
	 * </p>
	 *
	 * @param q the {@link Question} to add
	 */
	public void addQuestion(Question q) {
		if (q != null && q.getId() != -1) {
			questionMap.put(q.getId(), q);
		}
	}

	/**
	 * Removes a question from this theme by its ID.
	 *
	 * @param id the ID of the question to remove
	 * @return {@code true} if the question was removed, {@code false} otherwise
	 */
	public boolean removeQuestionById(int id) {
		if (questionMap == null) {
			return false;
		}
		return questionMap.remove(id) != null;
	}

	/**
	 * Returns all questions that belong to this theme.
	 *
	 * @return a collection of {@link Question} objects
	 */
	public Collection<Question> getAllQuestions() {
		return questionMap.values();
	}

	/**
	 * String representation of this theme.
	 * <p>
	 * Currently returns only the title.
	 * </p>
	 *
	 * @return the theme title
	 */
	@Override
	public String toString() {
		return getTitle();
	}

	/**
	 * Equality is based on the persisted ID of a theme.
	 *
	 * @param obj another object to compare
	 * @return {@code true} if both represent the same theme ID
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Theme))
			return false;
		Theme other = (Theme) obj;
		return this.getId() == other.getId();
	}

	/**
	 * Hash code is computed from the theme's unique ID.
	 *
	 * @return hash code value
	 */
	@Override
	public int hashCode() {
		return Integer.hashCode(getId());
	}
}
