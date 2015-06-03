package edu.bu.midas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class WeightsPopup {
	private JDialog dialog;	//this is the window that pops up
	private JTabbedPane tabbedPane;
	// private JPanel[] weightPane = new JPanel[MIDAS.mmaNames.length];
	private JPanel buttonPane;
	
	private JButton applyButton;
	private JButton closeButton;
	
	private JTextField[][][] weightField = 
		new JTextField[MIDAS.mmaNames.length][3][MIDAS.govWeights[0].length];
	
	private String title;
	private String[] outcomeTexts = new String[3];
	private String[] outcomeLetters;
	private String apply;
	private String close;
	
	private PropertyResourceBundle weightRes;
	
	public WeightsPopup() {
		setLanguage();
	}
	public void setLanguage() {
		if (MIDAS.LANGUAGE.equals("EN")) {
			weightRes = (PropertyResourceBundle) ResourceBundle.getBundle("Weights", new Locale("en"));
		}
		else if (MIDAS.LANGUAGE.equals("ES")) {
			weightRes = (PropertyResourceBundle) ResourceBundle.getBundle("Weights", new Locale("es"));
		}
		title = weightRes.getString("TITLE");
		// weightName = weightRes.getString("WEIGHT_NAME");
		// weightValue = weightRes.getString("WEIGHT_VALUE");
		outcomeTexts[0] = "Governance Index";
		outcomeTexts[1] = "Socioeconomic Index";
		outcomeTexts[2] = "Ecological Index";
		apply = weightRes.getString("APPLY");
		close = weightRes.getString("CLOSE");
	}
	public JPanel createWeightPane(int index) {
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(600,800));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JPanel outcomeLabelPane = new JPanel();
		outcomeLabelPane.setLayout(new GridLayout(1,3));
		JLabel[] outcomeLabels = new JLabel[3];
		outcomeLabels[0] = new JLabel(outcomeTexts[0]);
		outcomeLabels[0].setForeground(new Color(102, 0, 204));
		outcomeLabels[1] = new JLabel(outcomeTexts[1]);
		outcomeLabels[1].setForeground(new Color(255, 0, 204));
		outcomeLabels[2] = new JLabel(outcomeTexts[2]);
		outcomeLabels[2].setForeground(new Color(51, 153, 255));
		
		outcomeLabelPane.add(outcomeLabels[0]);
		outcomeLabelPane.add(outcomeLabels[1]);
		outcomeLabelPane.add(outcomeLabels[2]);
		panel.add(outcomeLabelPane);
		
		outcomeLetters = new String[MIDAS.govWeights[0].length];
		Color[] outcomeColors = new Color[MIDAS.govWeights[0].length];
		for (int i = 0; i < 6; i++) {
			outcomeLetters[i] = "G" + (i + 1) + ": ";
			outcomeLetters[i + 6] = "S" + (i + 1) + ": ";
			outcomeLetters[i + 12] = "E" + (i + 1) + ": ";
			
			outcomeColors[i] = new Color(102, 0, 204);
			outcomeColors[i + 6] = new Color(255, 0, 204);
			outcomeColors[i + 12] = new Color(51, 153, 255);
		}
		
		for (int i = 0; i < MIDAS.govWeights[0].length; i++) {
			JLabel outcomeLabel1 = new JLabel(outcomeLetters[i]);
			outcomeLabel1.setForeground(outcomeColors[i]);
			JLabel outcomeLabel2 = new JLabel(outcomeLetters[i]);
			outcomeLabel2.setForeground(outcomeColors[i]);
			JLabel outcomeLabel3 = new JLabel(outcomeLetters[i]);
			outcomeLabel3.setForeground(outcomeColors[i]);
			
			weightField[index][0][i] = new JTextField(5);
			weightField[index][1][i] = new JTextField(5);
			weightField[index][2][i] = new JTextField(5);
			
			weightField[index][0][i].setText(Double.toString(MIDAS.govWeights[index][i]));
			weightField[index][1][i].setText(Double.toString(MIDAS.socWeights[index][i]));
			weightField[index][2][i].setText(Double.toString(MIDAS.ecoWeights[index][i]));
			
			JPanel rowPanel = new JPanel();
			rowPanel.setPreferredSize(new Dimension(600, 20));
			rowPanel.setLayout(new GridLayout(1,6));
			rowPanel.add(outcomeLabel1);
			rowPanel.add(weightField[index][0][i]);
			rowPanel.add(outcomeLabel2);
			rowPanel.add(weightField[index][1][i]);
			rowPanel.add(outcomeLabel3);
			rowPanel.add(weightField[index][2][i]);
			
			panel.add(rowPanel);
		}
		
		return panel;
	}
	public void createTabbedPane() {
		tabbedPane = new JTabbedPane();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		tabbedPane.setPreferredSize(new Dimension(600, 800));
		
		for (int i = 0; i < MIDAS.mmaNames.length; i++) {
			tabbedPane.addTab(MIDAS.mmaNames[i], createWeightPane(i));
		}
	}
	public void createBottomPane() {
		applyButton = new JButton(apply);
		applyButton.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				boolean allNumbers = true;
				
				// Running through all input weights and checking to see if they are numbers
				for (int i = 0; i < MIDAS.mmaNames.length; i++) {
					for (int j = 0; j < MIDAS.govWeights[0].length; j++) {
						try {
							Double.parseDouble((weightField[i][0][j].getText()));
						}	catch(NumberFormatException nFE) {
							allNumbers = false;
							throwWeightInputError(MIDAS.mmaNames[i], "Governance", outcomeLetters[j]);
							return;
						}					
						try {
							Double.parseDouble((weightField[i][1][j].getText()));
						}	catch(NumberFormatException nFE) {
							allNumbers = false;
							throwWeightInputError(MIDAS.mmaNames[i], "Socioeconomic", outcomeLetters[j]);
							return;
						}	
						try {
							Double.parseDouble((weightField[i][0][j].getText()));
						}	catch(NumberFormatException nFE) {
							allNumbers = false;
							throwWeightInputError(MIDAS.mmaNames[i], "Ecological", outcomeLetters[j]);
							return;
						}	
					}
				}
				if (allNumbers == true) {
					System.out.println("Putting weights in");
					for (int i = 0; i < MIDAS.mmaNames.length; i++) {
						for (int j = 0; j < MIDAS.govWeights[0].length; j++) {
							MIDAS.govWeights[i][j] = Double.parseDouble((weightField[i][0][j].getText()));
							MIDAS.socWeights[i][j] = Double.parseDouble((weightField[i][1][j].getText()));
							MIDAS.ecoWeights[i][j] = Double.parseDouble((weightField[i][2][j].getText()));
						}
					}
					MIDAS.repaintGraphs();
				}
				else {
					
					
				}
			}
		});
		
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
		buttonPane.add(applyButton);
		buttonPane.add(closeButton);
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0,0,5,5));
	}
	public void createDialog() {
		dialog = new JDialog(MIDAS.frame, title);
	
		createTabbedPane();
		createBottomPane();
		
		JPanel mainPane = new JPanel(new BorderLayout());
		mainPane.add(tabbedPane, BorderLayout.CENTER);
		mainPane.add(buttonPane, BorderLayout.PAGE_END);
		mainPane.setOpaque(true);
		
		dialog.setContentPane(mainPane);
		dialog.setSize(new Dimension(600, 800));
		dialog.setLocationRelativeTo(MIDAS.frame);
		dialog.setVisible(true);
	}
	public void throwWeightInputError(String mmaName, String outcome, String outcomeLetter) {
		String message =  "Please enter only numbers for weights. The weight for " + outcomeLetter.substring(0, 2) +
			" for the " + outcome + " outcome in " + mmaName + " is not a suitable weight.";
		JOptionPane.showMessageDialog(new JFrame(), message, 
				"Weights input error!", JOptionPane.ERROR_MESSAGE);
	}
}
