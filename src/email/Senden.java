package email;

import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;

public class Senden extends JFrame {

/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	fur die Aktion
	private MeineAktionen sendenAct;

//	fur die Tabelle
	private JTable tabelle;

//	fur das Modell
	private DefaultTableModel modell;

//	eine innere Klasse fur die Aktionen
	class MeineAktionen extends AbstractAction {

		/**
				 * 
				 */
		private static final long serialVersionUID = 1L;

		// der Konstruktor
		public MeineAktionen(String text, ImageIcon icon, String beschreibung, KeyStroke shortcut, String actionText) {

			super(text, icon);

			putValue(SHORT_DESCRIPTION, beschreibung);
			putValue(ACCELERATOR_KEY, shortcut);
			putValue(ACTION_COMMAND_KEY, actionText);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("senden"))
				senden();

		}

	}

//	der Konstruktor
	Senden() {
		super();
		setTitle("E-Mail senden");
		setLayout(new BorderLayout());

		sendenAct = new MeineAktionen("New E-Mail", new ImageIcon("icons/mail-generic.gif"),
				"Erstellt eine neue E-Mail", null, "senden");
		
		add(symbolleiste(), BorderLayout.NORTH);
		setVisible(true);
		setSize(700, 300);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

//		die Tabelle erstellen und anzeigen
		tabelleErstellen();
		tabelleAktualisieren();
	}

//	die Symbolleiste erzeugen und zurukgeben
	private JToolBar symbolleiste() {
		JToolBar leiste = new JToolBar();

//		die Symbole uber die Aktionen einbauen
		leiste.add(sendenAct);

		return leiste;
	}

//	zum erstellen der Tabelle
	private void tabelleErstellen() {
//		fur die Spaltenbezeichner
		String[] spaltenNamen = { "ID", "Empfaenger", "Betreff", "Text" };

//		ein neues Standardmodell erstellen
		modell = new DefaultTableModel();

//		die Spaltennamen setzen
		modell.setColumnIdentifiers(spaltenNamen);
//		die Tabelle erzeugen
		tabelle = new JTable();
//		und mit dem Modell verbinden
		tabelle.setModel(modell);
//		der editor um die Tabelle zu bearbeiten
		tabelle.setDefaultEditor(Object.class, null);
//		es sollten immer alle Spaalten angepasst werden
		tabelle.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
//		und die volle Groesse genutzt werden
		tabelle.setFillsViewportHeight(true);
//		die Tabelle sitzen wir in ein Scrollpane
		JScrollPane scroll = new JScrollPane(tabelle);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		add(scroll);

//		einen Maus-Listener ergaenzen
		tabelle.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
//					die zeile beschaffen
					int zeile = tabelle.getSelectedRow();

					String empfaenger, betreff, inhalt, ID;
					ID = tabelle.getModel().getValueAt(zeile, 0).toString();
					empfaenger = tabelle.getModel().getValueAt(zeile, 1).toString();
					betreff = tabelle.getModel().getValueAt(zeile, 2).toString();
					inhalt = tabelle.getModel().getValueAt(zeile, 3).toString();
//					und anzeigen
//					ubergeben wir der Frame der auseren Klasse
					new Anzeige(Senden.this, true, ID, empfaenger, betreff, inhalt);

				}
			}
		});

	}

	private void tabelleAktualisieren() {
//		fur den Datenbankzugriff
		Connection verbiendung;
		ResultSet ergebnisMenge;

//		fur die Spalten
		String empfaenger, betreff, inhalt, ID;

//		die Inhalte loeschen
		modell.setRowCount(0);

		try {
//			Verbiendung herstellen und Ergebnismenge beschaffen
			verbiendung = MiniDBTools.oeffnenDB("jdbc:derby:C:/db/mailDB");
			ergebnisMenge = MiniDBTools.liefereErgebnis(verbiendung, "SELECT * FROM gesendet");
			
//			die Eintraege in die Tabelle schreiben
			while (ergebnisMenge.next()) {
				ID = ergebnisMenge.getString("iNummer");
				empfaenger = ergebnisMenge.getString("empfaenger");
				betreff = ergebnisMenge.getString("betreff");
//				den Inhalt vom CLOB beschaffen und in einen String umbauen
				Clob clob;
				clob = ergebnisMenge.getClob("inhalt");
				inhalt = clob.getSubString(1, (int) clob.length());

//				die Zeile zum Modell hinzufugen
//				dazu benutzen wir ein Array vom Typ Object
				modell.addRow(new Object[] { ID, empfaenger, betreff, inhalt });

			}

//			die Verbiendung wieder schliessen und trennen
			ergebnisMenge.close();
			verbiendung.close();
			MiniDBTools.schlissenDB("jdbc:derby:C:/db/mailDB");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Problem in tabelleAktualisieren(): \n" + e.toString());
		}

	};

	public void senden() {
//		den Dialog fur eine neue Nachricht modal anzeigen
		new NeueNachricht(this, true);
//		nach dem Versenden lassen wir die Anzeige aktualisieren
		tabelleAktualisieren();
	}
}
