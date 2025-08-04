package gui.My;

import java.awt.Font;

import javax.swing.JLabel;

public class MyLabel extends JLabel{
	private static final long serialVersionUID = 1L;
	protected static final Font FONT_TITLE = new Font("Helvetica", Font.BOLD, 30);
	protected static final Font FONT_LABEL = new Font("Helvetica", Font.ITALIC, 16);

	public MyLabel(String text) {
		super(text);
		setFont(MyLabel.FONT_LABEL); // Setzt die Schriftart des Labels auf FONT_LABEL
		setHorizontalAlignment(CENTER); // Zentriert den Text im Label horizontal
		
	}

}
