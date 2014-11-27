package de.luh.hci.toa.activities;

import de.luh.hci.toa.applications.tetris.Tetris;
import de.luh.hci.toa.network.TapListener;
import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

public class TetrisActivity extends Activity implements TapListener {

	Tetris tetris;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		tetris = new Tetris(this);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		MainActivity.instance.tapReceiver.addTapListener(this);
		
		setContentView(tetris);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		tetris.end();
		MainActivity.instance.tapReceiver.removeTapListener(this);
	}

	@Override
	public void onTap(double x, double y, double theta) {
		tetris.input(theta);
	}
}
