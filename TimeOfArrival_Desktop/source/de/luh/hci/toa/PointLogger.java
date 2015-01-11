package de.luh.hci.toa;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.channels.Pipe;

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
			
			out.println("messX, messY, messTheta, sollTheta");
			out.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public int i = 0;

	@Override
	public void onTap(double x, double y, double theta) {
		if(out == null) return;
		
		String initialSelection = ""+((i++)*10%360);
		String message = String.format("Gemessen:\n\tx = %.2fcm\n\ty = %.2fcm\n\nIst-Wert (in Grad) eingeben:", x*100, y*100);
		
		String str = JOptionPane.showInputDialog(Main.gui, message, initialSelection);
		
		if(str == null) return;
		
		String[] split = str.split(" ");
		
		try {
			double ctrlTheta = Double.parseDouble(split[0]);
			
			double winkel = (theta<0 ? theta+2*Math.PI : theta)%(2*Math.PI);
			
			winkel *= 360/(2*Math.PI);
			
			out.println(x+", "+y+", "+winkel+", "+ctrlTheta);
			out.flush();
			
		}catch(NumberFormatException e) {
			e.printStackTrace();
		}

	}
	
}
