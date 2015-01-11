package de.luh.hci.toa.applications.visu;

import android.graphics.Canvas;
import android.graphics.Paint;

public class WaveFunc {
	
	public static int MAX_TICKS = 50;
	public static double PROPAGATION = 0.01;

	private double centerX, centerY;
	private int ticks;
	
	public WaveFunc(double x, double y) {
		centerX = x;
		centerY = y;
	}
	
	public double value(double x, double y) {
		double dx = centerX-x;
		double dy = centerY-y;
		double dist = Math.sqrt(dx*dx+dy*dy);
		
		return getValueForDist(dist);
	}
	
	public boolean tick() {
		++ticks;
		return ticks<MAX_TICKS;
	}
	
	private double getValueForDist(double d) {
		double radius = PROPAGATION*ticks;
		
		if(d > radius) return 0;
		
		if(Math.abs(d-radius)<0.005) return 1;
		
		return 0;
	}
	
}
