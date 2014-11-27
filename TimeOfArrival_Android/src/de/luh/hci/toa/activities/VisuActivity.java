package de.luh.hci.toa.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;
import de.luh.hci.toa.applications.visu.Visu;
import de.luh.hci.toa.network.TapListener;

public class VisuActivity extends Activity implements TapListener {

	public static double xdpi, ydpi;
	public Visu visu;
	
	public GestureDetector gest;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		xdpi = getResources().getDisplayMetrics().xdpi;
		ydpi = getResources().getDisplayMetrics().ydpi;
		
		visu = new Visu(this, new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				visu.tap(e);
				System.out.println("tap");
				return true;
			}
			
			@Override
			public boolean onDown(MotionEvent e) {
				System.out.println("down");
				return super.onDown(e);
			}
		}));
		
		MainActivity.instance.tapReceiver.addTapListener(this);
		setContentView(visu);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		
		MainActivity.instance.tapReceiver.removeTapListener(this);
		visu.end();
	}

	@Override
	public void onTap(double x, double y, double theta) {
		visu.tap(x, y);
	}
	
}
