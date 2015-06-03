/*
 * This is the main class.
 */

package edu.bu.midas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;

public class MIDAS extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	public static Menu menu;
	public static JScrollPane mainScrollPane;
	public static MMA[] mmaPane;
	public static int mmaNum;
	
	// Declaring resolutions for program window & panes
	// 950x650 version
	public final static Dimension APPRES = new Dimension(970, 670);
	public final static Dimension LEFTRES = new Dimension(300, 650);
	public final static Dimension RIGHTRES = new Dimension(650, 650);
	public final static Dimension OUTCOMERES = new Dimension(650, 500);
	public final static Dimension GISRES = new Dimension(650, 650);
	public final static Dimension BOTTOMRES = new Dimension(630, 150);
	public final static Dimension HELPRES = new Dimension(430, 150);
	public final static Dimension OPTIONRES = new Dimension(200, 150);
	public final static Dimension EQNEDITRES = new Dimension(600, 500);
	public final static Color BACKGROUNDCOLOR = new Color(193, 205, 193);

	// Handling preferences
	public static Preferences userPrefs;

	// Setting default values for preferenes
	public static final boolean DEFAULT_SHOW_WELCOME = true;
	public static final String DEFAULT_LANGUAGE = "EN";
	public static final int STARTING_MMA = 0;
	public static final boolean DEFAULT_SHOW_EXPERT_OPINION = true;

	// Oil Model EULA (may be combined into default settings)
	public static final boolean DEFAULT_OIL_EULA = false;
	public static boolean OIL_EULA = false;
	
	// Declaring user preference variables
	public static boolean SHOW_WELCOME;
	public static String LANGUAGE;
	public static boolean SHOW_EXPERT_OPINION;

	// Setting keys for preferences
	public static final String WELCOME_KEY = "SHOW_WELCOME";
	public static final String LANG_KEY = "DEFAULT_LANGUAGE";
	public static final String EXPERT_OPINION_KEY = "SHOW_EXPERT_OPINION";

	private PropertyResourceBundle midasRes;
	private static boolean switchedLanguages = false;
	private static String view;

	private static String title;
	public static String countryName;
	public static String[] mmaNames;
	public static int[] numCDFs;
	public static int[][] govCDF;
	public static int[][] socCDF;
	public static int[][] ecoCDF;
	
	public static double[][] govWeights;
	public static double[][] socWeights;
	public static double[][] ecoWeights;

	public MIDAS() {		
		userPrefs = Preferences.userRoot();

		// Retrieve preferences from user
		SHOW_WELCOME = userPrefs.getBoolean(WELCOME_KEY, DEFAULT_SHOW_WELCOME);
		// LANGUAGE = userPrefs.get(LANG_KEY, DEFAULT_LANGUAGE);
		LANGUAGE = "EN";
		SHOW_EXPERT_OPINION = userPrefs.getBoolean(EXPERT_OPINION_KEY, DEFAULT_SHOW_EXPERT_OPINION);

		// super(new GridLayout(1,1));
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setSize(APPRES);
		setBackground(BACKGROUNDCOLOR);

		setLanguage();
		if (switchedLanguages == false) {
			initCDFs();
			// TODO implement a preference for default view?
			view = "CDF";
		}
		createMMAs();
		changeView(view);

		menu = new Menu();
	}

	public void setLanguage() {
		if (MIDAS.LANGUAGE.equals("EN")) {
			midasRes = (PropertyResourceBundle) ResourceBundle.getBundle(
					"MIDAS", new Locale("en"));
		} else if (MIDAS.LANGUAGE.equals("ES")) {
			midasRes = (PropertyResourceBundle) ResourceBundle.getBundle(
					"MIDAS", new Locale("es"));
		}
		title = midasRes.getString("TITLE");
		countryName = midasRes.getString("COUNTRY");
		mmaNames = midasRes.getString("MMA_NAMES").split(", ");
		String[] temp = midasRes.getString("NUM_CDF").split(", ");
		numCDFs = new int[temp.length];
		for (int i = 0; i < temp.length; i++) {
			numCDFs[i] = Integer.parseInt(temp[i]);
		}
	}
	public static void createAndShowGUI() {
		MIDAS midasApp = new MIDAS();
	
		frame = new JFrame(title + " - " + countryName);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	
		frame.setJMenuBar(menu);
		frame.add(midasApp, BorderLayout.CENTER);
	
		frame.pack();
		frame.setVisible(true);
			
		// Create a new Welcome Menu object, which will appear automatically
		// unless the
		// user said not to show it. Located here so that it appears after main
		// window does
		if (MIDAS.SHOW_WELCOME == true) {
			WelcomeMenu welcome = new WelcomeMenu();
			welcome.showDialogBox();
		}
	}

	public void createMMAs() {
		mainScrollPane = new JScrollPane();
		mainScrollPane.setSize(MIDAS.APPRES);
		mainScrollPane.setPreferredSize(MIDAS.APPRES);
		
		mmaPane = new MMA[mmaNames.length];
		for (int i = 0; i < mmaNames.length; i++) {
			mmaPane[i] = new MMA(mmaNames[i]);
			mmaPane[i].setMMAVisible(false);
			mainScrollPane.getViewport().add(mmaPane[i]);
			// add(mmaPane[i]);
		}
		mmaPane[STARTING_MMA].setMMAVisible(true);
		mainScrollPane.setViewportView(mmaPane[STARTING_MMA]);
		
		add(mainScrollPane);
	}

	public void initCDFs() {
		int numMMAs = mmaNames.length;
		int numGovCDFs = numCDFs[0];
		int numSocCDFs = numCDFs[1];
		int numEcoCDFs = numCDFs[2];
	
		govCDF = new int[numMMAs][numGovCDFs];
		socCDF = new int[numMMAs][numSocCDFs];
		ecoCDF = new int[numMMAs][numEcoCDFs];
	
		for (int i = 0; i < numMMAs; i++) {
			for (int j = 0; j < numGovCDFs; j++)
				govCDF[i][j] = 3;
			for (int j = 0; j < numSocCDFs; j++)
				socCDF[i][j] = 3;
			for (int j = 0; j < numEcoCDFs; j++)
				ecoCDF[i][j] = 3;
		}
		
		// Weights for outcomes
		govWeights = new double[numMMAs][numGovCDFs + numSocCDFs + numEcoCDFs];
		socWeights = new double[numMMAs][numGovCDFs + numSocCDFs + numEcoCDFs];
		ecoWeights = new double[numMMAs][numGovCDFs + numSocCDFs + numEcoCDFs];
		
		// Hard coding default weights into program	
		for (int i = 0; i < numMMAs; i++) {
			for (int j = 0; j < 5; j++) {
				govWeights[i][j] = 1.0;
				socWeights[i][j + 6] = 1.0;
				ecoWeights[i][j + 12] = 1.0;
			}
			govWeights[i][8] = 1.0;
			govWeights[i][9] = 1.0;
			govWeights[i][16] = 1.0;
			
			socWeights[i][0] = 1.0;
			socWeights[i][4] = 1.0;
			socWeights[i][12] = 1.0;
			socWeights[i][14] = 1.0;
			socWeights[i][16] = 1.0;
			
			ecoWeights[i][1] = 1.0;
			ecoWeights[i][3] = 1.0;
			ecoWeights[i][4] = 1.0;
		}
		
	}

	public static void savePrefs() {
		userPrefs.putBoolean(WELCOME_KEY, SHOW_WELCOME);
		userPrefs.put(LANG_KEY, LANGUAGE);
		userPrefs.putBoolean(EXPERT_OPINION_KEY, SHOW_EXPERT_OPINION);
	}

	public static void changeLanguage(String lang) {
		switchedLanguages = true;
	
		MIDAS.LANGUAGE = lang;
		frame.dispose();
		savePrefs();
		main(new String[0]);
	}

	public static void changeView(String v) {
		view = v;
		for (int i = 0; i < mmaPane.length; i++) {
			mmaPane[i].changeView(view);
		}
	}

	public static void selectMMA() {
		for (int i = 0; i < mmaPane.length; i++) {
			mmaPane[i].setMMAVisible(false);
		}
		mmaPane[mmaNum].setMMAVisible(true);
		mainScrollPane.setViewportView(mmaPane[mmaNum]);
	}

	// when you need to update the CDF scroll bars to match CDF values (ie after
	// loading in new values)
	public static void updateCDFs() {
		for (int i = 0; i < mmaNames.length; i++) {
			mmaPane[i].updateCDFs(i);
		}
		
	}
	public static void repaintGraphs() {
		for (int i = 0; i < mmaNames.length; i++) {
			mmaPane[i].repaintGraphs(i);
		}
	}

	public static void revertToDefaultSettings() {
		// TODO read in defaults from somewhere

		for (int i = 0; i < MIDAS.govCDF.length; i++) {
			for (int j = 0; j < MIDAS.govCDF[0].length; j++) {
				MIDAS.govCDF[i][j] = 3;
			}
		}
		for (int i = 0; i < MIDAS.socCDF.length; i++) {
			for (int j = 0; j < MIDAS.socCDF[0].length; j++) {
				MIDAS.socCDF[i][j] = 3;
			}
		}
		for (int i = 0; i < MIDAS.ecoCDF.length; i++) {
			for (int j = 0; j < MIDAS.ecoCDF[0].length; j++) {
				MIDAS.ecoCDF[i][j] = 3;
			}
		}
		updateCDFs();

	}
	
	/**
	 * Handles requests to set CDFs and checks for issues with input
	 * 
	 * @param cdf
	 * 		The CDF to be changed
	 * @param mma
	 * 		The MMA the CDF value we're changing is for
	 * @param index
	 * 		The index of the CDF
	 * @param value
	 * 		The value of the CDF we want to change to
	 */
	public static void setCDF(int[][] cdf, int mma, int index, int value) {
		// Checking to see if value is within possible CDF values
		if (value > 0 && value <= 5) {
			cdf[mma][index] = value;
		}
	}
	
	public static int[][] getGISHealthThreat(int mma) {
		return mmaPane[mma].getGISHealthThreat();
	}
	public static void setGISHealthThreat(int mma, int[][] mmaThreat) {
		mmaPane[mma].setGISHealthThreat(mmaThreat);
	}
	
	// MIDAS Report get functions
	public static String getCDFNames(String type, int num) {
		return mmaPane[MIDAS.mmaNum].getCDFNames(type, num);
	}
	public static String getCDFValues(String type, int num) {
		return mmaPane[MIDAS.mmaNum].getCDFValues(type, num);
	}
	public static BufferedImage getCDFReportBufferedImage(String name) {
		return mmaPane[MIDAS.mmaNum].getCDFReportBufferedImage(name);
	}
	public static boolean isRiskEnabled() {
		return mmaPane[MIDAS.mmaNum].isRiskEnabled();
	}
	public static BufferedImage getGISBufferedImage(String name) {
		return mmaPane[MIDAS.mmaNum].getGISBufferedImage(name);
	}
	public static BufferedImage getGISControlBufferedImage(String name) {
		return mmaPane[MIDAS.mmaNum].getGISControlBufferedImage(name);
	}

	public static void main(String[] args) {
		// final String nativeLF = UIManager.getSystemLookAndFeelClassName();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
					if ("Nimbus".equals(laf.getName())) {
						try {
							UIManager.setLookAndFeel(laf.getClassName());
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InstantiationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UnsupportedLookAndFeelException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				createAndShowGUI();
			}
		});		
	}

	public class mmaListener implements ActionListener {

		int num;

		mmaListener(int n) {
			num = n;
		}

		public void actionPerformed(ActionEvent e) {
			selectMMA();
		}
	}
}