package quizLogic;

import persistence.DataAccessObject;

/**
 * {@code Answer} represents a single possible answer to a {@link Question}.
 *
 * <p>
 * Responsibilities:
 * </p>
 * <ul>
 * <li>Stores the answer text</li>
 * <li>Flags whether this answer is correct</li>
 * <li>Maintains a transient reference to the owning {@link Question}</li>
 * </ul>
 *
 * <p>
 * Because {@code Answer} extends {@link DataAccessObject}, it also inherits a
 * unique persistence ID.
 * </p>
 *
 * <p>
 * <b>Serialization:</b><br>
 * The {@code question} reference is marked {@code transient} to avoid cycles
 * during serialization. Associations are re-established programmatically when
 * needed.
 * </p>
 */
public class Answer extends DataAccessObject {

	/** Serialization compatibility ID. */
	private static final long serialVersionUID = 1L;

	/** The text value of this answer option. */
	private String text;

	/** Flag indicating whether this answer is correct. */
	private boolean isCorrect;

	/**
	 * The parent question to which this answer belongs.
	 * <p>
	 * Declared as {@code transient} to prevent recursive serialization issues when
	 * saving/loading.
	 * </p>
	 */
	private transient Question question;

	/**
	 * Constructs a new {@code Answer} linked to a given {@link Question}.
	 *
	 * @param question the owning question
	 */
	public Answer(Question question) {
		super();
		this.question = question;
	}

	/**
	 * Returns the text of this answer.
	 *
	 * @return the answer text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Updates the text of this answer.
	 *
	 * @param text the new text content
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Checks whether this answer is marked as correct.
	 *
	 * @return {@code true} if this is the correct answer, else {@code false}
	 */
	public boolean isCorrect() {
		return isCorrect;
	}

	/**
	 * Sets the correctness flag for this answer.
	 *
	 * @param correct {@code true} if the answer is correct
	 */
	public void setCorrect(boolean correct) {
		this.isCorrect = correct;
	}

	/**
	 * Returns the owning question of this answer.
	 *
	 * @return the parent {@link Question}
	 */
	public Question getQuestion() {
		return question;
	}

	/**
	 * Assigns this answer to a specific question.
	 *
	 * @param question the parent question
	 */
	public void setQuestion(Question question) {
		this.question = question;
	}

	/**
	 * Returns a string representation of this answer.
	 * <p>
	 * Currently returns only the answer text.
	 * </p>
	 *
	 * @return the text of this answer
	 */
	@Override
	public String toString() {
		return text;
	}
}
