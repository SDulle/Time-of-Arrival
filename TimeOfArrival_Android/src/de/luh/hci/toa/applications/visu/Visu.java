package de.luh.hci.toa.applications.visu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import de.luh.hci.toa.activities.VisuActivity;


public class Visu extends View {
	private Paint linePaint = new Paint();
	private Paint textPaint = new Paint();
	
	private double realX = 0;
	private double realY = 0;
	
	private int tapX = 0;
	private int tapY = 0;

	public Visu(Context context) {
		super(context);
		
		linePaint.setColor(Color.RED);
		linePaint.setStrokeWidth(10);
		
		textPaint.setColor(Color.BLACK);
	}
	
	public void tap(double x, double y) {
		tapX = (int)((x/0.0254)*VisuActivity.xdpi);
		tapY = -(int)((y/0.0254)*VisuActivity.ydpi);
		
		postInvalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		
		
		canvas.translate(getWidth()/2, getHeight()/2);
		canvas.drawLine(0, 0, tapX, tapY, linePaint);
		
		canvas.rotate((float)(360*Math.atan2(tapY, tapX)));
		canvas.drawText(realX+" / "+realY, 0, 0, textPaint);
	}
}
