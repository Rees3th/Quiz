package gui.My;

import java.awt.Font;

import javax.swing.JTextArea;

public class MyTextArea extends JTextArea {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyTextArea(int rows, int cols) {
        super(rows, cols);
        setFont(new Font("Arial", Font.PLAIN, 13));
        setLineWrap(true);
        setWrapStyleWord(true);
    }
}
