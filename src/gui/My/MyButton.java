package gui.My;

import java.awt.Color;

import javax.swing.JButton;

public class MyButton extends JButton {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyButton(String text) {
        super(text);
        setBackground(Color.PINK);
        setFocusPainted(false);
    }
}
