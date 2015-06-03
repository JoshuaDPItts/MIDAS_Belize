/*
 * Written by Matt Carleton originally and rewritten by Chris Holden.
 */

package edu.bu.midas;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class WelcomeMenu {
	
	private JDialog dialog;	//this is the window that pops up
	private JTextPane textPane = new JTextPane();	
	private String htmlFile = "welcome.htm";
	private JScrollPane scrollPane = new JScrollPane();	//this contains the textPane
	private JButton closeButton, guideButton;	//Buttons at the bottom of the popup
	private JCheckBox showAgain;	//A Checkbox that asks the user if they'd like to continue seeing this welcome page at startup
	
	private boolean showMenu;
	
	private String welcomeText;
	private String showText;
	private String closeText;
	private String guideText;
	
	private PropertyResourceBundle welcomeRes;
	
	public WelcomeMenu() {
		setLanguage();
		createDialogBox();
		/*the dialog will appear automatically when a new instance of this class is 
		 * called, unless showMenu is set to false. If this is the case, then the 
		 * dialog may be shown manually by calling the method showDialog() 
		 * from the other class.
		 */
	}
	public void setLanguage() {
		if (MIDAS.LANGUAGE.equals("EN")) {
			welcomeRes = (PropertyResourceBundle) ResourceBundle.getBundle("Menu", new Locale("en"));
		}
		else if (MIDAS.LANGUAGE.equals("ES")) {
			welcomeRes = (PropertyResourceBundle) ResourceBundle.getBundle("Menu", new Locale("es"));
		}
		welcomeText = welcomeRes.getString("WELCOME_WELCOME");
		showText = welcomeRes.getString("WELCOME_SHOW");
		closeText = welcomeRes.getString("WELCOME_CLOSE");
		guideText = welcomeRes.getString("WELCOME_GUIDE");
	}
	
	private void createDialogBox() {
		//Create the dialog. The third argument makes the dialog modal [hogs user's attention]
		dialog = new JDialog(MIDAS.frame, welcomeText, true);

		textPane.setContentType("text/html");
		textPane.setEditable(false);
		
		//try to get the html from the file system, then set the textPane to that file
		try {
			URL url = Thread.currentThread().getContextClassLoader().getResource(htmlFile);
        	textPane.setPage(url);
		} 
		catch (IOException e) {
			System.out.println("Exception: " + e);	
		}
        
        //add html panel to the scroll pane
        scrollPane.getViewport().add(textPane);
		scrollPane.setBackground(MIDAS.BACKGROUNDCOLOR);
	        
		//add the check box
		showAgain = new JCheckBox(showText, MIDAS.SHOW_WELCOME);	//have the box automatically be selected if that is what is saved to the config file.
		
		// Add an action listener to the check box, and save the setting to the config file
	    showAgain.addActionListener(new ActionListener()  {
	    	public void actionPerformed(ActionEvent ae) {
	    		
	    		if (showAgain.isSelected() == true) {
	    			MIDAS.SHOW_WELCOME = true;
	    		}
	    		else if (showAgain.isSelected() == false) {
	    			MIDAS.SHOW_WELCOME = false;
	    		}
	    		/*
	    		AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
	        	showMenu = abstractButton.getModel().isSelected();
	        	savePreference();
	        	*/
	    		MIDAS.savePrefs();
	    	}
	    });
	  		
		//add the buttons at the bottom
		closeButton = new JButton(closeText);
		
		// This action listener closes the dialog 
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);	//close the dialog
				dialog.dispose();
				MIDAS.savePrefs();
			}
		});
		
		System.out.println(guideText);
		guideButton = new JButton(guideText);
		
		// This action listener, used by the "View User Guide" button will close the dialog window and show the user guide.
		guideButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {			
				UserGuide guide = new UserGuide();	//show the user guide
				dialog.setVisible(false);			//exit the dialog
				dialog.dispose();
			}
		});
		
		//add buttons/ checkbox to their panel		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setBackground(MIDAS.BACKGROUNDCOLOR);
		buttonPanel.add(showAgain);
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(guideButton);
		buttonPanel.add(closeButton);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0,0,5,5));
		
		//add panels to the content pane/ to the dialog
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(scrollPane, BorderLayout.CENTER);
		contentPane.add(buttonPanel, BorderLayout.PAGE_END);
		contentPane.setOpaque(true);
		dialog.setContentPane(contentPane);
	}
	
	public void showDialogBox() {
		dialog.setSize(new Dimension(500, 400));
		dialog.setLocationRelativeTo(MIDAS.frame);
		dialog.setVisible(true);
	}
	
	public boolean getShowWelcomePreference() {
		return showMenu;	
	}
}
