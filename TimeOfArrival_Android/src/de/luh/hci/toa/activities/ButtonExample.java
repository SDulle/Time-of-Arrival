package de.luh.hci.toa.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import de.luh.hci.toa.applications.borderbuttons.BorderButtons;
import de.luh.hci.toa.applications.borderbuttons.IRadialButtonClickHandler;
import de.luh.hci.toa.applications.borderbuttons.RadialButton;
import de.luh.hci.toa.applications.hockey.Hockey;
import de.luh.hci.toa.applications.tetris.Tetris;
import de.luh.hci.toa.applications.visu.Visu;
import de.luh.hci.toa.applications.webapp.WebViewport;
import de.luh.hci.toa.network.TapListener;

public class ButtonExample extends Activity implements TapListener {

	BorderButtons bb;
	RelativeLayout frameLayout;
	
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
		
		
		bb.addView(webViewPort);
		bb.setThetaOffset(0.0);
		
		
		
		
		//Tetris Sample
		RadialButton left = new RadialButton("Links", (IRadialButtonClickHandler) tetris );
		RadialButton turnLeft = new RadialButton("Links drehen", (IRadialButtonClickHandler) tetris );
		RadialButton right = new RadialButton("Rechts", (IRadialButtonClickHandler) tetris );
		RadialButton turnRight = new RadialButton("Rechts drehen", (IRadialButtonClickHandler) tetris );
		//RadialButton firstButton = new RadialButton("Test", (IRadialButtonClickHandler) tetris);
		bb.addVirtualButton(right);
		bb.addVirtualButton(left);
		bb.addVirtualButton(turnLeft);
		bb.addVirtualButton(turnRight);
		
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
