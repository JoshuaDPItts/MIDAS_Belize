/* 
 * Written by Chris Holden. Originally written by Matt Carleton. Completely revised due to
 * various bugs and memory leaks by Chris Holden.
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
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class Contributors {
	
	private JDialog dialog;
	private JScrollPane topicPane;
	private JTextPane textPane;
	private JPanel buttonPane;

	private JButton closeButton;
	private String title;
	
	private URL url;

	// Localizations
	private String close;

	private PropertyResourceBundle menuRes;

	public Contributors() {
		setLanguage();
		createTextPane();
		createBottomPane();
		
		createDialog();
	}
	public void setLanguage() {
		if (MIDAS.LANGUAGE.equals("EN")) {
			menuRes = (PropertyResourceBundle) ResourceBundle.getBundle("Menu", new Locale("en"));
		}
		else if (MIDAS.LANGUAGE.equals("ES")) {
			menuRes = (PropertyResourceBundle) ResourceBundle.getBundle("Menu", new Locale("es"));
		}
		close = menuRes.getString("WELCOME_CLOSE");
		title = menuRes.getString("DIAL_CONTRIB");
	}
	public void createTextPane() {
		textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setContentType("text/html");
		textPane.setPreferredSize(new Dimension(400, 400));
		
		url = Thread.currentThread().getContextClassLoader().getResource("Contrib.htm");
		try {
			textPane.setPage(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		topicPane = new JScrollPane();
		topicPane.getViewport().add(textPane);
	}
	public void createBottomPane() {
		closeButton = new JButton(close);
		closeButton.addActionListener(new ActionListener() {		
			public void actionPerformed(ActionEvent e) {	
				dialog.setVisible(false);
				dialog.dispose();
			}
		});
		
		buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.setBackground(MIDAS.BACKGROUNDCOLOR);
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(closeButton);
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0,0,5,5));
	}
	public void createDialog() {
		dialog = new JDialog(MIDAS.frame, title);
		
		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
		topicPane.setVisible(true);
		top.add(topicPane);
		
		JPanel mainPane = new JPanel(new BorderLayout());
		mainPane.add(top, BorderLayout.CENTER);
		mainPane.add(buttonPane, BorderLayout.PAGE_END);
		mainPane.setOpaque(true);
		
		dialog.setContentPane(mainPane);
		dialog.setSize(new Dimension(400, 600));
		dialog.setLocationRelativeTo(MIDAS.frame);
		dialog.setVisible(true);
	}
}