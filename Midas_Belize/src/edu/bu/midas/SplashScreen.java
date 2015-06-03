package edu.bu.midas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class SplashScreen {

	private JDialog dialog;

	private String title;
	private BufferedImage splashImg;


	private SplashScreenImage splashScreenImg;

	public SplashScreen() {
		setLanguage();
		getSplashImage("MIDAS_logo.jpg");
		createDialogBox();
	}
	public void setLanguage() {
		title = "Loading MIDAS";
	}
	public void createDialogBox() {
		dialog = new JDialog(MIDAS.frame, title);

		splashScreenImg = new SplashScreenImage(splashImg);
		dialog.setContentPane(splashScreenImg);
	}
	public void showDialogBox() {
		dialog.setSize(new Dimension(410, 200));
		dialog.setLocationRelativeTo(MIDAS.frame);
		dialog.setVisible(true);
	}
	public void closeDialogBox() {
		dialog.setVisible(false);
		dialog.dispose();
	}
	public void getSplashImage(String file) {
		
		URL url;
		try {
			splashImg = new BufferedImage(410, 200, BufferedImage.TYPE_INT_RGB);
			url = Thread.currentThread().getContextClassLoader().getResource("MIDAS-splash.gif");
			splashImg = ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public class SplashScreenImage extends JPanel {

		private BufferedImage image;

		public SplashScreenImage(BufferedImage img) {
			image = img;
			System.out.println("SplashScreenImage init");

			setBackground(Color.green);
		}

		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			super.paintComponent(g);

			System.out.println("SplashScreenImage paintComponent");
			g2d.drawImage(image, 0, 0, 410, 200, null);
		}
	}

}
