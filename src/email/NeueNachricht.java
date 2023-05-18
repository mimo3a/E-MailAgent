package email;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.derby.shared.common.error.PublicAPI;



public class NeueNachricht extends JDialog {
	
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	fur die Eingabefelder
	private JTextField empfaenger, betreff;
	private JTextArea inhalt;
	
//	fur die Schaltflaechen
	private JButton ok, abbrechen;
	
//	die innere Klasse fur den ActionListener
	class NeuListener implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("senden"))
			senden();
		if(e.getActionCommand().equals("abbrechen"))
			dispose();
		
		}
		
	}
	
//	der Konstruktor
	public NeueNachricht(JFrame parent, boolean modal) {
		super(parent, modal);
		
		
		setTitle("Neue Nachricht");
//		die Oberflaeche erstellen
		initGui();
//		Standardoperation setzen
//		hier den Dialog ausblenden und loeschen
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
	
	private void initGui() {
		setLayout(new BorderLayout());
		JPanel oben = new JPanel();
		oben.setLayout(new GridLayout(0,2));
		oben.add(new JLabel("Empfanger: "));
		empfaenger = new JTextField();
		oben.add(empfaenger);
		oben.add(new JLabel("Betreff: "));
		betreff = new JTextField();
		oben.add(betreff);
		add(oben, BorderLayout.NORTH);
		inhalt = new JTextArea("Text");
//		den zeilenumbruch aktivieren
		inhalt.setLineWrap(true);
		inhalt.setWrapStyleWord(true);
//		das Feld setzen wir in ein Scrollpane
		JScrollPane scroll = new JScrollPane(inhalt);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		add(scroll);
		
		JPanel unten = new JPanel();
//		die Schaltflaechen
		ok = new JButton("Senden");
		ok.setActionCommand("senden");
		
		abbrechen = new JButton("Abbrechen");
		abbrechen.setActionCommand("abbrechen");
		
		NeuListener listener = new NeuListener();
		ok.addActionListener(listener);
		abbrechen.addActionListener(listener);
		
		unten.add(ok);
		unten.add(abbrechen);
		add(unten, BorderLayout.SOUTH);
		
//		anzeigen
		setSize(600, 300);
		setVisible(true);
		
	}
	
//	die Methode legt einen neuen Datensatz an
	private void senden() {
//		fur die Sitzung
		Session sitzung;
		
//		die Verbiendung herstellen
		sitzung = verbiendungHerstellen();
//		die Nachricht verschicken und speichern
		nachrichtVerschicken(sitzung);
		nachrichtSpeichern();
	}
	
	private Session verbiendungHerstellen() {
//		die ZugangsDaten
		String [] zugang = MiniDBTools.zugangsDaten();
		String benutzerName = zugang[0];
		String kennwort = zugang[1];
		
//		der Server
		String server = "mail.gmx.net";
		
//		die Eigenschaften setzen
		Properties eigenschaften = new Properties();
//		die Authentifizierung uber TLZ
		eigenschaften.put("mail.smtp.auth", "true");
		eigenschaften.put("mail.smtp.starttls.enable", "true");
		eigenschaften.put("mail.smtp.ssl.trust", "mail.gmx.net");
//		der Server
		eigenschaften.put("mail.smtp.host", server);
//		der Port zum Versenden
		eigenschaften.put("mail.smtp.port", "587");
		
//		das Session-Objekt erstellen
		Session sitzung = Session.getInstance(eigenschaften, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(benutzerName, kennwort);
			}
		});
		return sitzung;
	}
	
	private void nachrichtVerschicken(Session sitzung) {
//		der Absender
		String absender = "demisandr@gmx.at";
//		
		try {
//			eine neue Nachricht vom Typ MimeMessage erzeugen
			MimeMessage nachricht = new MimeMessage(sitzung);
//			den Absender setzen
			nachricht.setFrom(new InternetAddress(absender));
//			den Empfaenger
			nachricht.setRecipients(Message.RecipientType.TO, InternetAddress.parse(empfaenger.getText()));
			nachricht.setSubject(betreff.getText());
//			und den Taxt
			nachricht.setText(inhalt.getText());
//			die Nachricht verschicken
			Transport.send(nachricht);
			JOptionPane.showMessageDialog(this, "Die Nachricht wurde verschickt");
			
//			den Dialog schliessen
			dispose();
		}
		catch(MessagingException e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
			
		}
	}
	
	
	private void nachrichtSpeichern() {
//		fur die Verbiendung
		Connection verbiendung;
		
//		die Datenbank oeffnen
		verbiendung = MiniDBTools.oeffnenDB("jdbc:derby:C:/db/mailDB");
		
		
		try {
//			einen Eintrag in der Tabelle gesendet anlegen
//			uber ein vorbereitetes Statement
			PreparedStatement prepState;			
			prepState = verbiendung.prepareStatement(
				    "INSERT INTO gesendet (empfaenger, betreff, inhalt) values (?,?,?)");

			prepState.setString(1, empfaenger.getText());
			prepState.setString(2, betreff.getText());
			prepState.setString(3, inhalt.getText());
				
//			das Statement ausfuhren
			prepState.executeUpdate();				
			verbiendung.commit();
			
//			Verbiendung schliessen
			prepState.close();
			verbiendung.close();
//			und die Datenbank schliessen
			MiniDBTools.schlissenDB("jdbc:derby:C:/db/mailDB");
			
		}
		catch(Exception e) {
			System.out.println("Problem in nachrichtSpeichern: \n" + e.toString());
		}
		

	}

}
