package edu.bu.midas;

import java.awt.Dimension;

import javax.swing.JScrollBar;

public class ScrollBar extends JScrollBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int width = 200;
	int height = 20;
	int orientation = JScrollBar.HORIZONTAL; 
	int value = 3;
	int extent = 1;
	int min = 1; 
	int max = 6;
		
	public ScrollBar() {
		setOrientation(orientation);
		setValues(value, extent, min, max);
		setPreferredSize(new Dimension(width, height));
	}
	public ScrollBar(int val) {
		value = val;
		
		setOrientation(orientation);
		setValues(value, extent, min, max);
		setPreferredSize(new Dimension(width, height));
	}
	public ScrollBar(int val, int ext, int mn, int mx) {
		value = val;
		extent = ext;
		min = mn;
		max = mx;
		
		setOrientation(orientation);
		setValues(value, extent, min, max);
		setPreferredSize(new Dimension(width, height));
	}
}