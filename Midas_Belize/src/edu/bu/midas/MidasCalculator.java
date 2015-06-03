/*
 * This class computes the various indicies required by the MIDAS program. These calculations are
 * centralized inside of this one class file in order to make reading in/out of preferred weights
 * in these equations easier.
 */

package edu.bu.midas;

import java.awt.Color;

public class MidasCalculator {

	public MidasCalculator() {

	}
	public double calcUserGovIndex() {
		/*
		 * Governance index for user - Present is mix of:
		 	G1. Stakeholder involvement
			G2. Stakeholder compliance with rules and regulations
			G3. Management operations
			G4. Support from government agencies
			G5. Empowerment
			S3. Non-extractive alternative livelihoods
			S4. Socio-Economic benefits from establishment of MMA
			E5. Focal species abundance
		 */
		/*
					
			double temp = MIDAS.govCDF[MIDAS.mmaNum][0] + MIDAS.govCDF[MIDAS.mmaNum][1] + 
			MIDAS.govCDF[MIDAS.mmaNum][2] + MIDAS.govCDF[MIDAS.mmaNum][3] + MIDAS.govCDF[MIDAS.mmaNum][4] +
			MIDAS.socCDF[MIDAS.mmaNum][2] + MIDAS.socCDF[MIDAS.mmaNum][3] +
			MIDAS.ecoCDF[MIDAS.mmaNum][4] - 8.0;
		
		System.out.println("GovIndex: " + ((temp - 16.0) / 16.0));
		
		return (temp - 16.0) / 16.0;
					
		*/
		double index = 0;
		for (int i = 0; i < MIDAS.govCDF[MIDAS.mmaNum].length; i++) {
			index = index + 
				((double) (MIDAS.govCDF[MIDAS.mmaNum][i] - 1) * MIDAS.govWeights[MIDAS.mmaNum][i]) +
				((double) (MIDAS.socCDF[MIDAS.mmaNum][i] - 1) * MIDAS.govWeights[MIDAS.mmaNum][i + 6]) +
				((double) (MIDAS.ecoCDF[MIDAS.mmaNum][i] - 1) * MIDAS.govWeights[MIDAS.mmaNum][i + 12]);
		}
				
		double maxIndex = 0;
		for (int i = 0; i < MIDAS.govCDF[MIDAS.mmaNum].length; i++) {
			maxIndex = maxIndex + 
				4.0 * MIDAS.govWeights[MIDAS.mmaNum][i] +
				4.0 * MIDAS.govWeights[MIDAS.mmaNum][i + 6] +
				4.0 * MIDAS.govWeights[MIDAS.mmaNum][i + 12];
		}
		
		return ((index - (maxIndex / 2)) / (maxIndex / 2));

	}
	public double calcUserSocIndex() {
		/*
		 * Socioeconomic index for user - near future is mix of:
		 * 	S1. Perceived threat due to development
			S2. Perception of local extractive resources
			S3. Non-extractive alternative livelihoods
			S4. Socio-Economic benefits from establishment of MMA
			S5. Perception of seafood availability
			E1. Level of fishing effort
			E3. Habitat quality
			E5. Focal species abundance
			G1. Stakeholder involvement
			G5. Empowerment
		 */
		/*
		double temp = MIDAS.socCDF[MIDAS.mmaNum][0] + MIDAS.socCDF[MIDAS.mmaNum][1] +
		MIDAS.socCDF[MIDAS.mmaNum][2] + MIDAS.socCDF[MIDAS.mmaNum][3] + MIDAS.socCDF[MIDAS.mmaNum][4] +
		MIDAS.ecoCDF[MIDAS.mmaNum][0] + MIDAS.ecoCDF[MIDAS.mmaNum][2] + MIDAS.ecoCDF[MIDAS.mmaNum][4] +
		MIDAS.govCDF[MIDAS.mmaNum][0] + MIDAS.ecoCDF[MIDAS.mmaNum][4] - 10.0;
		return (temp - 20) / 20.0;
		*/
		double index = 0;
		for (int i = 0; i < MIDAS.govCDF[MIDAS.mmaNum].length; i++) {
			index = index + 
				((double) (MIDAS.govCDF[MIDAS.mmaNum][i] - 1) * MIDAS.socWeights[MIDAS.mmaNum][i]) +
				((double) (MIDAS.socCDF[MIDAS.mmaNum][i] - 1) * MIDAS.socWeights[MIDAS.mmaNum][i + 6]) +
				((double) (MIDAS.ecoCDF[MIDAS.mmaNum][i] - 1) * MIDAS.socWeights[MIDAS.mmaNum][i + 12]);
		}
				
		double maxIndex = 0;
		for (int i = 0; i < MIDAS.govCDF[MIDAS.mmaNum].length; i++) {
			maxIndex = maxIndex + 
				4.0 * MIDAS.socWeights[MIDAS.mmaNum][i] +
				4.0 * MIDAS.socWeights[MIDAS.mmaNum][i + 6] +
				4.0 * MIDAS.socWeights[MIDAS.mmaNum][i + 12];
		}

		return ((index - (maxIndex / 2)) / (maxIndex / 2));
	}
	public double calcUserEcoIndex() {
		/*
		 * From User Guide:
		 * Ecological index for user - near future is mix of:
			E1. Level of fishing effort
			E2. Relative change in habitat extent
			E3. Habitat quality
			E4. Herbivory
			E5. Focal species abundance
			G2. Stakeholder compliance with rules and regulations
			G4. Support from government agencies
			G5. Empowerment
		 */
		/*
		double temp = MIDAS.ecoCDF[MIDAS.mmaNum][0] + MIDAS.ecoCDF[MIDAS.mmaNum][1] +
		MIDAS.ecoCDF[MIDAS.mmaNum][2] + MIDAS.ecoCDF[MIDAS.mmaNum][3] + MIDAS.ecoCDF[MIDAS.mmaNum][4] + 
		MIDAS.govCDF[MIDAS.mmaNum][1] + MIDAS.govCDF[MIDAS.mmaNum][3] + 
		MIDAS.govCDF[MIDAS.mmaNum][4] - 8.0;
		
		return (temp - 16.0) / 16.0;
		*/
		double index = 0;
		for (int i = 0; i < MIDAS.govCDF[MIDAS.mmaNum].length; i++) {
			index = index + 
				((double) (MIDAS.govCDF[MIDAS.mmaNum][i] - 1) * MIDAS.ecoWeights[MIDAS.mmaNum][i]) +
				((double) (MIDAS.socCDF[MIDAS.mmaNum][i] - 1) * MIDAS.ecoWeights[MIDAS.mmaNum][i + 6]) +
				((double) (MIDAS.ecoCDF[MIDAS.mmaNum][i] - 1) * MIDAS.ecoWeights[MIDAS.mmaNum][i + 12]);
		}
				
		double maxIndex = 0;
		for (int i = 0; i < MIDAS.govCDF[MIDAS.mmaNum].length; i++) {
			maxIndex = maxIndex + 
				4.0 * MIDAS.ecoWeights[MIDAS.mmaNum][i] +
				4.0 * MIDAS.ecoWeights[MIDAS.mmaNum][i + 6] +
				4.0 * MIDAS.ecoWeights[MIDAS.mmaNum][i + 12];
		}

		return ((index - (maxIndex / 2)) / (maxIndex / 2));
	}
	public double calcGovIndexFuture(int futureVal) {
		// futureVal = [1:5] - need to change it to -1:0.4:1
		double future = (double)(futureVal - 3.0) / 4.0;

		double present = calcUserGovIndex();	

		// Between 0 - 0.2 (Very bad)
		if (present >= -1.0 && present < -0.6) {
			future = present + future - (2.0 / 5.0);
		}
		// Between 0.2 - 0.4 (Bad)
		else if (present >= -0.6 && present < -0.2) {
			future = present + future - (1.0 / 5.0);
		}
		// Between 0.4 - 0.6 (Okay)
		else if (present >= -0.2 && present < 0.2) {
			future = present + future - (0.5 / 5.0);
		}
		// Between 0.6 - 0.8 (Good)
		else if (present >= 0.2 && present < 0.6) {
			future = present + future;
		}
		// Between 0.8 - 1 (Very good)
		else if (present >= 0.6 && present < 1) {
			future = present + future;
		}
		// Checking if value is not in 0-1 domain
		else {
			future = present;
		}

		if (future < -1) {
			future = -1;
		}
		else if (future > 1) {
			future = 1;
		}

		return future;	
	}

