package edu.bu.midas;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;

public class BarLabel extends JLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BarLabel(String name) {
		setFont(new Font("Arial", Font.PLAIN, 12));
		setPreferredSize(new Dimension(300, 30));
		setText(name);
	}
}