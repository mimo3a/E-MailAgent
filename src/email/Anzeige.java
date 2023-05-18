package email;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Anzeige extends JDialog{
	
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//	fur die Eingabefelder
	private JTextField empangerField, betreField;
	private JTextArea inhaltFeld;
//	fur die Schaltflaechen
	private JButton ok;
	
//	die innere Klasse fur den ActionListener
	class NeuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("ok"))
				dispose();
			
			
		}
		
	}
	
//	der Konstruktor
	public Anzeige(JFrame parent, boolean modal, String ID, String empfaenger, String betreff, String inhalt) {
		super(parent, modal);
		setTitle("Anzeige");
//		die Oberflaeche erstellen
		initGui(ID, empfaenger, betreff, inhalt);
		
//		Standardoperation setzen
//		hier den Dialog ausblenden und loeschen
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
	
	private void initGui(String ID, String empfaenger, String betreff, String inhalt) {
		setLayout(new BorderLayout());
		JPanel oben = new JPanel();
		oben.setLayout(new GridLayout(0,2));
		oben.add(new JLabel("Empfaenger: "));
		empangerField = new JTextField(empfaenger);
		oben.add(empangerField);
		oben.add(new JLabel("Betreff: "));
		betreField = new JTextField(betreff);
		oben.add(betreField);
		add(oben, BorderLayout.NORTH);
		inhaltFeld = new JTextArea(inhalt);
		inhaltFeld.setLineWrap(true);
		inhaltFeld.setWrapStyleWord(true);
//		das Feld setzen wir in ein Scrollpane
		JScrollPane scroll = new JScrollPane(inhaltFeld);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		add(scroll);
		
//		die Felder koennen nicht bearbeitet werden
		empangerField.setEditable(false);
		betreField.setEditable(false);
		inhaltFeld.setEditable(false);
		
		JPanel unten = new JPanel();
//		die Schaltflaeche
		ok = new JButton("OK");
		ok.setActionCommand("ok");
		
		NeuListener listener = new NeuListener();
		ok.addActionListener(listener);
		
		unten.add(ok);
		add(unten, BorderLayout.SOUTH);
		
//		anzeigen
		setSize(600,300);
		setVisible(true);
		
	}

}
