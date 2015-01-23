package de.luh.hci.toa.activities;

import de.luh.hci.toa.applications.borderbuttons.BorderButtons;
import de.luh.hci.toa.applications.borderbuttons.IRadialButtonClickHandler;
import de.luh.hci.toa.applications.borderbuttons.RadialButton;
import de.luh.hci.toa.applications.hockey.Hockey;
import de.luh.hci.toa.applications.tetris.Tetris;
import de.luh.hci.toa.applications.visu.Visu;
import de.luh.hci.toa.applications.webapp.WebViewport;
import de.luh.hci.toa.network.TapListener;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class WebViewActivity extends Activity implements TapListener  {
	BorderButtons bb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WebViewport webView = new WebViewport(this);
		bb = new BorderButtons(this, null);
		

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		MainActivity.instance.tapReceiver.addTapListener(this);
		
		
		bb.addView(webView);
		bb.setThetaOffset(Math.PI/2.0);
		
		//Tetris Sample
		RadialButton left = new RadialButton("left", webView);
		RadialButton right = new RadialButton("right", webView);

		bb.addVirtualButton(left);
		bb.addVirtualButton(right);
			
		setContentView(bb);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MainActivity.instance.tapReceiver.removeTapListener(this);
	}

	@Override
	public void onTap(double x, double y, double theta) {
		// if(theta<0) theta += 2*Math.PI;
		bb.input(theta);
	}

}
