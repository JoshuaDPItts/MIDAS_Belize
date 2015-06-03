package edu.bu.midas;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JPanel;

import au.com.bytecode.opencsv.CSVReader;

public class MIDASRaster extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int ncols;
	private int nrows;
	private double xllcorner;
	private double yllcorner;
	private int cellsize;
	private int NODATA_value;
	private String file;
	private Color color;
	private BufferedImage rasterImg;
	private Rectangle2D.Double[][] rasterRect;

	public MIDASRaster() {
		
	}
	public MIDASRaster(String fn) {
		file = fn;
	}
	public MIDASRaster(String fn, Color clr) {
		file = fn;
		color = clr;
	}
	public void setFile(String fn) {
		file = fn;
	}
	public void setColor(Color clr) {
		color = clr;
	}
	public void setResize(int newW, int newH) {
		rasterImg = resizeImg(newW, newH);
	}
	public int getColumns() {
		return ncols;
	}
	public int getRows() {
		return nrows;
	}
	public double getXLLCorner() {
		return xllcorner;
	}
	public double getYLLCorner() {
		return yllcorner;
	}
	public int getCellSize() {
		return cellsize;
	}
	public int getNODATA_value() {
		return NODATA_value;
	}
	public BufferedImage getRasterBI() {
		return rasterImg;
	}
	public int[][] calcIntRaster() {
		int[][] raster = null;
		
		CSVReader reader;
		
		InputStream inStream = getClass().getResourceAsStream("/"+file);
		InputStreamReader inReader = new InputStreamReader(inStream);
		try {
			reader = new CSVReader(inReader,'\t');
			String[] nextLine;
			int count = 0;
		    while ((nextLine = reader.readNext()) != null & count < 6) {
		    	count = count + 1;
		    	switch (count) {
		    	case 1:
		    		ncols = Integer.parseInt(nextLine[1]);
		    		break;
		    	case 2:
		    		nrows = Integer.parseInt(nextLine[1]);
		    		break;
		    	case 3:
		    		xllcorner = Double.parseDouble(nextLine[1]);
		    		break;
		    	case 4:
		    		yllcorner = Double.parseDouble(nextLine[1]);
		    		break;
		    	case 5:
		    		cellsize = Integer.parseInt(nextLine[1]);
		    		break;
		    	case 6:
		    		NODATA_value = Integer.parseInt(nextLine[1]);
		    		break;
		    	}
		    }
	    	// System.out.println("Layer: " + file + " read.");
	    	// System.out.println("Col: " + ncols + " Row: " + nrows + " cell size: " + cellsize + " NODATA: " + NODATA_value);
	    	inReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    raster = new int[ncols][nrows];
		
	    inStream = getClass().getResourceAsStream("/"+file);
	    inReader = new InputStreamReader(inStream);
    	reader = new CSVReader(inReader,'\t','\'',6);
	    try {
	    	int i = 0;
	    	String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				for (int j = 0; j < nextLine.length; j++) {
					raster[j][i] = Integer.parseInt(nextLine[j]);
					// System.out.print(raster[j][i] + " ");
				}
			//	System.out.println();
				i++;
			}
			inReader.close();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  
		
		return raster;
	}
	public void calcImage() {
		int[][] raster = calcIntRaster();
		
		rasterImg = new BufferedImage(ncols, nrows, BufferedImage.TYPE_INT_ARGB);
		
		int rgb = 0;
		for (int i = 0; i < ncols; i++) {
			for (int j = 0; j < nrows; j++) {
				if (raster[i][j] == NODATA_value) {
					Color trans = new Color(255,255,255,0);
					rgb = trans.hashCode();
				}
				else {
					rgb = color.hashCode();
					/*
					 * Incorporate code if we want potential to assign different colors per value in rater
					if (values.length == colors.length) {
						for (int a = 0; a < values.length; a++) {
							if (raster[i][j] == values[a]) {
								rgb = colors[a].hashCode();
							}
						}
					}
					*/
				}	
				rasterImg.setRGB(i,j, rgb);
			}
		}
		raster = null;
	}
	public void calcRect(int screenW, int screenH) {		
		rasterRect = new Rectangle2D.Double[ncols][nrows];
		
		double cellW = (double)screenW / (double)ncols;
		double cellH = (double)screenH / (double)nrows;
		
		// System.out.println("screenW: " + screenW + " and cellW: " + cellW);
		// System.out.println("screenH: " + screenH + " and cellH: " + cellH);
		
		for (int i = 0; i < nrows; i++) {
			for (int j = 0; j < ncols; j++) {
				rasterRect[j][i] = new Rectangle2D.Double(j * cellW, i * cellH, cellW, cellH);
			}
		}
	}
	public Rectangle2D.Double[][] getRect() {
		return rasterRect;
	}
	public void disposeImage() {
		rasterImg = null;
	}
	public void disposeRect() {
		rasterRect = null;
	}
	public BufferedImage resizeImg(int newW, int newH) {
		int w = rasterImg.getWidth();
		int h = rasterImg.getHeight();
		BufferedImage rImg = new BufferedImage(newW, newH, rasterImg.getType());
		Graphics2D g = rImg.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		/*
		One way of resizing:
		double xscale = (double) newW / w;
		double yscale = (double) newH / h;
		AffineTransform at = AffineTransform.getScaleInstance(xscale, yscale);
		g.drawRenderedImage(rasterImg, at);
		*/
		g.drawImage(rasterImg, 0, 0, newW, newH, 0, 0, w, h, null);
		g.dispose();
		return rImg;
	}
	/*
	public void calcRect(int imgWidth, int imgHeight) {
		mangroveRect = new Rectangle2D.Double[ncols][nrows];
		
		double cellW = imgWidth / ncols;
		double cellH = imgHeight / nrows;
		
		for (int i = 0; i < ncols; i++) {
			for (int j = 0; j < nrows; j++) {
				mangroveRect[i][ji] = new Rectangle2D.Double(j * cellW, i * cellH, cellW, cellH);
			}
		}
	}
	public Rectangle2D.Double[][] getRect() {
		return mangroveRect;
	}
	public int[][] returnRaster() {
		return raster;
	}
	*/
}
