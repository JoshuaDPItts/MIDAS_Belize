package edu.bu.midas;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class CDFPanel extends JPanel implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTabbedPane cdfTabs;
	private JPanel govPane;
	private JPanel socPane;
	private JPanel ecoPane;
	
	private OutcomePanel outcomePane;
	
	private PopupMenu popup;
		
	private String[] govCDFNames;
	private String[][] govValNames;
	private CDFGroup[] govCDFgroups;
	
	private String[] socCDFNames;
	private String[][] socValNames;
	private CDFGroup[] socCDFgroups;
	
	private String[] ecoCDFNames;
	private String[][] ecoValNames;
	private CDFGroup[] ecoCDFgroups;
	
	private BoxLayout cdfLayout;
	
	private String[] labels;
	private String[] headers;
	private String value;
	
	private Font headerFont = new Font("Helvectica", Font.BOLD, 20);
	
	private Color pink = new Color(255, 0, 204);
	private Color purple = new Color(102, 0, 204);
	private Color blue = new Color(51, 153, 255);
		
	public CDFPanel(String val, String[] lbls, String[] hdrs, String[] gCDFn, String[][] gValn, 
			String[] sCDFn, String[][] sValn, String[] eCDFn, String[][] eValn, int[] expertEvalVal) {
		value = val;
		labels = lbls;
		headers = hdrs;
		
		govCDFNames = gCDFn;
		govValNames = gValn;
		govCDFgroups = new CDFGroup[govCDFNames.length];
		
		socCDFNames = sCDFn;
		socValNames = sValn;
		socCDFgroups = new CDFGroup[socCDFNames.length];
		
		ecoCDFNames = eCDFn;
		ecoValNames = eValn;
		ecoCDFgroups = new CDFGroup[ecoCDFNames.length];
		
		popup = new PopupMenu();
		this.addMouseListener(popup.popupListener);
				
		createCDFPanes();
		createCDFTabs();
		createOutcomes(expertEvalVal);
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setBackground(MIDAS.BACKGROUNDCOLOR);
		add(cdfTabs);
		add(outcomePane);
		
	}
	public void createCDFTabs() {
		cdfTabs = new JTabbedPane();
		cdfTabs.setPreferredSize(MIDAS.LEFTRES);
		cdfTabs.setMinimumSize(MIDAS.LEFTRES);
		cdfTabs.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		
		cdfTabs.addTab(labels[0], govPane);
		cdfTabs.addTab(labels[1], socPane);
		cdfTabs.addTab(labels[2], ecoPane);
		
		cdfTabs.addMouseListener(popup.popupListener);
	}
	public void createCDFPanes() {
		govPane = new JPanel();
		cdfLayout = new BoxLayout(govPane, BoxLayout.Y_AXIS);
		govPane.setLayout(cdfLayout);
		govPane.setBackground(MIDAS.BACKGROUNDCOLOR);
		JLabel govHeader = new JLabel(headers[0]);
		govHeader.setFont(headerFont);
		govHeader.setForeground(purple);
		govHeader.addMouseListener(new cdfHelpListener("GovHeader"));
		govPane.add(govHeader);
		for (int i = 0; i < govCDFgroups.length; i++) {
			govCDFgroups[i] = new CDFGroup(value, govCDFNames[i], govValNames[i], 
					MIDAS.govCDF[MIDAS.mmaNum][i], purple);
			govCDFgroups[i].cdfLabel.addMouseListener(
					new cdfHelpListener("G" + Integer.toString(i + 1)));
			govCDFgroups[i].cdfValueLabel.addMouseListener(
					new cdfHelpListener("G" + Integer.toString(i + 1)));
			govCDFgroups[i].cdfScrollBar.addAdjustmentListener(
					new cdfListener(i, MIDAS.govCDF, govCDFgroups[i]));
			govPane.add(govCDFgroups[i]);
			// govPane.add(Box.createRigidArea(new Dimension(0, 15)));
		}
		govPane.add(Box.createVerticalGlue());
		govPane.addMouseListener(popup.popupListener);
		
		socPane = new JPanel();
		cdfLayout = new BoxLayout(socPane, BoxLayout.Y_AXIS);
		socPane.setLayout(cdfLayout);
		socPane.setBackground(MIDAS.BACKGROUNDCOLOR);
		JLabel socHeader = new JLabel(headers[1]);
		socHeader.setFont(headerFont);
		socHeader.setForeground(pink);
		socHeader.addMouseListener(new cdfHelpListener("SocHeader"));
		socPane.add(socHeader);
		for (int i = 0; i < socCDFgroups.length; i++) {
			socCDFgroups[i] = new CDFGroup(value, socCDFNames[i], socValNames[i], 
					MIDAS.socCDF[MIDAS.mmaNum][i], pink);
			socCDFgroups[i].cdfLabel.addMouseListener(
					new cdfHelpListener("SE" + Integer.toString(i + 1)));
			socCDFgroups[i].cdfValueLabel.addMouseListener(
					new cdfHelpListener("SE" + Integer.toString(i + 1)));
			socCDFgroups[i].cdfScrollBar.addAdjustmentListener(
					new cdfListener(i, MIDAS.socCDF, socCDFgroups[i]));
			socPane.add(socCDFgroups[i]);
		}
		socPane.add(Box.createVerticalGlue());
		socPane.addMouseListener(popup.popupListener);
		
		ecoPane = new JPanel();
		cdfLayout = new BoxLayout(ecoPane, BoxLayout.Y_AXIS);
		ecoPane.setLayout(cdfLayout);
		ecoPane.setBackground(MIDAS.BACKGROUNDCOLOR);
		JLabel ecoHeader = new JLabel(headers[2]);
		ecoHeader.setFont(headerFont);
		ecoHeader.setForeground(blue);
		ecoHeader.addMouseListener(new cdfHelpListener("EcoHeader"));
		ecoPane.add(ecoHeader);
		for (int i = 0; i < ecoCDFgroups.length; i++) {
			ecoCDFgroups[i] = new CDFGroup(value, ecoCDFNames[i], ecoValNames[i], 
					MIDAS.ecoCDF[MIDAS.mmaNum][i], blue);
			ecoCDFgroups[i].cdfLabel.addMouseListener(
					new cdfHelpListener("EC" + Integer.toString(i + 1)));
			ecoCDFgroups[i].cdfValueLabel.addMouseListener(
					new cdfHelpListener("EC" + Integer.toString(i + 1)));
			ecoCDFgroups[i].cdfScrollBar.addAdjustmentListener(
					new cdfListener(i, MIDAS.ecoCDF, ecoCDFgroups[i]));
			ecoPane.add(ecoCDFgroups[i]);
		}
		ecoPane.add(Box.createVerticalGlue());
		ecoPane.addMouseListener(popup.popupListener);
	}
	public void createOutcomes(int[] expertEvalVal) {
		outcomePane = new OutcomePanel(expertEvalVal);
	}
	
	public void saveCDFgroups(FileOutputStream fileStream, ObjectOutputStream objectStream){
		for (int i = 0; i < govCDFgroups.length; i++) {
			govCDFgroups[i].saveState(fileStream, objectStream);
		}
		for (int i = 0; i < socCDFgroups.length; i++) {
			socCDFgroups[i].saveState(fileStream, objectStream);
		}
		for (int i = 0; i < ecoCDFgroups.length; i++) {
			ecoCDFgroups[i].saveState(fileStream, objectStream);
		}
	}
	
	public void restoreCDFgroups(FileInputStream fileStream, ObjectInputStream objectStream) throws ClassNotFoundException{
		for (int i = 0; i < govCDFgroups.length; i++) {
			govCDFgroups[i].restoreState(fileStream, objectStream);
		}
		for (int i = 0; i < socCDFgroups.length; i++) {
			socCDFgroups[i].restoreState(fileStream, objectStream);
		}
		for (int i = 0; i < ecoCDFgroups.length; i++) {
			ecoCDFgroups[i].restoreState(fileStream, objectStream);
		}
	}
	public void updateCDFs(int mma) {
		for (int i = 0; i < govCDFgroups.length; i++) {
			govCDFgroups[i].updateCDFs(MIDAS.govCDF[mma][i]);
		}
		for (int i = 0; i < socCDFgroups.length; i++) {
			socCDFgroups[i].updateCDFs(MIDAS.socCDF[mma][i]);
		}
		for (int i = 0; i < ecoCDFgroups.length; i++) {
			ecoCDFgroups[i].updateCDFs(MIDAS.ecoCDF[mma][i]);
		}
	}
	public class cdfListener implements AdjustmentListener {

		int num;
		int[][] cdf;
		CDFGroup cdfGroup;
		
		public cdfListener(int n, int[][] _cdf, CDFGroup cdfG) {
			num = n;
			cdf = _cdf;
			cdfGroup = cdfG;
		}
		public void adjustmentValueChanged(AdjustmentEvent ae) {
			int val = ae.getValue();
			cdfGroup.updateLabel(val);
			cdf[MIDAS.mmaNum][num] = val;
			outcomePane.updateGraph();
		}
 	}
	
	public class cdfHelpListener extends MouseAdapter {
		
		String name;
		
		public cdfHelpListener(String n) {
			name = n;
		}
		public void mouseClicked(MouseEvent e) {
			outcomePane.helpRequest(name);
		}
	}

	public void repaintGraphs() {
		outcomePane.repaintGraphs();
	}
	public BufferedImage getCDFReportBufferedImage(String name) {
		return outcomePane.getCDFReportBufferedImage(name);
	}
}
