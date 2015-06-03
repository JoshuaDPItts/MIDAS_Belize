package edu.bu.midas;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import au.com.bytecode.opencsv.CSVReader;

public class OilModel {
	
	private String oilLandFile;
	private String oilZWFile;
	private String oilMWFile;
	private String oilZCFile;
	private String oilMCFile;
	
	private double[][] oilZW;
	private double[][] oilMW;
	private double[][] oilZC;
	private double[][] oilMC;

	private int landCols;
	private int landRows;
	private double landXLLCorner;
	private double landYLLCorner;
	private int landCellSize;
	private int landNoData;

	private int zonalWindCols;
	private int zonalWindRows;
	private double zonalWindXLLCorner;
	private double zonalWindYLLCorner;
	private double zonalWindCellSize;
	private double zonalWindNoData; 
	
	private int meridionalWindCols;
	private int meridionalWindRows;
	private double meridionalWindXLLCorner;
	private double meridionalWindYLLCorner;
	private double meridionalWindCellSize;
	private double meridionalWindNoData; 

	private int zonalCurrentCols;
	private int zonalCurrentRows;
	private double zonalCurrentXLLCorner;
	private double zonalCurrentYLLCorner;
	private double zonalCurrentCellSize;
	private double zonalCurrentNoData;
	
	private int meridionalCurrentCols;
	private int meridionalCurrentRows;
	private double meridionalCurrentXLLCorner;
	private double meridionalCurrentYLLCorner;
	private double meridionalCurrentCellSize;
	private double meridionalCurrentNoData;

	private double majorAxis;
	private double minorAxis;
	private double area;

	private double dX;
	private double dY;

	private double densityOil;
	private double densityWater;

	private double volume;

	private double[] UWind = new double[2]; // Uw
	private double[] UCurrent = new double[2]; // Ut
	private double KWind; // Kw - wind drift factor (0.03) 
	private double KCurrent; // Kt - current factor (1.0)

	private double dH; // m^2/s

	private int timeStep;

	private double[] xy = new double[2];
	private int[] landColRow = new int[2];
	private int[] zonalWindColRow= new int[2];
	private int[] meridionalWindColRow= new int[2];
	private int[] zonalCurrentColRow= new int[2];
	private int[] meridionalCurrentColRow= new int[2];

	private double screenW = MIDAS.GISRES.width;
	private double screenH = MIDAS.GISRES.height;

	public OilModel() {
		// oilModelFiles = oilModelFi;

 
		/*
		readLandHeader(oilModelFiles[0]);
		readWindHeader(oilModelFiles[1]);
		readCurrentHeader(oilModelFiles[2]);
		*/

		dX = 0;
		dY = 0;

		timeStep = 1;

		// Assumed parameters:
		dH = 7.0;
		KWind = 0.03;
		KCurrent = 1.0;
	}
	public void setOilLandFile(String file) {
		oilLandFile = file;
		readLandHeader(oilLandFile);
	}
	public void setOilZWFile(String file) {
		System.out.println("Zonal wind from: " + file);
		oilZWFile = file;
		readZonalWindHeader(oilZWFile);
	}
	public void setOilMWFile(String file) {
		System.out.println("Meridional wind from: " + file);
		oilMWFile = file;
		readMeridionalWindHeader(oilMWFile);
	}
	public void setOilZCFile(String file) {
		System.out.println("Zonal current from: " + file);
		oilZCFile = file;
		readZonalCurrentHeader(oilZCFile);
	}
	public void setOilMCFile(String file) {
		System.out.println("Meridional current from: " + file);
		oilMCFile = file;
		readMeridionalCurrentHeader(oilMCFile);
	}
	
