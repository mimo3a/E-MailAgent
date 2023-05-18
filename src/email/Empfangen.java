package email;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;

import email.Senden.MeineAktionen;

public class Empfangen extends JFrame {

	private static final long serialVersionUID = 1L;

//	fur die Aktion
	private EmpfangenActionen replyAct, forwardAct;

//	fur das Tabelle
	private JTable tabelle;
//	fur das Model
	private DefaultTableModel modell;

//	eine innere Klasse fur den WindowListener und den AktionListener
//	die Klasse ist von WindowAdapter abgeleitet

//	-----------------------------------------------------------------------
	class MeinWindowAdapter extends WindowAdapter {
//		fur das oeffnen des Fensters
		@Override
		public void windowOpened(WindowEvent e) {
//			die Methode nachrichtenEmpfanger() aufrufen
			nachrichtenEmpfangen();
		}
	}

//-------------------------------------------------------------------------------------------
//	eine innere Klasse fur die Aktionen
	class EmpfangenActionen extends AbstractAction {

		// der Konstruktor

		public EmpfangenActionen(String text, ImageIcon icon, String beschreibung, KeyStroke shortcut,
				String actionText) {
			super(text, icon);

			putValue(SHORT_DESCRIPTION, beschreibung);
			putValue(ACCELERATOR_KEY, shortcut);
			putValue(ACTION_COMMAND_KEY, actionText);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("reply"))
				reply();
			if (e.getActionCommand().equals("forward"))
				forward();
		}
	}
//	-----------------------------------------------------------------------------------------------

//	der Konsrtuctor
	Empfangen() {
		super();
		setTitle("E-Mail empfangen");
		setLayout(new BorderLayout());
		setVisible(true);
		setSize(700, 300);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

//		fur die Symbolleiste
		replyAct = new EmpfangenActionen("die E-Mail anrworten", new ImageIcon("icons/mail-reply.gif"),
				"Antworten die E-Mail", null, "reply");
		forwardAct = new EmpfangenActionen("die E-Mail weiterleiten", new ImageIcon("icons/mail-forward.gif"),
				"Weiterleiten die E-Mail", null, "forward");

		add(symbolleiste(), BorderLayout.NORTH);

//		den Listener verbienden
		addWindowListener(new MeinWindowAdapter());

//		die Tabelle erstellen und anzeigen
		tabelleErstellen();
		tabelleAktualisieren();

	}

