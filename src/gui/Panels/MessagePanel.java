package gui.Panels;

import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MessagePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JLabel messageLabel; // Immer EIN sichtbares Label

    public MessagePanel() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        messageLabel = new JLabel();
        messageLabel.setPreferredSize(new Dimension(350, 28));
        add(messageLabel);
    }

    // Setzt den Text direkt im angezeigten Label
    public void setText(String string) {
        messageLabel.setText(string != null ? string : "");
    }

    public JLabel getMessageLabel() {
        return messageLabel;
    }
}
