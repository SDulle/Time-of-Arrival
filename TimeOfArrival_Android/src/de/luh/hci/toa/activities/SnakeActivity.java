package de.luh.hci.toa.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import de.luh.hci.toa.applications.snake.Snake;
import de.luh.hci.toa.applications.tetris.Tetris;
import de.luh.hci.toa.network.TapListener;

public class SnakeActivity extends Activity implements TapListener {

	private Snake snake;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		snake = new Snake(this);
		

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		MainActivity.instance.tapReceiver.addTapListener(this);
		setContentView(snake);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();


		MainActivity.instance.tapReceiver.removeTapListener(this);
		snake.end();
	}

	@Override
	public void onTap(double x, double y, double theta) {
		snake.turn(snake.getHeadingForAngle(theta));
	}
}
