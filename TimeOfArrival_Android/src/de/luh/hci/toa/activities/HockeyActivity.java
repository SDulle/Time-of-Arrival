package de.luh.hci.toa.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import de.luh.hci.toa.applications.hockey.Hockey;
import de.luh.hci.toa.applications.snake.Snake;
import de.luh.hci.toa.applications.tetris.Tetris;
import de.luh.hci.toa.network.TapListener;

public class HockeyActivity extends Activity implements TapListener {

	private Hockey hockey;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		hockey = new Hockey(this);
		

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		MainActivity.instance.tapReceiver.addTapListener(this);
		setContentView(hockey);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();


		MainActivity.instance.tapReceiver.removeTapListener(this);
		hockey.end();
	}

	@Override
	public void onTap(double x, double y, double theta) {
		hockey.input(theta);
	}
}
