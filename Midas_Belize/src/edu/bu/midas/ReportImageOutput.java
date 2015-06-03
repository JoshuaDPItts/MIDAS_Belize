package edu.bu.midas;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.filechooser.FileFilter;

public class ReportImageOutput {

	// Main GUI panel
	private JDialog dialog;
	private JPanel mainPane;
	private JPanel guiPane;

	private ButtonGroup selectionGroup;

	// Print option button boolean values
	private boolean[] showCDFReports = new boolean[5];
	private boolean[] showGISReports = new boolean[4];

	// Localization strings
	private String title;

	// CDF selection texts
	private String instructionText;
	private String cdfLabelText;
	private String[] showOutcomeText = new String[5];
	// GIS selection texts
	private String gisInstructionText;
	private String[] showSpatialText = new String[4];
	// Show report button
	private String showReportText;

	public ReportImageOutput() {
		setLanguage();
		createDialogBox();
	}

	public void showReport() {
		dialog.setVisible(true);
	}

	public void setLanguage() { 
		// Localization 
		title = "MIDAS Report";
		// Basic instructions
		instructionText = "Select one report to save.";
		// CDF selection texts
		cdfLabelText = "CDF Outcomes";
		showOutcomeText[0] = "Governance Index outcome";
		showOutcomeText[1] = "Socioeconomic Index outcome";
		showOutcomeText[2] = "Ecological Index outcome";
		showOutcomeText[3] = "CDF Comparison graph";
		showOutcomeText[4] = "MMA Effectiveness outcome";
		// GIS selection texts
		gisInstructionText = "Spatial Reports";
		showSpatialText[0] = "GIS layers";
		showSpatialText[1] = "Risk model results";
		showSpatialText[2] = "Mangrove model results";
		showSpatialText[3] = "Oil model results";	
		// Button
		showReportText = "Save report as image.";
	}

	public void createDialogBox() { 
		dialog = new JDialog(MIDAS.frame, title);
		mainPane = new JPanel();

		createGUIPane();

		mainPane.add(guiPane);

		dialog.setContentPane(mainPane);
		dialog.setSize(new Dimension(250, 300));
		dialog.setLocationRelativeTo(MIDAS.frame);
	}

