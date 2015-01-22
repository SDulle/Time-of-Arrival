package de.luh.hci.toa.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import de.luh.hci.toa.applications.borderbuttons.BorderButtons;
import de.luh.hci.toa.applications.borderbuttons.FrameLayoutBB;
import de.luh.hci.toa.applications.hockey.Hockey;
import de.luh.hci.toa.applications.tetris.Tetris;
import de.luh.hci.toa.applications.visu.Visu;
import de.luh.hci.toa.applications.webapp.WebViewport;
import de.luh.hci.toa.network.TapListener;

public class ButtonExample extends Activity implements TapListener {

	BorderButtons bb;
	RelativeLayout frameLayout;
	FrameLayoutBB layout;
	View tetris;
	View visu;
	View hockey;
	View webViewPort; 


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tetris = new Tetris(this);
		visu = new Visu(this);
		hockey =new Hockey(this);
		bb = new BorderButtons(this, null);
		webViewPort = new WebViewport(this);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		MainActivity.instance.tapReceiver.addTapListener(this);
		bb.addView(hockey);
		setContentView(tetris);

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
