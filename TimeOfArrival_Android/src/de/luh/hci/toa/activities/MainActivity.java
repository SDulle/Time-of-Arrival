package de.luh.hci.toa.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import de.luh.hci.toa.R;
import de.luh.hci.toa.network.TapListener;
import de.luh.hci.toa.network.TapReceiver;
import de.luh.hci.toa.network.UDPAdapter;

public class MainActivity extends Activity {

	public static MainActivity instance;

	public TapReceiver tapReceiver;

	public EditText connectionInfoText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		instance = this;

		tapReceiver = new TapReceiver();

		tapReceiver.addTapListener(new TapListener() {

			@Override
			public void onTap(double x, double y, double theta) {
				System.out.println("tap: " + x + " " + y + " " + theta);
			}
		});

		setContentView(R.layout.main_activity);

		// connectionInfoText = (EditText)findViewById(R.id.editText1);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (tapReceiver != null) {
			tapReceiver.end();
		}

	}

	public void startSnake(View view) {
		Intent snakeIntent = new Intent(this, SnakeActivity.class);
		startActivity(snakeIntent);
	}

	public void startVisu(View view) {
		Intent visuIntent = new Intent(this, VisuActivity.class);
		startActivity(visuIntent);
	}

	public void startTetris(View view) {
		Intent tetrisIntent = new Intent(this, TetrisActivity.class);
		startActivity(tetrisIntent);
	}

	public void startButtonExample(View view) {
		Intent buttonExampleIntent = new Intent(this, ButtonExample.class);
		startActivity(buttonExampleIntent);
	}

	public void connect(View view) {

		UDPAdapter adapter = new UDPAdapter();

		try {
			adapter.open("");
			tapReceiver.setNetworkAdapter(adapter);
			tapReceiver.start();
			findViewById(R.id.toggleButton1).setEnabled(false);
			findViewById(R.id.toggleButton1).setBackgroundColor(Color.GREEN);

			System.out.println("Network start");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// private void deactivateNetworkOptions() {
	// findViewById(R.id.radio0).setEnabled(false);
	// findViewById(R.id.radio1).setEnabled(false);
	// findViewById(R.id.radio2).setEnabled(false);
	// findViewById(R.id.radio3).setEnabled(false);
	// }
	//
	// public void switchOff(View view) {
	// System.out.println("network off");
	// }
	//
	// public void switchUDP(View view) {
	// System.out.println("network udp");
	//
	// UDPAdapter adapter = new UDPAdapter();
	//
	// try {
	// adapter.open("");
	// } catch(Exception e) {
	// e.printStackTrace();
	// }
	//
	// tapReceiver.setNetworkAdapter(adapter);
	//
	// tapReceiver.start();
	//
	// deactivateNetworkOptions();
	// }
	//
	// public void switchTCP(View view) {
	// System.out.println("network tcp "+connectionInfoText.getText());
	// }
	//
	// public void switchBT(View view) {
	// System.out.println("network bt");
	// }

}
