package edu.bu.midas;

import java.awt.Dimension;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class SaveAndLoad {
	
	// Localization
	private String titleText;
	private String nameText;
	private String organizationText;
	private String mmaExperienceText;
	private String yearsExperienceText;
	
	// Storage of variables
	private String name;
	private String organization;
	private String mmaExperience;
	private int yearsExperience;

	public SaveAndLoad() {
		setLanguage();
	}
	public void setLanguage() {
		titleText = "Enter personal information.";
		nameText = "Name: ";
		organizationText = "Organization/Affiliation: ";
		mmaExperienceText = "Select MMAs you work with.";
		yearsExperienceText = "Enter ";
	}
	public void getPersonalInfo() {
		JDialog dialog = new JDialog(MIDAS.frame, titleText);
		JPanel pane = new JPanel();
		
		
		dialog.setContentPane(pane);
		dialog.setSize(new Dimension(400,400));
		dialog.setLocationRelativeTo(MIDAS.frame);
		dialog.setVisible(true);
	}
	public void readCSV(String file) throws IOException {
		int mmaRead = 0;

		// InputStream inStream = getClass().getResourceAsStream("/"+file);
		// InputStreamReader inReader = new InputStreamReader(inStream);

		// CSVReader reader = new CSVReader(inReader, ',', CSVWriter.NO_QUOTE_CHARACTER);
		CSVReader reader = new CSVReader(new FileReader(file), ',', CSVWriter.NO_QUOTE_CHARACTER);

		String [] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			// Check to see if line is a commment
			if (nextLine[0].charAt(0) != '#') {
				System.out.println(nextLine[0]);

				// Checking to see if we're starting a new MMA
				for (int i = 0; i < MIDAS.mmaNames.length; i++) {
					if (nextLine[0].equals(MIDAS.mmaNames[i])) {
						// Assign MMA number to index
						mmaRead = i;
						// Read a new line!
						nextLine = reader.readNext();
						// Checking for another comment
						if (nextLine[0].charAt(0) != '#') {
							// Now that we've found a MMA, get some CDFs
							if (nextLine[0].equals("Governance")) {
								nextLine = reader.readNext();
								for (int j = 0; j < nextLine.length; j++) {
									MIDAS.setCDF(MIDAS.govCDF, mmaRead, 
											j, Integer.parseInt(nextLine[j]));
								}
								// If we found a governance CDF, read again for weights
								nextLine = reader.readNext();
								for (int k = 0; k < nextLine.length; k++) {
									MIDAS.govWeights[mmaRead][k] = Double.parseDouble(nextLine[k]);
								}
								nextLine = reader.readNext();
							}
							if (nextLine[0].equals("Socioeconomic")) {
								nextLine = reader.readNext();
								for (int j = 0; j < nextLine.length; j++) {
									MIDAS.setCDF(MIDAS.socCDF, mmaRead, 
											j, Integer.parseInt(nextLine[j]));
									System.out.println("Setting SE " + j + " to: " + Integer.parseInt(nextLine[j]));
								}
								nextLine = reader.readNext();
								for (int k = 0; k < nextLine.length; k++) {
									MIDAS.socWeights[mmaRead][k] = Double.parseDouble(nextLine[k]);
								}
								nextLine = reader.readNext();
							}
							if (nextLine[0].equals("Ecological")) {
								nextLine = reader.readNext();
								for (int j = 0; j < nextLine.length; j++) {
									MIDAS.setCDF(MIDAS.ecoCDF, mmaRead, 
											j, Integer.parseInt(nextLine[j]));
								}
								nextLine = reader.readNext();
								for (int k = 0; k < nextLine.length; k++) {
									MIDAS.ecoWeights[mmaRead][k] = Double.parseDouble(nextLine[k]);
								}
							}
							// Update CDfs
							MIDAS.updateCDFs();
							// Update graphs
							MIDAS.repaintGraphs();

							nextLine = reader.readNext();
							// Reading in threat & health
							if (nextLine[0].equals("Risk")) {
								int numThreatLayers = Integer.parseInt(nextLine[1]);
								int numGridCells = Integer.parseInt(nextLine[2]);

								// Creating storage for data to push to GISPanel
								int[][] threatHealth = new int[numThreatLayers][numGridCells];
								// Looping through lines of habitat threat & health
								nextLine = reader.readNext();
								for (int j = 0; j < numThreatLayers; j++) {
									// Looping through line (each data sample is for one cell)
									for (int k = 0; k < numGridCells; k++) {
										threatHealth[j][k] = Integer.parseInt(nextLine[k]);
									}
									nextLine = reader.readNext();
								}

								MIDAS.setGISHealthThreat(i, threatHealth);
							}
						}
					}	    			
				}
			}
		}
		reader.close();
	}
	public void writeCSV(String file) throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter(file),',',CSVWriter.NO_QUOTE_CHARACTER);

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm a");
		Date date = new Date();
		String[] headerInfo = new String[2];
		headerInfo[0] = "# " + "MIDAS Save File";
		headerInfo[1] = "# " + dateFormat.format(date);

		writer.writeNext(headerInfo[0]);
		writer.writeNext(headerInfo[1]);

		// Looping through MMAs
		for (int i = 0; i < MIDAS.mmaNames.length; i++) {
			// Writing MMA name

			writer.writeNext(MIDAS.mmaNames[i]);

			// Adding Governance CDFs into String[] and writing
			writer.writeNext("Governance");
			String[] string = new String[MIDAS.govCDF[0].length];
			for (int j = 0; j < string.length; j++) {
				string[j] = Integer.toString(MIDAS.govCDF[i][j]);
			}
			writer.writeNext(string);

			string = new String[MIDAS.govWeights[0].length];
			for (int j = 0; j < string.length; j++) {
				string[j] = Double.toString(MIDAS.govWeights[i][j]);
			}
			writer.writeNext(string);

			// Adding Socioeconomic CDFs into String[] and writing
			writer.writeNext("Socioeconomic");
			string = new String[MIDAS.socCDF[0].length];
			for (int j = 0; j < string.length; j++) {
				string[j] = Integer.toString(MIDAS.socCDF[i][j]);
			}
			writer.writeNext(string);

			string = new String[MIDAS.socWeights[0].length];
			for (int j = 0; j < string.length; j++) {
				string[j] = Double.toString(MIDAS.socWeights[i][j]);
			}
			writer.writeNext(string);

			// Adding Ecological CDFs into String[] and writing
			writer.writeNext("Ecological");
			string = new String[MIDAS.ecoCDF[0].length];
			for (int j = 0; j < string.length; j++) {
				string[j] = Integer.toString(MIDAS.ecoCDF[i][j]);
			}
			writer.writeNext(string);

			string = new String[MIDAS.ecoWeights[0].length];
			for (int j = 0; j < string.length; j++) {
				string[j] = Double.toString(MIDAS.ecoWeights[i][j]);
			}
			writer.writeNext(string);

			// Writing out threat & health
			int[][] threatHealth = MIDAS.getGISHealthThreat(i);
			if (threatHealth != null) {
				int numThreatLayers = threatHealth.length;
				int numGridCells = threatHealth[0].length;

				string = new String[3];
				string[0] = "Risk";
				string[1] = Integer.toString(numThreatLayers);
				string[2] = Integer.toString(numGridCells);

				// Writing risk name & dimension information for reading in
				writer.writeNext(string);

				for (int j = 0; j < numThreatLayers; j++) {
					string = new String[numGridCells];
					for (int k = 0; k < numGridCells; k++) {
						string[k] = Integer.toString(threatHealth[j][k]);
					}
					writer.writeNext(string);
				}
			}
			writer.writeNext("# END OF " + MIDAS.mmaNames[i] + " DATA");
		}

		writer.close();
	}

}
