package gui.Panels;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import quizLogic.Question;
import quizLogic.Thema;

/**
 * Panel mit ComboBox für Themen und Liste der Fragen. Umschalten der Ansicht
 * zwischen Fragenliste und Themeninfo.
 */
public class ThemaFragenPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JComboBox<Thema> themaComboBox;
	private JList<Question> fragenList;
	private DefaultListModel<Question> fragenModel;
	private Collection<Thema> allThemen;
	private JButton themaInfoButton;
	private JPanel infoPanel;
	private JLabel infoTitelLbl;
	private JTextArea infoArea;
	private JPanel listPanel;
	private JPanel centerPanel;
	private CardLayout cardLayout;
	private boolean showingInfo = false;
	private JLabel fragenLabel;

	/**
	 * Statisches Thema, das alle Themen repräsentiert. Wird in der ComboBox als
	 * erstes Element angezeigt.
	 */
	public static final Thema ALLE_THEMEN = new Thema() {
		@Override
		public String toString() {
			return "Alle Themen";
		}
	};

	/**
	 * Konstruktor für das ThemaFragenPanel. Initialisiert UI-Komponenten, Layout
	 * sowie Listener und füllt die Fragenliste.
	 * 
	 * @param themen Sammlung von Themen, die in der ComboBox angezeigt werden
	 *               sollen.
	 */
	public ThemaFragenPanel(Collection<Thema> themen) {
		this.allThemen = themen;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
		initComponents();
		layoutComponents();
		setupListeners();
		fillFragenList();
	}

	/**
	 * Initialisiert die UI-Komponenten wie ComboBox, Buttons und Panels.
	 */
	private void initComponents() {
		themaComboBox = new JComboBox<>();
		themaComboBox.addItem(ALLE_THEMEN);
		if (allThemen != null) {
			for (Thema t : allThemen) {
				themaComboBox.addItem(t);
			}
		}
		themaComboBox.setPreferredSize(new Dimension(260, 30));

		fragenModel = new DefaultListModel<>();
		fragenList = new JList<>(fragenModel);
		fragenList.setVisibleRowCount(10);

		themaInfoButton = new JButton("Thema anzeigen");

		infoTitelLbl = new JLabel();
		infoArea = new JTextArea(4, 26);
		infoArea.setEditable(false);
		infoArea.setLineWrap(true);
		infoArea.setWrapStyleWord(true);

		infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		infoPanel.add(infoTitelLbl);
		infoPanel.add(Box.createVerticalStrut(5));
		infoPanel.add(new JScrollPane(infoArea));

		listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		listPanel.add(new JScrollPane(fragenList));

		centerPanel = new JPanel(new CardLayout());
		cardLayout = (CardLayout) centerPanel.getLayout();
		centerPanel.add(listPanel, "FRAGEN");
		centerPanel.add(infoPanel, "INFO");
	}

	/**
	 * Positioniert die Komponenten innerhalb des Panels.
	 */
	private void layoutComponents() {
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
		fragenLabel = new JLabel("Fragen zum Thema");
		headerPanel.add(fragenLabel);
		headerPanel.add(Box.createHorizontalGlue());
		headerPanel.add(themaInfoButton);
		headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		add(headerPanel);
		JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		comboPanel.add(themaComboBox);
		comboPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(comboPanel);
		add(centerPanel);
	}

	/**
	 * Setzt ActionListener für ComboBox und Button zur Themen- bzw. Fragenanzeige.
	 */
	private void setupListeners() {
		themaComboBox.addActionListener(e -> {
			if (!showingInfo) {
				fillFragenList();
			} else {
				updateThemaInfo((Thema) themaComboBox.getSelectedItem());
			}
		});

		themaInfoButton.addActionListener(e -> toggleInfoAnzeige());
	}

	/**
	 * Füllt die Fragenliste basierend auf dem aktuell ausgewählten Thema. Bei
	 * Auswahl "Alle Themen" werden alle Fragen aus allen Themen angezeigt.
	 */
	public void fillFragenList() {
		fragenModel.clear();
		Thema selected = (Thema) themaComboBox.getSelectedItem();
		if (selected == ALLE_THEMEN) {
			for (Thema t : allThemen) {
				if (t == ALLE_THEMEN)
					continue;
				if (t.getAllQuestions() != null) {
					for (Question q : t.getAllQuestions()) {
						fragenModel.addElement(q);
					}
				}
			}
		} else if (selected != null && selected.getAllQuestions() != null) {
			for (Question q : selected.getAllQuestions()) {
				fragenModel.addElement(q);
			}
		}
	}

	/**
	 * Aktualisiert die Anzeige des Themen-Info-Titels und -Textes.
	 * 
	 * @param thema Das Thema, dessen Beschreibung angezeigt wird.
	 */
	private void updateThemaInfo(Thema thema) {
		if (thema != null) {
			infoArea.setText(thema.getText() != null ? thema.getText() : "");
			infoTitelLbl.setText(thema.getTitle());
		}
	}

	/**
	 * Umschaltet zwischen der Anzeige der Fragenliste und der Themeninfo. Bei
	 * Umschaltung wird die Ansicht gewechselt und der Button-Text angepasst.
	 */
	private void toggleInfoAnzeige() {
		showingInfo = !showingInfo;
		if (showingInfo) {
			updateThemaInfo((Thema) themaComboBox.getSelectedItem());
			cardLayout.show(centerPanel, "INFO");
			themaInfoButton.setText("Liste anzeigen");
		} else {
			cardLayout.show(centerPanel, "FRAGEN");
			themaInfoButton.setText("Thema anzeigen");
		}
		revalidate();
		repaint();
	}

	/**
	 * Setzt die Themen in der ComboBox neu und aktualisiert die Fragenliste.
	 * 
	 * @param neueThemen Neue Sammlung von Themen, die angezeigt werden sollen.
	 */
	public void setThemen(Collection<Thema> neueThemen) {
		themaComboBox.removeAllItems();
		themaComboBox.addItem(ALLE_THEMEN);
		if (neueThemen != null) {
			for (Thema t : neueThemen) {
				if (t == ALLE_THEMEN)
					continue;
				themaComboBox.addItem(t);
			}
		}
		this.allThemen = neueThemen;
		fillFragenList();
	}

	/**
	 * Entfernt eine Frage aus der Liste anhand des Index.
	 * 
	 * @param index Index der zu entfernenden Frage.
	 */
	public void removeQuestionAt(int index) {
		if (index >= 0 && index < fragenModel.size()) {
			fragenModel.remove(index);
		}
	}

	/**
	 * Fügt eine Frage zur Fragenliste hinzu.
	 * 
	 * @param question Die Frage, die hinzugefügt werden soll.
	 */
	public void addQuestion(Question question) {
		fragenModel.addElement(question);
	}

	// Getter-Methoden

	public JList<Question> getFragenList() {
		return fragenList;
	}

	public JComboBox<Thema> getThemaComboBox() {
		return themaComboBox;
	}

	public JButton getThemaInfoButton() {
		return themaInfoButton;
	}

	public JLabel getInfoTitelLbl() {
		return infoTitelLbl;
	}

	public JLabel getFragenLabel() {
		return fragenLabel;
	}

	public void setFragenLabel(JLabel fragenLabel) {
		this.fragenLabel = fragenLabel;
	}
}
