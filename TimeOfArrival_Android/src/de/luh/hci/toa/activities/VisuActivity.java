package de.luh.hci.toa.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.webkit.WebView;
import de.luh.hci.toa.applications.WebViewport;
import de.luh.hci.toa.applications.visu.Visu;
import de.luh.hci.toa.network.TapListener;

public class VisuActivity extends Activity implements TapListener {

	public static double xdpi, ydpi;
	public Visu visu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		xdpi = getResources().getDisplayMetrics().xdpi;
		ydpi = getResources().getDisplayMetrics().ydpi;
		
		visu = new Visu(this);
		
		MainActivity.instance.tapReceiver.addTapListener(this);
		//setContentView(visu);
		
		setContentView(new WebViewport(this));
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		
		MainActivity.instance.tapReceiver.removeTapListener(this);
	}

	@Override
	public void onTap(double x, double y, double theta) {
		visu.tap(x, y);
	}
	
}
