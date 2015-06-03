package edu.bu.midas;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class OilEULA {

	private JDialog eulaDialog;
	
	private JButton acceptButton;
	private JButton rejectButton;
	
	private URL url;
	private String html;
	private String eulaTitleText;
	private String acceptText;
	private String rejectText;
	
	public OilEULA() {
		setLanguage();
		createDialogBox();
		show();
	}
	public void setLanguage() {
		eulaTitleText = "Oil Model End User License Agreement (EULA)";
		
		acceptText = "Accept";
		rejectText = "Reject";
		
		html = "oilEULA.htm";
	}
	public void createDialogBox() {
		eulaDialog = new JDialog(MIDAS.frame, eulaTitleText);

		acceptButton = new JButton(acceptText);
		acceptButton.addActionListener(new ActionListener() {		
			public void actionPerformed(ActionEvent e) {	
				eulaDialog.setVisible(false);
				eulaDialog.dispose();
				MIDAS.OIL_EULA = true;
			}
		});
		rejectButton = new JButton(rejectText);
		rejectButton.addActionListener(new ActionListener() {		
			public void actionPerformed(ActionEvent e) {	
				eulaDialog.setVisible(false);
				eulaDialog.dispose();
				MIDAS.OIL_EULA = false;
			}
		});
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.setBackground(MIDAS.BACKGROUNDCOLOR);
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(acceptButton);
		buttonPanel.add(rejectButton);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0,0,5,5));
		
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setBackground(MIDAS.BACKGROUNDCOLOR);
		contentPane.add(textPanel(), BorderLayout.CENTER);
		contentPane.add(buttonPanel, BorderLayout.PAGE_END);
		contentPane.setOpaque(true);
		
		eulaDialog.setContentPane(contentPane);
	}
	public JScrollPane  textPanel() {
		JScrollPane scrollPane = new JScrollPane();
		JTextPane textPane = new JTextPane();
		
		textPane.setContentType("text/html");
		textPane.setEditable(false);
		
        try {
        	url = Thread.currentThread().getContextClassLoader().getResource(html);
        	textPane.setPage(url);
        } catch (IOException e) {
            System.out.println("Exception: " + e);
        }
		scrollPane.getViewport().add(textPane);
        
		return scrollPane;
	}
	public void show() {
		eulaDialog.setSize(new Dimension(500,300));
		eulaDialog.setLocationRelativeTo(MIDAS.frame);
		eulaDialog.setVisible(true);	
	}
}


