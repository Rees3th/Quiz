package gui.QuizThemen;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import quizLogic.Thema;

/**
 * Linkes Panel für die Themenverwaltung im Quiz. Enthält Eingabefelder für
 * Titel und Information zum Thema.
 */
public class QuizThemenLeft extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextField titelField;
	private JTextArea infoArea;

	/**
	 * Konstruktor für das linke Themen-Panel.
	 */
	public QuizThemenLeft() {
		super();
		initPanelLayout();
		initComponents();
		buildLayout();
	}

	/**
	 * Initialisiert das Layout des Panels.
	 */
	private void initPanelLayout() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setPreferredSize(new Dimension(350, 500));
	}

	/**
	 * Initialisiert die Eingabe-Komponenten.
	 */
	private void initComponents() {
		titelField = new JTextField();
		titelField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
		titelField.setAlignmentX(Component.LEFT_ALIGNMENT);

		infoArea = new JTextArea(6, 30);
		infoArea.setLineWrap(true);
		infoArea.setWrapStyleWord(true);
	}

	/**
	 * Setzt die UI-Komponenten in das Panel ein, inklusive Layout.
	 */
	private void buildLayout() {
		// Überschrift
		JLabel lblNeuesThema = new JLabel("Neues Thema");
		lblNeuesThema.setFont(lblNeuesThema.getFont().deriveFont(Font.BOLD, 15f));
		lblNeuesThema.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(lblNeuesThema);
		add(Box.createVerticalStrut(10));

		// Titel-Label und Textfeld
		JLabel lblTitel = new JLabel("Titel");
		lblTitel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(lblTitel);
		add(Box.createVerticalStrut(5));
		add(titelField);
		add(Box.createVerticalStrut(12));

		// Info-Label und Textarea mit Scrollpane
		JLabel lblInfo = new JLabel("Information zum Thema");
		lblInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(lblInfo);
		add(Box.createVerticalStrut(5));
		JScrollPane areaScroll = new JScrollPane(infoArea);
		areaScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
		areaScroll.setPreferredSize(new Dimension(330, 300));
		areaScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
		add(areaScroll);
		add(Box.createVerticalStrut(12));
	}

	/**
	 * Setzt die Daten eines Themas in die Eingabefelder.
	 * 
	 * @param thema Das Thema, dessen Daten angezeigt werden sollen; bei null werden
	 *              die Felder geleert.
	 */
	public void setThema(Thema thema) {
		if (thema != null) {
			titelField.setText(thema.getTitle());
			infoArea.setText(thema.getText());
		} else {
			clearFields();
		}
	}

	/**
	 * Leert die Eingabefelder (Titel und Information).
	 */
	public void clearFields() {
		titelField.setText("");
		infoArea.setText("");
	}

	/**
	 * Setter für das rechte Panel. (Platzhalter für spätere Erweiterungen)
	 * 
	 * @param quizThemenRight Das rechte Themen-Panel.
	 */
	public void setPanelRight(QuizThemenRight quizThemenRight) {
		// Falls benötigt, kann die Verknüpfung zum rechten Panel hier erfolgen.
	}

	/**
	 * Getter für das Titelfeld.
	 * 
	 * @return Das JTextField für den Titel.
	 */
	public JTextField getTitelField() {
		return titelField;
	}

	/**
	 * Getter für das Info-Textfeld.
	 * 
	 * @return Das JTextArea für die Informationen.
	 */
	public JTextArea getInfoArea() {
		return infoArea;
	}
}
