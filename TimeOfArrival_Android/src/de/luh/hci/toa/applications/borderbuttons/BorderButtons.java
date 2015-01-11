package de.luh.hci.toa.applications.borderbuttons;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class BorderButtons extends View{
	
	private ArrayList<String> virtualButtons = new ArrayList<String>();
	
	Paint linePainter;
	Paint filledPainter;
	
	int paintIndex = -1;

	@SuppressLint("NewApi")
	public BorderButtons(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		linePainter = new Paint();
		linePainter.setColor(Color.BLACK);
		linePainter.setAntiAlias(true);
		
		filledPainter = new Paint();
		filledPainter.setColor(Color.RED);
		filledPainter.setStyle(Paint.Style.FILL);
		filledPainter.setAntiAlias(true);
		
		this.addVirtualButton("1");
		this.addVirtualButton("2");
		this.addVirtualButton("3");
		this.addVirtualButton("4");
		this.addVirtualButton("5");
		this.addVirtualButton("6");
		this.addVirtualButton("7");
		this.addVirtualButton("8");
		this.addVirtualButton("9");
		this.addVirtualButton("10");
		
		
		setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				float x = event.getX();
				float y = event.getY();

				double a = Math.atan2(y - getHeight() / 2, x - getWidth() / 2);
				
				if(a < 0.0){
					a = a + TWOPI;
				}
				a = 2 * Math.PI - a % (TWOPI);

				input(a);
				System.out.println("Winkel: "  + a);

				return true;
			}
		});
	}
	
	
	public void input(double theta){
		for(int i = 0; i< virtualButtons.size(); i++){
			if(theta >=  i*TWOPI/virtualButtons.size() && theta < (i+1)*TWOPI/virtualButtons.size()){
				System.out.println("Button :" + virtualButtons.get(i) + " gedrückt.");
				paintIndex = i;
				this.postInvalidate();
			}
		}
		
		
	}
	
	public void addVirtualButton(String text){
		virtualButtons.add(text);
	}
	
	private static final double PI2 = Math.PI /2.0;
	private static final double TWOPI = Math.PI * 2.0;
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		
		canvas.drawColor(Color.WHITE);
		
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		
		int min = Math.min(width, height);
		
if(paintIndex != -1){
			
			Path path = new Path();
			
			
			float firstX = (float)Math.sin(((double)paintIndex)/virtualButtons.size()*TWOPI + PI2) * min +width/2;
		    float firstY = (float) Math.cos(((double)paintIndex )/virtualButtons.size()*TWOPI + PI2) * min + height/2;
		    float secondX = (float)Math.sin(((double)paintIndex +1)/virtualButtons.size()*TWOPI + PI2) * min +width/2;
		    float secondY= (float)Math.cos(((double)paintIndex +1)/virtualButtons.size()*TWOPI + PI2) * min +height/2;
		    
		  
		    
		    path.moveTo(width/2, height/2);
			path.lineTo((int)firstX, (int)firstY);
			path.lineTo((int)secondX, (int)secondY);
			path.lineTo((int)width/2, (int)height/2);
			path.close();
			System.out.println("Print Path" + paintIndex);
//			System.out.println("1: " + (width/2) + ", " + height/2);
//			System.out.println("2: " + firstX + ", " + firstY);
//			System.out.println("3: " + secondX + ", " + secondY);
			canvas.drawPath(path, filledPainter);
			
			paintIndex = -1;
		}
		
		for(int i = 0; i< virtualButtons.size(); i++){
			float x = (float) Math.sin(((double)i)/virtualButtons.size()*TWOPI + PI2) * min ;
			float y = (float) Math.cos(((double)i)/virtualButtons.size()*TWOPI + PI2) * min ;
			
			float x1 = (float) Math.sin(((double)i + 1)/virtualButtons.size()*TWOPI + PI2) * min;
			float y1 = (float) Math.cos(((double)i + 1)/virtualButtons.size()*TWOPI + PI2) * min;
			
			canvas.drawLine(width/2, height/2, x + width/2, y + height/2, linePainter);
			canvas.drawText("i: " + i, x  * 0.2f + width/2, y * 0.2f + height/2, linePainter);
			//System.out.println("i: " + i + " x Punkt: " + (float)Math.sin(((double)i)/virtualButtons.size()*TWOPI) * width);
			
			canvas.drawText("Button: " + virtualButtons.get(i),  (x + x1)  * 0.2f + width/2, (y+y1) * 0.2f + height/2, linePainter);
		}
		
		
		
	}

}
