package gui.QuizThemen;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

import gui.My.MyButton;
import gui.Panels.ButtonPanel;
import gui.Panels.MessagePanel;

/**
 * Diese Klasse stellt die untere Leiste der Quiz-Themen-Ansicht dar. Sie
 * enthält Buttons zum Löschen, Speichern und Hinzufügen von Themen sowie ein
 * MessagePanel für Benachrichtigungen.
 */

public class QuizThemenBottom extends JPanel {
	private static final long serialVersionUID = 1L;

	private MyButton deleteButton;
	private MyButton speichernButton;
	private MyButton neueFrageButton;
	private QuizThemenDelegate delegate;
	private MessagePanel messagePanel;

	/**
	 * Konstruktor der Klasse QuizThemenBottom. Initialisiert die Buttons und das
	 * MessagePanel, fügt sie dem Layout hinzu und setzt die ActionListener für die
	 * Buttons.
	 */

	public QuizThemenBottom() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// Buttons initialisieren
		deleteButton = new MyButton("Thema löschen");
		speichernButton = new MyButton("Speichern");
		neueFrageButton = new MyButton("Neues Thema");

		// MessagePanel initialisieren
		messagePanel = new MessagePanel();
		messagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(messagePanel, BorderLayout.NORTH);

		// ButtonPanel erstellen und Buttons hinzufügen
		ButtonPanel bp = new ButtonPanel(deleteButton, speichernButton, neueFrageButton);
		add(bp, BorderLayout.CENTER);

		// ActionListener für den Speichern-Button
		speichernButton.addActionListener(e -> {
			if (delegate != null)
				delegate.onSaveTheme();
		});

		// ActionListener für den Löschen-Button
		deleteButton.addActionListener(e -> {
			if (delegate != null)
				delegate.onDeleteTheme();
		});

		// ActionListener für den Neuen-Theme-Button
		neueFrageButton.addActionListener(e -> {
			if (delegate != null)
				delegate.onNewTheme();
		});

	}

	// Getter und Setter für die Buttons und das MessagePanel
	public QuizThemenDelegate getDelegate() {
		return delegate;
	}

	public void setDelegate(QuizThemenDelegate delegate) {
		this.delegate = delegate;
	}

	public MessagePanel getMessagePanel() {
		return messagePanel;
	}

}
