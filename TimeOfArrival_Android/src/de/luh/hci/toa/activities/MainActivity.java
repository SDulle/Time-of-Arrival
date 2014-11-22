package de.luh.hci.toa.activities;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import de.luh.hci.toa.R;
import de.luh.hci.toa.applications.tetris.Tetromino;
import de.luh.hci.toa.network.TapListener;
import de.luh.hci.toa.network.TapReceiver;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends Activity {
	
	public static MainActivity instance;
	
	public TapReceiver tapReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		instance = this;
		
		try {
			tapReceiver = new TapReceiver();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		tapReceiver.addTapListener(new TapListener() {
			
			@Override
			public void onTap(double x, double y, double theta) {
				System.out.println("tap: "+x+" "+y+" "+theta);
			}
		});
		
		tapReceiver.start();
		
		setContentView(R.layout.main_activity);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if(tapReceiver != null) {
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
}
