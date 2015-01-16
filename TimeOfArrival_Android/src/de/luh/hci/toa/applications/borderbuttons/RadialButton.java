package de.luh.hci.toa.applications.borderbuttons;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.view.View;

public class RadialButton {
	/**
	 * @param view the view to set
	 */
	public void setView(View view) {
		this.view = view;
	}

	View view;
	Path path = new Path();
	String name;
	/**
	 * @param thetaMin the thetaMin to set
	 */
	public void setThetaMin(double thetaMin) {
		this.thetaMin = thetaMin;
	}



	/**
	 * @param thetaMax the thetaMax to set
	 */
	public void setThetaMax(double thetaMax) {
		this.thetaMax = thetaMax;
	}

	double thetaMin;
	double thetaMax;
	
	private IRadialButtonClickHandler clickHandler;
	
	public RadialButton(String name){
		this.name = name;
	}
	
	
	
	public void updatePositions(ArrayList<PointF> positionList){
		Path path = new Path();
		for(int i = 0; i< positionList.size(); i++){
			PointF f = positionList.get(i);
			if(i == 0){
				path.moveTo(f.x, f.y);
			}else{
				path.lineTo(f.x, f.y);
			}
			if(i == positionList.size() - 1)
			path.lineTo(positionList.get(0).x, positionList.get(0).y);	
		}
		path.close();	
		this.updatePositions(path);
	}
	
	public void updatePositions(Path path){
		this.path = path;
	}
	
	public String getName(){
		return name;
	}
	
	public boolean checkClick(double theta){
		if(theta > thetaMin && theta <= thetaMax){
			if(clickHandler != null){
			clickHandler.performClick(new RadialButtonEvent(this, System.currentTimeMillis(), theta));
			}
			else{
				System.err.println("Empty Clickhandler");
			}
			view.invalidate();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean equals(Object o){
		if( o == null){
			return false;
		}
		
		if(!(o instanceof RadialButton)){
			return false;
		}
		RadialButton other = (RadialButton)o;
		if(!other.name.equals(this.name) || other.thetaMax != this.thetaMax || other.thetaMin != this.thetaMin){
			return false;
		}
		
		return true;
	}
	
	public void paint(Canvas canvas, Paint paint){
		
		canvas.drawPath(path, paint);
	}
	
}
