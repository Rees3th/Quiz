package quizLogic;

import java.util.List;

/**
 * {@code ThemeValidator} provides static validation logic for {@link Theme}
 * objects.
 *
 * <p>
 * Responsibilities:
 * </p>
 * <ul>
 * <li>Ensure that a theme has a non-empty title</li>
 * <li>Ensure that a theme has a non-empty description</li>
 * <li>Prevent duplicate titles within the list of existing themes</li>
 * <li>Provide standardized feedback messages for validation, saving, and
 * deletion</li>
 * </ul>
 *
 * <p>
 * This class is typically used before creating, updating, or deleting a
 * {@link Theme} to guarantee data consistency and provide user-facing feedback.
 * </p>
 */
public class ThemeValidator {

	// ------------------- Validation Error Messages -------------------

	/** Error message: no title provided. */
	public static final String MSG_NO_TITLE = "Bitte einen Titel eingeben!";

	/** Error message: duplicate theme title detected. */
	public static final String MSG_DUPLICATE_TITLE = "Es existiert bereits ein Thema mit diesem Namen!";

	/** Error message: no description provided. */
	public static final String MSG_NO_DESCRIPTION = "Bitte eine Beschreibung eingeben!";

	// ------------------- Action / Result Messages -------------------

	/** Prefix for save error messages. */
	public static final String MSG_SAVE_ERROR_PREFIX = "Fehler beim Speichern: ";

	/** Success message for saving a theme. */
	public static final String MSG_SAVE_SUCCESS = "Thema erfolgreich gespeichert.";

	/** Prefix for delete error messages. */
	public static final String MSG_DELETE_ERROR_PREFIX = "Fehler beim Löschen: ";

	/**
	 * Confirmation message template for deletion (with theme name substitution).
	 */
	public static final String MSG_DELETE_CONFIRM_PREFIX = "Thema \"%s\" wirklich löschen?";

	/** Title for the delete confirmation dialog. */
	public static final String MSG_DELETE_CONFIRM_TITLE = "Löschen bestätigen";

	// ------------------- Validation Logic -------------------

	/**
	 * Validates a theme's input data before saving.
	 *
	 * <p>
	 * Checks performed:
	 * </p>
	 * <ul>
	 * <li>Title must not be {@code null} or blank</li>
	 * <li>Description must not be {@code null} or blank</li>
	 * <li>Theme title must be unique among all existing themes (excluding the
	 * provided {@code exclude} theme, which is useful for updates)</li>
	 * </ul>
	 *
	 * @param titel       the title to validate
	 * @param description the description to validate
	 * @param allThemes   list of all existing themes
	 * @param exclude     optional theme to ignore when checking for duplicates
	 *                    (e.g. during update)
	 * @return {@code null} if validation succeeded, otherwise one of
	 *         {@link #MSG_NO_TITLE}, {@link #MSG_NO_DESCRIPTION}, or
	 *         {@link #MSG_DUPLICATE_TITLE}
	 */
	public static String validate(String titel, String description, List<Theme> allThemes, Theme exclude) {
		if (titel == null || titel.trim().isEmpty())
			return MSG_NO_TITLE;

		if (description == null || description.trim().isEmpty())
			return MSG_NO_DESCRIPTION;

		String lower = titel.trim().toLowerCase();
		for (Theme t : allThemes) {
			if (t != null && t != exclude && t.getTitle() != null && t.getTitle().trim().toLowerCase().equals(lower)) {
				return MSG_DUPLICATE_TITLE;
			}
		}
		return null; // OK
	}
}
