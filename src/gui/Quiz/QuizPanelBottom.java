package gui.Quiz;

import javax.swing.*;

import gui.My.MyButton;
import gui.Panels.ButtonPanel;

import java.awt.*;

/** Diese Klasse repräsentiert das untere Panel im Quiz-Interface,
 * das die Buttons für die Aktionen "Antwort zeigen", "Antwort speichern"
 * und "Nächste Frage" enthält.
 */

public class QuizPanelBottom extends JPanel {
	private static final long serialVersionUID = 1L;

	private MyButton antwortenButton;
	private MyButton speichernButton;
	private MyButton neueFrageButton;
	private QuizDelegate delegate;

	/** Konstruktor der Klasse QuizPanelBottom.
	 * Initialisiert die Buttons und fügt sie dem Panel hinzu.
	 * Setzt ActionListener für die Buttons, um die entsprechenden
	 * Aktionen im QuizDelegate auszulösen.
	 */
	
	public QuizPanelBottom() {
		setLayout(new BorderLayout());
		antwortenButton = new MyButton("Antwort zeigen");
		speichernButton = new MyButton("Antwort speichern");
		neueFrageButton = new MyButton("Nächste Frage");

		ButtonPanel bp = new ButtonPanel(antwortenButton, speichernButton, neueFrageButton);
		add(bp, BorderLayout.CENTER);

		speichernButton.addActionListener(e -> {
		    if (delegate != null) delegate.onSaveAnswer();
		});
		antwortenButton.addActionListener(e -> {
		    if (delegate != null) delegate.onShowAnswer();
		});
		neueFrageButton.addActionListener(e -> {
		    if (delegate != null) delegate.onNewQuestion();
		});

	}
	
	// Getter
	public MyButton antwortenButton() {
		return antwortenButton;
	}

	public MyButton speicherButton() {
		return speichernButton;
	}

	public MyButton neueFrageButton() {
		return neueFrageButton;
	}

	public QuizDelegate getDelegate() {
		return delegate;
	}

	public void setDelegate(QuizDelegate delegate) {
		this.delegate = delegate;
	}
}
