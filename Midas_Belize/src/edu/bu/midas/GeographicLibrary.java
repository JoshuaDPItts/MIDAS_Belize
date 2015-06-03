/*
 * This class is intended to store common functions relevant to a Geographic Information System (GIS).
 * For instance, it handles the conversion between types of degrees minutes seconds (DMS) and decimal degrees,
 * and eventually between geographic coordinate systems.
 */

package edu.bu.midas;

import java.text.DecimalFormat;

public class GeographicLibrary {

	private DecimalFormat threeDFormat;
	
	public GeographicLibrary() 
	{
		threeDFormat = new DecimalFormat("#.###");
	}
	
	/*
	 * Returns a String formatted for DMS (includes degree, minute, seconds, and 
	 * cardinal direction signs) from an input decimal degree.
	 * @param	coord	the coordinate in decimal degrees
	 * @param	type	latitude or longitude
	 * @return	coordinate	the coorindate in DMS format
	 */
	public String convertDDToDMS(double coord, String type) 
	{
		// Seeing if coord is pos/negative
		boolean pos = true;
		if (coord < 0) {
			pos = false;
		}
		
		// Change coordinate to positive now that we have a boolean for positive/negative
		coord = Math.abs(coord);
		
		// Converting to Degrees/Min/Sec
		double deg = Math.floor(coord);
		coord = coord - deg;
		coord = coord * 60;		
		double min = Math.floor(coord);
		coord = coord - min;
		double sec  = coord * 60;		

		/*
		 * Formats doubles to have appropriate # of decimal places and converts them to DMS format.
		 * If the seconds have a decimal place, it limits the number of places to 3. If not, it
		 * outputs the seconds as an integer.
		 */
		String coordinate = new String();
		if (Double.compare(sec, Math.floor(sec)) == 0) 
		{
			coordinate = Integer.toString((int)deg) + "°" + Integer.toString((int)min) + "'" +
			Integer.toString((int)sec) + "\"";
		}
		else if (Double.compare(sec, Math.floor(sec)) != 0) 
		{
			sec = Double.valueOf(threeDFormat.format(sec));
			coordinate = Integer.toString((int)deg) + "°" + Integer.toString((int)min) + "'" +
			Double.toString(sec) + "\"";
		}
		
		// checking to see if long/lat, then if pos/neg for cardinal sign
		if (type.equals("lat")) 
		{
			if (pos == true) 
			{
				coordinate = coordinate + "W";
			}
			else if (pos == false) 
			{
				coordinate = coordinate + "E";
			}
		}
		else if (type.equals("long")) 
		{
			if (pos == true) 
			{
				coordinate = coordinate + "N";
			}
			else if (pos == false) 
			{
				coordinate = coordinate + "S";
			}
		}
		
		return coordinate;
	}
	
	/*
	 * Returns degrees minutes seconds coordinate as an array of 3 Strings
	 * 
	 * @param	dms	coordinate formatted in one line of DMS
	 * @return	sepDMS	coordinate formatted in 3 lines of DMS
	 */
	public String[] seperateDMS(String dms) 
	{
		int degIndex = dms.indexOf("°");
		String deg = dms.substring(0, degIndex + 1);
		int minIndex = dms.indexOf("'");
		String min = dms.substring(degIndex + 1, minIndex + 1);
		String sec = dms.substring(minIndex + 1, dms.length());
		
		String[] sepDMS = {deg, min, sec};
		return sepDMS;
	}
	
	/*
	 * Returns a double formatted in decimal degrees (to three places) from an input 
	 * coordinate in degrees/minutes/seconds (DMS).
	 * 
	 * @param	coord	String coordinate input
	 * @return coordinate	formatted coordinat in decimal degrees
	 */
	public double convertDMSToDD(String coord) 
	{
		
		// Parsing out deg/min/sec based on signs used in string
		String deg = coord.split("°", 2)[0];
		coord = coord.split("°", 2)[1];
		String min = coord.split("'", 2)[0];
		coord = coord.split("'", 2)[1];
		String sec = coord.split("\"", 2)[0];
		coord = coord.split("\"", 2)[1];
		String direction = coord;
		
		// Converting to double and converting to DD
		double degDD = Double.parseDouble(deg);
		double minDD = Double.parseDouble(min) / 60;
		double secDD = Double.parseDouble(sec) / 3600;
		// adding together coordinate components
		double coordinate = degDD + minDD + secDD;
		
		// formatting for 3 decimal places
		
		coordinate = Double.valueOf(threeDFormat.format(coordinate));
		
		// Determining sign of coordinate based on N/S or W/E cardinal direction
		if (direction.equals("S") || direction.equals("E")) 
		{
			coordinate = coordinate * -1;
		}
		return coordinate;
	}
}
