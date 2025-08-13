package gui.QuizFragen;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import gui.My.MyButton;
import gui.Panels.ButtonPanel;
import gui.Panels.MessagePanel;

/**
 * Diese Klasse stellt die untere Leiste im QuizFragenPanel dar. Sie enthält
 * Buttons zum Löschen, Speichern und Erstellen neuer Fragen. Die Klasse
 * implementiert ein Delegate-Pattern, um Aktionen an das übergeordnete Panel zu
 * delegieren.
 */

public class QuizFragenBottom extends JPanel {
	private static final long serialVersionUID = 1L;

	private MyButton deleteButton;
	private MyButton speichernButton;
	private MyButton neueFrageButton;
	private QuizFragenDelegate delegate;
	private MessagePanel messagePanel;

	/**
	 * Konstruktor der Klasse, der das Layout und die Buttons initialisiert. Die
	 * Buttons sind mit ActionListenern versehen, die Methoden im Delegate aufrufen.
	 */

	public QuizFragenBottom() {
		setLayout(new BorderLayout());
		deleteButton = new MyButton("Frage löschen");
		speichernButton = new MyButton("Speichern");
		neueFrageButton = new MyButton("Neue Frage");

		ButtonPanel bp = new ButtonPanel(deleteButton, speichernButton, neueFrageButton);
		add(bp, BorderLayout.CENTER);
		
		messagePanel = new MessagePanel();
		add(messagePanel, BorderLayout.NORTH);

		// ActionListener für Save, Delete und New Question Buttons
		speichernButton.addActionListener(e -> {
			if (delegate != null)
				delegate.onSaveQuestion();

		});

		deleteButton.addActionListener(e -> {
			if (delegate != null)
				delegate.onDeleteQuestion();
		});
		neueFrageButton.addActionListener(e -> {
			if (delegate != null)
				delegate.onNewQuestion();
		});

	}

	// Getter-Methoden für die Buttons, um sie im QuizFragenPanel zu verwenden
	public MyButton getDeleteButton() {
		return deleteButton;
	}

	public MyButton getNaechsteButton() {
		return speichernButton;
	}

	public MyButton getBeendenButton() {
		return neueFrageButton;
	}

	// Getter und Setter für das Delegate-Objekt
	public QuizFragenDelegate getDelegate() {
		return delegate;
	}

	public void setDelegate(QuizFragenDelegate delegate) {
		this.delegate = delegate;
	}
	public MessagePanel getMessagePanel() {
		return messagePanel;
	}
}
