package de.luh.hci.toa.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import de.luh.hci.toa.applications.borderbuttons.BorderButtons;
import de.luh.hci.toa.applications.borderbuttons.RadialButton;
import de.luh.hci.toa.applications.tetris.Tetris;
import de.luh.hci.toa.network.TapListener;

public class TetrisActivity extends Activity implements TapListener {

	Tetris tetris;
	BorderButtons borderButtons;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		tetris = new Tetris(this);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		MainActivity.instance.tapReceiver.addTapListener(this);
		
		borderButtons = new BorderButtons(this, null);		
		borderButtons.addView(tetris);
		borderButtons.setThetaOffset(0.0);

		//Tetris Sample
		RadialButton left = new RadialButton("Links", tetris );
		RadialButton turnLeft = new RadialButton("Links drehen", tetris );
		RadialButton right = new RadialButton("Rechts", tetris );
		RadialButton turnRight = new RadialButton("Rechts drehen", tetris );

		borderButtons.addVirtualButton(right);
		borderButtons.addVirtualButton(left);
		borderButtons.addVirtualButton(turnLeft);
		borderButtons.addVirtualButton(turnRight);
		
		setContentView(borderButtons);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		tetris.end();
		MainActivity.instance.tapReceiver.removeTapListener(this);
	}

	@Override
	public void onTap(double x, double y, double theta) {
		borderButtons.input(theta);
	}
}
