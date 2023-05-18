package email;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import javax.swing.JOptionPane;

//public class ErsteMail {
//	
//	private static Session verbiendungHerstellen() {
//		String benutzerName = "demisandr@gmx.at";
//		String kennwort = "inzing2019";
//		
//		
////		Server
//		String server = "mail.gmx.net";
//		Properties eigenschaften = new Properties();
//		
////		die Authentifizierung uber TLS
//		eigenschaften.put("mail.stmp.auth", "true");
//		eigenschaften.put("mail.smtp.starttls.enable", "true");
//		eigenschaften.put("mail.smtp.ssl.trust", "mail.gmx.net");
//		
////		der Server
//		eigenschaften.put("mail.smtp.host", server);
//		
////		der Port zum Versenden
//		eigenschaften.put("mail.smtp.port", "587");
//		
////		das Session-Objekt erstellen
//		Authenticator authenticator = new Authenticator() {
//			protected PasswordAuthentication getPasswordAuthentication() {
//				return new PasswordAuthentication("demisandr@gmx.com", "inzing2019");
//			}
//		};
//		
//		Session session = Session.getInstance(eigenschaften, authenticator);
//		System.out.println(session.toString());
//		return session;
//		
//	}
//	
//	private static void nachrichtVerschicken(Session sitzung) {
////		der Empfenger
//		String empfaenger = "mimo3a@gmail.com";
////		der Absender
//		String absender = "demisandr@gmx.com";
//		
//		try {
//			MimeMessage nachricht = new MimeMessage(sitzung);
//			
////			den Absender setzen
//			nachricht.setFrom(new InternetAddress(absender));
////			den Empfenger
//			nachricht.setRecipients(Message.RecipientType.TO, InternetAddress.parse(empfaenger));
////			den Betreff
//			nachricht.setSubject("Ein erster Test");
////			den Text
//			nachricht.setText("Ich bin eine Testnachricht aus einem eigenen Programm.");
//			System.out.println("befor send");
////			die Nachricht verschicken
//			Transport.send(nachricht);
//			
//			JOptionPane.showMessageDialog(null, "Die Nachricht wurde verschickt !");
//		}
//		catch(MessagingException e) {
//			JOptionPane.showMessageDialog(null, "Problem: /n" + e.toString());
//			System.out.println(e.toString());
//		}
//	}
//	
//	public static void main(String[] args) {
////		die Sitzung erstellen
//		Session sitzung = verbiendungHerstellen();
//		System.out.println(sitzung.toString());
////		die Nachricht verschicken
//		nachrichtVerschicken(sitzung);
//		
//	}
//
//}
//----------------------------------------------------------------------------------------------

public class ErsteMail {
    
    private static Session verbiendungHerstellen() {
        String benutzerName = "demisandr@gmx.at";
        String kennwort = "inzing2019";
        
        // GMX SMTP server
        String server = "mail.gmx.net";
        Properties eigenschaften = new Properties();
        
        // authentication using SSL/TLS
        eigenschaften.put("mail.smtp.auth", "true");
        eigenschaften.put("mail.smtp.starttls.enable", "true");
        eigenschaften.put("mail.smtp.ssl.trust", "mail.gmx.net");
        
        // the server
        eigenschaften.put("mail.smtp.host", server);
        
        // the port for sending
        eigenschaften.put("mail.smtp.port", "587");
        
        // create the Session object
        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(benutzerName, kennwort);
            }
        };
        
        Session session = Session.getInstance(eigenschaften, authenticator);
        
        return session;
        
    }
    
    private static void nachrichtVerschicken(Session sitzung) {
        // the recipient
        String empfaenger = "mimo3a@gmail.com";
        // the sender
        String absender = "demisandr@gmx.at";
        
        try {
            MimeMessage nachricht = new MimeMessage(sitzung);
            
            // set the sender
            nachricht.setFrom(new InternetAddress(absender));
            // set the recipient
            nachricht.addRecipient(Message.RecipientType.TO, new InternetAddress(empfaenger));
            // set the subject
            nachricht.setSubject("Ein erster Test");
            // set the message body
            nachricht.setText("Ich bin eine Testnachricht aus einem eigenen Programm.");
            
            // send the message
            Transport.send(nachricht);
            System.out.println("sending ...");
            
            JOptionPane.showMessageDialog(null, "Die Nachricht wurde verschickt !");
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(null, "Problem: /n" + e.toString());
            
        }
    }
    
    public static void main(String[] args) {
        // create the session
        Session sitzung = verbiendungHerstellen();
        
        // send the message
        nachrichtVerschicken(sitzung);
        
    }

}