	public void createGUIPane() {
		guiPane = new JPanel();
		guiPane.setLayout(new BoxLayout(guiPane, BoxLayout.Y_AXIS));

		selectionGroup = new ButtonGroup();

		addHeading();
		addCDFSelection();
		addGISSelection();

		JButton showReportButton = new JButton(showReportText);
		showReportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Saving");
				saveReport();
			}
		});
		guiPane.add(showReportButton);
	}

	public void addHeading() {
		guiPane.add(new JLabel(instructionText));
		guiPane.add(Box.createVerticalStrut(10));
	}

	public void addCDFSelection() {		
		guiPane.add(new JLabel(cdfLabelText));

		for (int i = 0; i < showOutcomeText.length; i++) {
			final int num = i;
			JRadioButton showOutcomesRadioButton = new JRadioButton(showOutcomeText[i]);
			showOutcomesRadioButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (showCDFReports[num] == true) {
						showCDFReports[num] = false;
					}
					else if (showCDFReports[num] == false) {

						for (int i = 0; i < showCDFReports.length; i++) {
							showCDFReports[i] = false;
						}
						for (int i = 0; i < showGISReports.length; i++) {
							showGISReports[i] = false;
						}
						showCDFReports[num] = true;
					}
				}
			});
			showOutcomesRadioButton.setSelected(showCDFReports[num]);
			System.out.println("Showing outcome?: " + showCDFReports[num]);

			guiPane.add(showOutcomesRadioButton);
			
			selectionGroup.add(showOutcomesRadioButton);
		}
		guiPane.add(Box.createVerticalStrut(10));
	}
	public void addGISSelection() {
		guiPane.add(new JLabel(gisInstructionText));

		for (int i = 0; i < showSpatialText.length; i++) {
			final int num = i;
			JRadioButton showSpatialRadioButton = new JRadioButton(showSpatialText[i]);
			showSpatialRadioButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (showGISReports[num] == true) {
						showGISReports[num] = false;
					}
					else if (showGISReports[num] == false) {

						for (int i = 0; i < showCDFReports.length; i++) {
							showCDFReports[i] = false;
						}
						for (int i = 0; i < showGISReports.length; i++) {
							showGISReports[i] = false;
						}
						showGISReports[num] = true;
					}
				}
			});
			showSpatialRadioButton.setSelected(showGISReports[num]);
			System.out.println("Showing outcome?: " + showGISReports[num]);

			// Only add risk button if risk is enabled
			if (i == 1) {
				if (MIDAS.isRiskEnabled() == true) {
					guiPane.add(showSpatialRadioButton);
				}
			}
			// If we're not adding the risk button, add as normal
			else {
				guiPane.add(showSpatialRadioButton);
			}
			selectionGroup.add(showSpatialRadioButton);
		}
		guiPane.add(Box.createVerticalStrut(10));
	}

	public void saveReport() {
		BufferedImage bi = getSelectedReport();

		JFileChooser imageFC = new JFileChooser();		
		
		// Adding selectable filters
		imageFC.addChoosableFileFilter(new ImageFilter("jpeg"));
		imageFC.addChoosableFileFilter(new ImageFilter("png"));
		imageFC.addChoosableFileFilter(new ImageFilter("gif"));
		imageFC.setFileFilter(new ImageFilter("jpg"));		
		
		if (imageFC.showSaveDialog(MIDAS.frame) == JFileChooser.APPROVE_OPTION) {

			File imageFile = imageFC.getSelectedFile();
			
			// Getting file extension selected
			String fileExtension = getFileExtension(imageFC.getFileFilter().getDescription());
			// If user selected All Files (e.g. fileExtension == null), default to JPG!
			if (fileExtension == null) {
				fileExtension = "jpg";
			}
			
			// Checking to make sure the imageFile File has the extension put in by user
			if (getFileExtension(imageFile) == null) {
				// If not, add it
				imageFile = new File(imageFile.toString() + "." + fileExtension);
			}
			
			System.out.println("Filename: " + imageFile.getName());
			System.out.println("File extension: " + fileExtension);

			try {
				ImageIO.write(bi, fileExtension, imageFile);
				System.out.println(imageFile.getAbsolutePath());

			} catch (IOException e) {

			}
			
		}
	}
	public class ImageFilter extends FileFilter {

		private String fileExtension;
		
		public ImageFilter(String fileEx) {
			fileExtension = fileEx;
		}
		
		public boolean accept(File f) {
	        if (f.isDirectory()) {
	            return true;
	        }
			
			String extension = getFileExtension(f);
			
			if (extension != null) {
				if (extension.equals(fileExtension)) {
					return true;
				} else {
					return false;
				}
			}

			return false;
		}

		//The description of this filter
		public String getDescription() {
			return "*." + fileExtension;
		}
	}
	
	/**
	 * Parses name of File f to get extension
	 * @param f File selected by user
	 * @return Extension of file (can be null)
	 */
	public String getFileExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 &&  i < s.length() - 1) {
			ext = s.substring(i+1).toLowerCase();
		}
		return ext;
	}
	/**
	 * Parses name of File f to get extension
	 * @param f String name of File selected by user
	 * @return Extension of file (can be null)
	 */
	public String getFileExtension(String f) {
		String ext = null;
		int i = f.lastIndexOf('.');

		if (i > 0 &&  i < f.length() - 1) {
			ext = f.substring(i+1).toLowerCase();
		}
		return ext;
	}

	/**
	 * Checks to see which report we selected, and returns appropriate BufferedImage
	 * @return BufferedImage to print
	 */
	public BufferedImage getSelectedReport() {
		BufferedImage bi = null;

		if (showCDFReports[0] == true) {
			bi = MIDAS.getCDFReportBufferedImage("Gov");
		}
		if (showCDFReports[1] == true) {
			bi = MIDAS.getCDFReportBufferedImage("Soc");
		}
		if (showCDFReports[2] == true) {
			bi = MIDAS.getCDFReportBufferedImage("Eco");
		}
		if (showCDFReports[3] == true) {
			bi = MIDAS.getCDFReportBufferedImage("Com");
		}
		if (showCDFReports[4] == true) {
			bi = MIDAS.getCDFReportBufferedImage("Tri");
		}
		if (showGISReports[0] == true) {
			BufferedImage gisImage = MIDAS.getGISBufferedImage("Layer");
			BufferedImage controlImage = MIDAS.getGISControlBufferedImage("Layer");

			bi = combineImages(controlImage, gisImage);
		}
		if (showGISReports[1] == true) {
			BufferedImage gisImage = MIDAS.getGISBufferedImage("Risk");
			BufferedImage controlImage = MIDAS.getGISControlBufferedImage("Risk");

			bi= combineImages(controlImage, gisImage);
		}
		if (showGISReports[2] == true) {
			BufferedImage gisImage = MIDAS.getGISBufferedImage("Mangrove");
			BufferedImage controlImage = MIDAS.getGISControlBufferedImage("Mangrove");

			bi = combineImages(controlImage, gisImage);
		}
		if (showGISReports[3] == true) {
			BufferedImage gisImage = MIDAS.getGISBufferedImage("Oil");
			BufferedImage controlImage = MIDAS.getGISControlBufferedImage("Oil");

			bi = combineImages(controlImage, gisImage);
		}

		return bi;
	}
	public BufferedImage combineImages(BufferedImage bi1, BufferedImage bi2) {
		BufferedImage output = new BufferedImage(bi1.getWidth() + bi2.getWidth(), 
				bi1.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = output.createGraphics();
		g2d.drawImage(bi1, 0, 0, null);
		g2d.drawImage(bi2, bi1.getWidth(), 0, null);

		return output;
	}
}
