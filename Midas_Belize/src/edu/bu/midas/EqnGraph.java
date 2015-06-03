package edu.bu.midas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class EqnGraph extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int width;
	private int height;
	
	private String equation;
	private double equationM;
	private double equationB;
	private double[] x = new double[200];
	private double[] y = new double[200];
	private double xStep = 0.01;
	
	// Drawing info
	private int tickLength = 10;
	private int pointDim = 2;
	
	public EqnGraph(int w, int h, String eqnType, double eqnM, double eqnB) {
		width = w;
		height = h;
		equation = eqnType;
		equationM = eqnM;
		equationB = eqnB;
		
		setPreferredSize(new Dimension(w, h));
		setBorder(BorderFactory.createLineBorder(Color.black));
		
		// generating x values [-1:1]
		double temp = -1;
		for (int i = 0; i < x.length; i++) {
			temp = temp + xStep;
			x[i] = temp;
		}
		calcY();
		
		// setBorder(BorderFactory.createLineBorder(Color.black));
	}
	public void calcY() {
		// Setting y values
		if (equation.equals("SIG")) {
			for (int i = 0; i < y.length; i++) {
				y[i] = Math.pow(1 + Math.exp(-equationM*x[i]), -1) + equationB;
			}
		}
		else if (equation.equals("LIN")) {
			for (int i = 0; i < y.length; i++) {
				y[i] = equationM * x[i] + equationB;
			}
		}
		for (int i = 0; i < y.length; i++) {
			if (y[i] > 1) {
				y[i] = 1;
			}
			else if (y[i] < -1) {
				y[i] = -1;
			}
		}
	}
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		super.paintComponent(g2d);
		
		calcY();
		// Drawing axis
		g2d.drawLine(width / 2, 0, width / 2, height);
		g2d.drawLine(0, height / 2, width, height / 2);
		
		// Drawing ticks on x axis
		g2d.drawLine(width / 4, height / 2 - tickLength / 2, width / 4, height / 2 + tickLength / 2);
		g2d.drawLine(width * 3 / 4, height /2 - tickLength / 2, width * 3 / 4, height / 2 + tickLength / 2);
		
		// Drawing y
		for (int i = 0; i < y.length; i++) {
			g2d.fillRect((int)((width / 2 + width / x.length * x[i] / xStep) - pointDim / 2), 
					(int)((height / 2 - height * 2 / y.length * y[i] / xStep) - pointDim / 2), 
					pointDim, pointDim);
		}
	}
	public void changeType(String eqn) {
		equation = eqn;
		calcY();
		repaint();
	}
	public void updateMB(double m, double b) {
		equationM = m;
		equationB = b;
	}
}
