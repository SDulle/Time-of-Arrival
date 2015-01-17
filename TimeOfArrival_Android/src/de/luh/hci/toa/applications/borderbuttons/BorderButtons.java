package de.luh.hci.toa.applications.borderbuttons;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class BorderButtons extends View {

	private ArrayList<RadialButton> virtualButtons = new ArrayList<RadialButton>();

	private double thetaOffset = 0.0;
	// in percent of Screen.
	private static final float BUTTON_SIZE = 0.05f;

	Handler handler = new Handler();

	Runnable r = new Runnable() {

		public void run() {
			invalidate();
		}
	};

	Paint linePainter;
	Paint filledPainter;

	int paintIndex = -1;

	/**
	 * @param thetaOffset
	 *            the thetaOffset to set
	 */
	public void setThetaOffset(double thetaOffset) {
		this.thetaOffset = thetaOffset % TWOPI;
		this.updateButtons();
	}

	public BorderButtons(Context context, AttributeSet attrs) {
		super(context, attrs);

		linePainter = new Paint();
		linePainter.setColor(Color.BLACK);
		linePainter.setAntiAlias(true);
		linePainter.setStyle(Paint.Style.STROKE);

		filledPainter = new Paint();
		filledPainter.setColor(Color.RED);
		filledPainter.setStyle(Paint.Style.FILL);
		filledPainter.setAntiAlias(true);

		// Buttons kreieren
		for (int i = 0; i < 4; i++) {
			this.addVirtualButton("" + i);
		}

		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float x = event.getX();
				float y = event.getY();

				double a = Math.atan2(y - getHeight() / 2, x - getWidth() / 2);

				// Minus ist wichtig
				input(-a);
				return true;
			}
		});

	}

	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		updateButtons();
		// this.setThetaOffset(Math.atan(((double)h)/w)-0.2);
	}

	public void updateButtons() {
		for (int i = 0; i < virtualButtons.size(); i++) {
			Path path = calculatePath(i,
					Math.min(this.getWidth(), this.getHeight()),
					this.getWidth(), this.getHeight());
			virtualButtons.get(i).updatePositions(path);
		}
	}

	@Override
	public boolean performClick() {
		return super.performClick();
	}

	public void input(double theta) {
		if (theta < 0.0) {
			theta = theta + TWOPI;
		}
		theta = theta - thetaOffset;
		if (theta < 0.0) {
			theta = theta + TWOPI;
		}
		theta = theta % (TWOPI);

		// System.out.println("theta: " + theta);

		for (int i = 0; i < virtualButtons.size(); i++) {
			if (virtualButtons.get(i).checkClick(theta)) {
				paintIndex = i;
				// System.out.println("Button matched: " + i + " with Theta: "
				// + theta);
				break;
			}
		}
	}

	public void addVirtualButton(String text) {
		addVirtualButton(new RadialButton(text));
	}

	public void addVirtualButton(RadialButton button) {
		virtualButtons.add(button);
		button.setView(this);
		for (int i = 0; i < virtualButtons.size(); i++) {
			RadialButton vButton = virtualButtons.get(i);
			vButton.setThetaMin(i * TWOPI / virtualButtons.size());
			vButton.setThetaMax((i + 1) * TWOPI / virtualButtons.size());
		}
		// invalidate View because a new Button was added.
		this.invalidate();
	}

	public void removeVirtualButton(RadialButton button) {
		virtualButtons.remove(button);
		// invalidate View because a Button was removed.
		this.invalidate();
	}

	public int getVirtualButtonCount() {
		return virtualButtons.size();
	}

	private static final double PI2 = Math.PI / 2.0;
	private static final double TWOPI = Math.PI * 2.0;

	private float[] getXY(int i, float min, float width, float height) {
		float x = (float) Math.sin(((double) i) / virtualButtons.size() * TWOPI
				+ PI2 + thetaOffset)
				* min;
		float y = (float) Math.cos(((double) i) / virtualButtons.size() * TWOPI
				+ PI2 + thetaOffset)
				* min;

		float startX = 0.0f;
		float startY = 0.0f;

		float m = 0.0f;
		// Steigung berechnen:
		if (x != 0.0f) {
			m = y / x;
		} else {
			System.err.println("Division durch 0!");
		}

		if (y < -height / 2) {
			y = -height / 2;
			x = y / m;
		}

		if (y > height / 2) {
			y = height / 2;
			x = y / m;
		}

		if (x < -width / 2) {
			x = -width / 2;
			y = x * m;
		}

		if (x > width / 2) {
			x = width / 2;
			y = x * m;
		}

		// y = m* x;
		// x= y/m;
		if (y <= 0) {
			if (x >= width / 2) {
				startX = width / 2 - BUTTON_SIZE * min;
				startY = startX * m;
			} else if (x <= -width / 2) {
				startX = -width / 2 + BUTTON_SIZE * min;
				startY = startX * m;
			} else {
				startY = BUTTON_SIZE * min - height / 2;
				startX = startY / m;
			}
		} else {
			if (x >= width / 2) {
				startX = width / 2 - BUTTON_SIZE * min;
				startY = startX * m;
			} else if (x <= -width / 2) {
				startX = -width / 2 + BUTTON_SIZE * min;
				startY = startX * m;
			} else {
				startY = height / 2 - BUTTON_SIZE * min;
				startX = startY / m;
			}
		}

		startX = Math.max(-width / 2 + BUTTON_SIZE * min,
				Math.min(startX, width / 2 - BUTTON_SIZE * min));
		startY = Math.max(-height / 2 + BUTTON_SIZE * min,
				Math.min(startY, height / 2 - BUTTON_SIZE * min));
		float[] ret = { startX, startY, x, y };
		return ret;
	}

	private Path calculatePath(int i, float min, float width, float height) {
		Path path = new Path();

		float[] coords = getXY(i, min, width, height);

		float startX = coords[0];
		float startY = coords[1];
		float x = coords[2];
		float y = coords[3];

		coords = getXY(i + 1, min, width, height);

		float startX1 = coords[0];
		float startY1 = coords[1];
		float x1 = coords[2];
		float y1 = coords[3];

		path.moveTo(startX + width / 2, startY + height / 2);
		path.lineTo(x + width / 2, y + height / 2);

		// if (Math.abs(x1 - x) >= min && min == getWidth()) {
		// // Punkte oben unten einf�gen
		// path.lineTo(x1, y1);
		//
		// path.lineTo(x1 + width / 2, y1 + height / 2);
		// path.lineTo(startX1 + width / 2, startY1 + height / 2);
		//
		// }
		//
		// if (Math.abs(y1 - y) >= min && min == getHeight()) {
		//
		// }

		this.addCorners(path, (int) (x + width / 2), (int) (y + height / 2),
				(int) (x1 + width / 2), (int) (y1 + height / 2), (int) width,
				(int) height, 0, 0, false);
		path.lineTo(x1 + width / 2, y1 + height / 2);
		path.lineTo(startX1 + width / 2, startY1 + height / 2);
		this.addCorners(path, (int) (startX + width / 2),
				(int) (startY + height / 2), (int) (startX1 + width / 2),
				(int) (startY1 + height / 2), (int) width
						- (int) (2 * BUTTON_SIZE * min), (int) height
						- (int) (2 * BUTTON_SIZE * min),
				(int) (BUTTON_SIZE * min), (int) (BUTTON_SIZE * min), true);

		/*
		 * if (x >= width / 2 && y1 <= -height / 2) { path.lineTo(width, 0);
		 * path.lineTo(x1 + width / 2, y1 + height / 2); path.lineTo(startX1 +
		 * width / 2, startY1 + height / 2); path.lineTo(width - min *
		 * BUTTON_SIZE, min * BUTTON_SIZE);
		 * 
		 * } else if (x1 <= -width / 2 && y <= -height / 2) { path.lineTo(0, 0);
		 * path.lineTo(x1 + width / 2, y1 + height / 2); path.lineTo(startX1 +
		 * width / 2, startY1 + height / 2); path.lineTo(BUTTON_SIZE * min, min
		 * * BUTTON_SIZE); } else if (x <= -width / 2 && y1 >= height / 2) {
		 * path.lineTo(0, height); path.lineTo(x1 + width / 2, y1 + height / 2);
		 * path.lineTo(startX1 + width / 2, startY1 + height / 2);
		 * path.lineTo(BUTTON_SIZE * min, height - min * BUTTON_SIZE); } else if
		 * (x1 >= width / 2 && y >= height / 2) { path.lineTo(width, height);
		 * path.lineTo(x1 + width / 2, y1 + height / 2); path.lineTo(startX1 +
		 * width / 2, startY1 + height / 2); path.lineTo(width - BUTTON_SIZE *
		 * min, height - min * BUTTON_SIZE); } else {
		 * 
		 * path.lineTo(x1 + width / 2, y1 + height / 2); path.lineTo(startX1 +
		 * width / 2, startY1 + height / 2); }
		 */

		path.lineTo(startX + width / 2, startY + height / 2);
		path.close();
		return path;
	}

	private void addCorners(Path path, int startX, int startY, int endX,
			int endY, int width, int height, int minX, int minY, boolean reverse) {
		
		boolean oneButton = (endY == startY && endX == startX);

		ArrayList<PointF> linesTo = new ArrayList<PointF>();

//		System.out.println("startX, " + startX + "endX: " + endX + " width: "
//				+ width + " startY: " + startY + " endY: " + endY + " height: "
//				+ height + " min: " + minX);

		int x = startX;
		int y = startY;

		if (x == width + minX) {
			for (int y1 = startY; y1 >= minY
					&& (!(y1 == endY && x == endX)||oneButton); y1--) {
				if (y1 == minY || y1 == height + minY) {
					linesTo.add(new PointF(x, y1));
				}
				y = y1;
			}
			if (y < minY) {
				y = minY;
			}
		}

		if (y == minY) {
			// System.out.println("X: " + x);
			for (int x1 = x; x1 >= minX
					&& (!(x1 == endX && y == endY) ||oneButton); x1--) {
				if (x1 == minX || x1 == width + minX) {
					linesTo.add(new PointF(x1, y));
				}
				x = x1;
			}
			if (x < minX) {
				x = minX;
			}
		}

		if (x == minX) {
			for (int y1 = startY; y1 <= height + minY
					&& (!(y1 == endY && x == endX)||oneButton); y1++) {
				if (y1 == minY || y1 == height + minY) {
					linesTo.add(new PointF(x, y1));
					//path.lineTo(x, y1);
				}
				y = y1;
			}
			if (y > height + minY) {
				y = height + minY;
			}
		}

		if (y == height + minY) {
			for (int x1 = x; x1 <= width + minX
					&& (!(x1 == endX && y == endY)||oneButton ); x1++) {
				if (x1 == minX || x1 == width + minX) {
					linesTo.add(new PointF(x1, y));
				}
				x = x1;
			}
			if (x > width + minX) {
				x = width + minX;
			}
		}
		

		if (reverse) {
			for (int i = linesTo.size() - 1; i >= 0; i--) {
				path.lineTo(linesTo.get(i).x, linesTo.get(i).y);
			}
		} else {
			for (int i = 0; i < linesTo.size(); i++) {
				path.lineTo(linesTo.get(i).x, linesTo.get(i).y);
			}
		}

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawColor(Color.WHITE);

		for (int i = 0; i < virtualButtons.size(); i++) {
			virtualButtons.get(i).paint(canvas, linePainter);
		}

		int width = canvas.getWidth();
		int height = canvas.getHeight();

		int min = Math.min(width, height);

		if (paintIndex != -1) {

			virtualButtons.get(paintIndex).paint(canvas, filledPainter);
			handler.removeCallbacks(r);
			handler.postDelayed(r, 1000);

			paintIndex = -1;
		}

		// filledPainter.setColor(Color.WHITE);
		// canvas.drawRect(BUTTON_SIZE * min, BUTTON_SIZE * min, width
		// - BUTTON_SIZE * min, height - BUTTON_SIZE * min, linePainter);
		// filledPainter.setColor(Color.RED);

	}
}
