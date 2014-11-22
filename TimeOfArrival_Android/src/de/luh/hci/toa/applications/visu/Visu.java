package de.luh.hci.toa.applications.visu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import de.luh.hci.toa.activities.VisuActivity;


public class Visu extends View{

	public static int TICK_DELAY = 100;

	private List<WaveFunc> waves = new ArrayList<WaveFunc>();
	private Handler loop = new Handler();
	private boolean alive = true;
	private Paint paint = new Paint();

	public Visu(Context context, final GestureDetector gest) {
		super(context);

		setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					tap(event);
					return true;
				}
				return false;
			}
		});

		loop.post(new Runnable() {

			@Override
			public void run() {

				if(alive) {
					tick();
					invalidate();
					loop.postDelayed(this, 50);
				}
			}
		});
	}

	public void end() {
		alive = false;
	}

	public void tap(MotionEvent e) {
		tap(xTOcm(e.getX()), yTOcm(e.getY()));
		invalidate();
	}

	public synchronized void tap(double x, double y) {
		waves.add(new WaveFunc(x, y));

		System.out.println("wave "+x+" "+y);
	}

	public synchronized void tick() {
		for(Iterator<WaveFunc> i = waves.iterator(); i.hasNext(); ) {
			WaveFunc w = i.next();
			if(!w.tick()) i.remove();
		}
	}

	public double yTOcm(double y) {
		y = getHeight()-y;
		y = y-getHeight()/2;

		return (2.54*y)/(100*VisuActivity.ydpi);
	}

	public double xTOcm(double x) {
		x = x-getWidth()/2;

		return (2.54*x)/(100*VisuActivity.xdpi);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		//super.draw(canvas);
		
		//canvas.drawColor(Color.BLACK);
		
		for(int y=0; y<getHeight(); y+=10) {
			for(int x=0; x<getWidth(); x+=10) {
				paint.setColor(colorForValue(valueAt(xTOcm(x), yTOcm(y))));
				canvas.drawPoint(x, y, paint);
			}
		}

	}

	private synchronized double valueAt(double x, double y) {
		double val = 0;
		for(WaveFunc w : waves) {
			val+=w.value(x, y);
		}
		return val;
	}

	public int colorForValue(double value) {
		if(value == 0) return Color.BLACK;
		return Color.HSVToColor(new float[] {(float)value*10, 1, 1});
	}

}
