package persistence.serialization;

import java.io.Serializable;

/**
 * {@code DataAccessObject} is a simple base class for all entities that are
 * persisted (e.g. {@code Theme}, {@code Question}, {@code Answer}).
 *
 * <p>
 * It provides:
 * </p>
 * <ul>
 * <li>A unique identifier field {@code id}, default value {@code -1}</li>
 * <li>Getter/setter for managing the ID</li>
 * <li>Serialization support (implements {@link Serializable})</li>
 * </ul>
 *
 * <p>
 * Subclasses are free to define additional properties but should rely on the
 * {@code id} field for persistence identity checks.
 * </p>
 * 
 * @author Oleg Kapirulya
 */
public class DataAccessObject implements Serializable {

	/** Serialization compatibility ID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Unique identifier for this entity. Default is {@code -1} (not yet persisted).
	 */
	private int id = -1;

	/**
	 * Returns the unique identifier of this entity.
	 *
	 * @return the entity ID, or {@code -1} if not yet assigned
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the unique identifier of this entity.
	 *
	 * <p>
	 * Typically assigned by persistence mechanisms such as a database
	 * (auto-increment) or serialization manager.
	 * </p>
	 *
	 * @param id the new ID to assign
	 */
	public void setId(int id) {
		this.id = id;
	}
}
