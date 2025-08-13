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
import javax.swing.SwingConstants;

import quizLogic.Question;
import quizLogic.Thema;

/**
 * ThemaFragenPanel zeigt eine ComboBox für Themen und eine Liste der Fragen zu
 * dem ausgewählten Thema. Es aktualisiert die Fragenliste, wenn ein neues Thema
 * ausgewählt wird. Außerdem kann man zwischen Fragenliste und Themeninfo mit
 * einem Button umschalten.
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
	
	private JPanel feedbackPanel;
	private JLabel feedbackLabel;


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
	 * Konstruktor für das ThemaFragenPanel.
	 * 
	 * @param themen Sammlung von Themen, die in der ComboBox angezeigt werden
	 *               sollen.
	 */

	public ThemaFragenPanel(Collection<Thema> themen) {
		this.allThemen = themen;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

		// Header: Label + Button in einer Zeile
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
		setFragenLabel(new JLabel("Fragen zum Thema"));
		headerPanel.add(getFragenLabel());
		headerPanel.add(Box.createHorizontalGlue());
		themaInfoButton = new JButton("Thema anzeigen");
		headerPanel.add(themaInfoButton);
		headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(headerPanel);

		// ComboBox-Panel
		JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		themaComboBox = new JComboBox<>();
		themaComboBox.addItem(ALLE_THEMEN);
		if (themen != null) {
			for (Thema t : themen) {
				themaComboBox.addItem(t);
			}
		}
		themaComboBox.setPreferredSize(new Dimension(260, 30));
		comboPanel.add(themaComboBox);
		comboPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(comboPanel);

		// InfoPanel für Themenbeschreibung
		infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

		infoTitelLbl = new JLabel();
		infoArea = new JTextArea(4, 26);
		infoArea.setEditable(false);
		infoArea.setLineWrap(true);
		infoArea.setWrapStyleWord(true);
		infoPanel.add(infoTitelLbl);
		infoPanel.add(Box.createVerticalStrut(5));
		infoPanel.add(new JScrollPane(infoArea));
		infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		// Fragenliste mit Model und ScrollPane
		fragenModel = new DefaultListModel<>();
		fragenList = new JList<>(fragenModel);
		fragenList.setVisibleRowCount(10);
		JScrollPane scrollPane = new JScrollPane(fragenList);
		scrollPane.setPreferredSize(new Dimension(260, 310));
		
	

		// ListPanel für die Fragenliste
		listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		listPanel.add(scrollPane);
		listPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		feedbackPanel = new JPanel();
		feedbackPanel.setLayout(new BoxLayout(feedbackPanel, BoxLayout.Y_AXIS));
		// Leerplatz oben und unten für optische Zentrierung
		feedbackPanel.add(Box.createVerticalGlue());

		feedbackLabel = new JLabel("", SwingConstants.CENTER);
		feedbackLabel.setFont(feedbackLabel.getFont().deriveFont(18f));
		feedbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		feedbackPanel.add(feedbackLabel);

		feedbackPanel.add(Box.createVerticalGlue());

		// CardLayout mit beiden Panels
		cardLayout = new CardLayout();
		centerPanel = new JPanel(cardLayout);
		centerPanel.add(listPanel, "FRAGEN");
		centerPanel.add(infoPanel, "INFO");
		centerPanel.add(feedbackPanel, "FEEDBACK");
		centerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(centerPanel);
		

		fillFragenList();

		// ActionListener für ComboBox und Button
		themaComboBox.addActionListener(e -> {
			if (!showingInfo) {

				fillFragenList();
			} else {

				Thema selected = (Thema) themaComboBox.getSelectedItem();
				updateThemaInfo(selected);
			}
		});

		themaInfoButton.addActionListener(e -> toggleInfoAnzeige());
	}

	/**
	 * Füllt die Fragenliste basierend auf dem aktuell ausgewählten Thema in der
	 * ComboBox.
	 */

	public void fillFragenList() {
		fragenModel.clear();
		Thema selected = (Thema) themaComboBox.getSelectedItem();
		if (selected == ALLE_THEMEN) {
			for (Thema t : allThemen) {
				if (t == ALLE_THEMEN)
					continue;
				for (Question q : t.getAllQuestions()) {
					fragenModel.addElement(q);
				}
			}
		} else if (selected != null && selected.getAllQuestions() != null) {
			for (Question q : selected.getAllQuestions()) {
				fragenModel.addElement(q);
			}
		}
	}

	/**
	 * Aktualisiert die Textarea mit der Beschreibung des ausgewählten Themas.
	 * 
	 * @param thema Das Thema, dessen Beschreibung angezeigt werden soll.
	 */

	private void updateThemaInfo(Thema thema) {
		if (thema != null) {
			infoArea.setText(thema.getText() != null ? thema.getText() : "");
		}
	}

	/**
	 * Umschaltet zwischen der Anzeige der Fragenliste und der Themeninfo.
	 * 
	 * Wenn die Info angezeigt wird, wird die Beschreibung des aktuell ausgewählten
	 * Themas in der Textarea aktualisiert.
	 */

	private void toggleInfoAnzeige() {
		showingInfo = !showingInfo;
		if (showingInfo) {
			Thema selected = (Thema) themaComboBox.getSelectedItem();
			updateThemaInfo(selected);
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
	 * @param neueThemen Neue Sammlung von Themen, die in der ComboBox angezeigt
	 *                   werden sollen.
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
	public void showFeedbackMessage(String msg) {
	    feedbackLabel.setText(msg);
	    cardLayout.show(centerPanel, "FEEDBACK");
	    revalidate();
	    repaint();
	}

	public void showFragenList() {
	    cardLayout.show(centerPanel, "FRAGEN");
	    revalidate();
	    repaint();
	}


	/**
	 * Entfernt eine Frage aus der Liste basierend auf dem Index.
	 * 
	 * @param index Index der zu entfernenden Frage.
	 */

	public void removeQuestionAt(int index) {
		if (index >= 0 && index < fragenModel.getSize()) {
			fragenModel.remove(index);
		}
	}

	/**
	 * Fügt eine Frage zur Liste hinzu.
	 * 
	 * @param question Die Frage, die hinzugefügt werden soll.
	 */

	public void addQuestion(Question question) {
		fragenModel.addElement(question);
	}

	// Getters für die GUI-Komponenten
	public JList<Question> getFragenList() {
		return fragenList;
	}

	// Getters für die ComboBox
	public JComboBox<Thema> getThemaComboBox() {
		return themaComboBox;
	}

	// Getter für den ThemaInfoButton
	public JButton getThemaInfoButton() {
		return themaInfoButton;
	}

	// Getter für das InfoPanel
	public JLabel getInfoTitelLbl() {
		return infoTitelLbl;
	}

	// Getter für die InfoArea
	public JLabel getFragenLabel() {
		return fragenLabel;
	}

	// Setter für das FragenLabel
	public void setFragenLabel(JLabel fragenLabel) {
		this.fragenLabel = fragenLabel;
	}

	}
