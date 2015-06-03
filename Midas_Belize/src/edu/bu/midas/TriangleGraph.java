/* 
 * This graph graphically shows the balance of CDFs as well as how well the MMA is doing.
 * 
 * Original conception by Hrishi Patel. Triangle graph written for this version by Matt Carleton.
 * Chris Holden completely rewrote Matt's code to allow for both triangles to fit onto the 
 * same panel as well as to optimize the code and fix the influence of the future CDF value (CDF 6).
 */

package edu.bu.midas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JPanel;

public class TriangleGraph extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Localization and String variables
	private ResourceBundle triRes;
	private String title;
	private String[] graphTitle = new String[2];
	private String[] labels = new String[5];
	private String[] relArea = new String[2];
	private Font titleFont = new Font("Helvectica", Font.BOLD, 24);
	private Font labelFont = new Font("Helvetica", Font.PLAIN, 12);

	
	private Polygon[] pLabel = new Polygon[3];
	private Polygon[] fLabel = new Polygon[3];
	private int labelWidth = 8;
	private int[][] pLabelX = new int[3][4];
	private int[][] pLabelY = new int[3][4];
	private int[][] fLabelX = new int[3][4];
	private int[][] fLabelY = new int[3][4];
	private Color pink = new Color(255, 0, 204);
	private Color purple = new Color(102, 0, 204);
	private Color blue = new Color(51, 153, 255);
	
	// Generic offset information
	private int xOffset = 50;
	private int yOffset = 125;
	private int futureOffset = 275 + xOffset;
	private Color[] colorRamp = new Color[101];
	private int outerTriLength = 220;
	private Color lightGray = new Color(150,150,150);
	
	// FOR PRESENT TRIANGLE - denoted by 'p' before variable name
	// Outer triangle variables
	private int[] pOuterTriPointsX = new int[3];
	private int[] pOuterTriPointsY = new int[3];
	// Inner triangle variables
	private int[] pMaxInnerTriPointsX = new int[3];
	private int[] pMinInnerTriPointsX = new int[3];
	private int[] pMaxInnerTriPointsY = new int[3];
	private int[] pMinInnerTriPointsY = new int[3];
	private int[] pInnerTriPointsXRange = new int[3];
	private int[] pInnerTriPointsYRange = new int[3];
	// CDF counts and values
	private int pGovTotal;
	private int pSocTotal;
	private int pEcoTotal;
	private int pGovMax;
	private int pSocMax;
	private int pEcoMax;
	// Triangle points
	private int[] pInnerTriPointsX = new int[3];
	private int[] pInnerTriPointsY = new int[3];
	// Drawing variables
	private Polygon pOuterTriangle;
	private Polygon pInnerTriangle;
	private Color pInnerTriangleColor;
	// FOR FUTURE TRIANGLE - denoted by 'f' before variable name
	// Outer triangle variables
	private int[] fOuterTriPointsX = new int[3];
	private int[] fOuterTriPointsY = new int[3];
	// Inner triangle variables
	private int[] fMaxInnerTriPointsX = new int[3];
	private int[] fMinInnerTriPointsX = new int[3];
	private int[] fMaxInnerTriPointsY = new int[3];
	private int[] fMinInnerTriPointsY = new int[3];
	private int[] fInnerTriPointsXRange = new int[3];
	private int[] fInnerTriPointsYRange = new int[3];
	// CDF counts and values
	private int fGovTotal;
	private int fSocTotal;
	private int fEcoTotal;
	private int fGovMax;
	private int fSocMax;
	private int fEcoMax;
	// Triangle points
	private int[] fInnerTriPointsX = new int[3];
	private int[] fInnerTriPointsY = new int[3];
	// Drawing variables
	private Polygon fOuterTriangle;
	private Polygon fInnerTriangle;
	private Color fInnerTriangleColor;
	
	public TriangleGraph() {
		setBackground(MIDAS.BACKGROUNDCOLOR);
		
		// Calculating color ramp
		for(int i = 0; i<50; i++)
			colorRamp[i] = new Color(255, (int)(i*5.1), 0);
		for(int i = 50; i<100; i++)
			colorRamp[i] = new Color((int)(255 - 5.1*(i-50)), 255, 0);
		colorRamp[100] = new Color(0,255,0);
		
		setLanguage();
		setLabels();
		setPresentVariables();
		setFutureVariables();
	}
	public void setLanguage() {
		if (MIDAS.LANGUAGE.equals("EN")) {
			triRes = (PropertyResourceBundle) ResourceBundle.getBundle("Graphs", new Locale("en"));
		}
		else if (MIDAS.LANGUAGE.equals("ES")) {
			triRes = (PropertyResourceBundle) ResourceBundle.getBundle("Graphs", new Locale("es"));
		}
		labels[0] = triRes.getString("TRI_HIGH");
		labels[1] = triRes.getString("TRI_LOW");
		labels[2] = triRes.getString("TRI_GOV");
		labels[3] = triRes.getString("TRI_SOC");
		labels[4] = triRes.getString("TRI_ECO");
		
		relArea[0] = triRes.getString("TRI_AREA1");
		relArea[1] = triRes.getString("TRI_AREA2");
		
		title = triRes.getString("TRI_P_TITLE");
		graphTitle[0] = triRes.getString("TRI_P_GTITLE1");
		graphTitle[1] = triRes.getString("TRI_P_GTITLE2");
	}
	public void setLabels() {
		// Governance display labels
		pLabelX[0][0] = pMaxInnerTriPointsX[0];
		pLabelX[0][1] = pMaxInnerTriPointsX[0] + labelWidth;
		pLabelX[0][2]= pMinInnerTriPointsX[0] + labelWidth;
		pLabelX[0][3] = pMinInnerTriPointsX[0];
		pLabelY[0][0] = pMaxInnerTriPointsY[0];
		pLabelY[0][1] = pMaxInnerTriPointsY[0];
		pLabelY[0][2] = pMinInnerTriPointsY[0];
		pLabelY[0][3] = pMinInnerTriPointsY[0];
		// Socioeconomic display labels
		pLabelX[1][0] = pMaxInnerTriPointsX[1];
		pLabelX[1][1] = pMaxInnerTriPointsX[1];
		pLabelX[1][2] = pMinInnerTriPointsX[1];
		pLabelX[1][3] = pMinInnerTriPointsX[1];
		pLabelY[1][0] = pMaxInnerTriPointsY[1];
		pLabelY[1][1] = pMaxInnerTriPointsY[1] + labelWidth;
		pLabelY[1][2] = pMinInnerTriPointsY[1] + labelWidth;
		pLabelY[1][3] = pMinInnerTriPointsY[1];	
		// Ecological display labels
		pLabelX[2][0] = pMaxInnerTriPointsX[2];
		pLabelX[2][1] = pMaxInnerTriPointsX[2] - labelWidth;
		pLabelX[2][2] = pMinInnerTriPointsX[2] - labelWidth;
		pLabelX[2][3] = pMinInnerTriPointsX[2];
		pLabelY[2][0] = pMaxInnerTriPointsY[2];
		pLabelY[2][1] = pMaxInnerTriPointsY[2];
		pLabelY[2][2] = pMinInnerTriPointsY[2];
		pLabelY[2][3] = pMinInnerTriPointsY[2];
		// Governance display labels
		fLabelX[0][0] = fMaxInnerTriPointsX[0];
		fLabelX[0][1] = fMaxInnerTriPointsX[0] + labelWidth;
		fLabelX[0][2] = fMinInnerTriPointsX[0] + labelWidth;
		fLabelX[0][3] = fMinInnerTriPointsX[0];
		fLabelY[0][0] = fMaxInnerTriPointsY[0];
		fLabelY[0][1] = fMaxInnerTriPointsY[0];
		fLabelY[0][2] = fMinInnerTriPointsY[0];
		fLabelY[0][3] = fMinInnerTriPointsY[0];
		// Socioeconomic display labels
		fLabelX[1][0] = fMaxInnerTriPointsX[1];
		fLabelX[1][1] = fMaxInnerTriPointsX[1];
		fLabelX[1][2] = fMinInnerTriPointsX[1];
		fLabelX[1][3] = fMinInnerTriPointsX[1];
		fLabelY[1][0] = fMaxInnerTriPointsY[1];
		fLabelY[1][1] = fMaxInnerTriPointsY[1] + labelWidth;
		fLabelY[1][2] = fMinInnerTriPointsY[1] + labelWidth;
		fLabelY[1][3] = fMinInnerTriPointsY[1];	
		// Ecological display labels
		fLabelX[2][0] = fMaxInnerTriPointsX[2];
		fLabelX[2][1] = fMaxInnerTriPointsX[2] - labelWidth;
		fLabelX[2][2] = fMinInnerTriPointsX[2] - labelWidth;
		fLabelX[2][3] = fMinInnerTriPointsX[2];
		fLabelY[2][0] = fMaxInnerTriPointsY[2];
		fLabelY[2][1] = fMaxInnerTriPointsY[2];
		fLabelY[2][2] = fMinInnerTriPointsY[2];
		fLabelY[2][3] = fMinInnerTriPointsY[2];
	}
	public void setPresentVariables() {
		// Outer Triangle Points
		pOuterTriPointsX[0] = xOffset;
		pOuterTriPointsX[1] = xOffset + outerTriLength;
		pOuterTriPointsX[2] = xOffset + outerTriLength / 2;
		pOuterTriPointsY[0] = (int)(yOffset + outerTriLength * 0.866025404);
		pOuterTriPointsY[1] = (int)(yOffset + outerTriLength * 0.866025404);
		pOuterTriPointsY[2] = yOffset;
		// Outer Triangle
		pOuterTriangle = new Polygon(pOuterTriPointsX, pOuterTriPointsY, 3);
		
		// Inner Triangle
		pMaxInnerTriPointsX[0] = xOffset + outerTriLength;
		pMaxInnerTriPointsX[1] = xOffset;
		pMaxInnerTriPointsX[2] = xOffset + outerTriLength / 2;
		pMinInnerTriPointsX[0] = xOffset + outerTriLength * 3 / 4;
		pMinInnerTriPointsX[1] = xOffset + outerTriLength / 2;
		pMinInnerTriPointsX[2] = xOffset + outerTriLength * 1 / 4;
		pMaxInnerTriPointsY[0] = (int)(yOffset + outerTriLength * 0.866025404);
		pMaxInnerTriPointsY[1] = (int)(yOffset + outerTriLength * 0.866025404);
		pMaxInnerTriPointsY[2] = yOffset;
		pMinInnerTriPointsY[0] = yOffset + (int)(outerTriLength * 0.866025404 / 2);
		pMinInnerTriPointsY[1] = yOffset + (int)(outerTriLength * 0.866025404);
		pMinInnerTriPointsY[2] = yOffset + (int)(outerTriLength * 0.866025404 / 2);
		
		pInnerTriPointsXRange[0] = pMaxInnerTriPointsX[0] - pMinInnerTriPointsX[0];
		pInnerTriPointsXRange[1] = pMaxInnerTriPointsX[1] - pMinInnerTriPointsX[1];
		pInnerTriPointsXRange[2] = pMaxInnerTriPointsX[2] - pMinInnerTriPointsX[2];
		pInnerTriPointsYRange[0] = pMaxInnerTriPointsY[0] - pMinInnerTriPointsY[0];
		pInnerTriPointsYRange[1] = pMaxInnerTriPointsY[1] - pMinInnerTriPointsY[1];
		pInnerTriPointsYRange[2] = pMaxInnerTriPointsY[2] - pMinInnerTriPointsY[2];
		// Finding number of CDF values we need to partition the inner triangle space into
		// Min CDF - 1, max CDF - 5
		pGovMax = (MIDAS.govCDF[MIDAS.mmaNum].length - 1) * 4;
		pSocMax = (MIDAS.socCDF[MIDAS.mmaNum].length - 1) * 4;
		pEcoMax = (MIDAS.ecoCDF[MIDAS.mmaNum].length - 1) * 4;

		setPresentTriangle();
	}
	public void setFutureVariables() {
		// Outer Triangle Points
		fOuterTriPointsX[0] = futureOffset;
		fOuterTriPointsX[1] = futureOffset + outerTriLength;
		fOuterTriPointsX[2] = futureOffset + outerTriLength / 2;
		fOuterTriPointsY[0] = (int)(yOffset + outerTriLength * 0.866025404);
		fOuterTriPointsY[1] = (int)(yOffset + outerTriLength * 0.866025404);
		fOuterTriPointsY[2] = yOffset;
		// Outer Triangle
		fOuterTriangle = new Polygon(fOuterTriPointsX, fOuterTriPointsY, 3);
		
		// Inner Triangle
		fMaxInnerTriPointsX[0] = futureOffset + outerTriLength;
		fMaxInnerTriPointsX[1] = futureOffset;
		fMaxInnerTriPointsX[2] = futureOffset + outerTriLength / 2;
		fMinInnerTriPointsX[0] = futureOffset + outerTriLength * 3 / 4;
		fMinInnerTriPointsX[1] = futureOffset + outerTriLength / 2;
		fMinInnerTriPointsX[2] = futureOffset + outerTriLength * 1 / 4;
		fMaxInnerTriPointsY[0] = (int)(yOffset + outerTriLength * 0.866025404);
		fMaxInnerTriPointsY[1] = (int)(yOffset + outerTriLength * 0.866025404);
		fMaxInnerTriPointsY[2] = yOffset;
		fMinInnerTriPointsY[0] = yOffset + (int)(outerTriLength * 0.866025404 / 2);
		fMinInnerTriPointsY[1] = yOffset + (int)(outerTriLength * 0.866025404);
		fMinInnerTriPointsY[2] = yOffset + (int)(outerTriLength * 0.866025404 / 2);
	
		fInnerTriPointsXRange[0] = fMaxInnerTriPointsX[0] - fMinInnerTriPointsX[0];
		fInnerTriPointsXRange[1] = fMaxInnerTriPointsX[1] - fMinInnerTriPointsX[1];
		fInnerTriPointsXRange[2] = fMaxInnerTriPointsX[2] - fMinInnerTriPointsX[2];
		fInnerTriPointsYRange[0] = fMaxInnerTriPointsY[0] - fMinInnerTriPointsY[0];
		fInnerTriPointsYRange[1] = fMaxInnerTriPointsY[1] - fMinInnerTriPointsY[1];
		fInnerTriPointsYRange[2] = fMaxInnerTriPointsY[2] - fMinInnerTriPointsY[2];
		// Finding number of CDF values we need to partition the inner triangle space into
		// Includes CDF 6 now
		fGovMax = (MIDAS.govCDF[MIDAS.mmaNum].length) * 4;
		fSocMax = (MIDAS.socCDF[MIDAS.mmaNum].length) * 4;
		fEcoMax = (MIDAS.ecoCDF[MIDAS.mmaNum].length) * 4;
		
		setFutureTriangle();
	}
	public void setPresentTriangle() {
		// Finding current lengths
		pGovTotal = 0;
		for(int i = 0; i < MIDAS.govCDF[MIDAS.mmaNum].length - 1; i++) {
			pGovTotal = pGovTotal + (MIDAS.govCDF[MIDAS.mmaNum][i] - 1);
		}
		
		pSocTotal = 0;
		for(int i = 0; i < MIDAS.socCDF[MIDAS.mmaNum].length - 1; i++) {
			pSocTotal = pSocTotal + (MIDAS.socCDF[MIDAS.mmaNum][i] - 1);
		}
		pEcoTotal = 0;
		for(int i = 0; i < MIDAS.ecoCDF[MIDAS.mmaNum].length - 1; i++) {
			pEcoTotal = pEcoTotal + (MIDAS.ecoCDF[MIDAS.mmaNum][i] - 1);
		}
		
		// Inner triangle		
		if (pGovTotal <= pGovMax) {
			pInnerTriPointsX[0] = pMinInnerTriPointsX[0] + (int)(pInnerTriPointsXRange[0] * pGovTotal / pGovMax);
			pInnerTriPointsY[0] = pMinInnerTriPointsY[0] + (int)(pInnerTriPointsYRange[0] * pGovTotal / pGovMax);
		}
		else if (pGovTotal > pGovMax) {
			pInnerTriPointsX[0] = pMinInnerTriPointsX[0] + (int)(pInnerTriPointsXRange[0]);
			pInnerTriPointsY[0] = pMinInnerTriPointsY[0] + (int)(pInnerTriPointsYRange[0]);
		}
		if (pSocTotal <= pSocMax) {
			pInnerTriPointsX[1] = pMinInnerTriPointsX[1] + (int)(pInnerTriPointsXRange[1] * pSocTotal / pSocMax);
			pInnerTriPointsY[1] = pMinInnerTriPointsY[1] + (int)(pInnerTriPointsYRange[1] * pSocTotal / pSocMax);
		}
		else if (pSocTotal > pSocMax) {
			pInnerTriPointsX[1] = pMinInnerTriPointsX[1] + (int)(pInnerTriPointsXRange[1]);
			pInnerTriPointsY[1] = pMinInnerTriPointsY[1] + (int)(pInnerTriPointsYRange[1]);
		}
		if (pEcoTotal <= pEcoMax) {
			pInnerTriPointsX[2] = pMinInnerTriPointsX[2] + (int)(pInnerTriPointsXRange[2] * pEcoTotal / pEcoMax);
			pInnerTriPointsY[2] = pMinInnerTriPointsY[2] + (int)(pInnerTriPointsYRange[2] * pEcoTotal / pEcoMax);
		}
		else if (pEcoTotal > pEcoMax) {
			pInnerTriPointsX[2] = pMinInnerTriPointsX[2] + (int)(pInnerTriPointsXRange[2]);
			pInnerTriPointsY[2] = pMinInnerTriPointsY[2] + (int)(pInnerTriPointsYRange[2]);
		}
		
		pInnerTriangle = new Polygon(pInnerTriPointsX, pInnerTriPointsY, 3);
	}
	public void setFutureTriangle() {
		// Finding current lengths
		fGovTotal = 0;
		for(int i = 0; i < MIDAS.govCDF[MIDAS.mmaNum].length; i++) {
			fGovTotal = fGovTotal + (MIDAS.govCDF[MIDAS.mmaNum][i] - 1);
		}
		
		fSocTotal = 0;
		for(int i = 0; i < MIDAS.socCDF[MIDAS.mmaNum].length; i++) {
			fSocTotal = fSocTotal + (MIDAS.socCDF[MIDAS.mmaNum][i] - 1);
		}
		fEcoTotal = 0;
		for(int i = 0; i < MIDAS.ecoCDF[MIDAS.mmaNum].length; i++) {
			fEcoTotal = fEcoTotal + (MIDAS.ecoCDF[MIDAS.mmaNum][i] - 1);
		}
		// Adding in effects of CDF 6 - future projections
		
		// Inner triangle		
		if (fGovTotal <= fGovMax) {
			fInnerTriPointsX[0] = fMinInnerTriPointsX[0] + (int)(fInnerTriPointsXRange[0] * fGovTotal / fGovMax);
			fInnerTriPointsY[0] = fMinInnerTriPointsY[0] + (int)(fInnerTriPointsYRange[0] * fGovTotal / fGovMax);
		}
		else if (fGovTotal > fGovMax) {
			fInnerTriPointsX[0] = fMinInnerTriPointsX[0] + (int)(fInnerTriPointsXRange[0]);
			fInnerTriPointsY[0] = fMinInnerTriPointsY[0] + (int)(fInnerTriPointsYRange[0]);
		}
		if (fSocTotal <= fSocMax) {
			fInnerTriPointsX[1] = fMinInnerTriPointsX[1] + (int)(fInnerTriPointsXRange[1] * fSocTotal / fSocMax);
			fInnerTriPointsY[1] = fMinInnerTriPointsY[1] + (int)(fInnerTriPointsYRange[1] * fSocTotal / fSocMax);
		}
		else if (fSocTotal > fSocMax) {
			fInnerTriPointsX[1] = fMinInnerTriPointsX[1] + (int)(fInnerTriPointsXRange[1]);
			fInnerTriPointsY[1] = fMinInnerTriPointsY[1] + (int)(fInnerTriPointsYRange[1]);
		}
		if (fEcoTotal <= fEcoMax) {
			fInnerTriPointsX[2] = fMinInnerTriPointsX[2] + (int)(fInnerTriPointsXRange[2] * fEcoTotal / fEcoMax);
			fInnerTriPointsY[2] = fMinInnerTriPointsY[2] + (int)(fInnerTriPointsYRange[2] * fEcoTotal / fEcoMax);
		}
		else if (fEcoTotal > fEcoMax) {
			fInnerTriPointsX[2] = fMinInnerTriPointsX[2] + (int)(fInnerTriPointsXRange[2]);
			fInnerTriPointsY[2] = fMinInnerTriPointsY[2] + (int)(fInnerTriPointsYRange[2]);
		}
		
		fInnerTriangle = new Polygon(fInnerTriPointsX, fInnerTriPointsY, 3);
	}
	public Color getInnerTriangleColor(int gTotal, int gMax, int sTotal, int sMax, int eTotal, int eMax) {
		
		int index = (int)(100 * (gTotal + sTotal + eTotal) / (gMax + sMax + eMax));
		if (index > 100) {
			index = 100;
		}
		else if (index < 0) {
			index = 0;
		}
		Color color = colorRamp[index];
		return color;
	}
	public void updateGraph() {
		setPresentTriangle();
		setFutureTriangle();
		repaint();
	}
	public void drawTriangles(Graphics2D g2d) {
		// Present
		g2d.setColor(lightGray);
		g2d.fill(pOuterTriangle);
		g2d.setColor(getInnerTriangleColor(pGovTotal, pGovMax, pSocTotal, pSocMax, pEcoTotal, pEcoMax));
		g2d.fill(pInnerTriangle);
		// Future
		g2d.setColor(lightGray);
		g2d.fill(fOuterTriangle);
		g2d.setColor(getInnerTriangleColor(fGovTotal, fGovMax, fSocTotal, fSocMax, fEcoTotal, fEcoMax));
		g2d.fill(fInnerTriangle);
	}
	public void drawSliders(Graphics2D g2d) {
		int barWidth = 200;
		int barHeight = 15;
		float lineWidth = 3;
		int sliderWidth = 5;
		int sliderHeight = 15;
		// Present
		System.out.println("Present CDF Totals: " + pGovTotal + ", " + pSocTotal + ", " + pEcoTotal);
		System.out.println("Present CDF Maxes: " + pGovMax + ", " + pSocMax + ", " + pEcoMax);
		int sliderStartX = xOffset + barWidth * (pGovTotal + pSocTotal + pEcoTotal) / 
			(pGovMax + pSocMax + pEcoMax) - sliderWidth / 2;
		int sliderStartY = yOffset + outerTriLength + 25;
		
		for(int i = 0; i < 100; i++) {
			g2d.setColor(colorRamp[i]);
			g2d.fillRect(xOffset + 2*i, yOffset + outerTriLength + 25, 2, barHeight);
		}
		
		g2d.setColor(Color.black);
		g2d.setStroke(new BasicStroke(lineWidth));
		g2d.drawLine(sliderStartX, sliderStartY, sliderStartX + sliderWidth, sliderStartY);
		g2d.drawLine(sliderStartX + sliderWidth, sliderStartY, sliderStartX + sliderWidth,
				sliderStartY + sliderHeight);
		g2d.drawLine(sliderStartX + sliderWidth,sliderStartY + sliderHeight, sliderStartX,
				sliderStartY + sliderHeight);
		g2d.drawLine(sliderStartX, sliderStartY + sliderHeight, sliderStartX, sliderStartY);
		// Future
		System.out.println("Future CDF Totals: " + fGovTotal + ", " + fSocTotal + ", " + fEcoTotal);
		System.out.println("Future CDF Maxes: " + fGovMax + ", " + fSocMax + ", " + fEcoMax);
		sliderStartX = xOffset + outerTriLength + 75 + barWidth * (fGovTotal + fSocTotal + fEcoTotal) / 
			(fGovMax + fSocMax + fEcoMax) - sliderWidth / 2;
		sliderStartY = yOffset + outerTriLength + 25;
		
		for(int i = 0; i < 100; i++) {
			g2d.setColor(colorRamp[i]);
			g2d.fillRect(xOffset + outerTriLength + 75 + 2*i, yOffset + outerTriLength + 25, 2, barHeight);
		}
		
		g2d.setColor(Color.black);
		g2d.setStroke(new BasicStroke(lineWidth));
		g2d.drawLine(sliderStartX, sliderStartY, sliderStartX + sliderWidth, sliderStartY);
		g2d.drawLine(sliderStartX + sliderWidth, sliderStartY, sliderStartX + sliderWidth,
				sliderStartY + sliderHeight);
		g2d.drawLine(sliderStartX + sliderWidth,sliderStartY + sliderHeight, sliderStartX,
				sliderStartY + sliderHeight);
		g2d.drawLine(sliderStartX, sliderStartY + sliderHeight, sliderStartX, sliderStartY);
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
	
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

		// Getting dimensions of container
		int width = MIDAS.OUTCOMERES.width;
		
		RoundRectangle2D.Float roundedRectangle = new RoundRectangle2D.Float(15, 5, 
				(int)(MIDAS.OUTCOMERES.getWidth() - 30), (int)(MIDAS.OUTCOMERES.getHeight() - 30), 15, 15);
		g2d.setColor(Color.WHITE);
        g2d.fill(roundedRectangle); 
		
        drawTriangles(g2d);
        drawSliders(g2d);
        
        // Draw the MMA name
		String mma = MIDAS.mmaNames[MIDAS.mmaNum];
		g2d.setColor(Color.black);
		g2d.setFont(titleFont);
		g2d.drawString(mma, MIDAS.OUTCOMERES.width * 2 / 5, 30);
		
		// Drawing triangle titles
		g2d.drawString(graphTitle[0], xOffset + 75, yOffset - 50);
		g2d.drawString(graphTitle[1], xOffset + futureOffset, yOffset - 50);
		
		// Drawing colored labels to show which area is governance
		setLabels();
		pLabel[0] = new Polygon(pLabelX[0], pLabelY[0], 4);
		fLabel[0] = new Polygon(fLabelX[0], fLabelY[0], 4);
		g2d.setColor(purple);
		g2d.fill(pLabel[0]);
		g2d.fill(fLabel[0]);
		// socioeconoimc
		pLabel[1] = new Polygon(pLabelX[1], pLabelY[1], 4);
		fLabel[1] = new Polygon(fLabelX[1], fLabelY[1], 4);
		g2d.setColor(pink);
		g2d.fill(pLabel[1]);
		g2d.fill(fLabel[1]);
		// ecological
		pLabel[2] = new Polygon(pLabelX[2], pLabelY[2], 4);
		fLabel[2] = new Polygon(fLabelX[2], fLabelY[2], 4);
		g2d.setColor(blue);
		g2d.fill(pLabel[2]);
		g2d.fill(fLabel[2]);
		
		// Drawing text labels
		g2d.setFont(labelFont);
		g2d.setColor(Color.black);
		// Drawing high
		g2d.drawString(labels[0], pMaxInnerTriPointsX[0] + 10, pMaxInnerTriPointsY[0] + 10);
		g2d.drawString(labels[0], fMaxInnerTriPointsX[0] + 10, fMaxInnerTriPointsY[0] + 10);
		g2d.drawString(labels[0], pMaxInnerTriPointsX[1] - 20, pMaxInnerTriPointsY[1] - 5);
		g2d.drawString(labels[0], fMaxInnerTriPointsX[1] - 20, fMaxInnerTriPointsY[1] - 5);
		g2d.drawString(labels[0], pMaxInnerTriPointsX[2] - 10, pMaxInnerTriPointsY[2] - 10);
		g2d.drawString(labels[0], fMaxInnerTriPointsX[2] - 10, fMaxInnerTriPointsY[2] - 10);
		// Drawing low
		g2d.drawString(labels[1], pMinInnerTriPointsX[0] + 15, pMinInnerTriPointsY[0]);
		g2d.drawString(labels[1], fMinInnerTriPointsX[0] + 15, fMinInnerTriPointsY[0]);
		g2d.drawString(labels[1], pMinInnerTriPointsX[1] + 10, pMinInnerTriPointsY[1] + 20);
		g2d.drawString(labels[1], fMinInnerTriPointsX[1] + 10, fMinInnerTriPointsY[1] + 20);
		g2d.drawString(labels[1], pMinInnerTriPointsX[2] - 40, pMinInnerTriPointsY[2]);
		g2d.drawString(labels[1], fMinInnerTriPointsX[2] - 40, fMinInnerTriPointsY[2]);
		// Drawing gov/soc/eco labels
		g2d.drawString(labels[2], pMinInnerTriPointsX[0] + 20, pMinInnerTriPointsY[0] + 20);
		g2d.drawString(labels[2], fMinInnerTriPointsX[0] + 20, fMinInnerTriPointsY[0] + 20);
		g2d.drawString(labels[3], pMinInnerTriPointsX[1] - 30, pMinInnerTriPointsY[1] + 35);
		g2d.drawString(labels[3], fMinInnerTriPointsX[1] - 30, fMinInnerTriPointsY[1] + 35);
		g2d.drawString(labels[4], pMaxInnerTriPointsX[2] - 125, pMaxInnerTriPointsY[2] + 45);
		g2d.drawString(labels[4], fMaxInnerTriPointsX[2] - 125, fMaxInnerTriPointsY[2] + 45);

	}
}
