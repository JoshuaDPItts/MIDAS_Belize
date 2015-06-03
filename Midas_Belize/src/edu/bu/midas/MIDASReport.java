package edu.bu.midas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class MIDASReport {

	// Main GUI panel & drawing panel
	private JDialog dialog;
	private JPanel mainPane;
	private JPanel guiPane;
	private JScrollPane drawingScrollPane;
	private MIDASReportPanel drawingPane;

	// Storage of name/organization
	private String userName;
	private String userOrg;
	private JTextField nameTF;
	private JTextField orgTF;

	// Print option button boolean values
	private boolean showCDFTexts;
	private boolean[] showCDFReports = new boolean[5];
	private boolean[] showGISReports = new boolean[4];

	// Localization strings
	private String title;
	private String nameText;
	private String orgText;
	// CDF selection texts
	private String instructionText;
	private String cdfLabelText;
	private String showCDFText;
	private String[] showOutcomeText = new String[5];
	// GIS selection texts
	private String gisInstructionText;
	private String[] showSpatialText = new String[4];
	// Show report button
	private String showReportText;

	public MIDASReport() { 
		setLanguage();
		createDialogBox();
	}

	public void showReport() {
		dialog.setVisible(true);
	}

	public void setLanguage() { 
		// Localization 
		title = "MIDAS Report";
		nameText = "Name: ";
		orgText = "Organization: ";
		// Basic instructions
		instructionText = "Select a report to print.";
		// CDF selection texts
		cdfLabelText = "CDF Outcomes";
		showCDFText = "Print CDF values?";
		showOutcomeText[0] = "Print Governance Index outcome?";
		showOutcomeText[1] = "Print Socioeconomic Index outcome?";
		showOutcomeText[2] = "Print Ecological Index outcome?";
		showOutcomeText[3] = "Print CDF Comparison graph?";
		showOutcomeText[4] = "Print MMA Effectiveness outcome?";
		// GIS selection texts
		gisInstructionText = "Spatial Reports";
		showSpatialText[0] = "Show GIS layers?";
		showSpatialText[1] = "Show risk model results?";
		showSpatialText[2] = "Show mangrove model results?";
		showSpatialText[3] = "Show oil model results?";	
		// Button
		showReportText = "Show report.";
	}

	public void createDialogBox() { 
		dialog = new JDialog(MIDAS.frame, title);
		mainPane = new JPanel();
		// mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));

		createGUIPane();
		drawingPane = new MIDASReportPanel();
		drawingScrollPane = new JScrollPane();
		drawingScrollPane.getViewport().add(drawingPane);

		mainPane.add(guiPane);
		mainPane.add(drawingScrollPane);

		guiPane.setVisible(true);
		drawingScrollPane.setVisible(false);

		dialog.setContentPane(mainPane);
		dialog.setSize(new Dimension(400,400));
		dialog.setLocationRelativeTo(MIDAS.frame);
		dialog.setVisible(true);
	}

	public void createGUIPane() {
		guiPane = new JPanel();
		guiPane.setLayout(new BoxLayout(guiPane, BoxLayout.Y_AXIS));

		addHeading();
		addCDFSelection();
		addGISSelection();

		JButton showReportButton = new JButton(showReportText);
		showReportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Grabbing info from user/org input
				userName = nameTF.getText();
				userOrg = orgTF.getText();
				drawingPane.setUserName(userName);
				drawingPane.setUserOrg(userOrg);

				// Changing screens
				guiPane.setVisible(false);
				drawingScrollPane.setVisible(true);

				// Drawing 
				drawingPane.setCDFValues();
				drawingPane.setShowCDFValues(showCDFTexts);
				drawSelectedReports();
				dialog.setSize(MIDAS.OUTCOMERES);
				mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
				drawingScrollPane.validate();

				//PrintUtilities P1 = new PrintUtilities(drawingPane);
				//P1.print();
				// drawingPane.printReport();
				printReport();
			}
		});
		guiPane.add(showReportButton);
	}
	public void printReport() {
		PrinterJob printJob = PrinterJob.getPrinterJob();

		printJob.setPrintable(drawingPane);

		if (printJob.printDialog()) {
			try {
				printJob.print();
			} catch (Exception PrintException) {
				PrintException.printStackTrace();
			}
		}
	}
	public void addHeading() {
		JLabel nameLabel = new JLabel(nameText);
		nameTF = new JTextField(20);
		JLabel orgLabel = new JLabel(orgText);
		orgTF = new JTextField(20);

		guiPane.add(nameLabel);
		guiPane.add(nameTF);
		guiPane.add(orgLabel);
		guiPane.add(orgTF);

		guiPane.add(new JLabel(instructionText));
		
		guiPane.add(Box.createVerticalStrut(10));

	}
	public void addCDFSelection() {		
		guiPane.add(new JLabel(cdfLabelText));

		JCheckBox showCDFCheckBox = new JCheckBox(showCDFText);
		showCDFCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (showCDFTexts == true) {
					showCDFTexts = false;
				}
				else if (showCDFTexts == false) {
					/*
					for (int i = 0; i < showCDFReports.length; i++) {
						showCDFReports[i] = false;
					}
					for (int i = 0; i < showGISReports.length; i++) {
						showGISReports[i] = false;
					}
					 */
					showCDFTexts = true;
				}
			}
		});
		showCDFCheckBox.setSelected(showCDFTexts);

		guiPane.add(showCDFCheckBox);

		for (int i = 0; i < showOutcomeText.length; i++) {
			final int num = i;
			JCheckBox showOutcomesCheckBox = new JCheckBox(showOutcomeText[i]);
			showOutcomesCheckBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (showCDFReports[num] == true) {
						showCDFReports[num] = false;
					}
					else if (showCDFReports[num] == false) {
						showCDFReports[num] = true;
					}
				}
			});
			showOutcomesCheckBox.setSelected(showCDFReports[num]);
			System.out.println("Showing outcome?: " + showCDFReports[num]);

			guiPane.add(showOutcomesCheckBox);
		}
		guiPane.add(Box.createVerticalStrut(10));
	}
	public void addGISSelection() {
		guiPane.add(new JLabel(gisInstructionText));

		for (int i = 0; i < showSpatialText.length; i++) {
			final int num = i;
			JCheckBox showSpatialCheckBox = new JCheckBox(showSpatialText[i]);
			showSpatialCheckBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (showGISReports[num] == true) {
						showGISReports[num] = false;
					}
					else if (showGISReports[num] == false) {
						showGISReports[num] = true;
					}
				}
			});
			showSpatialCheckBox.setSelected(showGISReports[num]);
			System.out.println("Showing outcome?: " + showGISReports[num]);

			// Only add risk button if risk is enabled
			if (i == 1) {
				if (MIDAS.isRiskEnabled() == true) {
					guiPane.add(showSpatialCheckBox);
				}
			}
			// If we're not adding the risk button, add as normal
			else {
				guiPane.add(showSpatialCheckBox);
			}		
		}
		guiPane.add(Box.createVerticalStrut(10));
	}
	public void drawSelectedReports() {
		drawingPane.clearCDFReports();
		drawingPane.clearGISReports();

		if (showCDFReports[0] == true) {
			drawingPane.setCDFImage(MIDAS.getCDFReportBufferedImage("Gov"), 0);
		}
		if (showCDFReports[1] == true) {
			drawingPane.setCDFImage(MIDAS.getCDFReportBufferedImage("Soc"), 1);
		}
		if (showCDFReports[2] == true) {
			drawingPane.setCDFImage(MIDAS.getCDFReportBufferedImage("Eco"), 2);
		}
		if (showCDFReports[3] == true) {
			drawingPane.setCDFImage(MIDAS.getCDFReportBufferedImage("Com"), 3);
		}
		if (showCDFReports[4] == true) {
			drawingPane.setCDFImage(MIDAS.getCDFReportBufferedImage("Tri"), 4);
		}
		if (showGISReports[0] == true) {
			BufferedImage gisImage = MIDAS.getGISBufferedImage("Layer");
			BufferedImage controlImage = MIDAS.getGISControlBufferedImage("Layer");

			drawingPane.setGISImage(combineImages(controlImage, gisImage), 0);
		}
		if (showGISReports[1] == true) {
			BufferedImage gisImage = MIDAS.getGISBufferedImage("Risk");
			BufferedImage controlImage = MIDAS.getGISControlBufferedImage("Risk");

			drawingPane.setGISImage(combineImages(controlImage, gisImage), 1);
		}
		if (showGISReports[2] == true) {
			BufferedImage gisImage = MIDAS.getGISBufferedImage("Mangrove");
			BufferedImage controlImage = MIDAS.getGISControlBufferedImage("Mangrove");

			drawingPane.setGISImage(combineImages(controlImage, gisImage), 2);
		}
		if (showGISReports[3] == true) {
			BufferedImage gisImage = MIDAS.getGISBufferedImage("Oil");
			BufferedImage controlImage = MIDAS.getGISControlBufferedImage("Oil");

			drawingPane.setGISImage(combineImages(controlImage, gisImage), 3);
		}

		drawingPane.repaint();
	}
	public BufferedImage combineImages(BufferedImage bi1, BufferedImage bi2) {
		BufferedImage output = new BufferedImage(bi1.getWidth() + bi2.getWidth(), 
				bi1.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = output.createGraphics();
		g2d.drawImage(bi1, 0, 0, null);
		g2d.drawImage(bi2, bi1.getWidth(), 0, null);

		return output;
	}
	private class MIDASReportPanel extends JPanel implements Printable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		// Localizations
		private String userNameText;
		private String userOrgText;
		private String dateText;

		private String userName = null;
		private String userOrg = null;

		// Length of string is number of CDFs (6) * 3 types = 18
		private boolean drawCDFNamesValues = false;
		private String[] userCDFNames = new String[MIDAS.govCDF[MIDAS.mmaNum].length * 3];
		private String[] userCDFValues = new String[MIDAS.govCDF[MIDAS.mmaNum].length * 3];

		private BufferedImage[] cdfReports = new BufferedImage[5];
		private BufferedImage[] gisReports = new BufferedImage[4];

		private int cdfCount;
		private int gisCount;

		public MIDASReportPanel() {
			setLanguage();
		}
		public void setLanguage() {
			userNameText = "Name: ";
			userOrgText = "Organization: ";
			dateText = "Date: ";
		}	
		public void setUserName(String name) {
			userName = name;
		}
		public void setUserOrg(String org) {
			userOrg = org;
		}
		public String getDateTime() {
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm a");
			Date date = new Date();
			return dateFormat.format(date);
		}
		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			super.paintComponent(g);

			// Resizing report window
			resizeReport();

			// Painting background white
			g2d.setColor(Color.WHITE);
			g2d.fill(new Rectangle(0, 0, this.getWidth(), this.getHeight()));
			g2d.setColor(Color.BLACK);

			// Drawing header (username, organization, date)
			int offsetY = drawHeader(g2d, this.getWidth());

			// Moving graphics below header
			g2d.translate(0, offsetY);

			// Drawing CDF values & names
			offsetY = drawCDFValues(g2d, this.getWidth());

			g2d.translate(0, offsetY);			

			// Drawing outcomes & GIS			
			offsetY = drawCDFReports(g2d);
			g2d.translate(0, offsetY);

			offsetY = drawGISReports(g2d);
		}
		public int drawHeader(Graphics2D g2d, int width) {			
			int headerSpacing = 0;

			Font font = new Font("Helvectica", Font.PLAIN, 12);
			g2d.setFont(font);
			FontMetrics metrics = g2d.getFontMetrics(font);
			int lineHeight = metrics.getHeight();

			// Drawing user name
			if (userName.equals("") == false) {
				g2d.drawString(userNameText + userName, 5, lineHeight);
				headerSpacing = headerSpacing + lineHeight;
			}
			// Drawing user organization
			if (userOrg.equals("") == false) {
				g2d.drawString(userOrgText + userOrg, 5, lineHeight * 2);
				headerSpacing = headerSpacing + lineHeight;
			}

			// Drawing date
			// First we get bounds of date string (this function uses a Rectangle2D as bounds)
			Rectangle2D dateBounds = metrics.getStringBounds(dateText + getDateTime(), g2d);
			int dateWidth = (int) dateBounds.getWidth();

			g2d.drawString(dateText + getDateTime(), width - dateWidth, lineHeight);
			headerSpacing = headerSpacing + lineHeight;

			// Buffering bottom section a bit
			headerSpacing = headerSpacing + 5;

			// Return the space taken by this painting to paintComponent function
			return headerSpacing;
		}
		public int drawCDFValues(Graphics2D g2d, int width) {
			if (drawCDFNamesValues == true) {
				Font font = new Font("Helvectica", Font.PLAIN, 12);
				g2d.setFont(font);
				FontMetrics metrics = g2d.getFontMetrics(font);
				int lineHeight = metrics.getHeight() + 2;

				// Getting maximum needed size to draw CDF names
				int leftBounds = (int) metrics.getStringBounds(userCDFNames[0], g2d).getWidth();
				for (int i = 0; i < userCDFNames.length; i++) {
					int leftBoundsTemp = (int) metrics.getStringBounds(userCDFNames[i], g2d).getWidth();
					if (leftBoundsTemp > leftBounds) {
						leftBounds = leftBoundsTemp;
					}
				}
				// Getting maximum needed size to draw CDF names
				int rightBounds = (int) metrics.getStringBounds(userCDFValues[0], g2d).getWidth();
				for (int i = 0; i < userCDFValues.length; i++) {
					int rightBoundsTemp = (int) metrics.getStringBounds(userCDFValues[i], g2d).getWidth();
					if (rightBoundsTemp > rightBounds) {
						rightBounds = rightBoundsTemp;
					}
				}

				// Finding X value to center list
				int xStart = (width / 2) - ((leftBounds + rightBounds) / 2);
				// Adding in spacing in X of 5 pixels for words
				int pixelXSpacing = 5;
				rightBounds = rightBounds + pixelXSpacing * 2;
				leftBounds = leftBounds + pixelXSpacing * 2;

				for (int i = 0; i < userCDFNames.length; i++) {
					g2d.setColor(Color.black);
					// Drawing left rectangle outlines

					g2d.draw(new Rectangle(xStart, lineHeight * i, 
							leftBounds, lineHeight));
					// Drawing right rectangle outlines
					g2d.draw(new Rectangle(xStart + leftBounds, lineHeight * i, 
							rightBounds, lineHeight));

					// Drawing strings
					if (i < 6) {
						g2d.setColor(new Color(102, 0, 204));
					}
					else if (i >= 6 && i < 12) {
						g2d.setColor(new Color(255, 0, 204));
					}
					else if (i >= 12 && i < 18) {
						g2d.setColor(new Color(51, 153, 255));
					}
					g2d.drawString(userCDFNames[i], xStart + pixelXSpacing, lineHeight * (i + 1) - 2);
					g2d.setColor(Color.BLACK);
					g2d.drawString(userCDFValues[i], xStart + leftBounds + pixelXSpacing, lineHeight * (i + 1) - 2);
				}

				// Adding a buffer to the offset we're returning
				int offsetY = lineHeight * userCDFNames.length + 5;

				return offsetY;
			}
			else {				
				return 0;
			}
		}
		public int drawCDFReports(Graphics2D g2d) {
			int offsetY = 0;

			for (int i = 0; i < cdfReports.length; i++) {
				if (cdfReports[i] != null) {
					g2d.drawImage(cdfReports[i], (this.getWidth() / 2) - (cdfReports[i].getWidth() / 2), 
							offsetY, this);
					offsetY = offsetY + cdfReports[i].getHeight() + 5;
				}
			}

			return offsetY;
		}
		public int drawGISReports(Graphics2D g2d) {
			int offsetY = 0;
			for (int i = 0; i < gisReports.length; i++) {
				if (gisReports[i] != null) {
					g2d.drawImage(gisReports[i], (this.getWidth() / 2) - (gisReports[i].getWidth() / 2), 
							offsetY, this);
					offsetY = offsetY + gisReports[i].getHeight() + 5;
				}
			}

			return offsetY;
		}
		public BufferedImage resizeImage(BufferedImage image, double maxWidth, double maxHeight) {
			double factorX = maxWidth / image.getWidth();
			double factorY = maxHeight / image.getHeight();
			double scaleFactor = Math.min(factorX, factorY);

			int width = (int) (image.getWidth() * scaleFactor);
			int height = (int) (image.getHeight() * scaleFactor);

			// Create new (blank) image of required (scaled) size
			BufferedImage scaledImage = new BufferedImage(
					width, height, BufferedImage.TYPE_INT_RGB);

			// Paint scaled version of image to new image
			Graphics2D graphics2D = scaledImage.createGraphics();
			graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			graphics2D.drawImage(image, 0, 0, width, height, null);

			// clean up
			graphics2D.dispose();

			return scaledImage;
		}
		public void resizeReport() {
			int[] sizeXY = new int[2];
			sizeXY[0] = MIDAS.OUTCOMERES.width;
			sizeXY[1] = MIDAS.OUTCOMERES.height;

			for (int i = 0; i < cdfReports.length; i++) {
				if (cdfReports[i] != null) {
					if (cdfReports[i].getWidth() > sizeXY[0]) {
						sizeXY[0] = cdfReports[i].getWidth();
					}
					sizeXY[1] = sizeXY[1] + cdfReports[i].getHeight();
				}
			}
			for (int i = 0; i < gisReports.length; i++) {
				if (gisReports[i] != null) {
					if (gisReports[i].getWidth() > sizeXY[0]) {
						sizeXY[0] = gisReports[i].getWidth();
					}
					sizeXY[1] = sizeXY[1] + gisReports[i].getHeight();
				}
			}

			setSize(new Dimension(sizeXY[0], sizeXY[1]));
			setPreferredSize(new Dimension(sizeXY[0], sizeXY[1]));
		}
		public void setShowCDFValues(boolean showCDFTxts) {
			drawCDFNamesValues = showCDFTxts;
		}
		public void setCDFValues() {
			for (int i = 0; i < 6; i++) {
				// Governance
				userCDFNames[i] = MIDAS.getCDFNames("Gov", i);
				userCDFValues[i] = MIDAS.getCDFValues("Gov", i);
				// Socioeconomic
				userCDFNames[i + 6] = MIDAS.getCDFNames("Soc", i);
				userCDFValues[i + 6] = MIDAS.getCDFValues("Soc", i);
				// Ecological
				userCDFNames[i + 12] = MIDAS.getCDFNames("Eco", i);
				userCDFValues[i + 12] = MIDAS.getCDFValues("Eco", i);
			}
		}
		public void setCDFImage(BufferedImage bi, int num) {
			cdfReports[num] = bi;
		}
		public void clearCDFReports() {
			for (int i = 0; i < cdfReports.length; i++) {
				cdfReports[i] = null;
			}
		}
		public void setGISImage(BufferedImage bi, int num) {
			gisReports[num] = bi;
		}
		public void clearGISReports() {
			for (int i = 0; i < gisReports.length; i++) {
				gisReports[i] = null;
			}
		}
		@Override
		public int print(Graphics g, PageFormat pageFormat, int pageIndex)
		throws PrinterException {
			Graphics2D g2d = (Graphics2D) g;
			g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

			double pageWidth = pageFormat.getImageableWidth();
			double pageHeight = pageFormat.getImageableHeight();

			// Painting background white
			g2d.setColor(Color.WHITE);
			g2d.fill(new Rectangle(0, 0, this.getWidth(), this.getHeight()));
			g2d.setColor(Color.BLACK);

			// Drawing header (username, organization, date)
			int offsetY = drawHeader(g2d, (int) pageFormat.getImageableWidth());

			// Translating below header
			g2d.translate(0, offsetY);

			// Adding images to one array
			BufferedImage[] reports = new BufferedImage[getImageCount()];
			int imgCount = 0;
			for (int i = 0; i < cdfReports.length; i++) {
				if (cdfReports[i] != null) {
					reports[imgCount] = resizeImage(cdfReports[i], 
							pageWidth, pageHeight);
					imgCount++;
				}
			}
			for (int i = 0; i < gisReports.length; i++) {
				if (gisReports[i] != null) {
					reports[imgCount] = resizeImage(gisReports[i], 
							pageWidth, pageHeight);
					imgCount++;
				}
			}	

			// Printing
			int pageElements = reports.length;
			if (drawCDFNamesValues == true) {
				pageElements++;
			}
			if (pageIndex < pageElements) {
				if (pageIndex == 0 && drawCDFNamesValues == true) {
					drawCDFValues(g2d, (int) pageWidth);
				}
				else {
					// If we already drew an element (the CDF values), we change index we pass
					if (drawCDFNamesValues == true) {
						// If there's more than 1 image left to print, we print both on same page
						g2d.drawImage(reports[pageIndex - 1], 
								(int) ((pageWidth / 2) - (reports[pageIndex - 1].getWidth() / 2)), 0, null);
					}
					else {
						g2d.drawImage(reports[pageIndex], 
								(int) ((pageWidth / 2) - (reports[pageIndex].getWidth() / 2)), 0, null);
					}

				}
				return PAGE_EXISTS;
			} else {
				return NO_SUCH_PAGE;
			}
		}
		public int getImageCount() {
			int imgCount = 0;
			for (int i = 0; i < cdfReports.length; i++) {
				if (cdfReports[i] != null) {
					imgCount++;
				}
			}
			for (int i = 0; i < gisReports.length; i++) {
				if (gisReports[i] != null) {
					imgCount++;
				}
			}

			return imgCount;
		}
	}
}