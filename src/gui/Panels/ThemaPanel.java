package gui.Panels;

import java.awt.Dimension;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import quizLogic.Thema;

/**
 * ThemaPanel ist ein JPanel, das eine Liste von Themen anzeigt. Es ermöglicht
 * die Anzeige und Auswahl von Themen in einer JList.
 */
public class ThemaPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JList<Thema> themenList;
	private DefaultListModel<Thema> themenModel;

	/**
	 * Konstruktor, der ein leeres ThemaPanel erstellt. Die Themenliste wird
	 * initialisiert, aber noch nicht befüllt.
	 */

	public ThemaPanel(Collection<Thema> themen) {
		super();

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JLabel lbl = new JLabel("Themen");
		lbl.setPreferredSize(new Dimension(80, 16));
		lbl.setAlignmentX(LEFT_ALIGNMENT);
		add(lbl);
		add(Box.createVerticalStrut(40));

		themenModel = new DefaultListModel<>();
		themenList = new JList<>(themenModel);
		themenList.setVisibleRowCount(10);

		JScrollPane scroll = new JScrollPane(themenList);
		scroll.setPreferredSize(new Dimension(220, 170));
		scroll.setAlignmentX(LEFT_ALIGNMENT);
		add(scroll);

		setThemen(themen);
	}

	/**
	 * Setzt die Themen in der JList. Die vorherigen Themen werden entfernt.
	 * 
	 * @param themen Collection von Thema-Objekten, die in der Liste angezeigt
	 *               werden sollen
	 */

	public void setThemen(Collection<Thema> themen) {
		themenModel.clear();
		if (themen != null) {
			for (Thema t : themen) {
				themenModel.addElement(t);
			}
		}
	}

	/**
	 * Gibt die JList zurück, die die Themen anzeigt.
	 * 
	 * @return JList von Thema-Objekten
	 */

	public JList<Thema> getThemenList() {
		return themenList;
	}
}
