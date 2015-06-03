/*
 * Written by Matt Carleton. Modified by Chris Holden.
 */

package edu.bu.midas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.RoundRectangle2D;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JPanel;


public class ComparisonGraph extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//defining colors
	private Color green = new Color(50, 235, 0);
	private Color lightGreen = new Color(150, 255, 0);
	private Color yellow = new Color(255, 255, 0);
	private Color orange = new Color(255, 128, 0);
	private Color red = new Color(255, 0, 0);
	private Color white = new Color(255, 255, 255);
	private Color black = new Color(0,0,0);
	private Color colors[] = {green, lightGreen, yellow, orange, red};	
	
	Font font = new Font("Helvetica", Font.BOLD, 12);
	private Font titleFont = new Font("Helvectica", Font.BOLD, 24);
		
	//declaring CDFs
	private int[] allCDFs;	//the three types of CDFs, with a space in between each
	private String labels[];	//ie G1, G2
	private int[] sums = new int[6];	//the number of CDFs with a value of 0,1,2,3,4,5 in that order (the 0s are ignored. it's more elegant this way.)
	private String title;
	
	private int govLength;	//how many gov CDFs are there?
	private int socLength;
	private int ecoLength;
	private int numColumns;	//equals the total number of CDFs

	private int barWidth = 21;
	private int barGap = 3;
	private int xOffset = 30;
	private int yOffset = 75;
	private int xAxisLength = 600;	//recalculated depending on number of CDFs present
	private int yAxisLength = 300;
	private int valueHeight = yAxisLength/5;	//how much each additional value adds to a bar's height
	private int lineWidth = 2;
	private int lineHeight = 20;	//specifically, the lines at the bottom (the lines that mark which bars are government, etc.)

	private String total;
	private String bottomLabels[] = new String[3];
	
	PropertyResourceBundle compRes;
	
	public ComparisonGraph() {
		setLanguage();
		setParameters();
		setPreferredSize(new Dimension(xAxisLength + 150, yAxisLength + 100));
	}

	public void updateGraph() {
		calculateAllCDFs();
		calculateSums();
		setLanguage();
		
		repaint();
	}
	
	public void setParameters() {
		govLength = MIDAS.govCDF[MIDAS.mmaNum].length;
		socLength = MIDAS.socCDF[MIDAS.mmaNum].length;
		ecoLength = MIDAS.ecoCDF[MIDAS.mmaNum].length;
				
		numColumns = govLength + socLength + ecoLength + 2;	//the +2 is because there will two empty columns to separate the sections
		allCDFs = new int[numColumns];
		calculateAllCDFs();
		calculateSums();
		
		xAxisLength = numColumns *(barWidth + barGap) + barGap;
		//create the graph's labels
		labels = new String[numColumns];

		int counter = 1;	//the number included in each label. Reset between CDFs so you get G1, S1, etc.
		for(int i = 0; i< govLength; i++)
			labels[i] = "G" + counter++;
		
		counter = 1;
		for(int i = (govLength+1); i< (govLength + socLength+1); i++)
			labels[i] = "S" + counter++;
		
		counter = 1;
		for(int i = (numColumns-ecoLength); i< numColumns; i++)
			labels[i] = "E" + counter++;
		
		labels[govLength] = " "; //the empty column between G and S bars
		labels[govLength + socLength + 1] = " ";	//the empty column between S and E bars	
	}
	
	public void setLanguage() {
		if (MIDAS.LANGUAGE.equals("EN")) {
			compRes = (PropertyResourceBundle) ResourceBundle.getBundle("Graphs", new Locale("en"));
		}
		else if (MIDAS.LANGUAGE.equals("ES")) {
			compRes = (PropertyResourceBundle) ResourceBundle.getBundle("Graphs", new Locale("es"));
		}
		
		total = compRes.getString("COMP_TOTAL");
		bottomLabels[0] = compRes.getString("COMP_GOV");
		bottomLabels[1] = compRes.getString("COMP_SOC");
		bottomLabels[2] = compRes.getString("COMP_ECO");
		title = compRes.getString("COMP_TITLE");
	}
	
	public void calculateAllCDFs()
	{	
		int counter = 0;//iterate through the length of allCDFs
		for(int i=0; i < govLength; i++)
			allCDFs[counter++] = MIDAS.govCDF[MIDAS.mmaNum][i];
		allCDFs[counter++] = 0;	//the first empty space
		for(int i=0; i< socLength; i++)
			allCDFs[counter++] = MIDAS.socCDF[MIDAS.mmaNum][i];
		allCDFs[counter++] = 0;	//the second empty space
		for(int i=0; i< ecoLength; i++)
			allCDFs[counter++] = MIDAS.ecoCDF[MIDAS.mmaNum][i];
	}
	public void calculateSums()
	{	
		for(int i=0; i<6; i++)//reset the sums
			sums[i] = 0;
		for(int i=0; i < numColumns; i++)	//see, i told you it was elegant
			sums[allCDFs[i]]++;		
	}	

	public void paintComponent(Graphics g) {
		super.paintComponent(g);		
		Graphics2D g2d = (Graphics2D)g;		
		
		// Drawing background
		g2d.setColor(MIDAS.BACKGROUNDCOLOR);
		g2d.fill(new Rectangle(0, 0, this.getWidth(), this.getHeight()));
		
		// Getting dimensions of container
		int width = MIDAS.OUTCOMERES.width;
		
        //Draw the inner white rectangle		
		g2d.setColor(Color.white);
		RoundRectangle2D.Float roundedRectangle = new RoundRectangle2D.Float(15, 5, 
				(int)(MIDAS.OUTCOMERES.getWidth() - 30), (int)(MIDAS.OUTCOMERES.getHeight() - 30), 15, 15);
        g2d.fill(roundedRectangle); 
	
		//Draw the MMA name above the Index graph
		String mma = MIDAS.mmaNames[MIDAS.mmaNum];
		g.setColor(Color.black);
		g.setFont(titleFont);
		g.drawString(mma, width/2 - mma.length()/2*14, 30);
		
		// Draw title
		g.setColor(Color.black);
		g.setFont(titleFont);
		g.drawString(title, width/2 - title.length()/2*14, 65);
		g.setColor(Color.black);
	
		g.setFont(font);
		
		//add the background colors
		for(int i=0; i<5; i++)
		{	
			int yLocation = yOffset + 10 + i*(yAxisLength/5);//(the +10 is start the rectangles below the start of the axis)
			g.setColor(colors[i]);
			g.fillRect(xOffset, yLocation, xAxisLength, yAxisLength/5);	
		}		
		
		//draw the bars and their labels
		int origin = yOffset + (yAxisLength/2) + 10;	//the height from which the bars begin (aka the horizontal axis line height)

		for(int i = 0; i < numColumns; i++) 
		{	
			int xLocation = barGap + xOffset + (i * barWidth + i * barGap);
			int barHeight = (3-allCDFs[i])* valueHeight;
			if(barHeight == 0)
				barHeight = -(valueHeight/4 +3);//for when the CDF has a value of 3
			
			if(i != govLength && i != (govLength + socLength + 1))	//aka not the two empty columns
			{	
				if(barHeight > 0)
				{	
					g.setColor(black);	//black outline
					g.fillRect(xLocation, origin, barWidth, barHeight);
					g.setColor(white);	//white-filled bar
					g.fillRect(xLocation+2, origin, barWidth-4, barHeight-2);	//if the CDF value is 1 or 2
				}
				 else 
				{	
					g.setColor(black);	//black outline
					g.fillRect(xLocation, origin + barHeight, barWidth, -barHeight);
					g.setColor(white);	//white-filled bar
					g.fillRect(xLocation+2, origin+barHeight +2, barWidth-4, -barHeight -2);
				}
			}
			//add the labels (ie G1) to the graph
			g.setColor(black);
			g.drawString(labels[i], xLocation + 4, origin - 4);
		}		
		
		//draw the axes
		g.setColor(black);
		g.fillRect(xOffset, yOffset, lineWidth, yAxisLength + 20);	//the vertical axis line	(+20 is to make the line longer than the color boxes)
		g.fillRect(xOffset, origin, xAxisLength + 10, lineWidth);	//horizontal axis line (+10 is to make up for the above-mentioned fact)
		
		for(int i=0; i<5; i++)//add the tick marks
		{	
			g.fillRect(xOffset-4, origin +(2-i)* valueHeight, 12, 2);
			g.drawString(i+1 + " ", xOffset- 12, origin +(2-i)* valueHeight + 5);
		}
			
		//add the legend to the right
		int xLocation = xOffset + xAxisLength + 38;		
		int yLocation = yOffset + 8;
		g.setColor(black);
		g.drawString(total, xLocation+6, yOffset+5);
		g.fillRect(xLocation, yLocation, 54, yAxisLength + 4);//draw the legend's outline

		xLocation += lineWidth; //the colored boxes start slightly to the right of the outline
		for(int i=0; i<5; i++)		
		{	
			yLocation = yOffset + 10 + i*(yAxisLength/5);
			g.setColor(colors[i]);
			g.fillRect(xLocation, yLocation, 54 - lineWidth*2, yAxisLength/5);	
			//add numbers to the box
			g.setColor(black);
			g.drawString(sums[5-i] + " ", xLocation +20, (i+2)* valueHeight -4);			
		}
		
		g.setColor(black);
		for(int i=1; i<5; i++)		//add lines between the colors
			g.fillRect(xOffset + xAxisLength + 40, yOffset + 10 + i*(yAxisLength/5), 50, 2);
				
		
		int lineLengths[] = new int[3];
		lineLengths[0] = govLength*(barWidth + barGap) -barGap;
		lineLengths[1] = socLength*(barWidth + barGap) -barGap;
		lineLengths[2] = ecoLength*(barWidth + barGap) -barGap;
		
		xLocation = xOffset + barGap;
		yLocation = yOffset +yAxisLength + 40;
		
		lineWidth +=1;
		for(int i=0; i<3; i++)
		{	
			g.fillRect(xLocation, yLocation, lineLengths[i], lineWidth);
			g.fillRect(xLocation, yLocation - lineHeight/2, lineWidth, lineHeight);
			g.fillRect(xLocation + lineLengths[i] -lineWidth, yLocation - lineHeight/2, lineWidth, lineHeight);
			if(i != 1)
				g.drawString(bottomLabels[i], xLocation + lineLengths[i]/2 - 40, yLocation - 10);
			else g.drawString(bottomLabels[i], xLocation + lineLengths[i]/2 - 52, yLocation - 10);
			xLocation += lineLengths[i] + 2*barGap + barWidth; 
		}
		lineWidth -= 1;
	}
}
