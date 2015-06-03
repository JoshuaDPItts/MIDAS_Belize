/* 
 * Written by Chris Holden.
 */

package edu.bu.midas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class UserGuide {
	
	private JDialog dialog;
	private JScrollPane scrollPane;
	private JPanel contentsPane;
	private JScrollPane topicPane;
	private JTextPane textPane;
	private JPanel buttonPane;

	private JButton closeButton;
	private JButton contentsButton;
	
	private URL url;

	private Color pink = new Color(255, 0, 204);
	private Color purple = new Color(102, 0, 204);
	private Color blue = new Color(51, 153, 255);
	private Color black = new Color(0,0,0);

	private Color topicColors[] = {black, 
			purple, black, black, black, black, black, black, black, //Gov
			pink, black, black, black, black, black, black, black, //Soc
			blue, black, black, black, black, black, black, black, //Eco
			black, black, black, black, black}; // GIS

	// Localizations
	private String[] helpContents = new String[30];
	private String[] helpFiles = new String[30];
	private String title;
	private String close;
	private String viewTopics;

	//true if the appropriate index of help[] is a header
	private boolean isAHelpHeader[] = {true, 	
			true, false, false, false, false, false, false, false, //Gov cdfs
			true, false, false, false, false, false, false, false, //Soc cdfs
			true, false, false, false, false, false, false, false, //Eco cdfs
			true, false, false, false, false}; // GIS

	private PropertyResourceBundle guideRes;

	public UserGuide() {
		setLanguage();
		createTableOfContents();
		createTopicPane();
		createBottomPane();
		
		createDialog();
	}
	public void setLanguage() {
		if (MIDAS.LANGUAGE.equals("EN")) {
			guideRes = (PropertyResourceBundle) ResourceBundle.getBundle("Guide", new Locale("en"));
		}
		else if (MIDAS.LANGUAGE.equals("ES")) {
			guideRes = (PropertyResourceBundle) ResourceBundle.getBundle("Guide", new Locale("es"));
		}
		title = guideRes.getString("TITLE");
		close = guideRes.getString("CLOSE");
		viewTopics = guideRes.getString("VIEW_TOPICS");

		// Declaring contents
		helpContents[0] = guideRes.getString("MAIN");
		// Governance CDFs
		helpContents[1] = guideRes.getString("GOV_CDF");
		helpContents[2] = guideRes.getString("GOV_1");
		helpContents[3] = guideRes.getString("GOV_2");
		helpContents[4] = guideRes.getString("GOV_3");
		helpContents[5] = guideRes.getString("GOV_4");
		helpContents[6] = guideRes.getString("GOV_5");
		helpContents[7] = guideRes.getString("GOV_6");
		helpContents[8] = guideRes.getString("GOV_INDEX");
		// Socioeconomic CDFs
		helpContents[9] = guideRes.getString("SOC_CDF");
		helpContents[10] = guideRes.getString("SOC_1");
		helpContents[11] = guideRes.getString("SOC_2");
		helpContents[12] = guideRes.getString("SOC_3");
		helpContents[13] = guideRes.getString("SOC_4");
		helpContents[14] = guideRes.getString("SOC_5");
		helpContents[15] = guideRes.getString("SOC_6");
		helpContents[16] = guideRes.getString("SOC_INDEX");
		// Ecological CDFs
		helpContents[17] = guideRes.getString("ECO_CDF");
		helpContents[18] = guideRes.getString("ECO_1");
		helpContents[19] = guideRes.getString("ECO_2");
		helpContents[20] = guideRes.getString("ECO_3");
		helpContents[21] = guideRes.getString("ECO_4");
		helpContents[22] = guideRes.getString("ECO_5");
		helpContents[23] = guideRes.getString("ECO_6");
		helpContents[24] = guideRes.getString("ECO_INDEX");
		// GIS
		helpContents[25] = "<HTML><b>Spatial Guide</b></HTML>";
		helpContents[26] = "Layers";
		helpContents[27] = "Risk Model";
		helpContents[28] = "Oil Model";
		helpContents[29] = "Mangrove Model";
		
		// Declaring file names for contents
		helpFiles[0] = guideRes.getString("MAIN_FN");
		// Governance
		helpFiles[1] = guideRes.getString("GOV_CDF_FN");
		helpFiles[2] = guideRes.getString("GOV_1_FN");
		helpFiles[3] = guideRes.getString("GOV_2_FN");
		helpFiles[4] = guideRes.getString("GOV_3_FN");
		helpFiles[5] = guideRes.getString("GOV_4_FN");
		helpFiles[6] = guideRes.getString("GOV_5_FN");
		helpFiles[7] = guideRes.getString("GOV_6_FN");
		helpFiles[8] = guideRes.getString("GOV_INDEX_FN");
		// Socioeconomic
		helpFiles[9] = guideRes.getString("SOC_CDF_FN");
		helpFiles[10] = guideRes.getString("SOC_1_FN");
		helpFiles[11] = guideRes.getString("SOC_2_FN");
		helpFiles[12] = guideRes.getString("SOC_3_FN");
		helpFiles[13] = guideRes.getString("SOC_4_FN");
		helpFiles[14] = guideRes.getString("SOC_5_FN");
		helpFiles[15] = guideRes.getString("SOC_6_FN");
		helpFiles[16] = guideRes.getString("SOC_INDEX_FN");
		// Ecological
		helpFiles[17] = guideRes.getString("ECO_CDF_FN");
		helpFiles[18] = guideRes.getString("ECO_1_FN");
		helpFiles[19] = guideRes.getString("ECO_2_FN");
		helpFiles[20] = guideRes.getString("ECO_3_FN");
		helpFiles[21] = guideRes.getString("ECO_4_FN");
		helpFiles[22] = guideRes.getString("ECO_5_FN");
		helpFiles[23] = guideRes.getString("ECO_6_FN");
		helpFiles[24] = guideRes.getString("ECO_INDEX_FN");
		// GIS
		helpFiles[25] = null;
		helpFiles[26] = "GIS_Layers.htm";
		helpFiles[27] = "GIS_Risk.htm";
		helpFiles[28] = "GIS_Oil.htm";
		helpFiles[29] = "GIS_Mangrove.htm";
	}
	public void createTableOfContents() {
		contentsPane = new JPanel();
		
		// contentsPane.setLayout(new GridLayout(0, helpContents.length));
		contentsPane.setLayout(new BoxLayout(contentsPane, BoxLayout.Y_AXIS));
		
		JLabel label;
		
		for (int i = 0; i < helpContents.length; i++) {
			label = new JLabel(helpContents[i]);
			label.setForeground(topicColors[i]);
			
			if (isAHelpHeader[i] == true) {
				contentsPane.add(new JLabel("	"));
				// label.setFont(new Font("Helvectica", 15, Font.BOLD));
			}
			else if (isAHelpHeader[i] == false) {
				// label.setFont(new Font("Helvectica", 15, Font.PLAIN));
				label.addMouseListener(new TopicListener(helpFiles[i]));
			}
			contentsPane.add(label);
		}
		
		scrollPane = new JScrollPane();
		scrollPane.getViewport().add(contentsPane);
		contentsPane.setPreferredSize(new Dimension(400, 400));
	}
	public void createTopicPane() {
		textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setContentType("text/html");
		textPane.setPreferredSize(new Dimension(400, 400));
		
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

		contentsButton = new JButton(viewTopics);
		contentsButton.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				contentsButton.setEnabled(false);
				topicPane.setVisible(false);
				scrollPane.setVisible(true);
				
				/*
				 * TODO Find out why the dialog box needs to be resized for the contents to show
				 * once the pages are switched
				 */
				dialog.setSize(dialog.getWidth() + 1, dialog.getHeight() + 1);
				dialog.setSize(dialog.getWidth() - 1, dialog.getHeight() - 1);
			}
		});
		contentsButton.setEnabled(false);
		
		buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.setBackground(MIDAS.BACKGROUNDCOLOR);
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(contentsButton);
		buttonPane.add(closeButton);
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0,0,5,5));
	}
	public void createDialog() {
		dialog = new JDialog(MIDAS.frame, title);
		
		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
		topicPane.setVisible(false);
		top.add(topicPane);
		top.add(scrollPane);
		
		JPanel mainPane = new JPanel(new BorderLayout());
		mainPane.add(top, BorderLayout.CENTER);
		mainPane.add(buttonPane, BorderLayout.PAGE_END);
		mainPane.setOpaque(true);
		
		dialog.setContentPane(mainPane);
		dialog.setSize(new Dimension(400, 600));
		dialog.setLocationRelativeTo(MIDAS.frame);
		dialog.setVisible(true);
	}
	public class TopicListener extends MouseAdapter {
		
		String file;
		
		public TopicListener(String f) {
			file = f;
		}
		public void mouseClicked(MouseEvent e) {
			contentsButton.setEnabled(true);
			topicPane.setVisible(true);
			scrollPane.setVisible(false);

			dialog.setSize(dialog.getWidth() + 1, dialog.getHeight() + 1);
			dialog.setSize(dialog.getWidth() - 1, dialog.getHeight() - 1);
			
			try {
				url = Thread.currentThread().getContextClassLoader().getResource(file);
				System.out.println("Help file name: " + file);
				textPane.setPage(url);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
	}
}