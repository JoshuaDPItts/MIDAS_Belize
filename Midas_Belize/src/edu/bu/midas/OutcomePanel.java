package edu.bu.midas;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class OutcomePanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GraphPanel graphPane;
	private HelpPanel helpPane;
	private JPanel bottomPane;
	private JPanel indexPane;
	
	private JButton govGraphButton;
	private JButton socGraphButton;
	private JButton ecoGraphButton;
	private JButton comGraphButton;
	private JButton triGraphButton;
	
	private String govButtonText;
	private String socButtonText;
	private String ecoButtonText;
	private String comButtonText;
	private String triButtonText;
	
	private PopupMenu popup;
	
	private PropertyResourceBundle outRes;
	
	public OutcomePanel(int[] expertEvalVal) {
		setPreferredSize(MIDAS.RIGHTRES);
		setMaximumSize(MIDAS.RIGHTRES);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		setLanguage();
		createBottomPane();
		graphPane = new GraphPanel(expertEvalVal);
		
		add(graphPane);
		add(bottomPane);
		addPopupMenu();
	}
	public void setLanguage() {
		if (MIDAS.LANGUAGE.equals("EN")) {
			outRes = (PropertyResourceBundle) ResourceBundle.getBundle("Graphs", new Locale("en"));
		}
		else if (MIDAS.LANGUAGE.equals("ES")) {
			outRes = (PropertyResourceBundle) ResourceBundle.getBundle("Graphs", new Locale("es"));
		}
		govButtonText = outRes.getString("GOV_BUTTON");
		socButtonText = outRes.getString("SOC_BUTTON");
		ecoButtonText = outRes.getString("ECO_BUTTON");
		comButtonText = outRes.getString("COM_BUTTON");
		triButtonText = outRes.getString("TRI_BUTTON");
	}
	public void createBottomPane() {
		bottomPane = new JPanel();
		
		helpPane = new HelpPanel();
		indexPane = new JPanel();
		createIndexPane();
		
		bottomPane.setLayout(new BoxLayout(bottomPane, BoxLayout.X_AXIS));
		bottomPane.setPreferredSize(MIDAS.BOTTOMRES);
		bottomPane.setBackground(MIDAS.BACKGROUNDCOLOR);
		bottomPane.add(indexPane);
		bottomPane.add(helpPane);
	}
	public void createIndexPane() {
		indexPane = new JPanel();
		indexPane.setPreferredSize(MIDAS.OPTIONRES);
		indexPane.setLayout(new GridLayout(5,0));
		indexPane.setBackground(MIDAS.BACKGROUNDCOLOR);
		
		govGraphButton = new JButton(govButtonText);
		govGraphButton.addActionListener(new graphButtonListener("Gov"));
		socGraphButton = new JButton(socButtonText);
		socGraphButton.addActionListener(new graphButtonListener("Soc"));
		ecoGraphButton = new JButton(ecoButtonText);
		ecoGraphButton.addActionListener(new graphButtonListener("Eco"));
		comGraphButton = new JButton(comButtonText);
		comGraphButton.addActionListener(new graphButtonListener("Com"));
		triGraphButton = new JButton(triButtonText);
		triGraphButton.addActionListener(new graphButtonListener("Tri"));
		
		indexPane.add(govGraphButton);
		indexPane.add(socGraphButton);
		indexPane.add(ecoGraphButton);
		indexPane.add(comGraphButton);
		indexPane.add(triGraphButton);
	}
	public void addPopupMenu() {
		popup = new PopupMenu();
		this.addMouseListener(popup.popupListener);
		govGraphButton.addMouseListener(popup.popupListener);
		ecoGraphButton.addMouseListener(popup.popupListener);
		socGraphButton.addMouseListener(popup.popupListener);
		comGraphButton.addMouseListener(popup.popupListener);
		triGraphButton.addMouseListener(popup.popupListener);
	}
	public void updateGraph() {
		graphPane.updateGraph();
	}
	public void helpRequest(String name) {
		helpPane.helpRequest(name);
	}
	public BufferedImage getCDFReportBufferedImage(String name) {
		graphPane.changeGraphs(name);
		return graphPane.getCDFReportBufferedImage(name);
	}
	
	public class graphButtonListener implements ActionListener {
		
		String name;
		
		public graphButtonListener(String n) {
			name = n;
		}
		public void actionPerformed(ActionEvent e) {
			graphPane.changeGraphs(name);
			helpPane.helpRequest(name);
		}
	}
	public void repaintGraphs() {
		graphPane.repaintGraphs();
	}
}
