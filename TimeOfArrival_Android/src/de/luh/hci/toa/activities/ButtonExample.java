package de.luh.hci.toa.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import de.luh.hci.toa.R;
import de.luh.hci.toa.applications.borderbuttons.BorderButtons;
import de.luh.hci.toa.network.TapListener;

public class ButtonExample extends Activity implements TapListener {

	BorderButtons bb;
	View view;
	int number = 0;

	TextView offset;
	TextView offsetText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bb = new BorderButtons(this, null);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		MainActivity.instance.tapReceiver.addTapListener(this);

		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = layoutInflater.inflate(R.layout.button_example_layout, bb, true);

		Button buttonAdd = (Button) view.findViewById(R.id.buttonAdd);
		Button buttonRemove = (Button) view.findViewById(R.id.buttonRemove);
		SeekBar seekbar = (SeekBar) view.findViewById(R.id.seekBar1);
		offset = (TextView) view.findViewById(R.id.textView1);
		offset.setTextColor(Color.BLACK);
		offsetText = (TextView) view.findViewById(R.id.textOffset);
		offsetText.setTextColor(Color.BLACK);

		bb.setThetaOffset(0.0);
		setContentView(bb);

		seekbar.setMax(360);

		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@SuppressLint("NewApi")
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				bb.setThetaOffset(progress / 180.0 * Math.PI);
				offset.setText(progress + "°");
			}
		});

		buttonAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				bb.addVirtualButton("New Button NR: " + number);
				number++;
			}

		});

		buttonRemove.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				bb.removeVirtualButton();

			}

		});

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
