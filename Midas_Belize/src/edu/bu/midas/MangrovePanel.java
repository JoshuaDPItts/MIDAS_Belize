package edu.bu.midas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;

import javax.swing.JPanel;

public class MangrovePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// private double[] barValues = new double[15];
	private double[] barValues = new double[4];
	private Color[] colorRamp = new Color[101];
	// private String[] categories = new String[] { "Distance to Riverbank: ",
	// null, "Distance to protected areas", null, null,
	// "Mangrove backed by rainforest: ", null,
	// "Distance to development on islands: ", null,
	// "Mangroves above water:", null, "Distance to corals/seagrass: ",
	// "fragmentation:", null, null };
	private String[] categories = new String[] { "Tourism", "Nursery",
			"Protection", "Total Score" };
	private String[] subCategories = new String[] { "greater than 500m",
			"within 500m", "non-adjacent", "adjacent", "within protected area",
			"non-adjacent", "adjacent", "more than 5km", "within 5km",
			"below 1m to sealevel", "above 1m altitude",
			"average weighted rating", "convoluted shape", "non-fragmented",
			"isolated" };
	private double total = 0;
	private Font categoryFont = new Font("Helvectica", Font.BOLD, 12);
	private Font subCategoryFont = new Font("Helvectica", Font.BOLD, 12);
	private int xOffset = 50;
	private int yOffset = 125;
	private GradientPaint gradient = new GradientPaint(30, 0, new Color(0, 255,
			0), 200, 0, new Color(255, 0, 0), true);
	private DecimalFormat dfPercent;

	public MangrovePanel() {
		dfPercent = new DecimalFormat("##.#");
	}

	public void paintComponent(Graphics g) {	
		for (int i = 0; i < 50; i++)
			colorRamp[i] = new Color(255, (int) (i * 5.1), 0);
		for (int i = 50; i < 100; i++)
			colorRamp[i] = new Color((int) (255 - 5.1 * (i - 50)), 255, 0);
		colorRamp[100] = new Color(0, 255, 0);
		Graphics2D g2d = (Graphics2D) g;

		super.paintComponent(g);
		
		// Painting background
		g2d.setColor(MIDAS.BACKGROUNDCOLOR);
		g2d.fill(new Rectangle(0, 0, MIDAS.LEFTRES.width, MIDAS.LEFTRES.height));

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		
		int sliderStartX = 45, sliderWidth = 10, sliderHeight = 40;
		int startX = 10, startY = 20, height = 40, width = 208, index = 0;
		int totalWidth = this.getWidth();
		if (total == 0) {
			total = 1;
		}
		// System.out.println("total width= " + this.getWidth());
		startY += 30;
		for (double x : barValues) {
			
			if (index == 3) {
				g2d.drawLine(startX + 10, startY, startX + 260, startY);
				startY += 50;
			}
			
			g2d.setColor(Color.black);
			g2d.setFont(categoryFont);
			g2d.drawString(categories[index], startX, startY);
			startY += 20;

			drawSliders(g2d, startX, startY, width, height);
			g2d.setColor(Color.BLACK);
			g2d.setColor(Color.black);

			g2d.setStroke(new BasicStroke(3));
			if (barValues[index] == 0 || Double.isNaN(barValues[index])) {
				sliderStartX = startX + 20;
			} else {
				if (index == 3)
					sliderStartX = (int) ((barValues[index] / 15) * width)+20;
				else
					sliderStartX = (int) ((barValues[index] / 5) * width)+20;
			}

			g2d.drawLine(sliderStartX, startY, sliderStartX + sliderWidth,
					startY);
			g2d.drawLine(sliderStartX + sliderWidth, startY, sliderStartX
					+ sliderWidth, startY + sliderHeight);
			g2d.drawLine(sliderStartX + sliderWidth, startY + sliderHeight,
					sliderStartX, startY + sliderHeight);
			g2d.drawLine(sliderStartX, startY + sliderHeight, sliderStartX,
					startY);
			g2d.setColor(Color.black);
			g2d.setFont(subCategoryFont);
			
			if (index == 3) {
				g2d.drawString( (Math.round(x*100))/100.0 + " / " + 15, totalWidth - 60,
						startY + 25);
			} else {
				g2d.drawString( (Math.round(x*100))/100.0 + " / " + 5, totalWidth - 55,
						startY + 25);
			}
			// System.out.println("rounded values " + ((Math.round(x*100))/100.0));
			
			startY += 80;
			// System.out.println("bar value " + index + "= " + barValues[index]);
			index++;
		}
		g2d.setColor(Color.black);
		g2d.setFont(categoryFont);
		g2d.drawString("You have selected " + total + " mangrove cells.",
				startX, startY);
		startY += 15;
		g2d.drawString("Scores are averaged over all selected cells.", startX,
				startY);
		startY += 40;
	}

	public void refreshPanel() {
		repaint();
	}

	public void updateValues(double[] statistics) {
		total = statistics[0];	
		
//		Tourism = ( mangroves within 500m of river / total ) +
//        ( mangroves within protected area / total ) +
//        ( mangroves adjacent to rain forest / total ) +
//        ( mangroves within 5km of development / total ) +
//        ( mangroves above sea level / total ) 

		barValues[0] = (statistics[2] + statistics[5] + 
					   statistics[7] + statistics[9] +
					   statistics[11]) / total;

//		Nursery = ( ( total of all values for distances to coral / number of cells ) +
//        ( convoluted shape frag ) +
//        ( non-frag ) +
//        ( isolated ) ) / 19
		
		// System.out.println("coral/seagrass = " + statistics[12]);
		barValues[1] = ((statistics[12] + statistics[13] +
					  statistics[14] + statistics[15])/total)/5;
		//		Protection = average of protection rating over all selected mangroves
		 barValues[2] = statistics[16]/total;
		//implement above math once it is confirmed
		//below are test variables
	

		barValues[3] = barValues[0] + barValues[1] + barValues[2];
		
//		barValues[2] = 0;
//		barValues[0] = 0;
//		barValues[1] = 3;
//		barValues[2] = 5;
		
	}

	public void drawSliders(Graphics2D g2d, int startX, int startY, int w, int h) {
		int barWidth = 200;
		int barHeight = 40;
		for (int i = 0; i < 100; i++) {
			g2d.setColor(colorRamp[i]);
			// piece = new RoundRectangle2D.Float(startX, startY,
			// (int)(this.getWidth() - w), h,
			// 20, 20);
			// g2d.fillRect(startX + 2*i+25, startY, 2, barHeight);
			g2d.fill(new RoundRectangle2D.Float(startX + 2 * i + 25, startY, 2,
					barHeight, 20, 20));
		}

	}

	private RoundRectangle2D makeRectangle(int x, int y, double w, int h,
			int index, boolean blue) {
		if (blue) {
			if (index == 3) {
				return new RoundRectangle2D.Float(
						(int) ((barValues[index] / 15) * (this.getWidth() - w)),
						y, 10, h, 20, 20);
			} else {
				return new RoundRectangle2D.Float(
						(int) ((barValues[index] / 5) * (this.getWidth() - w)),
						y, 10, h, 20, 20);
			}

			// if (index != 11) {
			// return new RoundRectangle2D.Float(x+20,y,
			// (int)((barValues[index]/5)*(this.getWidth() - w)), h, 20, 20);
			// }
			// else {
			// return new RoundRectangle2D.Float(x+20, y, (int)(barValues[index]
			// /
			// (double)(16 * 5)*(this.getWidth() - w)), h, 20, 20);
			// }
		} else {
			return new RoundRectangle2D.Float(x + 20, y,
					(int) (this.getWidth() - w), h, 20, 20);
		}

	}
}
