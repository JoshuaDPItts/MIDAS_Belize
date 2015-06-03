package edu.bu.midas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LegendGroup extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String layerName;
	private JLabel layerLabel;
	private JCheckBox layerBox;
	private String boxName = "Turn layer ";
	/*
	private JLabel riskLabel;
	private scrollBar riskBar;
	*/
	private Color color;
	
	/*
	private int riskVal = 0;
	private int riskExtent = 1;
	private int riskMin = 0;
	private int riskMax = 101;
	*/
	

	public LegendGroup(String lyrName, Color clr) {
		layerName = lyrName;
		color = clr;
		
		setBorder(BorderFactory.createLineBorder(Color.black));
		setBackground(color);
		setLayout(new GridLayout(1,0));
		setPreferredSize(new Dimension((int)MIDAS.LEFTRES.getWidth(), 30));
		setMinimumSize(new Dimension((int)MIDAS.LEFTRES.getWidth(), 30));
		setMaximumSize(new Dimension(10000, 30));
		
		layerLabel = new JLabel(layerName, JLabel.CENTER);
		layerBox = new JCheckBox(boxName + "on");
		layerBox.addActionListener(new layerLabelListener());
		/*
		riskBar = new scrollBar(riskVal, riskExtent, riskMin, riskMax);
		riskBar.addAdjustmentListener(new riskBarLabelListener());
		riskLabel = new JLabel("Risk: " + Integer.toString(riskBar.getValue()) + "%");
		// sets riskBar as false until layer is turned on
		riskBar.setEnabled(false);
		*/

		add(layerLabel);
		add(layerBox, JLabel.CENTER);
		//add(riskLabel);
		//add(riskBar);
	}
	/*
	public void changeCell(double riskVal) {
		riskBar.setValue((int)riskVal);
	}
	public void enableDisableRisk(boolean bool) {
		riskBar.setEnabled(bool);
	}
	*/
	public void addLayerListener(ActionListener ae) {
		layerBox.addActionListener(ae);
	}
	/*
	public void addRiskListener(AdjustmentListener a) {
		riskBar.addAdjustmentListener(a);
	}
	*/
	class layerLabelListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (layerBox.isSelected() == true) {
				layerBox.setText(boxName + "off");
			}
			else if (layerBox.isSelected() == false) {
				layerBox.setText(boxName + "on");
			}
		}
		
	}
	/*
	class riskBarLabelListener implements AdjustmentListener {

		public void adjustmentValueChanged(AdjustmentEvent e) {
			riskLabel.setText("Risk: " + Integer.toString(e.getValue()) + "%");
		}	
	}
	*/
}
