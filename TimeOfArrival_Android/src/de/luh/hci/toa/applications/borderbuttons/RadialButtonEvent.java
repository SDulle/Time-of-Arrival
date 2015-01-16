package de.luh.hci.toa.applications.borderbuttons;

public class RadialButtonEvent {
	public double theta;
	public RadialButton button;
	public long timeStamp;
	
	public RadialButtonEvent(RadialButton button, long timeStamp, double theta){
		this.timeStamp = timeStamp;
		this.theta = theta;
		this.button = button;
	}
}
