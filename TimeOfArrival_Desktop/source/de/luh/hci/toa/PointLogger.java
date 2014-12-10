package de.luh.hci.toa;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

public class PointLogger implements TapListener {
	
	PrintWriter out;
	
	public void start() {
		String str = JOptionPane.showInputDialog(Main.gui, "Filename:");
		
		if(str == null) return;
		
		setFile(str);
	}
	
	public void stop() {
		if(out != null) {
			out.close();
		}
		
		out = null;
	}
	
	public void setFile(String fileName) {
		try {
			out = new PrintWriter(fileName);
			
			out.println("messX, messY, messTheta, sollX, sollY, sollTheta");
			out.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTap(double x, double y, double theta) {
		if(out == null) return;
		
		String str = JOptionPane.showInputDialog(Main.gui, String.format("Gemessen:\n\tx = %.2fcm\n\ty = %.2fcm\n\nIst-Wert (in cm) eingeben:", x*100, y*100));
		
		if(str == null) return;
		
		String[] split = str.split(" ");
		
		try {
			double ctrlX = Double.parseDouble(split[0])/100;
			double ctrlY = Double.parseDouble(split[1])/100;
			double ctrlTheta = Math.atan2(ctrlY, ctrlX);
			
			out.println(x+", "+y+", "+theta+", "+ctrlX+", "+ctrlY+", "+ctrlTheta);
			out.flush();
			
		}catch(NumberFormatException e) {
			e.printStackTrace();
		}

	}
	
}