//	zum erstellen der Tabelle
	private void tabelleErstellen() {

//		fur die Spaltenbezeichner
		String[] spalterNamen = { "ID", "Sender", "Betreff", "Text" };

//		ein neues Standardmodell erstellen
		modell = new DefaultTableModel();
//		die SpaltenNamen setzen
		modell.setColumnIdentifiers(spalterNamen);
//		die Tabelle erzeugen
		tabelle = new JTable();
//		und mit Modell verbienden
		tabelle.setModel(modell);
//		wir konnen die Tabelle nicht bearbeiten
		tabelle.setDefaultEditor(Object.class, null);
//		es sollen immer alle Spalten angepasst werden
		tabelle.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
//		und die volle Groesse genutzt werden
		tabelle.setFillsViewportHeight(true);
//		die Tabelle setzen wir in ein Scrollpane
		JScrollPane scroll = new JScrollPane(tabelle);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		add(scroll);

//		einen Mauslisteber ergenzen
		tabelle.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int zeile = tabelle.getSelectedRow();
//					die Daten beschaffen
					String sender, betreff, inhalt, ID;
					ID = tabelle.getModel().getValueAt(zeile, 0).toString();
					sender = tabelle.getModel().getValueAt(zeile, 1).toString();
					betreff = tabelle.getModel().getValueAt(zeile, 2).toString();
					inhalt = tabelle.getModel().getValueAt(zeile, 3).toString();
//					und anzeigen
//					ubergeben wird der Frame der ausser Klasse
					new Anzeige(Empfangen.this, true, ID, sender, betreff, inhalt);
				}
			}
		});
	}

	private void tabelleAktualisieren() {

//		fur den Datenbankzugrif
		Connection verbiendung;
		ResultSet ergebnisMenge;

//		fur die Spalten
		String sender, betreff, inhalt, ID;
//		die inhalte loeschen
		modell.setRowCount(0);

		try {
//			Verbiendung herstellen und Ergebnismenge beschaffen
			verbiendung = MiniDBTools.oeffnenDB("jdbc:derby:C:/db/mailDB");
			ergebnisMenge = MiniDBTools.liefereErgebnis(verbiendung, "SELECT * FROM empfangen");
//			die Eintrage in die Tabelle schreiben
			while (ergebnisMenge.next()) {
				ID = ergebnisMenge.getString("iNummer");
				sender = ergebnisMenge.getString("sender");
				betreff = ergebnisMenge.getString("betreff");
				Clob clob;
				clob = ergebnisMenge.getClob("inhalt");
				inhalt = clob.getSubString(1, (int) clob.length());

//				die zeile zum Mlodel hinzufugen
//				dazu benutzen wir Array vom Typ Object
				modell.addRow(new Object[] { ID, sender, betreff, inhalt });
			}

//				die Verbiendung wieder schliessen und trennen 
			ergebnisMenge.close();
			verbiendung.close();
			MiniDBTools.schlissenDB("jdbc:derby:C:/db/mailDB");

		} catch (Exception e) {
			System.out.println("Problem in tabelle aktualisieren :\n" + e.toString());
		}

	}

	private void nachrichtenAbholen() {
//		die zugangsdaten
		String[] zugang = MiniDBTools.zugangsDaten();
		String benutzerName = zugang[0];
		String kennwort = zugang[1];

//		der Server
		String server = "pop.gmx.net";

//		die Eigenschaften setzen
		Properties eigenschaften = new Properties();
//		das Protokoll
		eigenschaften.put("mail.store.protocol", "pop3");
//		den Host
		eigenschaften.put("mail.pop3.host", server);
//		den Port zum Empfangen
		eigenschaften.put("mail.pop3.port", "995");
//		die Authentifizierung uber TLS
		eigenschaften.put("mail.pop3.starttls.enable", "true");
//		das Session-Objekt erstellen
		Session sitzung = Session.getDefaultInstance(eigenschaften);

//		das Store-Objekt uber die Sitzung erzeugen
		try (Store store = sitzung.getStore("pop3s")) {
//			und verbienden
			store.connect(server, benutzerName, kennwort);
//			ein OrdnerObjekt fur den Posteingang erzeugen
			Folder postEingang = store.getFolder("INBOX");
//			und oeffnen
//			dabei sind auch Aenderungen zugelassen
			postEingang.open(Folder.READ_WRITE);

//			die Nachricht beschaffen
			Message nachrichten[] = postEingang.getMessages();

//			gibt es neue Nachrichten?
			if (nachrichten.length != 0) {
//				dann die Anzahl zeigen
				JOptionPane.showMessageDialog(this,
						"Es gibt " + postEingang.getUnreadMessageCount() + " neue Nachrichten.");
//				Jede Nachricht verarbeiten
				for (Message nachricht : nachrichten)
					nachrichtVerarbeiten(nachricht);

			} else
				JOptionPane.showMessageDialog(this, "Es gibt keine neuen Nachrichten.");

//			den Order schliessen
//			durch das Argument true werden die Nachrichten geloescht
			postEingang.close(true);

		} catch (Exception e) {
			System.out.println("Problem in nacrichtenAbholen : \n" + e.toString());
		}

	}

	private void nachrichtVerarbeiten(Message nachricht) {
		try {
//			ist es einfacher Text?
			System.out.println(nachricht.getContentType().toString());
			if (nachricht.isMimeType("text/plain")) {
//				den ersten Sender beschaffen
				String sender = nachricht.getFrom()[0].toString();
//				den Betreff
				String betreff = nachricht.getSubject();
//				den Inhalt
				String inhalt = nachricht.getContent().toString();
//				die Nachricht speichern
				nachrichtSpeichern(sender, betreff, inhalt);
//				und zum Loeschen markieren
				nachricht.setFlag(Flags.Flag.DELETED, true);
			}
//			sonst geben wir eine Meldung aus
			else
				JOptionPane.showMessageDialog(this,
						"Der Typ von Nachricht " + nachricht.getContentType() + "kann nicht verarbeitet werden.");

		} catch (Exception e) {
			System.out.println("Problem in nachrichtVerarbeiten :" + e.toString());
		}

	}

	private void nachrichtSpeichern(String sender, String betreff, String inhalt) {
//		fur die Verbiendung
		Connection verbiendung;
//		die Datenbank oeffnen
		verbiendung = MiniDBTools.oeffnenDB("jdbc:derby:C:/db/mailDB");
		try {
//			einen Eintrag in der Tabelle empfangen anlegen
//			uber ein vorbereitetes Statement
			PreparedStatement prepState;
			prepState = verbiendung
					.prepareStatement("INSERT INTO " + "empfangen (sender, betreff, inhalt) VALUES (?,?,?)");
			prepState.setString(1, sender);
			prepState.setString(2, betreff);
			prepState.setString(3, inhalt);
//			das Statement ausfuhren
			prepState.executeUpdate();
			verbiendung.commit();
//			Verbiendung schliessen
			prepState.close();
			verbiendung.close();
//			und die Datenbank schliessen
			MiniDBTools.schlissenDB("jdbc:derby:C:/db/mailDB");
		} catch (Exception e) {
			System.out.println("Problem is nachrichtSpeichern : " + e.toString());
		}
	}

	private void nachrichtenEmpfangen() {
		nachrichtenAbholen();
//		nach dem Empfangen lassen wir die Anzeige aktualisieren
		tabelleAktualisieren();
	}

//	-----------------------------------------------------------------------------------------------
//	die Methode fur die Symbolleiste
	private JToolBar symbolleiste() {

		JToolBar leisteBar = new JToolBar();
		leisteBar.add(replyAct);
		leisteBar.add(forwardAct);

		return leisteBar;

	}

//	die Methode fur die Antwort	
	private void reply() {
		int zeile = tabelle.getSelectedRow();
//		die Daten beschaffen
		String sender, betreff, inhalt;
		
		sender = tabelle.getModel().getValueAt(zeile, 1).toString();
		betreff = tabelle.getModel().getValueAt(zeile, 2).toString();
		betreff = "AW : " + betreff;
		inhalt = tabelle.getModel().getValueAt(zeile, 3).toString();
		inhalt = "----------------------------------------------------------------------\n" + inhalt +
				 "----------------------------------------------------------------------\n";
		
		new Antworten(this, true, "Antwort", sender, betreff, inhalt);
		tabelleAktualisieren();

	}

//	die Methode fur die das Weiterleiten
	private void forward() {
		
		int zeile = tabelle.getSelectedRow();
//		die Daten beschaffen
		String betreff, inhalt;
		
		betreff = tabelle.getModel().getValueAt(zeile, 2).toString();
		betreff = "WG : " + betreff;
		inhalt = tabelle.getModel().getValueAt(zeile, 3).toString();
		
		
		new Antworten(this, true, "Weiterleiten", null, betreff, inhalt);
		tabelleAktualisieren();

	}
}
