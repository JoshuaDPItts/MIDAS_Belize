package edu.bu.midas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

import au.com.bytecode.opencsv.CSVReader;


public class GISPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MapPanel mapPane;
	private JTabbedPane tabPane;

	private int layerPaneIndex;
	private int riskPaneIndex;
	private int oilPaneIndex;
	private int mangrovePaneIndex;

	private JPanel layerPane;
	private JPanel riskPane;
	private JPanel oilPane;
	private MangrovePanel mangrovePane;
	private MidasCalculator midasCalc;
	private OilModel oilModel;

	private String configFile;

	// Tab texts
	private String layerPaneText;
	private String riskPaneText;
	private String oilPaneText;
	private String mangrovePaneText;

	// Layer tab texts
	private String layerText;
	private String onText;
	private String offText;

	// Layer tab text - map info text
	private String showLandLayerText;
	private String showRemoteSensingText;
	private String showArrowText;
	private String showScaleText;
	private String showLongLatText;

	// Risk text
	private String enableRiskText;
	private String healthText;
	private String[] healthAmountText = new String[4];
	private String threatText;
	private String[] threatAmountText = new String[4];
	private String noRiskText;
	private String showRiskText;
	private String hideRiskText;

	private int cellNum;
	private String backgroundFile;
	private String landLayerFile;
	private Color landLayerColor;
	private String[] layerNames;
	private String[] layerFiles;
	private Color[] layerColors;
	private boolean[] layersVisible;

	private int[] threatLayers;
	private int[][] mmaThreat;
	private boolean[] makeGrid;
	private boolean[][] threatHabitatAtCell;

	private int riskCellSelected;
	private int maxThreat = 4;
	private int minThreat = 1;

	private JPanel[] layerGroups;
	private JCheckBox[] layerButton;
	
	private JCheckBox enableRiskCheckBox;
	private JPanel[] threatGroups;
	private ScrollBar[] threatBars;
	
	double[] mangroveStats = new double[16];
	private String[] mangroveLayerFiles;
	private int mangroveLayerIndex;

	// Oil model:
	private String oilLandFile;
	private String[] oilZWFiles = new String[12];
	private String[] oilMWFiles = new String[12];
	private String[] oilZCFiles = new String[12];
	private String[] oilMCFiles = new String[12];

	private double oilDensity; // User input oil density
	private int oilVolume; // User input oil volume
	private int maxTime; // Max time model can run - set to 300 min (6 hour)
	private int monthSelected; // User selected
	private boolean calcedSpill; // Set on/off if user uses model
	private double[] spillXY = new double[2]; // x/y of spill (m since we're using UTM)
	
	private String calculatingModelText;
	private String calculatingModelTitle;

	// Localization information for oil model buttons/labels/etc
	private String oilSelectMonth;
	private String[] months = new String[12];
	private String selectType;
	private String densityTypes;
	private String selectVolume;
	private String timeSinceSpill;
	private String areaSpill;
	private JLabel oilMonthLabel;
	private ScrollBar monthScrollBar;
	private JLabel APIdensityLabel;
	private ScrollBar densityBar;
	private JLabel timeSinceSpillLabel;
	private ScrollBar timeStepBar;
	private JLabel volumeLabel;
	private JLabel volumeAmountLabel;
	private ScrollBar volumeBar;
	private JLabel spillAreaLabel;

	public GISPanel(String cfgFile) {
		configFile = cfgFile;

		getLanguage();
		getGISInfo();

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setBackground(MIDAS.BACKGROUNDCOLOR);

		mapPane = new MapPanel(cellNum, backgroundFile, landLayerFile,
				landLayerColor, layerFiles, layerColors, layersVisible,
				makeGrid, mangroveLayerFiles, mangroveLayerIndex);
		riskCellSelected = mapPane.getRiskCellSelected();
		System.out.println("Default risk cell selected: " + riskCellSelected);

		midasCalc = new MidasCalculator();

		add(tabPanel());
		add(mapPane);

		mapPane.addMouseMotionListener(new CursorLocationListener());
		mapPane.addMouseListener(new CellMouseListener());
		mapPane.addMouseListener(new OilModelListener());

		MangroveSelectListener mangroveListener = new MangroveSelectListener();
		mapPane.addMouseListener(mangroveListener);
		mapPane.addMouseMotionListener(mangroveListener);
	}

	public void getLanguage() {
		// Tab texts
		layerPaneText = "Layers";
		riskPaneText = "Risk";
		oilPaneText = "Oil";
		mangrovePaneText = "Mangrove";

		// Layer tab text
		layerText = "Layer: ";
		onText = "Turn layer on";
		offText = "Turn layer off";

		showLandLayerText = "Show land layer";
		showRemoteSensingText = "Show background satellite image";
		showArrowText = "Show north arrow";
		showScaleText = "Show scale bar";
		showLongLatText = "Show cursor location";

		// Risk text
		enableRiskText = "Enable risk at this cell";
		healthText = "Health: ";
		healthAmountText[0] = "Very Bad";
		healthAmountText[1] = "Bad";
		healthAmountText[2] = "Good";
		healthAmountText[3] = "Very Good";
		threatText = "Threat: ";
		threatAmountText[0] = "Low";
		threatAmountText[1] = "Moderate";
		threatAmountText[2] = "High";
		threatAmountText[3] = "Very High";
		noRiskText = "No risk for this area.";
		showRiskText = "Display risk for MMA";
		hideRiskText = "Hide risk for MMA";

		// Oil model texts
		oilSelectMonth = "Select month: ";
		months[0] = "January";
		months[1] = "February";
		months[2] = "March";
		months[3] = "April";
		months[4] = "May";
		months[5] = "June";
		months[6] = "July";
		months[7] = "August";
		months[8] = "September";
		months[9] = "October";
		months[10] = "November";
		months[11] = "December";
		selectType = "Select type of oil: ";
		densityTypes = "Heavy ------------------> Light";
		selectVolume = "Select intial volume of spill (barrels): ";
		timeSinceSpill = "Time since spill (minutes): ";
		areaSpill = "Area of spill (m^2): ";
		
		calculatingModelText = "Model will run after you click OK";
		calculatingModelTitle = "Warning";

	}

	public void getGISInfo() {
		CSVReader reader;
		InputStream headerStream = getClass().getResourceAsStream(
				"/" + configFile);
		InputStreamReader headerReader = new InputStreamReader(headerStream);
		try {
			reader = new CSVReader(headerReader);
			String[] nextLine;
			int count = 0;
			int skip = 0;
			while ((nextLine = reader.readNext()) != null) {
				if (count == 0) {
					cellNum = Integer.parseInt(nextLine[0]);
				} 
				else if (count == 1) {
					// If there is no land layer to display
					if (nextLine[1].equals("null")) {
						if (nextLine[0].equals("null")) {
							backgroundFile = null;
						}
						else {
							backgroundFile = nextLine[0];
						}
						landLayerFile = null;
						landLayerColor = null;
					}
					// Else proceed as usual by getting layer file and color
					else {
						if (nextLine[0].equals("null")) {
							backgroundFile = null;
						}
						else {
							backgroundFile = nextLine[0];
						}
						landLayerFile = nextLine[1];
						landLayerColor = Color.decode("#" + nextLine[2]);
					}
				} 
				else if (count == 2) {
					layerNames = nextLine;
				} 
				else if (count == 3) {
					layerFiles = nextLine;
				} 
				else if (count == 4) {
					layerColors = new Color[nextLine.length];
					for (int i = 0; i < nextLine.length; i++) {
						layerColors[i] = Color.decode("#" + nextLine[i]);
					}
				}
				else if (count == 5) {
					if (nextLine[0].equals("null")) {
						threatLayers = null;
					}
					else {
						threatLayers = new int[nextLine.length];
						for (int i = 0; i < nextLine.length; i++) {
							threatLayers[i] = Integer.parseInt(nextLine[i]);
						}
					}
				} 
				else if (count == 6) {
					makeGrid = new boolean[nextLine.length];
					for (int i = 0; i < makeGrid.length; i++) {
						makeGrid[i] = Boolean.parseBoolean(nextLine[i]);
					}
				} 
				else if (count == 7) {
					skip = 0;
					if (threatLayers != null) {
						threatHabitatAtCell = new boolean[threatLayers.length][makeGrid.length];
						// skip = threatLayers.length;
						for (int i = 0; i < threatLayers.length; i++) {
							for (int j = 0; j < nextLine.length; j++) {
								threatHabitatAtCell[i][Integer.parseInt(nextLine[j])] = true;
							}
							/*
							 * Because it reads a new line at the start of the while loop,
							 * we don't read a new line for each of this habitat cell section
							 */
							if (i < threatLayers.length - 1) {
								nextLine = reader.readNext();
							}
						}
					}
				} 
				else if (count == (8 + skip)) {
					// If there is no mangrove rating system in the MMA
					if (nextLine[0].equals("null")) {
						mangroveLayerFiles = null;
					}
					// Else read in the mangrove rating raster files
					else {
						mangroveLayerFiles = nextLine;
					}
				}
				else if (count == (9 + skip)) {
					if (mangroveLayerFiles == null) {
						mangroveLayerIndex = -1;
					} else {
						mangroveLayerIndex = Integer.parseInt(nextLine[0]);
					}
				} 
				else if (count == (10 + skip)) {
					oilLandFile = nextLine[0];
					for (int i = 0; i < 12; i++) {
						String temp = "";
						if (i < 9) {
							temp = "0" + (i + 1) + ".txt";
						}
						else if (i >= 9) {
							temp = (i + 1) + ".txt";
						}
						oilZWFiles[i] = nextLine[1] + temp;
						oilMWFiles[i] = nextLine[2] + temp;
						oilZCFiles[i] = nextLine[3] + temp;
						oilMCFiles[i] = nextLine[4] + temp;
					}
				}
				count = count + 1;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		layersVisible = new boolean[layerFiles.length];
		for (int i = 0; i < layersVisible.length; i++) {
			layersVisible[i] = false;
		}
		layersVisible[0] = true;

		// Setting up P to be as many layers as there are and as many cells as
		// the window fits
		if (threatLayers != null) {
			mmaThreat = new int[threatLayers.length * 2][makeGrid.length];

			// Setting non-threat cells to null (-1) value
			for (int i = 0; i < makeGrid.length; i++) {
				if (makeGrid[i] == false) {
					for (int j = 0; j < mmaThreat.length; j++) {
						mmaThreat[j][i] = -1;
					}
				} else if (makeGrid[i] == true) {
					for (int j = 0; j < mmaThreat.length; j++) {
						mmaThreat[j][i] = 1;
					}
				}
			}
		}
		else {
			mmaThreat = null;
		}
	}

	public JTabbedPane tabPanel() {
		tabPane = new JTabbedPane();
		tabPane.setPreferredSize(MIDAS.LEFTRES);
		tabPane.setMinimumSize(MIDAS.LEFTRES);
		tabPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		tabPane.setBackground(MIDAS.BACKGROUNDCOLOR);

		tabPane.add(layerPanel(), layerPaneText);
		tabPane.add(riskPanel(), riskPaneText);
		tabPane.add(oilPanel(), oilPaneText);
		tabPane.add(mangrovePane(), mangrovePaneText);

		layerPaneIndex = 0;
		riskPaneIndex = 1;
		oilPaneIndex = 2;
		mangrovePaneIndex = 3;
		
		if (threatLayers == null) {
			tabPane.remove(1);
			riskPaneIndex = -1;
			oilPaneIndex = 1;
			mangrovePaneIndex = 2;
		}

		// Setting which thing to display - for when you change languages/MMAs
		int index = tabPane.getSelectedIndex();
		if (index == layerPaneIndex) {
			mapPane.setShowCellSelected(false);
		} else if (index == riskPaneIndex && threatLayers != null) {
			mapPane.setShowCellSelected(true);
			// mapPane.setShowRisk(true);
		} else if (index == oilPaneIndex) {
			mapPane.setShowRisk(false);
			mapPane.setShowMangroves(false);
			mapPane.setShowOil(true);
		} else if (index == mangrovePaneIndex) {
			mapPane.setShowRisk(false);
			mapPane.setShowMangroves(true);
			mapPane.setShowOil(false);
		}

		tabPane.addChangeListener(new ChangeListener() {
			// This method is called whenever the selected tab changes
			public void stateChanged(ChangeEvent e) {
				JTabbedPane tabSource = (JTabbedPane) e.getSource();
				int index = tabSource.getSelectedIndex();

				changeTabsToIndex(index);

				mapPane.repaint();
			}
		});

		return tabPane;
	}
	public void changeTabsToIndex(int index) {
		if (index == layerPaneIndex) {
			mapPane.setShowCellSelected(false);
			mapPane.setShowRisk(false);
			mapPane.setShowMangroves(false);
			mapPane.setShowOil(false);
		} 
		else if (index == riskPaneIndex) {
			mapPane.setShowCellSelected(true);
			mapPane.setShowRisk(false);
			mapPane.setShowMangroves(false);
			mapPane.setShowOil(false);
		} 
		else if (index == oilPaneIndex) {
			// Show oil EULA
			if (MIDAS.OIL_EULA == false) {
				OilEULA oilEula = new OilEULA();
			}

			mapPane.setShowCellSelected(false);
			mapPane.setShowRisk(false);
			mapPane.setShowMangroves(false);
			mapPane.setShowOil(true);
		} 
		else if (index == mangrovePaneIndex) {
			mapPane.setShowCellSelected(false);
			mapPane.setShowRisk(false);
			mapPane.setShowMangroves(true);

			// Force showing mangroves if mangrove tab is up
			int mIndex = 0;
			for (int i = 0; i < layerNames.length; i++) {
				if (layerNames[i].equals("Mangrove")) {
					mIndex = i;
				}
			}

			if (layersVisible[mIndex] == false) {
				mapPane.updateLayers(mIndex, true);
				layersVisible[mIndex] = true;
				layerButton[mIndex].setSelected(true);
			}

			mapPane.setShowOil(false);
		}
	}

	public JPanel layerPanel() {
		layerPane = new JPanel();
		layerPane.setPreferredSize(MIDAS.LEFTRES);
		layerPane.setLayout(new BoxLayout(layerPane, BoxLayout.Y_AXIS));
		layerPane.setBackground(MIDAS.BACKGROUNDCOLOR);
		layerGroups = new JPanel[layerFiles.length];
		layerButton = new JCheckBox[layerFiles.length];
		// JPanel[] layerGroups = new JPanel[layerFiles.length];
		for (int i = 0; i < layerGroups.length; i++) {
			layerGroups[i] = new JPanel();
			layerGroups[i].setLayout(new BoxLayout(layerGroups[i],
					BoxLayout.Y_AXIS));
			layerGroups[i].setPreferredSize(new Dimension(MIDAS.LEFTRES.width,
					50));
			layerGroups[i].setBorder(BorderFactory
					.createLineBorder(Color.black));
			JPanel topPane = new JPanel();
			topPane.setLayout(new GridLayout(0, 2));
			JPanel bottomPane = new JPanel();
			bottomPane.setLayout(new BoxLayout(bottomPane, BoxLayout.X_AXIS));
			JLabel layerLabel = new JLabel(layerText);
			JLabel layerNameLabel = new JLabel(layerNames[i]);
			layerButton[i] = new JCheckBox(onText);
			layerButton[i].setSelected(layersVisible[i]);
			layerButton[i].addActionListener(new LayerListener(i,
					layerButton[i]));
			topPane.add(layerLabel);
			topPane.add(layerNameLabel);
			bottomPane.add(layerButton[i]);
			layerGroups[i].add(topPane);
			layerGroups[i].add(bottomPane);

			// Setting components to color of layer:
			layerGroups[i].setBackground(layerColors[i]);
			layerLabel.setBackground(layerColors[i]);
			layerNameLabel.setBackground(layerColors[i]);
			layerButton[i].setBackground(layerColors[i]);
			topPane.setBackground(layerColors[i]);
			bottomPane.setBackground(layerColors[i]);



			layerPane.add(layerGroups[i]);
		}

		JPanel controlPane = new JPanel();
		controlPane.setLayout(new GridLayout(5,0));
		controlPane.setBackground(MIDAS.BACKGROUNDCOLOR);

		JCheckBox showLandLayerCheckBox = new JCheckBox(showLandLayerText);
		JCheckBox showRemoteSensingCheckBox = new JCheckBox(showRemoteSensingText);
		JCheckBox showArrowCheckBox = new JCheckBox(showArrowText);
		JCheckBox showScaleCheckBox = new JCheckBox(showScaleText);
		JCheckBox showLongLatCheckBox = new JCheckBox(showLongLatText);

		showLandLayerCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (mapPane.getShowLandLayer() == true) {
					mapPane.setShowLandLayer(false);
				}
				else if (mapPane.getShowLandLayer() == false) {
					mapPane.setShowLandLayer(true);
				}
				// Repaint mapPane to show updates
				mapPane.repaint();
			}
		});

		showRemoteSensingCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (mapPane.getShowRemoteSensing() == true) {
					mapPane.setShowRemoteSensing(false);
				}
				else if (mapPane.getShowRemoteSensing() == false) {
					mapPane.setShowRemoteSensing(true);
				}
				// Repaint mapPane to show updates
				mapPane.repaint();
			}
		});

		showArrowCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (mapPane.getShowArrow() == true) {
					mapPane.setShowArrow(false);
				}
				else if (mapPane.getShowArrow() == false) {
					mapPane.setShowArrow(true);
				}
				// Repaint mapPane to show updates
				mapPane.repaint();
			}
		});
		showScaleCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (mapPane.getShowScale() == true) {
					mapPane.setShowScale(false);
				}
				else if (mapPane.getShowScale() == false) {
					mapPane.setShowScale(true);
				}
				// Repaint mapPane to show updates
				mapPane.repaint();
			}
		});
		showLongLatCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (mapPane.getShowLongLat() == true) {
					mapPane.setShowLongLat(false);
				}
				else if (mapPane.getShowLongLat() == false) {
					mapPane.setShowLongLat(true);
				}
				// Repaint mapPane to show updates
				mapPane.repaint();
			}
		});

		showLandLayerCheckBox.setSelected(true);
		showRemoteSensingCheckBox.setSelected(true);
		showArrowCheckBox.setSelected(true);
		showScaleCheckBox.setSelected(true);
		showLongLatCheckBox.setSelected(true);

		showLandLayerCheckBox.setBackground(MIDAS.BACKGROUNDCOLOR);
		showRemoteSensingCheckBox.setBackground(MIDAS.BACKGROUNDCOLOR);
		showArrowCheckBox.setBackground(MIDAS.BACKGROUNDCOLOR);
		showScaleCheckBox.setBackground(MIDAS.BACKGROUNDCOLOR);
		showLongLatCheckBox.setBackground(MIDAS.BACKGROUNDCOLOR);

		controlPane.add(showLandLayerCheckBox);
		controlPane.add(showRemoteSensingCheckBox);
		controlPane.add(showArrowCheckBox);
		controlPane.add(showScaleCheckBox);
		controlPane.add(showLongLatCheckBox);

		if (landLayerFile == null) {
			showLandLayerCheckBox.setEnabled(false);
			mapPane.setShowLandLayer(false);
			showLandLayerCheckBox.setSelected(false);
		}

		layerPane.add(controlPane);

		return layerPane;
	}
	public class LayerListener implements ActionListener {

		private int num;
		private JCheckBox checkBox;

		public LayerListener(int n, JCheckBox ckBox) {
			num = n;
			checkBox = ckBox;
		}

		public void actionPerformed(ActionEvent e) {
			if (layersVisible[num] == true) {
				layersVisible[num] = false;
				mapPane.updateLayers(num, false);
				checkBox.setText(onText);
			} else if (layersVisible[num] == false) {
				layersVisible[num] = true;
				checkBox.setText(offText);
				mapPane.updateLayers(num, true);
			}
			// mapPane.updateLayers(layersVisible);
		}
	}

	public class CursorLocationListener extends MouseMotionAdapter {
		public void mouseMoved(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();

			mapPane.calculateLongLat(x,y);
		}
	}

	public JPanel riskPanel() {
		riskPane = new JPanel();
		riskPane.setPreferredSize(MIDAS.LEFTRES);
		riskPane.setLayout(new BoxLayout(riskPane, BoxLayout.Y_AXIS));
		riskPane.setBackground(MIDAS.BACKGROUNDCOLOR);

		if (threatLayers == null) {
			riskPane.add(new JLabel(noRiskText));
		} else {
			
			enableRiskCheckBox = new JCheckBox(enableRiskText);
			enableRiskCheckBox.setSelected(makeGrid[riskCellSelected]);
			enableRiskCheckBox.setBackground(MIDAS.BACKGROUNDCOLOR);
			enableRiskCheckBox.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					if (enableRiskCheckBox.isSelected() == true) {
						makeGrid[riskCellSelected] = true;
					}
					if (enableRiskCheckBox.isSelected() == false) {
						makeGrid[riskCellSelected] = false;
					}				
					
					for (int i = 0; i < threatBars.length / 2; i++) {
						// If user wants to input risk at location, turn on habitats
						if (threatHabitatAtCell[i][riskCellSelected] == true && 
								makeGrid[riskCellSelected] == true) {
							threatBars[i].setEnabled(true);
							threatBars[i + threatBars.length / 2].setEnabled(true);
							
							mmaThreat[i][riskCellSelected] = 1;
							mmaThreat[i + threatBars.length / 2][riskCellSelected] = 1;

							threatBars[i].setValue(mmaThreat[i][riskCellSelected]);
							threatBars[i + threatBars.length / 2].setValue(
									mmaThreat[i + threatBars.length / 2][riskCellSelected]);
						}
						else if (threatHabitatAtCell[i][riskCellSelected] == false || 
								makeGrid[riskCellSelected] == false) {
							threatBars[i].setEnabled(false);
							threatBars[i + threatBars.length / 2].setEnabled(false);
							
							mmaThreat[i][riskCellSelected] = -1;
							mmaThreat[i + threatBars.length / 2][riskCellSelected] = -1;
						}

					}
				}
				
			});
			
			riskPane.add(enableRiskCheckBox);
			
			threatGroups = new JPanel[threatLayers.length];
			threatBars = new ScrollBar[threatLayers.length * 2];
			for (int i = 0; i < threatGroups.length; i++) {
				threatGroups[i] = new JPanel();
				threatGroups[i].setLayout(new GridLayout(5, 1));
				threatGroups[i].setPreferredSize(new Dimension(
						MIDAS.LEFTRES.width, 50));
				threatGroups[i].setBorder(BorderFactory
						.createLineBorder(Color.black));
				threatGroups[i].setBackground(MIDAS.BACKGROUNDCOLOR);

				JLabel layerLabel = new JLabel("<HTML><B>" + layerText + "</B>"
						+ layerNames[threatLayers[i]]);
				layerLabel.setBackground(MIDAS.BACKGROUNDCOLOR);
				JLabel healthAmount = new JLabel(
						"<HTML><B>"
						+ healthText
						+ "</B>"
						+ healthAmountText[mmaThreat[i
						                             + threatLayers.length][riskCellSelected] - 1]);
				healthAmount.setBackground(MIDAS.BACKGROUNDCOLOR);
				JLabel threatAmount = new JLabel("<HTML><B>" + threatText
						+ "</B>"
						+ threatAmountText[mmaThreat[i][riskCellSelected] - 1]);
				threatAmount.setBackground(MIDAS.BACKGROUNDCOLOR);
				threatBars[i] = new ScrollBar(minThreat, 1, minThreat,
						maxThreat + 1);
				threatBars[i].addAdjustmentListener(new ThreatListener(i,
						threatBars[i], threatAmount));
				threatBars[i + threatLayers.length] = new ScrollBar(minThreat,
						1, minThreat, maxThreat + 1);
				threatBars[i + threatLayers.length]
				           .addAdjustmentListener(new ThreatListener(i
				        		   + threatLayers.length, threatBars[i
				        		                                     + threatLayers.length], healthAmount));
				threatGroups[i].add(layerLabel);
				threatGroups[i].add(threatAmount);
				threatGroups[i].add(threatBars[i]);
				threatGroups[i].add(healthAmount);
				threatGroups[i].add(threatBars[i + threatLayers.length]);
				threatGroups[i].add(threatBars[i + threatLayers.length]);

				// Searching for color of layer and setting risk bar to the
				// color
				threatGroups[i].setBackground(layerColors[threatLayers[i]]);

				// If there's no habitat in default risk cell
				if (threatHabitatAtCell[i][riskCellSelected] == false) {
					threatBars[i].setEnabled(false);
					threatBars[i + threatLayers.length].setEnabled(false);
				}

				riskPane.add(threatGroups[i]);
			}
			JButton riskDisplayButton = new JButton(showRiskText);
			riskDisplayButton.addActionListener(new RiskDisplayListener(
					riskDisplayButton));
			riskPane.add(riskDisplayButton);
		}

		return riskPane;
	}
	public void setThreat(int type, int cellSelected, int threat) {
		mmaThreat[type][cellSelected] = threat;
	}

	public int[][] getThreat() {
		return mmaThreat;
	}

	public void calcAndSetRiskColor() {
		mapPane.setRiskColor(midasCalc.calculateRiskColor(mmaThreat, threatHabitatAtCell));
	}

	public class RiskDisplayListener implements ActionListener {

		JButton button;

		public RiskDisplayListener(JButton btn) {
			button = btn;
		}

		public void actionPerformed(ActionEvent e) {
			if (button.getText().equals(showRiskText) == true) {
				button.setText(hideRiskText);
				calcAndSetRiskColor();
				mapPane.setShowCellSelected(false);
				mapPane.setShowRisk(true);
				mapPane.repaint();
			} else if (button.getText().equals(hideRiskText) == true) {
				button.setText(showRiskText);
				mapPane.setShowCellSelected(true);
				mapPane.setShowRisk(false);
				mapPane.repaint();
			}
		}
	}

	public class ThreatListener implements AdjustmentListener {

		private int num;
		ScrollBar threatBar;
		JLabel threatAmount;

		public ThreatListener(int n, ScrollBar thrtBar, JLabel thrtAmount) {
			num = n;
			threatBar = thrtBar;
			threatAmount = thrtAmount;
		}

		public void adjustmentValueChanged(AdjustmentEvent e) {
			int threat = threatBar.getValue();
			mmaThreat[num][riskCellSelected] = threat;

			if (num < threatLayers.length) {
				threatAmount
				.setText("<HTML><B>"
						+ threatText
						+ "</B>"
						+ threatAmountText[mmaThreat[num][riskCellSelected] - 1]);
			} else {
				threatAmount
				.setText("<HTML><B>"
						+ healthText
						+ "</B>"
						+ healthAmountText[mmaThreat[num][riskCellSelected] - 1]);
			}
		}
	}

	public class CellMouseListener extends MouseAdapter {

		private double x;
		private double y;

		// Event handling for selection of raster cells
		public void mouseClicked(MouseEvent e) {
			// Only perform actions if layer selection or risk tabs are visible
			if ((tabPane.getSelectedIndex() == layerPaneIndex || tabPane.getSelectedIndex() == riskPaneIndex)
					&& threatLayers != null) {

				x = e.getX();
				y = e.getY();

				riskCellSelected = mapPane.checkCell(x, y);
				
				// Updating overall risk check box
				enableRiskCheckBox.setSelected(makeGrid[riskCellSelected]);

				for (int i = 0; i < threatBars.length / 2; i++) {
					// If user wants to input risk at location, turn on habitats
					if (threatHabitatAtCell[i][riskCellSelected] == true && 
							makeGrid[riskCellSelected] == true) {
						threatBars[i].setEnabled(true);
						threatBars[i + threatBars.length / 2].setEnabled(true);

						threatBars[i].setValue(mmaThreat[i][riskCellSelected]);
						threatBars[i + threatBars.length / 2].setValue(
								mmaThreat[i + threatBars.length / 2][riskCellSelected]);
					}
					else if (threatHabitatAtCell[i][riskCellSelected] == false ||
							makeGrid[riskCellSelected] == false) {
						threatBars[i].setEnabled(false);
						threatBars[i + threatBars.length / 2].setEnabled(false);
					}

				}
			}
		}
	}

	public JPanel oilPanel() {			
		oilPane = new JPanel();
		oilPane.setPreferredSize(MIDAS.LEFTRES);
		oilPane.setLayout(new BoxLayout(oilPane, BoxLayout.Y_AXIS));
		oilPane.setBackground(MIDAS.BACKGROUNDCOLOR);

		oilModel = new OilModel();
		
		// Setting default values
		oilDensity = 0.7;
		maxTime = 1;
		oilVolume = 50;
		monthSelected = 0;
		oilModel.setVolume(oilVolume);
		
		// Feeding model land file
		oilModel.setOilLandFile(oilLandFile);
		// Feeding model wind / current data for January
		oilModel.setOilZWFile(oilZWFiles[monthSelected]);
		oilModel.setOilMWFile(oilMWFiles[monthSelected]);
		oilModel.setOilZCFile(oilZCFiles[monthSelected]);
		oilModel.setOilMCFile(oilMCFiles[monthSelected]);

		JTextPane textPane = new JTextPane();
		textPane.setPreferredSize(new Dimension(MIDAS.LEFTRES.width, 300));
		textPane.setSize(new Dimension(MIDAS.LEFTRES.width, 300));
		textPane.setContentType("text/html");
		textPane.setEditable(false);
		String html = "oilExplanation.html";

		try {
			URL url = Thread.currentThread().getContextClassLoader().getResource(html);
			textPane.setPage(url);
		} catch (IOException e) {
			System.out.println("Exception: " + e);
		}

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setSize(new Dimension(MIDAS.LEFTRES.width, 150));
		scrollPane.setPreferredSize(new Dimension(MIDAS.LEFTRES.width, 150));

		scrollPane.getViewport().add(textPane);

		oilPane.add(scrollPane);

		final DecimalFormat dfAPI = new DecimalFormat("#0.###");
		final DecimalFormat dfVolume = new DecimalFormat("0.###");

		JPanel[] componentPanes = new JPanel[6];
		for (int i = 0; i < componentPanes.length; i++) {
			componentPanes[i] = new JPanel();
			componentPanes[i].setBackground(MIDAS.BACKGROUNDCOLOR);
			componentPanes[i].setPreferredSize(new Dimension(
					MIDAS.LEFTRES.width, 60));
			componentPanes[i].setMinimumSize(new Dimension(MIDAS.LEFTRES.width,
					60));
			componentPanes[i].setLayout(new BoxLayout(componentPanes[i],
					BoxLayout.Y_AXIS));
		}
		oilMonthLabel = new JLabel("<HTML><b>" + oilSelectMonth + "</b>" + months[monthSelected]);
		monthScrollBar = new ScrollBar(monthSelected, 1, monthSelected, 12);
		monthScrollBar.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				monthSelected = e.getValue();
				oilMonthLabel.setText("<HTML><b>" + oilSelectMonth  + "</b>" + months[monthSelected]);
				
				oilModel.setOilZWFile(oilZWFiles[monthSelected]);
				oilModel.setOilMWFile(oilMWFiles[monthSelected]);
				oilModel.setOilZCFile(oilZCFiles[monthSelected]);
				oilModel.setOilMCFile(oilMCFiles[monthSelected]);
			}
		});
		componentPanes[0].add(oilMonthLabel);
		componentPanes[0].add(monthScrollBar);

		JLabel selectTypeLabel = new JLabel("<HTML><b>" + selectType + "</b>");
		JLabel densityLabel = new JLabel(densityTypes);
		APIdensityLabel = new JLabel("American Petroleum Index (API): "
				+ dfAPI.format(141.5 / oilDensity - 131.5));
		densityBar = new ScrollBar((int) (oilDensity * 100), 5, 70, 105);
		densityBar.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				oilDensity = (double) (e.getValue() / 100.01);
				APIdensityLabel.setText("American Petroleum Index (API): "
						+ dfAPI.format(141.5 / oilDensity - 131.5));
				oilModel.setDensityOil(oilDensity);
			}
		});
		componentPanes[1].add(selectTypeLabel);
		componentPanes[1].add(densityLabel);
		componentPanes[1].add(APIdensityLabel);
		componentPanes[1].add(densityBar);

		volumeLabel = new JLabel("<HTML><b>" + selectVolume + "</b>");
		volumeAmountLabel = new JLabel(dfVolume.format(oilVolume) + " barrels");
		volumeBar = new ScrollBar(oilVolume, 10, oilVolume, 250000);
		volumeBar.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				oilVolume = e.getValue();
				volumeAmountLabel.setText(dfVolume.format(oilVolume) + " barrels");
				oilModel.setVolume(oilVolume);

			}
		});
		componentPanes[2].add(volumeLabel);
		componentPanes[2].add(volumeAmountLabel);
		componentPanes[2].add(volumeBar);

		timeSinceSpillLabel = new JLabel("<HTML><b>" + timeSinceSpill + "</b>"+ maxTime);
		spillAreaLabel = new JLabel("<HTML><b>" + areaSpill + "</b>");
		timeStepBar = new ScrollBar(maxTime, 15, 1, 315);
		timeStepBar.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				maxTime = e.getValue();
				timeSinceSpillLabel.setText("<HTML><b>" + timeSinceSpill + "</b>"+ maxTime);

				if (calcedSpill == true) {
					oilModel.moveOil(maxTime, spillXY[0], spillXY[1]);
					mapPane.drawSpill(oilModel.getXY(),
							oilModel.getMajorAxis(), oilModel.getMinorAxis());
					spillAreaLabel.setText("<HTML><b>" + areaSpill + "</b>" + dfVolume.format(oilModel.getArea()));
				}

			}
		});
		componentPanes[3].add(timeSinceSpillLabel);
		componentPanes[3].add(timeStepBar);

		for (int i = 0; i < 4; i++) {
			oilPane.add(componentPanes[i]);
		}
		oilPane.add(spillAreaLabel);

		return oilPane;
	}

	public class OilModelListener extends MouseAdapter {

		public void mouseClicked(MouseEvent e) {
			// Only perform actions if oil model is visible
			if (tabPane.getSelectedIndex() == oilPaneIndex && e.isMetaDown() == false
					&& MIDAS.OIL_EULA == true) {
				calcedSpill = true;
				mapPane.setShowOil(true);

				spillXY[0] = e.getX();
				spillXY[1] = e.getY();

				oilModel.setVolume(oilVolume);
				oilModel.setDensityOil(oilDensity);
				
				JOptionPane.showMessageDialog(MIDAS.frame,
						calculatingModelText, calculatingModelTitle,
						JOptionPane.WARNING_MESSAGE);	
				
				oilModel.moveOil(maxTime, spillXY[0], spillXY[1]);

				mapPane.drawSpill(oilModel.getXY(), oilModel.getMajorAxis(),
						oilModel.getMinorAxis());

			} else if (tabPane.getSelectedIndex() == oilPaneIndex && e.isMetaDown()) {
				calcedSpill = false;
				mapPane.setShowOil(false);
				mapPane.repaint();
			}
		}
	}

	public JPanel mangrovePane() {
		mangrovePane = new MangrovePanel();

		mangrovePane.setPreferredSize(MIDAS.LEFTRES);

		return mangrovePane;
	}

	public void updateMangroveStatisticLabels(double[] mangroveStatistics) {
		mangroveStats = mangroveStatistics;
		mangrovePane.updateValues(mangroveStatistics);
		mangrovePane.refreshPanel();
	}

	public class MangroveSelectList extends MouseAdapter {

		private int maxPoints = 50;
		private int pointCount = 0;
		private int[] xCoord = new int[maxPoints];
		private int[] yCoord = new int[maxPoints];

		public void mousePressed(MouseEvent e) {
			// Only perform actions if mangrove panel is visible
			if (tabPane.getSelectedIndex() == mangrovePaneIndex) {
				if (pointCount > 0 && (Math.abs(xCoord[0]) - e.getX() <= 2)
						&& (Math.abs(yCoord[0] - e.getY()) <= 2)) {
					mapPane.putPolygon(pointCount, xCoord, yCoord);
					pointCount = 0;
					// mangroveStatistics = mapPane.getMangroveStatistics();
					updateMangroveStatisticLabels(mapPane
							.getMangroveStatistics());
				} else if (e.isMetaDown() || pointCount == maxPoints) {
					mapPane.putPolygon(pointCount, xCoord, yCoord);
					pointCount = 0;
					updateMangroveStatisticLabels(mapPane
							.getMangroveStatistics());
				} else {
					xCoord[pointCount] = e.getX();
					yCoord[pointCount] = e.getY();
					pointCount++;
					if (pointCount >= 2) {
						mapPane.putLine(xCoord[pointCount - 2],
								yCoord[pointCount - 2], xCoord[pointCount - 1],
								yCoord[pointCount - 1]);
					}
				}
			}
		}
	}

	public class MangroveSelectListener extends MouseInputAdapter {
		private int initialX;
		private int initialY;
		private int[] xCoord = new int[4];
		private int[] yCoord = new int[4];

		public void mousePressed(MouseEvent e) {
			if (tabPane.getSelectedIndex() == mangrovePaneIndex) {
				initialX = e.getX();
				initialY = e.getY();
				xCoord[0] = initialX;
				yCoord[0] = initialY;
			}
		}

		public void mouseDragged(final MouseEvent e) {
			if (tabPane.getSelectedIndex() == mangrovePaneIndex) {
				final int x = initialX;
				final int y = initialY;
				mapPane.clearPaint();
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						mapPane.putRectangle(x, y, e.getX(), e.getY());
					}
				});
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (tabPane.getSelectedIndex() == mangrovePaneIndex) {
				xCoord[1] = initialX;
				xCoord[2] = e.getX();
				xCoord[3] = e.getX();

				yCoord[1] = e.getY();
				yCoord[2] = e.getY();
				yCoord[3] = initialY;

				mapPane.putPolygon(4, xCoord, yCoord);
				updateMangroveStatisticLabels(mapPane.getMangroveStatistics());

			}
		}
	}

	public void updateTabs() {
		updateMangroveStatisticLabels(mangroveStats);
		mapPane.repaint();
	}
	public int[][] getGISHealthThreat() {
		return mmaThreat;
	}
	public void setGISHealthThreat(int[][] threat) {
		// Threat/Health = input threat/health
		mmaThreat = threat;
		
		// Setting values of bars
		for (int i = 0; i < threatBars.length; i++) {
			threatBars[i].setValue(mmaThreat[i][riskCellSelected]);
			threatBars[i].repaint();
		}
		
		// Parsing to determine which cells had been turn on/off (e.g. in makeGrid)
		for (int i = 0; i < mmaThreat.length; i++) {
			for (int j = 0; j < mmaThreat[0].length; j++) {
				if (mmaThreat[i][j] != -1) {
					makeGrid[j] = true;
				}
			}
		}
		
		if (makeGrid[riskCellSelected] == true) {
			enableRiskCheckBox.setSelected(true);
		}
		else if (makeGrid[riskCellSelected] == false) {
			enableRiskCheckBox.setSelected(false);
		}
		
		// Redoing on/off based on new makeGrid value
		for (int i = 0; i < threatBars.length / 2; i++) {
			// If user wants to input risk at location, turn on habitats
			if (threatHabitatAtCell[i][riskCellSelected] == true && 
					makeGrid[riskCellSelected] == true) {
				threatBars[i].setEnabled(true);
				threatBars[i + threatBars.length / 2].setEnabled(true);
				
				mmaThreat[i][riskCellSelected] = 1;
				mmaThreat[i + threatBars.length / 2][riskCellSelected] = 1;

				threatBars[i].setValue(mmaThreat[i][riskCellSelected]);
				threatBars[i + threatBars.length / 2].setValue(
						mmaThreat[i + threatBars.length / 2][riskCellSelected]);
			}
			else if (threatHabitatAtCell[i][riskCellSelected] == false || 
					makeGrid[riskCellSelected] == false) {
				threatBars[i].setEnabled(false);
				threatBars[i + threatBars.length / 2].setEnabled(false);
				
				mmaThreat[i][riskCellSelected] = -1;
				mmaThreat[i + threatBars.length / 2][riskCellSelected] = -1;
			}

			mapPane.repaint();
		}
	}

	public boolean isRiskEnabled() {
		if (threatLayers != null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public BufferedImage getGISBufferedImage(String name) {		
		BufferedImage bi = new BufferedImage(MIDAS.GISRES.width, MIDAS.GISRES.height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bi.createGraphics();

		if (name.equals("Layer")) {
			changeTabsToIndex(0);
			mapPane.paintComponent(g2d);
		}
		else if (name.equals("Risk")) {
			changeTabsToIndex(1);
			mapPane.setRiskColor(midasCalc.calculateRiskColor(mmaThreat, threatHabitatAtCell));
			mapPane.setShowRisk(true);
			mapPane.paintComponent(g2d);
		}
		else if (name.equals("Oil")) {
			changeTabsToIndex(2);
			mapPane.paintComponent(g2d);
		}
		else if (name.equals("Mangrove")) {
			changeTabsToIndex(3);
			mapPane.paintComponent(g2d);
		}

		return bi;
	}

	public BufferedImage getGISControlBufferedImage(String name) {
		BufferedImage bi = null;
		
		if (name.equals("Layer")) {
			bi = new BufferedImage(layerPane.getWidth(), layerPane.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics g = bi.getGraphics();
			changeTabsToIndex(0);		
			layerPane.repaint();
			layerPane.validate();
			layerPane.paint(g);
		}
		else if (name.equals("Risk")) {
			bi = new BufferedImage(riskPane.getWidth(), riskPane.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics g = bi.getGraphics();

			changeTabsToIndex(1);
			mapPane.setRiskColor(midasCalc.calculateRiskColor(mmaThreat, threatHabitatAtCell));
			mapPane.setShowRisk(true);

			riskPane.repaint();
			riskPane.validate();
			riskPane.paint(g);
		}
		else if (name.equals("Oil")) {
			bi = new BufferedImage(oilPane.getWidth(), oilPane.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics g = bi.getGraphics();
			changeTabsToIndex(2);		
			oilPane.repaint();
			oilPane.validate();
			oilPane.paint(g);
		}
		else if (name.equals("Mangrove")) {
			bi = new BufferedImage(mangrovePane.getWidth(), mangrovePane.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics g = bi.getGraphics();

			changeTabsToIndex(3);
			mangrovePane.paintComponent(g);
		}

		return bi;
	}

	public void setMMAVisible(boolean visible) {
		/*
		 * This method will tell the MapPanel whether or not to calculate image
		 * variables based on whether or not the panel is visible in order to
		 * maximize memory efficiency.
		 */
		mapPane.setMMAVisible(visible);
	}
}
