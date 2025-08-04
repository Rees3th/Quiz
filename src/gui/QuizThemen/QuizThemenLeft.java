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

import gui.Panels.MessagePanel;
import quizLogic.Thema;

/**
 * Linkes Panel für die Themenverwaltung im Quiz. Enthält Eingabefelder für
 * Titel und Information zum Thema.
 */
public class QuizThemenLeft extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextField titelField;
	private JTextArea infoArea;

	/** Konstruktor für das linke Panel */
	public QuizThemenLeft() {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setPreferredSize(new Dimension(350, 500));

		// Label "Neues Thema"
		JLabel lblNeuesThema = new JLabel("Neues Thema");
		lblNeuesThema.setFont(lblNeuesThema.getFont().deriveFont(Font.BOLD, 15f));
		lblNeuesThema.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(lblNeuesThema);
		add(Box.createVerticalStrut(10));

		// Label "Titel" + TextField
		JLabel lblTitel = new JLabel("Titel");
		lblTitel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(lblTitel);
		add(Box.createVerticalStrut(5));

		titelField = new JTextField();
		titelField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
		titelField.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(titelField);
		add(Box.createVerticalStrut(12));

		// Label "Information zum Thema"
		JLabel lblInfo = new JLabel("Information zum Thema");
		lblInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(lblInfo);
		add(Box.createVerticalStrut(5));

		// TextArea mit ScrollPane
		infoArea = new JTextArea(6, 30);
		infoArea.setLineWrap(true);
		infoArea.setWrapStyleWord(true);
		JScrollPane areaScroll = new JScrollPane(infoArea);
		areaScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
		areaScroll.setPreferredSize(new Dimension(330, 300));
		areaScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
		add(areaScroll);
		add(Box.createVerticalStrut(12));

	}

	/**
	 * Setzt die Daten des Themas in die Eingabefelder.
	 * 
	 * @param thema Das Thema, dessen Daten gesetzt werden sollen.
	 */

	public void setThemaData(Thema thema) {
		if (thema != null) {
			titelField.setText(thema.getTitle());
			infoArea.setText(thema.getText());

		} else {
			clearFields();
		}
	}

	/**
	 * Setzt die Daten des Themas in die Eingabefelder oder leert sie, wenn das
	 * Thema null ist.
	 * 
	 * @param thema Das Thema, dessen Daten gesetzt werden sollen.
	 */

	public void setThema(Thema thema) {
		if (thema != null) {
			setThemaData(thema); // oder direkte Feldbefüllung
		} else {
			clearFields();
		}
	}

	/**
	 * Leert die Eingabefelder für Titel und Information.
	 */
	public void clearFields() {
		titelField.setText("");
		infoArea.setText("");

	}

	/**
	 * Setzt das rechte Panel für die Themenverwaltung.
	 * 
	 * @param quizThemenRight Das rechte Panel, das gesetzt werden soll.
	 */

	public void setPanelRight(QuizThemenRight quizThemenRight) {

	}

	// Getter für die Eingabefelder
	public JTextField getTitelField() {
		return titelField;
	}

	// Getter für das TextArea
	public JTextArea getInfoArea() {
		return infoArea;
	}
}