	public double calcSocIndexFuture(int futureVal) {
		// futureVal = [1:5] - need to change it to -1:0.4:1
		double future = (double)(futureVal - 3.0) / 4.0;

		double present = calcUserSocIndex();	

		// Between 0 - 0.2 (Very bad)
		if (present >= -1.0 && present < -0.6) {
			future = present + future - (2.0 / 5.0);
		}
		// Between 0.2 - 0.4 (Bad)
		else if (present >= -0.6 && present < -0.2) {
			future = present + future - (1.0 / 5.0);
		}
		// Between 0.4 - 0.6 (Okay)
		else if (present >= -0.2 && present < 0.2) {
			future = present + future - (0.5 / 5.0);
		}
		// Between 0.6 - 0.8 (Good)
		else if (present >= 0.2 && present < 0.6) {
			future = present + future;
		}
		// Between 0.8 - 1 (Very good)
		else if (present >= 0.6 && present < 1) {
			future = present + future;
		}
		// Checking if value is not in 0-1 domain
		else {
			future = present;
		}

		if (future < -1) {
			future = -1;
		}
		else if (future > 1) {
			future = 1;
		}

		return future;	
	}
	public double calcEcoIndexFuture(int futureVal) {
		// futureVal = [1:5] - need to change it to -1:0.4:1
		double future = (double)(futureVal - 3.0) / 4.0;

		double present = calcUserEcoIndex();	

		// Between 0 - 0.2 (Very bad)
		if (present >= -1.0 && present < -0.6) {
			future = present + future - (2.0 / 5.0);
		}
		// Between 0.2 - 0.4 (Bad)
		else if (present >= -0.6 && present < -0.2) {
			future = present + future - (1.0 / 5.0);
		}
		// Between 0.4 - 0.6 (Okay)
		else if (present >= -0.2 && present < 0.2) {
			future = present + future - (0.5 / 5.0);
		}
		// Between 0.6 - 0.8 (Good)
		else if (present >= 0.2 && present < 0.6) {
			future = present + future;
		}
		// Between 0.8 - 1 (Very good)
		else if (present >= 0.6 && present < 1) {
			future = present + future;
		}
		// Checking if value is not in 0-1 domain
		else {
			future = present;
		}

		if (future < -1) {
			future = -1;
		}
		else if (future > 1) {
			future = 1;
		}

		return future;	
	}
	/*
	public double calcGovIndexFuture(int futureVal) {
		// futureVal = [1:5] - need to change it to [-2:2]
		futureVal = futureVal - 3;

		double present = calcUserGovIndex();
		double weight = 0;

		if (MIDAS.govEqnType.equals("SIG")) {
				weight = Math.pow(1 + Math.exp(-MIDAS.govEqnM*futureVal), -1) + MIDAS.govEqnB;
		}
		else if (MIDAS.govEqnType.equals("LIN")) {
				weight = MIDAS.govEqnM * futureVal + MIDAS.govEqnB;
		}

		double future = present + present * weight;

		if (future > 1) {
			future = 1;
		}
		else if (future < -1) {
			future = -1;
		}
		return future;	
	}
	 */
	public Color[] calculateRiskColor(int[][] input, boolean[][] threatHabitatAtCell) {

		int layerCount = input.length / 2;
		int cellCount = input[0].length;
		int[][] risk = new int[layerCount][cellCount];
		System.out.println("Layer count: " + layerCount + " and cellCount: " + cellCount);

		Color[] color = new Color[cellCount];

		// Looping through all cells
		for (int i = 0; i < cellCount; i++) {
			int sum = 0;

			int totalHabitatInCell = 0;
			for (int m = 0; m < threatHabitatAtCell.length; m++) {
				if (threatHabitatAtCell[m][i] == true) {
					totalHabitatInCell++;
				}
			}

			// Looping through all layers
			for (int j = 0; j < layerCount; j++) {
				if (threatHabitatAtCell[j][i] == true) {
					int threat = input[j][i];
					int health = input[j+layerCount][i];
					System.out.println("At cell " + i + " Threat: " + threat + " and health: " + health);
					// Logic for which value to assign each layer based on health / risk
					// First: 4-3 health, 1-2 threat
					if ((health == 4 || health == 3) && (threat == 1 || threat == 2)) {
						sum = sum + 1;
					}
					// Second: 4-3 health, 3-4 threat
					else if ((health == 4 || health == 3) && (threat == 3 || threat == 4)) {
						sum = sum + 2;
					}
					// Third: 1-2 health, 1-2 threat
					else if ((health == 1 || health == 2) && (threat == 1 || threat == 2)) {
						sum = sum + 3;
					}
					// Fourth: 1-2 health, 3-4 threat
					else if ((health == 1 || health == 2) && (threat == 3 || threat == 4)) {
						sum = sum + 4;
					}
				}
			}
			double percent = (double) sum / (double) (4 * totalHabitatInCell);
			System.out.println("Sum: " + sum + " and Percent: " + percent);
			if (percent >= 0 && percent <= 0.25) {
				color[i] = Color.green;
			}
			else if (percent > 0.25 && percent <= 0.5) {
				color[i] = Color.yellow;
			}
			else if (percent > 0.5 && percent <= 0.75) {
				// Orange
				color[i] = new Color(255, 130, 0);
			}
			else if (percent > 0.75 && percent <= 1.0) {
				color[i] = Color.red;
			}
		}

		return color;
	}
	/*
	public Color[][] calculateRiskColor(int cellnum, int[][][] riskPerception, double[][][] landCoverPercent) {
		Color[][] riskColor = new Color[cellnum][cellnum];
		int numRisk = riskPerception.length;

		int initialGreen = 200;

		// Determining the range of risk values for each cell; for scaling purposes
		double[][] maxRisk = new double[cellnum][cellnum];
		double[][] minRisk = new double[cellnum][cellnum];

		for (int i = 0; i < cellnum; i++) {
			for (int j = 0; j < cellnum; j++) {
				// Min risk means max enforcement
				minRisk[i][j] = 5 * landCoverPercent[0][i][j];
				for (int k = 1; k < numRisk; k++) {
					// Factoring in ecological layer risks
					maxRisk[i][j] = maxRisk[i][j] + 5 * landCoverPercent[k][i][j];
				}
			} 
		}

		double[][] enforcement = new double[cellnum][cellnum];
		double[][] threat = new double[cellnum][cellnum];

		for (int i = 0; i < cellnum; i++) {
			for (int j = 0; j < cellnum; j++) {
				enforcement[i][j] = riskPerception[0][i][j] * landCoverPercent[0][i][j];
				for (int k = 1; k < numRisk; k++) {
					// Threat is equal to threat - (Sigma) risk * % habitat
					threat[i][j] = threat[i][j] + (riskPerception[k][i][j] * landCoverPercent[k][i][j]);
				}
				// Risk is equal to enforcement - threat in each cell
				// Scaling risk by it's range for each cell
				// Scaling is (risk) / (maxRisk) -> should yield a decimal
				double risk = (threat[i][j] - enforcement[i][j]) / (Math.abs(maxRisk[i][j]) + Math.abs(minRisk[i][j]));

				/* TODO diagnostics: delete later
				System.out.println("Risk at: i: " + i + " j: " + j + " is: " + risk);
				System.out.println("Min Risk at: i: " + i + " j: " + j + " is: " + minRisk[i][j]);
				System.out.println("Max Risk at: i: " + i + " j: " + j + " is: " + maxRisk[i][j]);
				System.out.println("Enforcement at: i= " + i + " j= " + j + " is: " + enforcement[i][j]);
				System.out.println("Threat at: i= " + i + " j= " + j + " is: " + threat[i][j]);


				// Scaling risk for color green in RGB
				double red = 255;
				double green = 255 - 255 * risk;
				if (green > 255) {
					red = red - (green - 255);
					green = 255;
					if (red < 0) {
						red = 0;
					}
				}
				riskColor[i][j] = new Color((int) red, (int) green, 0);
			}
		}

		return riskColor;
	}
	 */
	/* This calculation is on longer current as of 1-24-10
	public Color[] calculateRiskColor(int[][] input, int min, int max) {
		int[] enforcement = input[0];
		int[][] threat = new int[input.length - 1][input[0].length];
		for (int i = 0; i < input.length - 1; i++) {
			threat[i] = input[i + 1];
		}

		int numThreat = threat.length;
		int cellNum = threat[0].length;
		Color[] riskColor = new Color[cellNum];

		// Determining the range of risk values for each cell; for scaling purposes
		// *NOTE: This will be more useful when we have expert opinion for coefficients
		// Originally used for layer cover percentage in each cell
		int[] maxRisk = new int[cellNum];
		int[] minRisk = new int[cellNum];

		for (int i = 0; i < cellNum; i++) {
			minRisk[i] = min;
			for (int j = 1; j < numThreat; j++) {
				// Factoring in ecological layer risks
				maxRisk[i] = maxRisk[i] + max;
			} 
		}

		int[] allThreat = new int[cellNum];

		for (int i = 0; i < enforcement.length; i++) {
			if (threat[0][i] != -1) {
				for (int j = 0; j < numThreat; j++) {
					// Summing all threat from each layer for each cell
					allThreat[i] = allThreat[i] + threat[j][i];
				}
				// Risk is equal to enforcement - threat in each cell
				// Scaling risk by it's range for each cell
				// Scaling is (risk) / (maxRisk) -> should yield a decimal
				double risk = (double)(allThreat[i] - enforcement[i]) / (double)((numThreat)*max);
				System.out.println("Risk: " + risk + " = " + allThreat[i] + " - " + enforcement[i] + " + " + max + " / " + numThreat);


				// Scaling risk for color green in RGBA
				int red = 255;
				int green = 255;
				if (risk > 0.05) {
					green = (int)(green - 255 * risk);
				}
				else if (risk <= 0.05) {
					red = 255 + (int)(500 * risk);
				}
				int alpha = 200;
				riskColor[i] = new Color(red, green, 0, alpha);
			}
		}

		return riskColor;
	}
	 */
}