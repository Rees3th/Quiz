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
	 * Konstruktor für das QuizThemenPanel. Initialisiert alle Panel-Bestandteile,
	 * setzt das Layout und die Verknüpfungen.
	 */
	public QuizThemenPanel() {
		super();
		fdd = new FakeDataDeliver();
		setLayout(new BorderLayout(10, 10));
		initComponents();
		linkPanels();
		buildLayout();
		setupSelectionListener();
	}

	/** Initialisiert die Panel-Komponenten. */
	private void initComponents() {
		quizThemenLeft = new QuizThemenLeft();
		quizThemenRight = new QuizThemenRight(fdd.getAllThemen());
		quizThemenBottom = new QuizThemenBottom();
	}

	/**
	 * Verknüpft die Panels miteinander (Links-Rechts-Kommunikation, Delegation).
	 */
	private void linkPanels() {
		quizThemenRight.setPanelLeft(quizThemenLeft);
		quizThemenLeft.setPanelRight(quizThemenRight);
		quizThemenBottom.setDelegate(this);
	}

	/** Baut das Layout, indem die Panels eingefügt werden. */
	private void buildLayout() {
		add(quizThemenLeft, BorderLayout.WEST);
		add(quizThemenRight, BorderLayout.EAST);
		add(quizThemenBottom, BorderLayout.SOUTH);
	}

	/**
	 * Setzt einen Listener auf die Themenliste, um bei Auswahl die linke Ansicht zu
	 * aktualisieren.
	 */
	private void setupSelectionListener() {
		quizThemenRight.getThemaPanel().getThemenList().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				Thema selected = quizThemenRight.getThemaPanel().getThemenList().getSelectedValue();
				quizThemenLeft.setThema(selected);
			}
		});
	}

	/**
	 * Wird aufgerufen, wenn ein Thema gelöscht werden soll. Zeigt eine
	 * Bestätigungsdialog an und löscht das Thema bei Zustimmung.
	 */
	@Override
	public void onDeleteTheme() {
		Thema selected = quizThemenRight.getThemaPanel().getThemenList().getSelectedValue();
		if (selected == null)
			return;

		int result = JOptionPane.showConfirmDialog(this, "Thema \"" + selected.getTitle() + "\" wirklich löschen?",
				"Löschen bestätigen", JOptionPane.YES_NO_OPTION);

		if (result == JOptionPane.YES_OPTION) {
			fdd.removeThema(selected.getId());
			quizThemenRight.setThemen(fdd.getAllThemen());
			quizThemenLeft.clearFields();
			if (quizFragenPanel != null) {
				quizFragenPanel.reloadThemen();
			}
		}
	}

	/**
	 * Speichert ein Thema aus den Eingabefeldern. Erstellt ein neues Thema oder
	 * aktualisiert ein bestehendes, validiert die Eingaben.
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
			quizThemenLeft.setThema(neu);
		}
	}

	/**
	 * Setzt die Oberfläche auf ein neues Theme. Leert die Eingabefelder und
	 * entfernt jede Auswahl.
	 */
	@Override
	public void onNewTheme() {
		quizThemenRight.getThemaPanel().getThemenList().clearSelection();
		quizThemenLeft.clearFields();
	}

	/**
	 * Prüft, ob ein Thema mit dem übergebenen Titel bereits existiert.
	 * 
	 * @param titel   Titel des zu prüfenden Themas.
	 * @param exclude Thema, das ausgelassen werden soll (z.B. beim Bearbeiten).
	 * @return true, wenn ein anderes Thema mit diesem Titel existiert.
	 */
	private boolean titelExists(String titel, Thema exclude) {
		titel = titel.trim().toLowerCase();
		for (Thema t : fdd.getAllThemen()) {
			if (t == exclude)
				continue;
			if (t.getTitle().trim().toLowerCase().equals(titel)) {
				return true;
			}
		}
		return false;
	}

	/** Getter für die FakeDataDeliver-Instanz. */
	public FakeDataDeliver getDataDeliver() {
		return fdd;
	}

	/**
	 * Setter für das QuizFragenPanel, damit nach Themenänderung auch die
	 * Fragenansicht upgedatet werden kann.
	 */
	public void setQuizFragenPanel(QuizFragenPanel quizFragenPanel) {
		this.quizFragenPanel = quizFragenPanel;
	}
}
