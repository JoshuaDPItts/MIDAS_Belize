package edu.bu.midas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class CDFGroup extends JPanel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String cdfName;
	private String[] cdfValNames;
	private int cdfVal;
	private Color cdfColor;
	
	public JLabel cdfLabel;
	public JLabel cdfValueLabel;
	public ScrollBar cdfScrollBar;
	
	private Font cdfFont = new Font("Helvectica", Font.BOLD, 14);
	private Font cdfLabelFont = new Font("Helvectica", Font.PLAIN, 12);

	private String value;	//Either "Value: " or the spanish equivalent
	
	public CDFGroup(String val, String cdfN, String[] cdfVN, int cdfV, Color cdfC){
		value = val;
		cdfName = cdfN;
		cdfValNames = cdfVN;
		cdfVal = cdfV;
		cdfColor = cdfC;
		
		setPreferredSize(new Dimension((int)MIDAS.LEFTRES.getWidth(), 100));
		setMinimumSize(new Dimension((int)MIDAS.LEFTRES.getWidth(), 75));
		setBackground(MIDAS.BACKGROUNDCOLOR);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
				
		addComponents();
	}
	public void addComponents() {
		cdfLabel = new JLabel("<html>" + cdfName + "</html>", SwingConstants.LEFT);
		cdfLabel.setForeground(cdfColor);
		// cdfLabel.setForeground(cdfColor);
		cdfLabel.setFont(cdfFont);
		cdfValueLabel = new JLabel(value + ": " + cdfValNames[cdfVal - 1]);
		cdfValueLabel.setFont(cdfLabelFont);
		cdfScrollBar = new ScrollBar(cdfVal);
		
		add(cdfLabel);
		add(cdfValueLabel);
		add(Box.createRigidArea(new Dimension(0, 10)));
		add(cdfScrollBar);
	}
	public void updateLabel(int val) 
	{	
		cdfVal = val;
		cdfValueLabel.setText(value + ": " + cdfValNames[cdfVal - 1]);
	}
	public void saveState(FileOutputStream fileStream, ObjectOutputStream objectStream){
		
		try{
			
			
			objectStream.writeInt(cdfVal);
			System.out.println("saving integer: " + cdfVal);
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void restoreState(FileInputStream fileStream, ObjectInputStream objectStream) throws ClassNotFoundException{
	
		try{
	
			cdfVal = objectStream.readInt();
			System.out.println("loading integer: " + cdfVal);
		
			
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void updateCDFs(int val) {
		cdfVal = val;
		cdfScrollBar.setValue(cdfVal);
		cdfScrollBar.repaint();
	}
}
