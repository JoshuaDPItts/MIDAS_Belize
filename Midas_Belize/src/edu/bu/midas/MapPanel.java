package edu.bu.midas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class MapPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean mmaVisible = false;

	private String backgroundFile;
	private String landLayerFile;
	private BufferedImage backgroundImg;	
	private String[] layerFiles;
	private int imgWidth = MIDAS.GISRES.width;
	private int imgHeight = MIDAS.GISRES.height;

	private int cellnum;
	private Rectangle2D.Double[][] rasterCells;
	private double cellW;
	private double cellH;
	private int cellSelected;

	private MIDASRaster landLayer = null;
	private Color landLayerColor;
	private MIDASRaster[] habitatLayers = null;
	private Color[] layerColors;
	private boolean[] layersVisible;
	private boolean[] makeGrid;
	private Color[] riskColors;

	private MIDASRaster[] mangroveLayers = null;
	private String[] mangroveFiles;
	private int mangroveLayerIndex;
	private int[][][] mangroveLayerRasters;
	private int mangroveSelectMax = 10000;
	private int mangroveSelectCount = 0;
	private int[][] mangrovesSelected = new int[mangroveSelectMax][2];
	private Color mangrovesSelectedColor = new Color(200, 0, 150);

	private double[] oilXY = new double[2];
	private double oilMajorAxis;
	private double oilMinorAxis;

	private boolean showCellSelected = true;
	private boolean showRisk = false;
	private boolean showMangroves = false;
	private boolean showOil = false;

	// Map display booleans (e.g. for arrow, scale, latlong)
	private boolean showLandLayer = true;
	private boolean showRemoteSensing = true;
	private boolean showArrow = true;
	private boolean showScale = true;
	private boolean showLongLat = true;

	CoordinateConversion coordConvert = new CoordinateConversion();
	private double[] longLat = new double[2];

	public MapPanel(int cnum, String bckgrndFile, String landLyrFile,
			Color landLyrColor, String[] lyrs, Color[] lyrColors,
			boolean[] lyrsVisible, boolean[] mkGrid, String[] mgroveFiles,
			int mgroveLyrIndex) {
		setPreferredSize(MIDAS.GISRES);
		setMaximumSize(MIDAS.GISRES);
		setBackground(MIDAS.BACKGROUNDCOLOR);

		cellnum = cnum;
		rasterCells = new Rectangle2D.Double[cellnum][cellnum];
		cellW = (MIDAS.GISRES.width) / cellnum;
		cellH = (MIDAS.GISRES.height) / cellnum;
		backgroundFile = bckgrndFile;
		landLayerFile = landLyrFile;
		landLayerColor = landLyrColor;
		layerFiles = lyrs;
		layerColors = lyrColors;
		layersVisible = lyrsVisible;
		makeGrid = mkGrid;
		mangroveFiles = mgroveFiles;
		mangroveLayerIndex = mgroveLyrIndex;

		//getRasterLayers();
		//getMangroveLayers();
		initData();

		/*
		 * To display the remote sensing image if used try { backgroundImg = new
		 * BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB); url
		 * =
		 * Thread.currentThread().getContextClassLoader().getResource(backgroundFile
		 * ); backgroundImg = ImageIO.read(url); } catch (IOException e) {
		 * e.printStackTrace(); }
		 */

		// Get remote sensing image if it is not null
		/*
		if (backgroundFile != null) {
			initRemoteSensingImage();
		}
		 */

		// i specifies the row
		for (int i = 0; i < cellnum; i++) {
			// j specifies the column
			for (int j = 0; j < cellnum; j++) {
				rasterCells[j][i] = new Rectangle2D.Double(j * cellW,
						i * cellH, cellW, cellH);
			}
		}

		// Specifies which cell should be the first to get highlighted when
		// selected
		// Needed because not all cells will be risk cells available for
		// selection
		int i = 0;
		boolean foundCell = false;
		while (i < cellnum && foundCell == false) {
			int j = 0;
			while (j < cellnum && foundCell == false) {
				if (makeGrid[i * cellnum + j] == true) {
					cellSelected = i * cellnum + j;
					foundCell = true;
				}
				j++;
			}
			i++;
		}
	}

	public void initData() {
		if (mmaVisible == true) {
			initRemoteSensingImage();
			initRasterLayers();
			initMangroveLayers();
		}
	}

	public void disposeData() {
		if (mmaVisible == false && habitatLayers != null) {
			disposeRemoteSensing();
			disposeRasterLayers();
			disposeMangroveLayers();
		}
	}

	public void initRasterLayers() {
		if (mmaVisible == true) {

			if (landLayerFile != null) {
				landLayer = new MIDASRaster(landLayerFile, landLayerColor);
				landLayer.calcImage();
				landLayer.setResize(imgWidth, imgHeight);
			}

			habitatLayers = new MIDASRaster[layerFiles.length];

			// Adding alpha channel to color for MMA (MMA is always first layer
			// visible)
			layerColors[0] = new Color(layerColors[0].getRed(), layerColors[0]
			                                                                .getGreen(), layerColors[0].getBlue(), 150);

			for (int i = 0; i < habitatLayers.length; i++) {
				habitatLayers[i] = new MIDASRaster(layerFiles[i],
						layerColors[i]);
				habitatLayers[i].calcImage();
				habitatLayers[i].setResize(imgWidth, imgHeight);
				if (layersVisible[i] == false) {
					habitatLayers[i].disposeImage();
				}
			}

			// Giving us a base latitude/longitude based on upper left corner
			calculateLongLat(0,0);
		}
	}
	public void disposeRasterLayers() {
		for (int i = 0; i < habitatLayers.length; i++) {
			habitatLayers[i].disposeImage();
		}
	}

	public void initMangroveLayers() {

		if (mangroveLayerIndex != -1) {
			// System.out.println("PASSED MANGROVE NOT BEING NULL TEST");

			mangroveLayers = new MIDASRaster[mangroveFiles.length];

			for (int i = 0; i < mangroveLayers.length; i++) {
				mangroveLayers[i] = new MIDASRaster(mangroveFiles[i]);
			}

			if (mmaVisible == true) {
				mangroveLayerRasters = new int[mangroveLayers.length][mangroveLayers[0]
				                                                                     .getColumns()][mangroveLayers[0].getRows()];
				for (int i = 0; i < mangroveLayerRasters.length; i++) {
					mangroveLayerRasters[i] = mangroveLayers[i].calcIntRaster();
				}
				mangroveLayers[mangroveLayerIndex]
				               .calcRect(imgWidth, imgHeight);
			}
		}
	}
	public void disposeMangroveLayers () {
		if (mangroveLayerIndex != -1) {
			for (int i = 0; i < mangroveLayers.length; i++) {
				mangroveLayers[i].disposeImage();
				mangroveLayerRasters = null;
				if (i == mangroveLayerIndex) {
					mangroveLayers[i].disposeRect();
				}
			}
		}
	}

	public void updateLayers(int num, boolean layerOnOff) {
		/*
		 * This method will change which layers are visible to the GIS panel.
		 * The method ensures that only the layers which are visible will store
		 * the corresponding BufferedImage in MIDASRaster in order to decrease
		 * the memory requirement of having all layers visible at once.
		 */
		layersVisible[num] = layerOnOff;

		if (layerOnOff == true) {
			habitatLayers[num].calcImage();
			habitatLayers[num].setResize(imgWidth, imgHeight);
		} else if (layerOnOff == false) {
			habitatLayers[num].disposeImage();
		}
		repaint();
	}

	public int checkCell(double x, double y) {
		// i specifies the row
		for (int i = 0; i < cellnum; i++) {
			// j specifies the column
			for (int j = 0; j < cellnum; j++) {
				// if (makeGrid[i * cellnum + j] == true) {
					if (rasterCells[j][i].contains(x, y)) {
						cellSelected = i * cellnum + j;
					}
				// }
			}
		}

		repaint();
		
		System.out.println("Cell selected: " + cellSelected);
		
		return cellSelected;
	}

	public int getRiskCellSelected() {
		return cellSelected;
	}


	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		super.paintComponent(g);

		// Drawing spill if it exists (so it's below all habitat layers)
		drawSpill(g2d);
		
		// Drawing layers & visual data
		drawRemoteSensingImage(g2d);
		drawLandLayer(g2d);
		drawLayers(g2d);


		/*
		 * if we want to show the risk cell selector and if there's more than 1
		 * cell (e.g. belize = 1 cell so don't select)
		 */

		drawCells(g2d);
		drawCellSelected(g2d);

		// Drawing of MIDAS modules		
		drawRisk(g2d);

		paintSelectedMangroves(g2d);

		// Drawing of important map stuff
		if (showRemoteSensing == true) {
			g2d.setColor(Color.white);
		}
		else if (showRemoteSensing == false) {
			g2d.setColor(Color.black);
		}
		drawNorthArrow(g2d);
		drawScaleBar(g2d);
		drawLatLong(g2d);
	}
	// Drawing land layer
	public void setShowLandLayer(boolean show) {
		showLandLayer = show;
	}
	public boolean getShowLandLayer() {
		return showLandLayer;
	}
	public void drawLandLayer(Graphics2D g2d) {
		if (showLandLayer == true) {
			g2d.drawImage(landLayer.getRasterBI(), 0, 0, null);
		}
	}
	// Drawing of remote sensing image
	public void setShowRemoteSensing(boolean show) {
		showRemoteSensing = show;
	}
	public boolean getShowRemoteSensing() {
		return showRemoteSensing;
	}
	public void initRemoteSensingImage() {
		if (backgroundFile != null) {
			// System.out.println("BACKGROUND IMAGE: " + backgroundFile);
			URL url;
			try {
				backgroundImg = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
				url = Thread.currentThread().getContextClassLoader().getResource(backgroundFile);
				backgroundImg = ImageIO.read(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void disposeRemoteSensing() {
		backgroundImg = null;
	}
	public void drawRemoteSensingImage(Graphics2D g2d) {
		if (showRemoteSensing == true) {
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2d.drawImage(backgroundImg, 0, 0, imgWidth, imgHeight, null);
		}
	}

	// Drawing standard mapping info - scale bar & north arrow
	public void setShowArrow(boolean show) {
		showArrow = show;
	}
	public boolean getShowArrow() {
		return showArrow;
	}
	public void drawNorthArrow(Graphics2D g2d) {
		if (showArrow == true) {
			// Draw line / arrow shaft
			g2d.setStroke(new BasicStroke(3.0f));
			g2d.draw(new Line2D.Double(50,MIDAS.GISRES.height - 20,50,MIDAS.GISRES.height - 50));

			// Draw arrow
			int[] xPoints = {40,50,60};
			int[] yPoints = {MIDAS.GISRES.height - 50, MIDAS.GISRES.height - 60,MIDAS.GISRES.height - 50};
			Polygon arrow = new Polygon(xPoints,yPoints,3);
			g2d.fill(arrow);

			// Draw north letter
			g2d.drawString("N",60,MIDAS.GISRES.height - 35);
		}
	}
	public void setShowScale(boolean show) {
		showScale = show;
	}
	public boolean getShowScale() {
		return showScale;
	}
	public void drawScaleBar(Graphics2D g2d) {
		if (showScale == true) {
			g2d.setStroke(new BasicStroke(3.0f));

			// Length of scalebar
			double scaleLength = 100;
			// Error checking for null land
			if (habitatLayers[0] != null) {
				// We can just use width because length/width are the same (and all in meters)
				// Equation: (map cell width) * (map cell size) / (size of GIS frame) * (scale bar length)
				double scale = (double)habitatLayers[0].getColumns() * (double)habitatLayers[0].getCellSize() 
				/ (double)MIDAS.GISRES.width * scaleLength;
				String scaleText;

				// If scale is over 10,000m, use km instead
				if (scale > 10000) {
					scale = scale / 1000;

					DecimalFormat dfScale = new DecimalFormat("0.##");

					scaleText = dfScale.format(scale) + "km";
				}
				else {
					DecimalFormat dfScale = new DecimalFormat("0.#");
					scaleText = dfScale.format(scale) + "m";
				}


				// Drawing
				g2d.drawString(scaleText, MIDAS.GISRES.width - 65, MIDAS.GISRES.height - 30);
				g2d.draw(new Line2D.Double(MIDAS.GISRES.width - 50 - scaleLength, MIDAS.GISRES.height - 20,
						MIDAS.GISRES.width - 50, MIDAS.GISRES.height - 20));
				// Tick marks
				g2d.draw(new Line2D.Double(MIDAS.GISRES.width - 50 - scaleLength,MIDAS.GISRES.height - 15,
						MIDAS.GISRES.width - 50 - scaleLength,MIDAS.GISRES.height - 25));
				g2d.draw(new Line2D.Double(MIDAS.GISRES.width - 50,MIDAS.GISRES.height - 15,
						MIDAS.GISRES.width - 50,MIDAS.GISRES.height - 25));
			}
		}
	}
	public void setShowLongLat(boolean show) {
		showLongLat = show;
	}
	public boolean getShowLongLat() {
		return showLongLat;
	}
	public void calculateLongLat(int x, int y) {
		if (showLongLat == true) {

			// Reminder: latitude = y, longitude = x
			int zone = 16;
			String latZone = "N";

			/*
			 *  Because rasters use lower left corner and Java XY is based off of upper left corner,
			 *  we need to convert our y to base it off of lower left corner.
			 *  
			 *  We do this by subtracting our Y position from the maximum height of the container
			 */
			y = MIDAS.GISRES.height - y;

			double utmLong = habitatLayers[0].getXLLCorner() + (x * habitatLayers[0].getCellSize());
			double utmLat = habitatLayers[0].getYLLCorner() + (y * habitatLayers[0].getCellSize());

			longLat = coordConvert.utm2LatLon(zone, latZone, utmLong, utmLat);

			// Repaint the longitude / latitude
			// drawLatLong(this.getGraphics());
			repaint();
		}
	}
	public void drawLatLong(Graphics2D g2d) {
		if (showLongLat == true) {
			DecimalFormat dfLatLong = new DecimalFormat("0.###");
			String longitude = "Longitude: " + dfLatLong.format(longLat[1]) + "\u00B0";
			String latitude = "Latitude: " + dfLatLong.format(longLat[0]) + "\u00B0";

			g2d.setFont(new Font("Ariel", Font.BOLD, 12));
			g2d.drawString(longitude, 10, 10);
			g2d.drawString(latitude, 10, 30);
		}
	}
	public void drawLayers(Graphics2D g2d) {
		// Drawing visible layers
		for (int i = 0; i < habitatLayers.length; i++) {
			if (layersVisible[i] == true) {
				g2d.drawImage(habitatLayers[i].getRasterBI(), 0, 0, null);
			}
		}
	}
	public void drawCells(Graphics2D g2d) {
		if (showCellSelected == true  && cellnum > 1) {
			
			Color enabledCellColor = Color.black;
			Color disbaledCellColor = Color.gray;
			
			// Drawing risk cells - 10km each
			if (showRemoteSensing == true) {
				enabledCellColor = Color.white;
				disbaledCellColor = Color.black;
			}
			else if (showRemoteSensing == false) {
				enabledCellColor = Color.black;
				disbaledCellColor = Color.gray;
			}
			
			// Set color to disabled cell color
			g2d.setColor(disbaledCellColor);
			for (int i = 0; i < cellnum; i++) {
				for (int j = 0; j < cellnum; j++) {
					if (makeGrid[i * cellnum + j] == false) {
						g2d.draw(rasterCells[j][i]);
					}
				}
			}
			// Repeat in order to force cells enabled to draw OVER disabled cells
			// Make sure color is default cell color
			g2d.setColor(enabledCellColor);
			for (int i = 0; i < cellnum; i++) {
				for (int j = 0; j < cellnum; j++) {
					if (makeGrid[i * cellnum + j] == true) {
						g2d.draw(rasterCells[j][i]);
					}
				}
			}

		}
	}
	public void drawCellSelected(Graphics2D g2d) {
		if (showCellSelected == true && cellnum > 1) {
			// Drawing cell selection highlight
			g2d.setColor(new Color(255, 130, 80, 200));
			// cellSelected = {0 to 16} so to get column divide and use
			// remainder operator for row
			// E.g. cellSelected = 14 -> 3 and 2
			g2d.fill(rasterCells[cellSelected % cellnum][cellSelected
			                                             / cellnum]);
		}
	}
	public void drawRisk(Graphics2D g2d) {
		if (showRisk == true) {

			// System.out.println("Showing risk.");
			for (int i = 0; i < cellnum; i++) {
				for (int j = 0; j < cellnum; j++) {
					if (makeGrid[i * cellnum + j] == true) {
						g2d.setColor(riskColors[i * cellnum + j]);
						g2d.fill(rasterCells[j][i]);
					}
				}
			}
			drawCells(g2d);
		}
	}

	public void putPolygon(int pointCount, int xCoord[], int yCoord[]) {
		if (pointCount < 2) {
			return;
		}
		Graphics g = getGraphics();
		if (pointCount == 2) {
			g.drawLine(xCoord[0], yCoord[0], xCoord[1], yCoord[1]);
		} else {
			// Drawing polygon
			repaint();
			g.setColor(new Color(255, 0, 0, 200));
			Polygon pgon = new Polygon(xCoord, yCoord, pointCount);
			g.fillPolygon(pgon);
			g.setColor(Color.BLACK);
			g.drawPolygon(xCoord, yCoord, pointCount);

			// Checking for which cells polygon is in
			mangroveSelectCount = 0;
			// Reinitialize x/y of selected mangroves in case you select less
			// than last time
			mangrovesSelected = new int[mangroveSelectMax][2];
			// System.out.println("MANGROVELAYERINDEX: " + mangroveLayerIndex);
			for (int i = 0; i < mangroveLayers[mangroveLayerIndex].getRows(); i++) {
				// j specifies the column
				for (int j = 0; j < mangroveLayers[mangroveLayerIndex]
				                                   .getColumns(); j++) {
					if (pgon.contains(mangroveLayers[mangroveLayerIndex]
					                                 .getRect()[j][i])
					                                 && mangroveLayerRasters[mangroveLayerIndex][j][i] == 1) {
						mangrovesSelected[mangroveSelectCount][0] = j;
						mangrovesSelected[mangroveSelectCount][1] = i;
						mangroveSelectCount++;
					}
				}
			}
		}
		g.dispose();
	}

	public void putRectangle(int x1, int y1, int x2, int y2) {
		Graphics g = getGraphics();
		g.setColor(new Color(255, 0, 0, 200));
		g.setColor(Color.BLACK);		
		if (x2 < x1 && y2 < y1) {
			g.drawRect(x2, y2, Math.abs(x2 - x1), Math.abs(y2 - y1));
		} else if (y2 < y1) {
			g.drawRect(x1, y2, Math.abs(x1 - x2), Math.abs(y2 - y1));
		} else if (x2 < x1) {
			g.drawRect(x2, y1, Math.abs(x2 - x1), Math.abs(y1 - y2));
		} else {
			g.drawRect(x1, y1, Math.abs(x1 - x2), Math.abs(y1 - y2));
		}
		g.dispose();
	}

	public void putLine(int x1, int y1, int x2, int y2) {
		Graphics g = getGraphics();
		g.drawLine(x1, y1, x2, y2);
		g.dispose();
	}

	public void clearPaint() {
		repaint();
	}

	public void paintSelectedMangroves(Graphics2D g2d) {
		if (showMangroves == true) {
			if (mangroveSelectCount > 0) {
				g2d.setColor(mangrovesSelectedColor);
				for (int i = 0; i < mangroveSelectCount; i++) {
					g2d.fill(mangroveLayers[mangroveLayerIndex].getRect()[mangrovesSelected[i][0]][mangrovesSelected[i][1]]);
				}
			}
		}
	}

	public double[] getMangroveStatistics() {
		double[] stats = new double[17];

		/*
		 * 	stats - 15 totals: 
		 * 
		 * 	stats[0] - cells selected 
		 * 	stats[1] - more than 500m to riverbank = 0 
		 * 	stats[2] - within 500m to riverbank = 1
		 * 	stats[3] - non-adjacent to protected area = 0 
		 * 	stats[4] - adjacent to protected area = 1 
		 * 	stats[5] - within protected area = 2 
		 * 	stats[6] - mangrove non-adjacent to rainforest = 0 
		 * 	stats[7] - mangrove adjacent to rainforest = 1 
		 * 	stats[8] - more than 5km from island development = 0 
		 * 	stats[9] - within 5km to island development = 1 
		 * 	stats[10] - mangrove below sealevel = 0 
		 * 	stats[11] - mangrove above sealevel = 1
		 * 	stats[12] - (total of all values for distance to corals) / number of
		 * 		cells 
		 * 	stats[13] - convoluted shape frag = 3 
		 * 	stats[14] - non-frag = 2
		 * 	stats[15] - isolated = 1
		 *  stats[16] - mangrove protection (rated 0-5)
		 */

		stats[0] = mangroveSelectCount;
		// System.out.println("Mangroves selected: " + stats[0]);
		for (int i = 0; i < mangroveSelectCount; i++) {
			// System.out.println("i: " + i);

			int x = mangrovesSelected[i][0];
			int y = mangrovesSelected[i][1];
			// System.out.println("x,y: " + x + "," + y);

			switch (mangroveLayerRasters[1][x][y]) {
			case 0:
				stats[1]++;
				break;
			case 1:
				stats[2]++;
				break;
			}
			switch (mangroveLayerRasters[2][x][y]) {
			case 0:
				stats[3]++;
				break;
			case 1:
				stats[4]++;
				break;
			case 2:
				stats[5]++;
				break;
			}
			switch (mangroveLayerRasters[3][x][y]) {
			case 0:
				stats[6]++;
				break;
			case 1:
				stats[7]++;
				break;
			}
			switch (mangroveLayerRasters[4][x][y]) {
			case 0:
				stats[8]++;
				break;
			case 1:
				stats[9]++;
				break;
			}
			switch (mangroveLayerRasters[5][x][y]) {
			case 0:
				stats[10]++;
				break;
			case 1:
				stats[11]++;
				break;
			}
			stats[12] = stats[12] + mangroveLayerRasters[6][x][y];
			// System.out.println("Distance to coral value " + i + " is: " + mangroveLayerRasters[6][x][y]);
			switch (mangroveLayerRasters[7][x][y]) {
			case 3:
				stats[13]++;
				break;
			case 2:
				stats[14]++;
				break;
			case 1:
				stats[15]++;
				break;
			}
			//mangrove protection values
			if(mangroveLayerRasters[8][x][y] != -9999){
				stats[16] = stats[16] + mangroveLayerRasters[8][x][y];
			}

		}

		return stats;
	}

	public void setShowCellSelected(boolean showCell) {
		showCellSelected = showCell;
	}

	public void setShowRisk(boolean showRsk) {
		showRisk = showRsk;
	}

	public void setRiskColor(Color[] rskColors) {
		riskColors = rskColors;
	}

	public void setShowMangroves(boolean showMgroves) {
		showMangroves = showMgroves;
	}

	public void setShowOil(boolean showOl) {
		showOil = showOl;
	}
	public void drawSpill(Graphics2D g2d) {
		// This method is implemented by paintComponent() in order to redraw the oil spill on subsequent repaints
		if (showOil == true) {
			Ellipse2D.Double ellipse = new Ellipse2D.Double(
					oilXY[0] - (oilMajorAxis / 2.0), 
					oilXY[1] - (oilMinorAxis / 2.0), 
					oilMajorAxis, oilMinorAxis);
			g2d.setColor(Color.black);
			g2d.draw(ellipse);
			g2d.setColor(new Color(0, 0, 0, 50));
			g2d.fill(ellipse);
		}

		drawLandLayer(g2d);
	}

	public void drawSpill(double[] xy, double majorAxis, double minorAxis) {

		oilXY = xy;
		oilMajorAxis = majorAxis;
		oilMinorAxis = minorAxis;

		/*
		 * Check to make sure that the x-y coordinate aren't off screen
		 */
		if ((oilXY[0] >= 0 && oilXY[0] <= MIDAS.GISRES.width)
				&& (oilXY[1] >= 0 && oilXY[1] <= MIDAS.GISRES.height)) {

			Graphics2D g2d = (Graphics2D) getGraphics();
			Ellipse2D.Double ellipse = new Ellipse2D.Double(
					oilXY[0] - (oilMajorAxis / 2.0), 
					oilXY[1] - (oilMinorAxis / 2.0), 
					oilMajorAxis, oilMinorAxis);
			g2d.setColor(Color.black);
			g2d.draw(ellipse);
			g2d.setColor(new Color(0, 0, 0, 50));
			g2d.fill(ellipse);

		}

		drawLandLayer((Graphics2D)getGraphics());
	}

	public void setMMAVisible(boolean visible) {
		mmaVisible = visible;
		initData();
		disposeData();
	}
}