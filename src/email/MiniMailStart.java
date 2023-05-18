package email;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class MiniMailStart extends JFrame{
	
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	die innere Klasse fur den Aktionlistener
	class MeinListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ev) {
			if(ev.getActionCommand().equals("senden"))
				senden();
			if(ev.getActionCommand().equals("empfangen"))
				empfangen();
			if(ev.getActionCommand().equals("ende"))
				beenden();			
		}
		
	}
	
//	der Konstruktor
	public MiniMailStart(String titel) {
		super(titel);
		setLayout(new FlowLayout(FlowLayout.LEFT));
		
		JButton liste = new JButton("Senden");
		liste.setActionCommand("senden");
		JButton einzel = new JButton("Empfanger");
		einzel.setActionCommand("empfangen");
		JButton beenden = new JButton("Beenden");
		beenden.setActionCommand("ende");
		
		MeinListener listener = new MeinListener();
		liste.addActionListener(listener);
		einzel.addActionListener(listener);
		beenden.addActionListener(listener);
		
		add(liste);
		add(einzel);
		add(beenden);
		
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void senden() {
		new Senden();
		
	}
	
	private void empfangen() {
		new Empfangen();
	}
	
	private void beenden() {
		dispose();
		
	}

	public static void main(String[] args) {
		new MiniMailStart("Mini Mail");

	}

}
