package de.luh.hci.toa.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import de.luh.hci.toa.applications.borderbuttons.BorderButtons;
import de.luh.hci.toa.applications.tetris.Tetris;
import de.luh.hci.toa.network.TapListener;

public class ButtonExample extends Activity implements TapListener {

	BorderButtons bb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		bb = new BorderButtons(this, null);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		MainActivity.instance.tapReceiver.addTapListener(this);

		setContentView(bb);
		System.out.println("blabla");
		System.out.println("blabla");

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MainActivity.instance.tapReceiver.removeTapListener(this);
	}

	@Override
	public void onTap(double x, double y, double theta) {
		if(theta<0) theta += 2*Math.PI;
		bb.input(theta);
	}

}
