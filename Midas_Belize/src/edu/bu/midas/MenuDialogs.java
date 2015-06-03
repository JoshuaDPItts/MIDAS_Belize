/*
 * Written by Matt Carleton and completely rewritten by Chris Holden.
 */

package edu.bu.midas;

import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MenuDialogs extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final int HELP = 1;
	public final int CONTRIBUTORS = 2;
	public final int EQUATIONS = 3;

	private String contrib;
	private String[] equations = new String[2];

	private PropertyResourceBundle menuRes;

	public MenuDialogs() {
		setLanguage();
	}
	public void setLanguage() {
		if (MIDAS.LANGUAGE.equals("EN")) {
			menuRes = (PropertyResourceBundle) ResourceBundle.getBundle("Menu", new Locale("en"));
		}
		else if (MIDAS.LANGUAGE.equals("ES")) {
			menuRes = (PropertyResourceBundle) ResourceBundle.getBundle("Menu", new Locale("es"));
		}
		contrib = menuRes.getString("DIAL_CONTRIB");
		equations[0] = menuRes.getString("DIAL_EQN1");
		equations[1] = menuRes.getString("DIAL_EQN2");
	}
	public void showMessage(int i) {

		if(i == HELP) {
			UserGuide guide = new UserGuide();
		}
		else if(i == CONTRIBUTORS) {
			Contributors contrib = new Contributors();
		}
		else if(i == EQUATIONS) {
			JOptionPane.showMessageDialog(MIDAS.frame,
					equations[0], equations[1], JOptionPane.PLAIN_MESSAGE);
		} 
	}
}
