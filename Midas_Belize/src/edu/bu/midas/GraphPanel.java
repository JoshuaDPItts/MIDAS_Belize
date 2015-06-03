package edu.bu.midas;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class GraphPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GovernanceGraph govGraph;
	private SocioecoGraph socGraph;
	private EcologicalGraph ecoGraph;
	private ComparisonGraph comGraph;
	private TriangleGraph triGraph;
	private PopupMenu popup = new PopupMenu();
	
	public GraphPanel(int[] expertEvalVal) {
		setPreferredSize(MIDAS.OUTCOMERES);
		setMaximumSize(MIDAS.OUTCOMERES);
		setBackground(MIDAS.BACKGROUNDCOLOR);
		
		// Partitioning expert values into appropriate graphs
		int[] govExpertVals = new int[6];
		int[] socExpertVals = new int[6];
		int[] ecoExpertVals = new int[6];
		// Making sure values are not null
		if (expertEvalVal != null) {
			for (int i = 0; i < 6; i++) {
				govExpertVals[i] = expertEvalVal[i];
				socExpertVals[i] = expertEvalVal[i + 6];
				ecoExpertVals[i] = expertEvalVal[i + 12];
			}
		}
		else {
			govExpertVals = null;
			socExpertVals = null;
			ecoExpertVals = null;
		}
		
		govGraph = new GovernanceGraph(govExpertVals);
		socGraph = new SocioecoGraph(socExpertVals);
		ecoGraph = new EcologicalGraph(ecoExpertVals);
		comGraph = new ComparisonGraph();
		triGraph = new TriangleGraph();
		
		govGraph.setPreferredSize(MIDAS.OUTCOMERES);
		socGraph.setPreferredSize(MIDAS.OUTCOMERES);
		ecoGraph.setPreferredSize(MIDAS.OUTCOMERES);
		comGraph.setPreferredSize(MIDAS.OUTCOMERES);
		triGraph.setPreferredSize(MIDAS.OUTCOMERES);
		
		add(govGraph);
		add(socGraph);
		add(ecoGraph);
		add(comGraph);
		add(triGraph);
		//add(triGraph2);
		
		socGraph.setVisible(false);
		ecoGraph.setVisible(false);
		comGraph.setVisible(false);
		triGraph.setVisible(false);
		//triGraph2.setVisible(false);
		
		this.addMouseListener(popup.popupListener);
	}
	public void updateGraph() {
		govGraph.updateGraph();
		socGraph.updateGraph();
		ecoGraph.updateGraph();
		comGraph.updateGraph();
		triGraph.updateGraph();
		//triGraph2.updateGraph();
	}
	public void changeGraphs(String name) 
	{
		govGraph.setVisible(false);
		socGraph.setVisible(false);
		ecoGraph.setVisible(false);
		comGraph.setVisible(false);
		triGraph.setVisible(false);
		//triGraph2.setVisible(false);
		
		if(name.equals("Gov"))
			govGraph.setVisible(true);
		else if(name.equals("Soc"))
			socGraph.setVisible(true);
		else if(name.equals("Eco")) 
			ecoGraph.setVisible(true);
		else if(name.equals("Com"))
			comGraph.setVisible(true);
		else if(name.equals("Tri"))
			triGraph.setVisible(true);
		//else if(name.equals("Tri2"))
		//	triGraph2.setVisible(true);
	}
	public BufferedImage getCDFReportBufferedImage(String name) {
		BufferedImage bi = new BufferedImage(MIDAS.OUTCOMERES.width, MIDAS.OUTCOMERES.height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bi.createGraphics();
		
		if(name.equals("Gov")) {
			govGraph.repaint();
			govGraph.paintComponent(g2d);
		}
		else if(name.equals("Soc")) {
			socGraph.repaint();
			socGraph.paintComponent(g2d);
		}
		else if(name.equals("Eco")) {
			ecoGraph.repaint();
			ecoGraph.paintComponent(g2d);
		}
		else if(name.equals("Com")) {
			comGraph.repaint();
			comGraph.paintComponent(g2d);
		}
		else if(name.equals("Tri")) {
			triGraph.repaint();
			triGraph.paintComponent(g2d);
		}
		
		return bi;
	}
	public void repaintGraphs() {
		govGraph.repaintGraphs();
		socGraph.repaintGraphs();
		ecoGraph.repaintGraphs();
	}
}