	public void readLandHeader(String configFile) {
		CSVReader reader;
		InputStream headerStream = getClass().getResourceAsStream("/"+configFile);
		InputStreamReader headerReader = new InputStreamReader(headerStream);
		try {
			reader = new CSVReader(headerReader, '\t');
			String[] nextLine;
			int count = 0;
			while ((nextLine = reader.readNext()) != null & count < 6) {
				count = count + 1;
				switch (count) {
				case 1:
					landCols = Integer.parseInt(nextLine[1]);
					break;
				case 2:
					landRows = Integer.parseInt(nextLine[1]);
					break;
				case 3:
					landXLLCorner = Double.parseDouble(nextLine[1]);
					break;
				case 4:
					landYLLCorner = Double.parseDouble(nextLine[1]);
					break;
				case 5:
					landCellSize = Integer.parseInt(nextLine[1]);
					break;
				case 6:
					landNoData = Integer.parseInt(nextLine[1]);
					break;
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public double getLandData(String configFile, int col, int row) {
		int land = landNoData;

		CSVReader reader;
		InputStream dataStream = getClass().getResourceAsStream("/"+configFile);
		InputStreamReader dataReader = new InputStreamReader(dataStream);
		try {
			reader = new CSVReader(dataReader,'\t','\'', (6 + row));
			String[] nextLine = reader.readNext();
			land = Integer.parseInt(nextLine[col]);
			System.out.println("Land code: " + land + " at: " + landColRow[0] + " and: " + landColRow[1]);

			dataReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return land;
	}
	public void readZonalWindHeader(String configFile) {
		CSVReader reader;
		InputStream headerStream = getClass().getResourceAsStream("/"+configFile);
		InputStreamReader headerReader = new InputStreamReader(headerStream);
		try {
			reader = new CSVReader(headerReader, '\t');
			String[] nextLine;
			int count = 0;
			while ((nextLine = reader.readNext()) != null & count < 6) {
				count = count + 1;
				switch (count) {
				case 1:
					zonalWindCols = Integer.parseInt(nextLine[1]);
					break;
				case 2:
					zonalWindRows = Integer.parseInt(nextLine[1]);
					break;
				case 3:
					zonalWindXLLCorner = Double.parseDouble(nextLine[1]);
					break;
				case 4:
					zonalWindYLLCorner = Double.parseDouble(nextLine[1]);
					break;
				case 5:
					zonalWindCellSize = Double.parseDouble(nextLine[1]);
					break;
				case 6:
					zonalWindNoData = Double.parseDouble(nextLine[1]);
					break;
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void readMeridionalWindHeader(String configFile) {
		CSVReader reader;
		InputStream headerStream = getClass().getResourceAsStream("/"+configFile);
		InputStreamReader headerReader = new InputStreamReader(headerStream);
		try {
			reader = new CSVReader(headerReader, '\t');
			String[] nextLine;
			int count = 0;
			while ((nextLine = reader.readNext()) != null & count < 6) {
				count = count + 1;
				switch (count) {
				case 1:
					meridionalWindCols = Integer.parseInt(nextLine[1]);
					break;
				case 2:
					meridionalWindRows = Integer.parseInt(nextLine[1]);
					break;
				case 3:
					meridionalWindXLLCorner = Double.parseDouble(nextLine[1]);
					break;
				case 4:
					meridionalWindYLLCorner = Double.parseDouble(nextLine[1]);
					break;
				case 5:
					meridionalWindCellSize = Double.parseDouble(nextLine[1]);
					break;
				case 6:
					meridionalWindNoData = Double.parseDouble(nextLine[1]);
					break;
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void getZonalWindData(String config) {
		oilZW = new double[zonalWindCols][zonalWindRows];
		
		CSVReader reader;
		InputStream dataStream = getClass().getResourceAsStream("/"+config);
		InputStreamReader dataReader = new InputStreamReader(dataStream);
		try {
			reader = new CSVReader(dataReader,'\t','\'', 6);
			
	    	int i = 0;
	    	String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				for (int j = 0; j < nextLine.length; j++) {
					oilZW[j][i] = Double.parseDouble(nextLine[j]);
					// System.out.print(raster[j][i] + " ");
				}
			//	System.out.println();
				i++;
			}
			dataReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Handling no data values - substitute average
		double sum = 0;
		int count = 0;
		for (int i = 0; i < oilZW.length; i++) {
			for (int j = 0; j < oilZW[0].length; j++) {
				if (oilZW[i][j] != zonalWindNoData) {
					sum = sum + oilZW[i][j];
					count++;
				}
			}
		}
		for (int i = 0; i < oilZW.length; i++) {
			for (int j = 0; j < oilZW[0].length; j++) {
				if (oilZW[i][j] == zonalWindNoData) {
					oilZW[i][j] = sum / count;
				}
			}
		}
	}
	public void getMeridionalWindData(String config) {
		oilMW = new double[meridionalWindCols][meridionalWindRows];
		
		CSVReader reader;
		InputStream dataStream = getClass().getResourceAsStream("/"+config);
		InputStreamReader dataReader = new InputStreamReader(dataStream);
		try {
			reader = new CSVReader(dataReader,'\t','\'', 6);
			
	    	int i = 0;
	    	String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				for (int j = 0; j < nextLine.length; j++) {
					oilMW[j][i] = Double.parseDouble(nextLine[j]);
				}
				i++;
			}
			dataReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Handling no data values - substitute average
		double sum = 0;
		int count = 0;
		for (int i = 0; i < oilMW.length; i++) {
			for (int j = 0; j < oilMW[0].length; j++) {
				if (oilMW[i][j] != meridionalWindNoData) {
					sum = sum + oilMW[i][j];
					count++;
				}
			}
		}
		for (int i = 0; i < oilMW.length; i++) {
			for (int j = 0; j < oilMW[0].length; j++) {
				if (oilMW[i][j] == meridionalWindNoData) {
					oilMW[i][j] = sum / count;
				}
			}
		}
	}
	public void readZonalCurrentHeader(String configFile) {
		CSVReader reader;
		InputStream headerStream = getClass().getResourceAsStream("/"+configFile);
		InputStreamReader headerReader = new InputStreamReader(headerStream);
		try {
			reader = new CSVReader(headerReader, '\t');
			String[] nextLine;
			int count = 0;
			while ((nextLine = reader.readNext()) != null & count < 6) {
				count = count + 1;
				switch (count) {
				case 1:
					zonalCurrentCols = Integer.parseInt(nextLine[1]);
					break;
				case 2:
					zonalCurrentRows = Integer.parseInt(nextLine[1]);
					break;
				case 3:
					zonalCurrentXLLCorner = Double.parseDouble(nextLine[1]);
					break;
				case 4:
					zonalCurrentYLLCorner = Double.parseDouble(nextLine[1]);
					break;
				case 5:
					zonalCurrentCellSize = Double.parseDouble(nextLine[1]);
					break;
				case 6:
					zonalCurrentNoData = Double.parseDouble(nextLine[1]);
					break;
				}
			}

			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void readMeridionalCurrentHeader(String configFile) {
		CSVReader reader;
		InputStream headerStream = getClass().getResourceAsStream("/"+configFile);
		InputStreamReader headerReader = new InputStreamReader(headerStream);
		try {
			reader = new CSVReader(headerReader, '\t');
			String[] nextLine;
			int count = 0;
			while ((nextLine = reader.readNext()) != null & count < 6) {
				count = count + 1;
				switch (count) {
				case 1:
					meridionalCurrentCols = Integer.parseInt(nextLine[1]);
					break;
				case 2:
					meridionalCurrentRows = Integer.parseInt(nextLine[1]);
					break;
				case 3:
					meridionalCurrentXLLCorner = Double.parseDouble(nextLine[1]);
					break;
				case 4:
					meridionalCurrentYLLCorner = Double.parseDouble(nextLine[1]);
					break;
				case 5:
					meridionalCurrentCellSize = Double.parseDouble(nextLine[1]);
					break;
				case 6:
					meridionalCurrentNoData = Double.parseDouble(nextLine[1]);
					break;
				}
			}

			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void getZonalCurrentData(String config) {
		oilZC = new double[zonalCurrentCols][zonalCurrentRows];
		
		CSVReader reader;
		InputStream dataStream = getClass().getResourceAsStream("/"+config);
		InputStreamReader dataReader = new InputStreamReader(dataStream);
		try {
			reader = new CSVReader(dataReader,'\t','\'', 6);
			
	    	int i = 0;
	    	String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				for (int j = 0; j < nextLine.length; j++) {
					oilZC[j][i] = Double.parseDouble(nextLine[j]);
				}
				i++;
			}
			dataReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Handling no data values - substitute average
		double sum = 0;
		int count = 0;
		for (int i = 0; i < oilZC.length; i++) {
			for (int j = 0; j < oilZC[0].length; j++) {
				if (oilZC[i][j] != zonalCurrentNoData) {
					sum = sum + oilZC[i][j];
					count++;
				}
			}
		}
		for (int i = 0; i < oilZC.length; i++) {
			for (int j = 0; j < oilZC[0].length; j++) {
				if (oilZC[i][j] == zonalCurrentNoData) {
					oilZC[i][j] = sum / count;
				}
			}
		}
	}
	public void getMeridionalCurrentData(String config) {
		oilMC = new double[meridionalCurrentCols][meridionalCurrentRows];
		
		CSVReader reader;
		InputStream dataStream = getClass().getResourceAsStream("/"+config);
		InputStreamReader dataReader = new InputStreamReader(dataStream);
		try {
			reader = new CSVReader(dataReader,'\t','\'', 6);
			
	    	int i = 0;
	    	String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				for (int j = 0; j < nextLine.length; j++) {
					oilMC[j][i] = Double.parseDouble(nextLine[j]);
				}
				i++;
			}
			dataReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		// Handling no data values - substitute average
		double sum = 0;
		int count = 0;
		for (int i = 0; i < oilMC.length; i++) {
			for (int j = 0; j < oilMC[0].length; j++) {
				if (oilMC[i][j] != meridionalCurrentNoData) {
					sum = sum + oilMC[i][j];
					count++;
				}
			}
		}
		for (int i = 0; i < oilMC.length; i++) {
			for (int j = 0; j < oilMC[0].length; j++) {
				if (oilMC[i][j] == meridionalCurrentNoData) {
					oilMC[i][j] = sum / count;
				}
			}
		}
	}
	public void moveOil(int maxTime, double initialX, double initialY) {
		if (MIDAS.OIL_EULA == true) {
			
			// Getting wind/current data
			getZonalWindData(oilZWFile);
			getMeridionalWindData(oilMWFile);
			getZonalCurrentData(oilZCFile);
			getMeridionalCurrentData(oilMCFile);

			// From Chao et al 2001
			xy[0] = initialX;
			xy[1] = initialY;

			System.out.println("Pixel x: " + xy[0] + " y: " + xy[1]);
			landColRow[0] = (int) ((xy[0] / screenH) * landCols);
			landColRow[1] = (int) ((xy[1] / screenH) * landRows);
			System.out.println("Land raster file x: " + landColRow[0] + " y: " + landColRow[1]);

			System.out.println("screenW: " + screenW + " screenH: " + screenH);
			// Scalings: pixels / m
			double xScaling = screenW / (double)(landCols * landCellSize);
			double yScaling = screenH / (double)(landRows * landCellSize);
			System.out.println("xScaling: " + xScaling + " yScaling: " + yScaling);


			double densityWater = 1.02;
			double deltaDensity = densityWater - densityOil;

			double[] UWindTotal = new double[maxTime / timeStep];

			for (int i = 0; i < maxTime; i=i+timeStep) {
				System.out.println("++++++++++ XY: " + xy[0] + " and " + xy[1]);
				landColRow[0] = (int) ((xy[0] / screenH) * landCols);
				landColRow[1] = (int) ((xy[1] / screenH) * landRows);
				
				zonalWindColRow[0] = (int) ((xy[0] / screenH) * zonalWindCols);
				zonalWindColRow[1] = (int) ((xy[1] / screenH) * zonalWindRows);
				meridionalWindColRow[0] = (int) ((xy[0] / screenH) * meridionalWindCols);
				meridionalWindColRow[1] = (int) ((xy[1] / screenH) * meridionalWindRows);
				
				zonalCurrentColRow[0] = (int) ((xy[0] / screenW) * zonalCurrentCols);
				zonalCurrentColRow[1] = (int) ((xy[1] / screenH) * zonalCurrentRows);
				meridionalCurrentColRow[0] = (int) ((xy[0] / screenW) * meridionalCurrentCols);
				meridionalCurrentColRow[1] = (int) ((xy[1] / screenH) * meridionalCurrentRows);

				// UWind and UCurrent in m/s
				// UWind[0] = U = Zonal... UWind[1] = V = Meridional
				UWind[0] = oilZW[zonalWindColRow[0]][zonalWindColRow[1]];
				UWind[1] = oilMW[meridionalWindColRow[0]][meridionalWindColRow[1]];

				// UWindTotal is for area calculation
				double angleC = Math.atan(UWind[1] / UWind[0]);
				UWindTotal[i] = UWind[1] / Math.sin(angleC);
				
				// We now want to average only the values input in the path
				// Do this by adding in values to a sum (if it isn't specified, Java uses 0 so it won't affect count)
				double meanUWind = 0;
				for (int j = 0; j < UWindTotal.length; j++) {
					meanUWind = meanUWind + UWindTotal[j];
				}
				// Absolute value of path for later use
				meanUWind = Math.abs(meanUWind / (i + 1));
				// Getting current data for location
				UCurrent[0] = oilZC[zonalCurrentColRow[0]][zonalCurrentColRow[1]];
				UCurrent[1] = oilMC[meridionalCurrentColRow[0]][meridionalCurrentColRow[1]];
				
				// Check to see if oil is on land
				if (getLandData(oilLandFile, landColRow[0], landColRow[1]) == landNoData) {
					System.out.println("Oil hasn't hit land!");

					// U = Kt*Ut + Kw*Uw
					double[] U = new double[2];
					U[0] = KCurrent*UCurrent[0] + KWind*UWind[0]; // meters
					U[1] = KCurrent*UCurrent[1] + KWind*UWind[1]; // meters	
					// Horizontal turbulent diffusion - Chao et al 2001 2.2.3
					// deltaS = random[0-1]*sqrt(12*Dh*dt) -> Dh = m^2/s and dt = min so dt*60
					double[] deltaS = new double[2];
					deltaS[0] = Math.random()*Math.sqrt(12.0*dH*timeStep*60.0);
					deltaS[1] = Math.random()*Math.sqrt(12.0*dH*timeStep*60.0);
					// System.out.println("deltaSX: " + deltaS[0] + " deltaSY: " + deltaS[1]);
					// theta = 2*pi*random[0-1]
					double[] theta = new double[2];
					theta[0] = 2.0*Math.PI*Math.random();
					theta[1] = 2.0*Math.PI*Math.random();

					dX = U[0]*timeStep*60 + deltaS[0]*Math.cos(theta[0]);
					dY = U[1]*timeStep*60 + deltaS[1]*Math.sin(theta[1]);

					// Redefine X and Y - first translate dX/dY from m to pixels
					xy[0] = xy[0] + dX * xScaling;
					xy[1] = xy[1] + dY * yScaling;
					
					// Calculate area
					// A = 2270(dp/pOil)^(2/3)*V^(2/3)*t*(1/2)+40(dp/pOil)^(1/3)*V^(1/3)*UWind^(4/3)*t
					// UWind in knots - 1 m/s = 1.94384449 knots
					area = 2270 * Math.pow(deltaDensity / densityOil, 2.0/3.0) * Math.pow(volume, 2.0 / 3.0) 
					* Math.pow(i, -0.50) + 40 * Math.pow(deltaDensity / densityOil, 1.0 / 3.0) * 
					Math.pow(volume, 1.0 / 3.0) * Math.pow(meanUWind * 1.94384449, 4.0 / 3.0) * i;
					
					// Calculate minor axis
					// Q = C1[(pw-po)/po]^alpha*V^beta*t^gamma
					minorAxis = 1700 * Math.pow(deltaDensity/densityOil, 1.0 / 3.0) * 
					Math.pow(volume, 1.0 / 3.0) * Math.pow(i, 1.0 / 4.0);
					
					
					// Calculate major axis
					// R = C1[(pw-po)/po]^alpha*V^beta*t^gamma+C2*W^delta*t^epsilon
					// Or: R = minor axis + C2*W^delta*t^epsilon
					majorAxis = minorAxis + 30 * Math.pow(meanUWind*1.94384449, 4.0 / 3.0) * Math.pow(i, 3.0 / 4.0);

					// Scale the axis lengths (m) to pixels to send to draw
					// Figure out which is X and Y:
					// If zonal (x) > meridional wind, major axis is X scaled
					// If meridional (y) > zonal (x) wind, major is Y scaled
					if (Math.abs(UWind[0]) >= Math.abs(UWind[1])) {
						majorAxis = majorAxis * xScaling;
						minorAxis = minorAxis * yScaling;
					}
					else if (Math.abs(UWind[0]) < Math.abs(UWind[1])){
						majorAxis = majorAxis * yScaling;
						minorAxis = minorAxis * xScaling;
					}
					
				}
			}
		}
	}
	public double[] getXY() {
		return xy;
	}
	public void setDensityOil(double dOil) {
		densityOil = dOil;
		System.out.println("Density oil: " + densityOil);
	}
	public void setDensityWater(double dWater) {
		densityWater = dWater;
		System.out.println("Density water: " + densityWater);
	}
	public void setVolume(double vol) {
		volume = vol;
	}
	public double getMajorAxis() {
		return majorAxis;
	}
	public double getMinorAxis() {
		return minorAxis;
	}
	public double getArea() {
		return area;
	}
}
