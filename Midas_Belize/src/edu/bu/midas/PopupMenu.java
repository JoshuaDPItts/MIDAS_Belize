/*
 * Written by Matt Carleton originally and rewritten by Chris Holden.
 */

package edu.bu.midas;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class PopupMenu implements ActionListener
{   
	MouseListener popupListener;
    String[] names = MIDAS.mmaNames;
    String switchView, switchMMA;
    JPopupMenu popup;
    
    private PropertyResourceBundle popRes;
    
	public PopupMenu()
	{	
		setLanguage();	
		
		popup = new JPopupMenu();	
		populateMenu();
 	}

	public void populateMenu()
	{	
		JMenuItem menuItem;
	    
	    menuItem = new JMenuItem(switchView);
	    menuItem.addActionListener(this);
	    popup.add(menuItem);
	    
	    JMenu submenu = new JMenu(switchMMA);
	    for(int i = 0; i< names.length; i++)
	    {  	menuItem = new JMenuItem(names[i]);
	        menuItem.addActionListener(this);
	        submenu.add(menuItem);
	    }        
	    popup.add(submenu);
	
	    popupListener = new PopupListener(popup);
	}
	
	public void setLanguage() {	
		if (MIDAS.LANGUAGE.equals("EN")) {
			popRes = (PropertyResourceBundle) ResourceBundle.getBundle("Menu", new Locale("en"));
		}
		else if (MIDAS.LANGUAGE.equals("ES")) {
			popRes = (PropertyResourceBundle) ResourceBundle.getBundle("Menu", new Locale("es"));
		}
		switchView = popRes.getString("POP_SWITCH_VIEW");
		switchMMA = popRes.getString("POP_SWITCH_MMA");
	}
	public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());

        if(source.getText().equals(switchView)) {
        	if(MIDAS.mmaPane[MIDAS.mmaNum].cdfPane.isVisible()) {
        		MIDAS.changeView("GIS");
        	}
        	else {
        		MIDAS.changeView("CDF"); 
        	}
        }
        else {       //select MMA 
	        for(int i = 0; i<names.length; i++) {
	        	if(source.getText().equals(names[i])) {
	        		MIDAS.mmaNum = i;
	        		MIDAS.selectMMA();
	        	}
	        }
        }
	}

	public class PopupListener extends MouseAdapter {
		JPopupMenu popup;

        public PopupListener(JPopupMenu popupMenu) {	
        	popup = popupMenu;        
        }

        public void mousePressed(MouseEvent e) {	
        	maybeShowPopup(e);   
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }
        private void maybeShowPopup(MouseEvent e) {	
        	if (e.isPopupTrigger()) {
        		popup.show(e.getComponent(), e.getX(), e.getY());
        	}
        }
    }
}