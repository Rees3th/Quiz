package gui.QuizThemen;

import java.awt.BorderLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import gui.QuizFragen.QuizFragenPanel;
import quizLogic.FakeDataDeliver;
import quizLogic.Thema;

/**
 * Panel für die Themenverwaltung im Quiz. Enthält die linken, rechten und
 * unteren Panels für die Themenanzeige und -bearbeitung. Implementiert das
 * QuizThemenDelegate-Interface für die Interaktion mit dem unteren Panel.
 */

public class QuizThemenPanel extends JPanel implements QuizThemenDelegate {

	private static final long serialVersionUID = 1L;
	private QuizThemenRight quizThemenRight;
	private QuizThemenBottom quizThemenBottom;
	private QuizThemenLeft quizThemenLeft;
	private QuizFragenPanel quizFragenPanel;
	private FakeDataDeliver fdd;

	/**
	 * Konstruktor für das QuizThemenPanel. Initialisiert die Panels und setzt die
	 * Layouts.
	 */

	public QuizThemenPanel() {
		super();
		setLayout(new BorderLayout(10, 10));

		// Initialisiere die FakeDataDeliver-Klasse, die die Themen verwaltet
		fdd = new FakeDataDeliver();

		// Initialisiere die Panels für die Themenverwaltung
		quizThemenLeft = new QuizThemenLeft();
		quizThemenRight = new QuizThemenRight(fdd.getAllThemen());
		quizThemenBottom = new QuizThemenBottom();

		// Setze die Referenzen zwischen den Panels
		quizThemenRight.setPanelLeft(quizThemenLeft);
		quizThemenLeft.setPanelRight(quizThemenRight);

		// Setze das Layout und füge die Panels hinzu
		add(quizThemenLeft, BorderLayout.WEST);
		add(quizThemenRight, BorderLayout.EAST);
		add(quizThemenBottom, BorderLayout.SOUTH);

		quizThemenBottom.setDelegate(this);

		// Wenn ein Thema in der rechten Liste ausgewählt wird, aktualisiere die linken Felder
		quizThemenRight.getThemaPanel().getThemenList().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				Thema selected = quizThemenRight.getThemaPanel().getThemenList().getSelectedValue();
				quizThemenLeft.setThemaData(selected);
			}
		});
	}

	/**
	 * Methode, die aufgerufen wird, wenn ein Thema gelöscht werden soll. Zeigt eine
	 * Bestätigungsdialog an und löscht das Thema, wenn bestätigt.
	 */
	@Override
	public void onDeleteTheme() {
		Thema selected = quizThemenRight.getThemaPanel().getThemenList().getSelectedValue();
		int result = JOptionPane.showConfirmDialog(this, "Thema \"" + selected.getTitle() + "\" wirklich löschen?",
				"Löschen bestätigen", JOptionPane.YES_NO_OPTION);
		if (result == JOptionPane.YES_OPTION) {
			fdd.removeThema(selected.getId());

			// GUI aktualisieren
			fdd.removeThema(selected.getId());
			quizThemenRight.setThemen(fdd.getAllThemen());
			quizThemenLeft.clearFields();
			if (quizFragenPanel != null) {
				quizFragenPanel.reloadThemen();
			}
		}
	}

	/**
	 * Speichert das Thema basierend auf den Eingabefeldern im linken Panel.
	 * Erstellt ein neues Thema oder aktualisiert ein bestehendes. Validiert die
	 * Eingaben und zeigt Fehlermeldungen an, wenn nötig.
	 */
	@Override
	public void onSaveTheme() {
		String titel = quizThemenLeft.getTitelField().getText().trim();
		String info = quizThemenLeft.getInfoArea().getText();
		Thema selected = quizThemenRight.getThemaPanel().getThemenList().getSelectedValue();

		if (titel.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Titel darf nicht leer sein.", "Fehler", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (titelExists(titel, selected)) {
			JOptionPane.showMessageDialog(this, "Es existiert bereits ein Thema mit diesem Namen!", "Fehler",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (selected != null) {
			selected.setTitle(titel);
			selected.setText(info);
			quizThemenRight.getThemaPanel().getThemenList().repaint();
		} else {
			Thema neu = new Thema();
			neu.setId(fdd.getNextThemaId());
			neu.setTitle(titel);
			neu.setText(info);

			fdd.addThema(neu);

			quizThemenRight.setThemen(fdd.getAllThemen());
			if (quizFragenPanel != null) {
				quizFragenPanel.reloadThemen();
			}
			quizThemenRight.getThemaPanel().getThemenList().setSelectedValue(neu, true);

			quizThemenLeft.setThemaData(neu);

		}
	}
	
	
	/**
	 * Methode, die aufgerufen wird, wenn ein neues Thema erstellt werden soll.
	 * Löscht die Eingabefelder im linken Panel und setzt den Fokus auf das Titel-Feld.
	 */
	@Override
	public void onNewTheme() {
		quizThemenRight.getThemaPanel().getThemenList().clearSelection();
		quizThemenLeft.clearFields();

	}
	
	/**
	 * Überprüft, ob ein Thema mit dem angegebenen Titel bereits existiert.
	 * 
	 * @param titel   Der Titel des Themas, das überprüft werden soll.
	 * @param exclude Das Thema, das ausgeschlossen werden soll (z.B. beim Bearbeiten).
	 * @return true, wenn ein Thema mit dem Titel existiert, sonst false.
	 */
	
	private boolean titelExists(String titel, Thema exclude) {
		titel = titel.trim().toLowerCase();
		for (Thema t : fdd.getAllThemen()) {
			if (t == exclude)
				continue; // beim Bearbeiten: sich selbst ignorieren
			if (t.getTitle().trim().toLowerCase().equals(titel)) {
				return true;
			}
		}
		return false;
	}
	
	// Getter für die FakeDataDeliver-Instanz, die die Themen verwaltet.
	public FakeDataDeliver getDataDeliver() {
		return fdd; 
	}
	
	// Setter für das QuizFragenPanel, um die Themenliste zu aktualisieren
	public void setQuizFragenPanel(QuizFragenPanel quizFragenPanel) {
		this.quizFragenPanel = quizFragenPanel;
	}

}
