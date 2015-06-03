package edu.bu.midas;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class HelpPanel extends JScrollPane {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextPane textPane = new JTextPane();
	private String htmlFile = "Help.htm";
	// String cd = System.getProperty("user.dir") + "/help/";
	private URL url;
	private PopupMenu popup;
	
	public HelpPanel() {		
		textPane.setContentType("text/html");
		textPane.setEditable(false);

        try {
        	url = Thread.currentThread().getContextClassLoader().getResource(htmlFile);
        	textPane.setPage(url);
        } catch (IOException e) {
            System.out.println("Exception: " + e);
        }
        textPane.setBorder(BorderFactory.createLineBorder(Color.black));
        
        getViewport().add(textPane);
        setPreferredSize(MIDAS.HELPRES);
		setBorder(BorderFactory.createLineBorder(Color.black));
		
		popup = new PopupMenu();
		textPane.addMouseListener(popup.popupListener); 
	}
	public HelpPanel(String html) {		
		textPane.setContentType("text/html");
		textPane.setEditable(false);

        try {
        	url = Thread.currentThread().getContextClassLoader().getResource(html);
        	textPane.setPage(url);
        } catch (IOException e) {
            System.out.println("Exception: " + e);
        }
        textPane.setBorder(BorderFactory.createLineBorder(Color.black));
        
        getViewport().add(textPane);
        setPreferredSize(MIDAS.HELPRES);
		setBorder(BorderFactory.createLineBorder(Color.black));
		
		popup = new PopupMenu();
		textPane.addMouseListener(popup.popupListener); 
	}
	public void helpRequest(String name) {
		if (name.equals("GovHeader")) {
			htmlFile = "GovHead.htm";
		}
		else if(name.equals("G1")) {
			htmlFile = "G1.htm";
		}
		else if(name.equals("G2")) {
			htmlFile = "G2.htm";
		}
		else if(name.equals("G3")) {
			htmlFile = "G3.htm";
		}
		else if(name.equals("G4")) {
			htmlFile = "G4.htm";
		}
		else if(name.equals("G5")) {
			htmlFile = "G5.htm";
		}
		else if (name.equals("G6")) {
			htmlFile = "G6.htm";
		}
		else if (name.equals("SocHeader")) {
			htmlFile = "SocEconHead.htm";
		}
		else if(name.equals("SE1")) {
			htmlFile = "S1.htm";
		}
		else if(name.equals("SE2")) {
			htmlFile = "S2.htm";
		}
		else if(name.equals("SE3")) {
			htmlFile = "S3.htm";
		}
		else if(name.equals("SE4")) {
			htmlFile = "S4.htm";
		}
		else if(name.equals("SE5")) {
			htmlFile = "S5.htm";
		}
		else if (name.equals("SE6")) {
			htmlFile = "S6.htm";
		}
		// Ecological help files
		else if (name.equals("EcoHeader")) {
			htmlFile = "EcoHead.htm";
		}
		else if(name.equals("EC1")) {
			htmlFile = "E1.htm";
		}
		else if(name.equals("EC2")) {
			htmlFile = "E2.htm";
		}
		else if(name.equals("EC3")) {
			htmlFile = "E3.htm";
		}
		else if(name.equals("EC4")) {
			htmlFile = "E4.htm";
		}
		else if(name.equals("EC5")) {
			htmlFile = "E5.htm";
		}
		else if (name.equals("EC6")) {
			htmlFile = "E6.htm";
		}
		// Graphs/Index help files
		else if(name.equals("Gov")) {
			htmlFile = "GovIndex.htm";
		}
		else if(name.equals("Soc")) {
			htmlFile = "SocEconIndex.htm";
		}
		else if(name.equals("Eco")) {
			htmlFile = "EcoIndex.htm";
		}
		else if(name.equals("Com")) {
			htmlFile = "CDFComp.htm";
		}
		else if(name.equals("Tri")) {
			htmlFile = "MMAEffect.htm";
		}
		else {
			System.out.println("No help file associated with this label");
		}
		try {
			url = Thread.currentThread().getContextClassLoader().getResource(htmlFile);
        	textPane.setPage(url);
        } catch (IOException e) {
            System.out.println("Exception: " + e);
        }
	}
}