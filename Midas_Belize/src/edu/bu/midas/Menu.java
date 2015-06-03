/*	
 * Menu.java
 * Creates the menu bar used by MIDAS. Includes action listeners for the menu items.
 * 
 * Written by Matt Carleton and rewritten by Chris Holden
 */

package edu.bu.midas;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import java.io.IOException;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public class Menu extends JMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JMenu fileMenu, mmaMenu, viewMenu, aboutMenu, advancedMenu;
	//these contain the names of the menus and their menu items
	private String[] menuHeaders;
	private String[] fileMenuText;
	private String[] viewMenuText;
	private String[] aboutMenuText;
	//private String[] languageMenuText;
	private String exitDialog;
	private String[] exitChoices;
	private String adjustWeightsText;
	// private String eqnEditText;
	private String showExpertText;

	// mi = menu item
	private JMenuItem miSave, miLoad, miExportReport, miPrintReport, miExit; // File menu items
	private JMenuItem[] mmaMenuItems; // MMA menu items
	private JMenuItem miCDF, miSpatial; // view menu items
	private JMenuItem miHelp, miPeople, miEquations, miWelcome; // about menu items
	private JMenuItem miWeights;
	private JCheckBoxMenuItem cbmiShowExpert;

	private String[] mmaNames;

	private MenuDialogs dialog = new MenuDialogs();
	private SaveAndLoad saveAndLoad = new SaveAndLoad();

	private PropertyResourceBundle menuRes;

	// Create the menu bar
	public Menu() {		
		mmaNames = MIDAS.mmaNames;

		setLanguage();
		initMenus();
		addActionListeners();	//also sets accelerators
		addToMenuBar();
	}

	private void setLanguage() {
		menuHeaders = new String[5];
		fileMenuText = new String[6];
		viewMenuText = new String[2];
		aboutMenuText = new String[4];
		exitChoices = new String[2];

		if (MIDAS.LANGUAGE.equals("EN")) {
			menuRes = (PropertyResourceBundle) ResourceBundle.getBundle("Menu", new Locale("en"));
		}
		else if (MIDAS.LANGUAGE.equals("ES")) {
			menuRes = (PropertyResourceBundle) ResourceBundle.getBundle("Menu", new Locale("es"));
		}

		// File menu
		menuHeaders[0] = menuRes.getString("FILE");
		fileMenuText[0] = menuRes.getString("FILE_SAVE_SETTINGS");
		fileMenuText[1] = menuRes.getString("FILE_LOAD_SETTINGS");
		fileMenuText[2] = menuRes.getString("FILE_REVERT_DEF");
		fileMenuText[3] = "Print Report";
		fileMenuText[4] = "Export Report to Image";
		fileMenuText[5] = menuRes.getString("FILE_EXIT");

		// MMA Menu
		menuHeaders[1] = menuRes.getString("SELECT_MMA");

		// Switch view menu
		menuHeaders[2] = menuRes.getString("SWITCH_VIEW");
		viewMenuText[0] = menuRes.getString("VIEW_CDF");
		viewMenuText[1] = menuRes.getString("VIEW_GIS");

		// About menu
		menuHeaders[3] = menuRes.getString("ABOUT");
		aboutMenuText[0] = menuRes.getString("ABOUT_GUIDE");
		aboutMenuText[1] = menuRes.getString("ABOUT_CONTRIB");
		aboutMenuText[2] = menuRes.getString("ABOUT_TECH");
		aboutMenuText[3] = menuRes.getString("ABOUT_WELCOME");

		// Exit dialog
		exitDialog = menuRes.getString("EXIT_DIALOG");
		exitChoices[0] = menuRes.getString("CHOICE_YES");
		exitChoices[1] = menuRes.getString("CHOICE_NO");

		// Advanced menu
		menuHeaders[4] = menuRes.getString("ADVANCED");
		adjustWeightsText = menuRes.getString("ADJUST_WEIGHT");
		// eqnEditText = menuRes.getString("EQN_EDITOR");
		// TODO: Put this in localizations
		showExpertText = "Show expert opinion";
	}
	private void initMenus() { 
		// Populate the File menu
		fileMenu = new JMenu(menuHeaders[0]);
		miSave = new JMenuItem(fileMenuText[0]);
		miLoad = new JMenuItem(fileMenuText[1]);
		miPrintReport = new JMenuItem(fileMenuText[3]);
		miExportReport = new JMenuItem(fileMenuText[4]);
		miExit = new JMenuItem(fileMenuText[5]);

		// Populating the MMA menu
		mmaMenu = new JMenu(menuHeaders[1]);
		mmaMenuItems = new JMenuItem[mmaNames.length];
		for (int i = 0; i < mmaNames.length; i++) {
			mmaMenuItems[i] = new JMenuItem(mmaNames[i]);
		}

		// Populating the view menu
		viewMenu = new JMenu(menuHeaders[2]);
		miCDF = new JMenuItem(viewMenuText[0]);
		miSpatial = new JMenuItem(viewMenuText[1]);

		//Populating the About menu
		aboutMenu = new JMenu(menuHeaders[3]);
		miHelp = new JMenuItem(aboutMenuText[0]);
		miPeople = new JMenuItem(aboutMenuText[1]);
		miEquations = new JMenuItem(aboutMenuText[2]);
		miWelcome = new JMenuItem(aboutMenuText[3]);

		// Advanced menu
		advancedMenu = new JMenu(menuHeaders[4]);
		miWeights = new JMenuItem(adjustWeightsText);
		if (MIDAS.SHOW_EXPERT_OPINION == true) {
			cbmiShowExpert = new JCheckBoxMenuItem(showExpertText, true);
		}
		else {
			cbmiShowExpert = new JCheckBoxMenuItem(showExpertText, false);
		}
	}

	private void addActionListeners() {
		miSave.addActionListener(new fileListener(1));
		miLoad.addActionListener(new fileListener(2));
		miPrintReport.addActionListener(new fileListener(3));
		miExportReport.addActionListener(new fileListener(4));
		miExit.addActionListener(new fileListener(5));		

		// MMA menu
		for (int i = 0; i < mmaNames.length; i++) {
			mmaMenuItems[i].addActionListener(new mmaListener(i));
		}

		// Switch view menu
		miCDF.addActionListener(new viewListener("CDF"));
		miSpatial.addActionListener(new viewListener("GIS"));

		// About menu
		miHelp.addActionListener(new aboutListener(1));
		miPeople.addActionListener(new aboutListener(2));
		miEquations.addActionListener(new aboutListener(3));
		miWelcome.addActionListener(new aboutListener(4));		

		// Advanced menu
		miWeights.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WeightsPopup weightsPop = new WeightsPopup();
				weightsPop.createDialog();
			}	
		});
		
		cbmiShowExpert.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (cbmiShowExpert.getState() == true) {
					MIDAS.SHOW_EXPERT_OPINION = true;
				}
				else if (cbmiShowExpert.getState() == false){
					MIDAS.SHOW_EXPERT_OPINION = false;
				}
				MIDAS.savePrefs();
				MIDAS.repaintGraphs();
			}
		});
	}
	// adds menu items to their menus, and the menus
	// to the menu bar
	private void addToMenuBar() {
		fileMenu.add(miSave);
		fileMenu.add(miLoad);
		fileMenu.add(miPrintReport);
		fileMenu.add(miExportReport);
		fileMenu.addSeparator();
		fileMenu.add(miExit);

		for (int i = 0; i < mmaNames.length; i++) {
			mmaMenu.add(mmaMenuItems[i]);
		}

		viewMenu.add(miCDF);
		viewMenu.addSeparator();
		viewMenu.add(miSpatial);

		aboutMenu.add(miHelp);
		aboutMenu.add(miPeople);
		aboutMenu.add(miWelcome);

		advancedMenu.add(miWeights);
		advancedMenu.add(cbmiShowExpert);

		add(fileMenu);
		add(mmaMenu);
		add(viewMenu);
		add(aboutMenu);
		add(advancedMenu);
	}

	public class fileListener implements ActionListener {
		private int event;

		public fileListener(int e) {
			event = e;
		}

		final JFileChooser fileChooser = new JFileChooser();
		private midasFileFilter fileFilter = new midasFileFilter();

		public void actionPerformed(ActionEvent e) {
			fileChooser.setFileFilter(fileFilter);
			int openSaveCncl;
			switch (event) {
			case 1:
				openSaveCncl = fileChooser.showSaveDialog(MIDAS.frame);
				if (openSaveCncl == JFileChooser.APPROVE_OPTION) {
					
					String fileName = fileChooser.getSelectedFile().toString();
					
					// Appending .midas if necessary
					if (fileName.substring(fileName.indexOf(".") + 1).equals("midas")) {
						
					}
					else {
						fileName = fileName + ".midas";
					}
					
					try {
						saveAndLoad.writeCSV(fileName);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
				break;

			case 2: 
				openSaveCncl = fileChooser.showOpenDialog(MIDAS.frame);
				if (openSaveCncl == JFileChooser.APPROVE_OPTION) {
					String fileName = fileChooser.getSelectedFile().toString();

					try {
						saveAndLoad.readCSV(fileName);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				break;
				
			case 3:
				MIDASReport midasReport = new MIDASReport();
				midasReport.showReport();
				
				midasReport = null;
				
				break;
			case 4:
				ReportImageOutput reportImageOutput = new ReportImageOutput();
				reportImageOutput.showReport();
				
				break;
				
			case 5: 	
				if (JOptionPane.showOptionDialog(new JFrame(), exitDialog, "", 
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, 
						null, exitChoices, exitChoices[0]) == JOptionPane.YES_OPTION)
					System.exit(0);
				break;	

			default: 
				System.out.println("Invalid menu option");
				break;
			}
		}
	}

	public class midasFileFilter extends FileFilter {

		@Override
		public boolean accept(File f) {
			if(f.isDirectory()){
				return true;
			}
			
			// Getting what user input for extension
			String extension = null;
			String s = f.getName();
			int i = s.lastIndexOf('.');

			if (i > 0 &&  i < s.length() - 1) {
				extension = s.substring(i+1).toLowerCase();
			}
			
			if (extension != null) {
				if (extension.equals("midas")) {
					return true;
				}
			} else {
				return false;
			}
			return false;
		}

		@Override
		public String getDescription() {

			return "*.midas";
		}

	}
	public class mmaListener implements ActionListener {

		int num;

		public mmaListener(int n) {
			num = n;
		}
		public void actionPerformed(ActionEvent e) {
			MIDAS.mmaNum = num;
			MIDAS.selectMMA();
		}
	} 
	public class viewListener implements ActionListener {

		String view;

		public viewListener(String v) {
			view = v;
		}
		public void actionPerformed(ActionEvent e) {
			MIDAS.changeView(view);
		}
	}
	public class aboutListener implements ActionListener {

		int num;

		public aboutListener(int n) {
			num = n;
		}
		public void actionPerformed(ActionEvent e) 
		{
			switch (num) {
			case 1:  
				dialog.showMessage(dialog.HELP);
				break;

			case 2:  
				dialog.showMessage(dialog.CONTRIBUTORS);
				break;

			case 3:  
				dialog.showMessage(dialog.EQUATIONS);
				break;

			case 4:  
				WelcomeMenu welcome = new WelcomeMenu();
				welcome.showDialogBox();
				break;
			default: 
				System.out.println("Invalid menu option");
				break;
			}
		}	
	} 	
}
