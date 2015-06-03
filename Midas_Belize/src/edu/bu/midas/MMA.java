package edu.bu.midas;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import au.com.bytecode.opencsv.CSVReader;

public class MMA extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Initialize cdfPanel and gisPanel for adding to each MMA panel
	public CDFPanel cdfPane;
	private GISPanel gisPane;
	
	//cdfPanel:
	private String[] labels;
	private String[] headers;
	private String value;
	private String[] govCDFNames;
	private String[][] govValNames;
	private String[] socCDFNames;
	private String[][] socValNames;
	private String[] ecoCDFNames;
	private String[][] ecoValNames;

	// config file for GIS
	private String configFile;
	private int expertEvalNumber;
	private int[] expertEvalValues = new int[18];
	
	private PropertyResourceBundle cdfRes;

	public MMA(String name) {
		setLanguage();
		populateCDFs();
		setMMA(name);
		readExpertEval();
		setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		setBackground(MIDAS.BACKGROUNDCOLOR);
		
		cdfPane = new CDFPanel(value, labels, headers, govCDFNames, govValNames, 
				socCDFNames, socValNames, ecoCDFNames, ecoValNames, expertEvalValues);
		gisPane = new GISPanel(configFile);
		
		add(cdfPane);
		add(gisPane);
		gisPane.setVisible(false);
	}

	public void setLanguage() {
		if (MIDAS.LANGUAGE.equals("EN")) {
			cdfRes = (PropertyResourceBundle) ResourceBundle.getBundle("CDFs", new Locale("en"));
		}
		else if (MIDAS.LANGUAGE.equals("ES")) {
			cdfRes = (PropertyResourceBundle) ResourceBundle.getBundle("CDFs", new Locale("es"));
		}
	}
	public void populateCDFs() {
		// Initializing the strings
		labels = new String[3];
		headers = new String[3];
		govCDFNames = new String[MIDAS.govCDF[0].length];
		govValNames = new String[MIDAS.govCDF[0].length][5];
		socCDFNames = new String[MIDAS.socCDF[0].length];
		socValNames = new String[MIDAS.socCDF[0].length][5];
		ecoCDFNames = new String[MIDAS.ecoCDF[0].length];
		ecoValNames = new String[MIDAS.ecoCDF[0].length][5];
		
		// Populating them with info from CDFs_(locale).properties files
		// Value name for showing which value slider is at
		value = cdfRes.getString("VALUE");
		// Label info
		labels[0] = cdfRes.getString("LABEL_GOV");
		labels[1] = cdfRes.getString("LABEL_SOC");
		labels[2] = cdfRes.getString("LABEL_ECO");
		// Header info
		headers[0] = cdfRes.getString("HEADER_GOV");
		headers[1] = cdfRes.getString("HEADER_SOC");
		headers[2] = cdfRes.getString("HEADER_ECO");
		// Governance info
		govCDFNames[0] = cdfRes.getString("GOV_CDF_1");
		govCDFNames[1] = cdfRes.getString("GOV_CDF_2");
		govCDFNames[2] = cdfRes.getString("GOV_CDF_3");
		govCDFNames[3] = cdfRes.getString("GOV_CDF_4");
		govCDFNames[4] = cdfRes.getString("GOV_CDF_5");
		govCDFNames[5] = cdfRes.getString("GOV_CDF_6");
		
		// TODO rewrite each value name in .properties file with seperate keys
		// TODO rewrite section to use the seperate keys for each value of CDFs
		for (int i = 0; i < 5; i++) {
			govValNames[i][0] = cdfRes.getString("GOV_VAL_1");
			govValNames[i][1] = cdfRes.getString("GOV_VAL_2");
			govValNames[i][2] = cdfRes.getString("GOV_VAL_3");
			govValNames[i][3] = cdfRes.getString("GOV_VAL_4");
			govValNames[i][4] = cdfRes.getString("GOV_VAL_5");
		}
		govValNames[5][0] = cdfRes.getString("GOV_VAL_6_1");
		govValNames[5][1] = cdfRes.getString("GOV_VAL_6_2");
		govValNames[5][2] = cdfRes.getString("GOV_VAL_6_3");
		govValNames[5][3] = cdfRes.getString("GOV_VAL_6_4");
		govValNames[5][4] = cdfRes.getString("GOV_VAL_6_5");
		
		// Socioeconomics
		socCDFNames[0] = cdfRes.getString("SOC_CDF_1");
		socCDFNames[1] = cdfRes.getString("SOC_CDF_2");
		socCDFNames[2] = cdfRes.getString("SOC_CDF_3");
		socCDFNames[3] = cdfRes.getString("SOC_CDF_4");
		socCDFNames[4] = cdfRes.getString("SOC_CDF_5");
		socCDFNames[5] = cdfRes.getString("SOC_CDF_6");
		
		// TODO rewrite each value name in .properties file with seperate keys
		// TODO rewrite section to use the seperate keys for each value of CDFs
		for (int i = 0; i < 5; i++) {
			socValNames[i][0] = cdfRes.getString("SOC_VAL_1");
			socValNames[i][1] = cdfRes.getString("SOC_VAL_2");
			socValNames[i][2] = cdfRes.getString("SOC_VAL_3");
			socValNames[i][3] = cdfRes.getString("SOC_VAL_4");
			socValNames[i][4] = cdfRes.getString("SOC_VAL_5");
		}
		socValNames[5][0] = cdfRes.getString("SOC_VAL_6_1");
		socValNames[5][1] = cdfRes.getString("SOC_VAL_6_2");
		socValNames[5][2] = cdfRes.getString("SOC_VAL_6_3");
		socValNames[5][3] = cdfRes.getString("SOC_VAL_6_4");
		socValNames[5][4] = cdfRes.getString("SOC_VAL_6_5");
		
		// Ecological
		ecoCDFNames[0] = cdfRes.getString("ECO_CDF_1");
		ecoCDFNames[1] = cdfRes.getString("ECO_CDF_2");
		ecoCDFNames[2] = cdfRes.getString("ECO_CDF_3");
		ecoCDFNames[3] = cdfRes.getString("ECO_CDF_4");
		ecoCDFNames[4] = cdfRes.getString("ECO_CDF_5");
		ecoCDFNames[5] = cdfRes.getString("ECO_CDF_6");
		
		for (int i = 0; i < ecoCDFNames.length; i++) {
			for (int j = 0; j < ecoValNames[0].length; j++) {
				ecoValNames[i][j] = cdfRes.getString("ECO_VAL_" + 
						Integer.toString(i + 1) + "_" + Integer.toString(j + 1));
			}
		}
	}
	public void readExpertEval() {
		CSVReader reader;
		
		InputStream inStream = getClass().getResourceAsStream("/" + "ExpertEvals.cfg");
		InputStreamReader inReader = new InputStreamReader(inStream);
		try {
			reader = new CSVReader(inReader,',','\'', expertEvalNumber);
			String[] nextLine = reader.readNext();
			
			for (int i = 0; i < nextLine.length; i++) {
				if (nextLine[i].equals("null") == false) {
					expertEvalValues[i] = Integer.parseInt(nextLine[i]);
				}
				else {
					expertEvalValues = null;
				}
			}
			
	    	inReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	//Dummy method to pass dummy variables based on name of MMA
	// TODO Will be replaced by reading in name of config file with MMA.class' constructor
	public void setMMA(String name) {
		if (name.equals("Belize")) {
			configFile = "belize.cfg";
			expertEvalNumber = 0;
		}
		else if (name.equals("Hol Chan")) {
			configFile = "holchan.cfg";
			expertEvalNumber = 1;
		}
		else if (name.equals("Southwater")) {
			configFile = "southwater.cfg";
			expertEvalNumber = 2;
		}
		else if (name.equals("Laughing Bird")) {
			configFile = "laughingbird.cfg";
			expertEvalNumber = 3;
		}
		else if (name.equals("Port Honduras")) {
			configFile = "porthonduras.cfg";
			expertEvalNumber = 4;
		}
		else if (name.equals("Sapodilla Caye")) {
			configFile = "sapodilla.cfg";
			expertEvalNumber = 5;
		}
		else if (name.equals("Lighthouse Reef")) {
			configFile = "lighthouse.cfg";
			expertEvalNumber = 6;
		}
	}
	public void setMMAVisible(boolean visible) {
		/* 
		 * This method will set the MMA JPanel to visible / not visible with setVisible(bool).
		 * The method was created in order to also tell components beneath it (mostly 
		 * GISPanel and MapPanel) that this MMA is not visible, so don't keep variables stored
		 * in memory.
		 */
		setVisible(visible);
		gisPane.setMMAVisible(visible);
	}
	public void changeView(String view) {
		if (view.equals("CDF")) {
			cdfPane.setVisible(true);
			gisPane.setVisible(false);
		}
		else if (view.equals("GIS")) {
			cdfPane.setVisible(false);
			gisPane.setVisible(true);
		}
	}
	public void updateCDFs(int mma) {
		cdfPane.updateCDFs(mma);
	}
	public int[][] getThreat() {
		return gisPane.getThreat();
	}

	public void repaintGraphs() {
		cdfPane.repaintGraphs();
	}

	// MIDAS Report get functions
	public String getCDFNames(String type, int num) {
		if (type.equals("Gov")) {
			return govCDFNames[num];
		}
		else if (type.equals("Soc")) {
			return socCDFNames[num];
		}
		else if (type.equals("Eco")) {
			return ecoCDFNames[num];
		}
		else {
			return null;
		}
	}
	public int[][] getGISHealthThreat() {
		return gisPane.getGISHealthThreat();
	}
	public void setGISHealthThreat(int[][] mmaThreat) {
		gisPane.setGISHealthThreat(mmaThreat);
	}
	public String getCDFValues(String type, int num) {
		/*
		 * Explanation: we're returning the String value of the
		 * CDF we've chosen (num) for a given value of that CDF (we need
		 * to input the MMA we're at as well as the CDF number we're looking
		 * for to get this value).
		 */
		
		if (type.equals("Gov")) {
			return govValNames[num][MIDAS.govCDF[MIDAS.mmaNum][num] - 1];
		}
		else if (type.equals("Soc")) {
			return socValNames[num][MIDAS.socCDF[MIDAS.mmaNum][num] - 1];
		}
		else if (type.equals("Eco")) {
			return ecoValNames[num][MIDAS.ecoCDF[MIDAS.mmaNum][num] - 1];
		}
		else {
			return null;
		}
	}
	public BufferedImage getCDFReportBufferedImage(String name) {
		return cdfPane.getCDFReportBufferedImage(name);
	}
	public boolean isRiskEnabled() {
		return gisPane.isRiskEnabled();
	}
	
	public BufferedImage getGISBufferedImage(String name) {
		return gisPane.getGISBufferedImage(name);
	}
	public BufferedImage getGISControlBufferedImage(String name) {
		return gisPane.getGISControlBufferedImage(name);
	}

	public void repaintGraphs(int i) {
		cdfPane.repaintGraphs();
	}
}
