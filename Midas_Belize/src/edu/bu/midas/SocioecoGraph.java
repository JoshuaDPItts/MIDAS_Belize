package edu.bu.midas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JPanel;

public class SocioecoGraph extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String title;
	private String[] yTitle = new String[2];
	private String xTitle;
	private String[] yValues = new String[5];
	private String[] xValues = new String[2];
	private int barHeight = 60;
	private int barWidth = 100;
	private int barSpace = 50;
	private int legendBarWidth = 75;
	private int xAxisLength = 2 * barSpace + 2 * barWidth;
	private int yAxisLength = 300;
	private int startX = 175;
	private int startY = 125;
	private int axisStartX = startX + barWidth;
	private int axisWidth = 2;
	private int barStartX = axisStartX + barSpace / 2;

	private Point2D.Double[] userPt = new Point2D.Double[2];
	private Point2D.Double[] expertPt = new Point2D.Double[3];
	private double[] expertStdev = new double[3];

	private int presentX = barStartX + barWidth / 2;
	private int futureX = barStartX + barSpace + barWidth + barWidth / 2;
	private int dotWidth = 10;
	private String[] lineNames = new String[5];
	private float []dashes = {10F};
	private BasicStroke userStroke = new BasicStroke(2.0F);
	private BasicStroke expertStroke = new BasicStroke(2.0F, BasicStroke.CAP_ROUND, 
			BasicStroke.JOIN_ROUND, 10.0F, dashes, 5.0F);

	private double presentRatio;
	private double futureRatio;

	private Color green = new Color(51, 153, 0);
	private Color yellow = new Color(255, 255, 0);
	private Color lightGreen = new Color(102, 255, 0);
	private Color orange = new Color(255, 102, 0);
	private Color red = new Color(255, 0, 0);
	private Color[] scale = {green, lightGreen, yellow, orange, red};
	private Color pink = new Color(255, 0, 204);

	private Font titleFont = new Font("Helvectica", Font.BOLD, 24);
	private Font axisTitleFont = new Font("Helvectica", Font.BOLD, 18);
	private Font valueFont = new Font("Helvectica", Font.BOLD, 16);
	private Font lineFont = new Font("Helvectica", Font.BOLD, 12);

	MidasCalculator midasCalc;

	private PropertyResourceBundle socRes;

	private boolean hasExpertValues = true;
	private int[] expertValues;

	public SocioecoGraph(int[] expertVals) {	
		if (expertVals == null) {
			hasExpertValues = false;
		}
		else {
			expertValues = expertVals;
		}		
		userPt[0] = new Point2D.Double(presentX, startY + yAxisLength / 2);
		userPt[1] = new Point2D.Double(futureX, startY + yAxisLength / 2);
		expertPt[0] = new Point2D.Double(presentX, startY + yAxisLength / 2);
		expertPt[1] = new Point2D.Double(futureX, startY);
		expertPt[2] = new Point2D.Double(futureX, startY + yAxisLength);
		expertStdev[0] = 0;
		expertStdev[1] = 0;
		expertStdev[2] = 0;

		setLanguage();
		midasCalc = new MidasCalculator();
		calculatePts();
	}
	public void updateGraph() {
		setLanguage();
		calculatePts();
		repaint();
	}

	public void setLanguage()
	{	
		if (MIDAS.LANGUAGE.equals("EN")) {
			socRes = (PropertyResourceBundle) ResourceBundle.getBundle("Graphs", new Locale("en"));
		}
		else if (MIDAS.LANGUAGE.equals("ES")) {
			socRes = (PropertyResourceBundle) ResourceBundle.getBundle("Graphs", new Locale("es"));
		}

		title = socRes.getString("SOC_TITLE");
		yTitle[0] = socRes.getString("SOC_YTITLE1");
		yTitle[1] = socRes.getString("SOC_YTITLE2");

		xTitle = socRes.getString("CDF_XTITLE");
		yValues[0] = socRes.getString("CDF_YVAL1");
		yValues[1] = socRes.getString("CDF_YVAL2");
		yValues[2] = socRes.getString("CDF_YVAL3");
		yValues[3] = socRes.getString("CDF_YVAL4");
		yValues[4] = socRes.getString("CDF_YVAL5");
		xValues[0] = socRes.getString("CDF_XVAL1");
		xValues[1] = socRes.getString("CDF_XVAL2");
		lineNames[0] = socRes.getString("CDF_USER_PRES");
		lineNames[1] = socRes.getString("CDF_USER_FUTURE");
		lineNames[2] = socRes.getString("CDF_EXP_PRES");
		lineNames[3] = socRes.getString("CDF_EXP_F_OPT");
		lineNames[4] = socRes.getString("CDF_EXP_F_WRST");

	}

	public void calculatePts() {

		/*
		 * midasCalc.calcUserGovIndex() returns a value between 0 - 1 in which 
		 * if all CDFs are set to 3 (out of 1-5), the value will be 0.5 and goes down if you lower
		 * CDFs, or up if you raise them, etc.
		 * This ratio is then scaled by half the axis length and is taken away from the starting point
		 * since a positive ratio (good values) would have to move the point upwards in Java x-y space
		 */

		presentRatio = startY + yAxisLength / 2 - midasCalc.calcUserSocIndex() * yAxisLength / 2;
		futureRatio = startY + yAxisLength / 2 - midasCalc.calcSocIndexFuture(MIDAS.socCDF[MIDAS.mmaNum][5]) * yAxisLength / 2;

		userPt[0].setLocation(presentX, presentRatio);
		userPt[1].setLocation(futureX, futureRatio);

		if (hasExpertValues == true) {
			// Setting expert points
			expertPt[0] = new Point2D.Double(presentX, startY + yAxisLength - (double)(expertValues[0] / 30.0 * yAxisLength));
			expertPt[1] = new Point2D.Double(futureX, startY + yAxisLength - (double)(expertValues[2] / 30.0 * yAxisLength));
			expertPt[2] = new Point2D.Double(futureX, startY + yAxisLength - (double)(expertValues[4] / 30.0 * yAxisLength));

			// Setting expert standard deviations
			expertStdev[0] = (double)(expertValues[1] / 30.0 * yAxisLength);
			expertStdev[1] = (double)(expertValues[3] / 30.0 * yAxisLength);
			expertStdev[2] = (double)(expertValues[5] / 30.0 * yAxisLength);
		}
	}
	public void paintComponent(Graphics g) {		
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		super.paintComponent(g2d);

		// Drawing background
		g2d.setColor(MIDAS.BACKGROUNDCOLOR);
		g2d.fill(new Rectangle(0, 0, this.getWidth(), this.getHeight()));

		// Getting dimensions of container
		int width = MIDAS.OUTCOMERES.width;

		//Draw the colored border
		g2d.setColor(pink);
		//last two arguments define the curviness of the rectangle
		RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(15, 5, 
				(int)(MIDAS.OUTCOMERES.getWidth() - 30), (int)(MIDAS.OUTCOMERES.getHeight() - 30), 20, 20);	
		g2d.fill(roundedRectangle); 

		//Draw the inner white rectangle
		g2d.setColor(Color.white);
		roundedRectangle = new RoundRectangle2D.Float(30, 40, (int)(MIDAS.OUTCOMERES.getWidth() - 60), 
				(int)(MIDAS.OUTCOMERES.getHeight() - 80), 15, 15);
		g2d.fill(roundedRectangle); 

		//Draw the MMA name above the Governance Index graph
		String mma = MIDAS.mmaNames[MIDAS.mmaNum];
		g2d.setColor(Color.white);
		g2d.setFont(titleFont);
		g2d.drawString(mma, width / 2 - mma.length() / 2 * 14, 30);

		// Draw title
		g2d.setColor(pink);
		g2d.setFont(titleFont);
		g2d.drawString(title, width / 2 - title.length() / 2 * 14, startY - 50);
		g2d.setColor(Color.black);

		// Draw y-axis title
		g2d.setFont(axisTitleFont);
		for (int i = 0; i < yTitle.length; i++) {
			g2d.drawString(yTitle[i], startX - 100, startY + (yAxisLength / 2) + 
					axisTitleFont.getSize() * i);
		}

		// Draw x-axis value names
		g2d.setFont(valueFont);
		g2d.drawString(xValues[0], barStartX + barWidth / 5, startY + 
				yAxisLength + valueFont.getSize());
		g2d.drawString(xValues[1], barStartX + barWidth + barSpace, startY + 
				yAxisLength + valueFont.getSize());
		g2d.setFont(axisTitleFont);
		g2d.drawString(xTitle, barStartX + xAxisLength / 4 + 15, startY + yAxisLength + valueFont.getSize() + 
				axisTitleFont.getSize() - 5);


		// Draw y-axis
		g2d.fillRect(axisStartX, startY, axisWidth, yAxisLength);
		// Draw x-axis
		g2d.fillRect(axisStartX, startY + yAxisLength, xAxisLength, axisWidth);

		// Draw label bars
		g2d.setFont(valueFont);
		for (int i = 0; i < yValues.length; i++) {
			// Black bars for y-axis 
			g2d.setColor(Color.black);
			g2d.fillRect(axisStartX - legendBarWidth - axisWidth, startY + barHeight * i, 
					legendBarWidth, barHeight);
			// Color depending on bar
			g2d.setColor(scale[i]);
			// Drawing labels
			g2d.drawString(yValues[i], axisStartX - legendBarWidth - 5, startY
					+ barHeight * i + barHeight / 2 + 5);

			// Drawing present bars
			g2d.fillRect(barStartX, startY
					+ barHeight * i, barWidth, barHeight);
			// Drawing future bars
			g2d.fillRect(barStartX + barSpace + barWidth, startY 
					+ barHeight * i, barWidth, barHeight);
			// g2d.fillRect(axisStartX + axisWidth, startY + barHeight * i , xAxisLength, barHeight);
		}


		// Draw legend
		drawLegend(g2d);
		// Draw expert points & labels
		if (hasExpertValues == true && MIDAS.SHOW_EXPERT_OPINION == true) {
			drawExpertPoints(g2d);
		}
		// Draw user points & labels
		drawUserPoints(g2d);
	}
	public void drawLegend(Graphics2D g2d) {
		int startX = 35;
		int startY = 75;
		// Drawing legend box
		g2d.setColor(Color.black);
		if (hasExpertValues == true && MIDAS.SHOW_EXPERT_OPINION == true) {
			g2d.draw(new Rectangle2D.Double(startX, startY, 150, 100));

			// Drawing labels
			g2d.setFont(lineFont);
			for (int i = 0; i < lineNames.length; i++) {
				g2d.drawString(lineNames[i], startX + 20, startY + 15 + 20 * i);
			}

			// Drawing symbols
			g2d.setStroke(new BasicStroke(2.0F));
			// User present & future
			g2d.setColor(Color.black);
			g2d.fill(new Ellipse2D.Double(startX + dotWidth / 2, startY + dotWidth / 2,
					dotWidth, dotWidth));
			g2d.draw(new Ellipse2D.Double(startX + dotWidth / 2, startY + dotWidth / 2 + 20,
					dotWidth, dotWidth));
			
			// Expert present & future
			g2d.setColor(Color.gray);
			g2d.draw(new Ellipse2D.Double(startX + dotWidth / 2, startY + dotWidth / 2 + 40,
					dotWidth, dotWidth));
			g2d.draw(new Rectangle2D.Double(startX + dotWidth / 2, startY + dotWidth / 2 + 60,
					dotWidth, dotWidth));
			g2d.fill(new Rectangle2D.Double(startX + dotWidth / 2, startY + dotWidth / 2 + 80,
					dotWidth, dotWidth));
		}
		else {
			g2d.draw(new Rectangle2D.Double(startX, startY, 150, 40));

			// Drawing labels
			g2d.setFont(lineFont);
			for (int i = 0; i < 2; i++) {
				g2d.drawString(lineNames[i], startX + 20, startY + 15 + 20 * i);
			}

			// Drawing symbols
			g2d.setStroke(new BasicStroke(2.0F));
			// User present & future
			g2d.setColor(Color.black);
			g2d.fill(new Ellipse2D.Double(startX + dotWidth / 2, startY + dotWidth / 2,
					dotWidth, dotWidth));
			g2d.draw(new Ellipse2D.Double(startX + dotWidth / 2, startY + dotWidth / 2 + 20,
					dotWidth, dotWidth));
		}
	}
	public void drawUserPoints(Graphics2D g2d) {
		g2d.setColor(Color.black);
		g2d.setStroke(new BasicStroke(2.0F));
		g2d.fill(new Ellipse2D.Double(userPt[0].getX() - dotWidth / 2, 
				userPt[0].getY() - dotWidth / 2, dotWidth, dotWidth));
		g2d.draw(new Ellipse2D.Double(userPt[1].getX() - dotWidth / 2, 
				userPt[1].getY() - dotWidth / 2, dotWidth, dotWidth));
		g2d.setStroke(userStroke);
		g2d.draw(new Line2D.Double(userPt[0], userPt[1]));
		/*
		g2d.setFont(lineFont);
		g2d.drawString(lineNames[0], (int)(userPt[0].getX() - dotWidth), 
				(int)(userPt[0].getY()) - dotWidth);
		g2d.drawString(lineNames[1], (int)(userPt[1].getX() - 3 * dotWidth), 
				(int)(userPt[1].getY() - dotWidth));
		 */
	}
	public void drawExpertPoints(Graphics2D g2d) {
		g2d.setColor(Color.gray);
		g2d.setStroke(new BasicStroke(2.0F));
		// Drawing points
		g2d.draw(new Ellipse2D.Double(expertPt[0].getX() - dotWidth / 2, 
				expertPt[0].getY() - dotWidth / 2, dotWidth, dotWidth));
		g2d.fill(new Rectangle2D.Double(expertPt[1].getX() - dotWidth / 2, 
				expertPt[1].getY() - dotWidth / 2, dotWidth, dotWidth));
		g2d.draw(new Rectangle2D.Double(expertPt[2].getX() - dotWidth / 2, 
				expertPt[2].getY() - dotWidth / 2, dotWidth, dotWidth));

		// Drawing standard deviations
		g2d.draw(new Line2D.Double(expertPt[0].getX(), expertPt[0].getY() - expertStdev[0], 
				expertPt[0].getX(), expertPt[0].getY() + expertStdev[0]));
		g2d.draw(new Line2D.Double(expertPt[1].getX() - 2, expertPt[1].getY() - expertStdev[1], 
				expertPt[1].getX(), expertPt[1].getY() + expertStdev[1]));
		g2d.draw(new Line2D.Double(expertPt[2].getX() + 2, expertPt[2].getY() - expertStdev[2], 
				expertPt[2].getX(), expertPt[2].getY() + expertStdev[2]));

		// Setting line font
		g2d.setStroke(expertStroke);
		// Drawing line connecting present -> future
		g2d.draw(new Line2D.Double(expertPt[0], expertPt[1]));
		g2d.draw(new Line2D.Double(expertPt[0], expertPt[2]));

		/*
		// Drawing labels
		g2d.drawString(lineNames[2], (int)(expertPt[0].getX() - dotWidth), 
				(int)(expertPt[0].getY() + 2 * dotWidth));
		g2d.drawString(lineNames[3], (int)(expertPt[1].getX() - 3 * dotWidth), 
				(int)(expertPt[1].getY() - dotWidth));
		g2d.drawString(lineNames[4], (int)(expertPt[2].getX() - 3 * dotWidth), 
				(int)(expertPt[2].getY() - dotWidth));
		 */
	}
	public void repaintGraphs() {
		calculatePts();
		repaint();
	}
}