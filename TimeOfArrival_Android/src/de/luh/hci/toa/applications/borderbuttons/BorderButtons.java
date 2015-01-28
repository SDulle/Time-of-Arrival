package de.luh.hci.toa.applications.borderbuttons;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class BorderButtons extends FrameLayout {
	int width = 0;
	int height = 0;
	int min = 0;

	private ArrayList<RadialButton> virtualButtons = new ArrayList<RadialButton>();

	private double thetaOffset = 0.0;
	// in percent of Screen.
	private static final float BUTTON_SIZE = 0.05f;
	private static final int BUTTON_CLICKED_TIME = 1000;

	Handler handler = new Handler();

	Runnable r = new Runnable() {

		public void run() {
			postInvalidate();
		}
	};

	Paint linePainter;
	Paint filledPainter;

	/**
	 * @param thetaOffset
	 *            the thetaOffset to set
	 */
	public void setThetaOffset(double thetaOffset) {
		this.thetaOffset = thetaOffset % TWOPI;
		if (thetaOffset < 0) {
			thetaOffset = thetaOffset + TWOPI;
		}
		this.updateButtons();
		this.postInvalidate();
	}

	public BorderButtons(Context context) {
		this(context, null);
	}

	public BorderButtons(Context context, AttributeSet attrs) {
		super(context, attrs);

		linePainter = new Paint();
		linePainter.setColor(Color.BLACK);
		linePainter.setAntiAlias(true);
		linePainter.setStyle(Paint.Style.STROKE);
		linePainter.setTextSize(18);

		filledPainter = new Paint();
		filledPainter.setColor(Color.BLUE);
		filledPainter.setStyle(Paint.Style.FILL);
		filledPainter.setAntiAlias(true);
		this.setWillNotDraw(false);

		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if ((event.getAction() == MotionEvent.ACTION_DOWN)) {
					float x = event.getX();
					float y = event.getY();
					double a = Math.atan2(y - getHeight() / 2, x - getWidth()
							/ 2);
					// Minus ist wichtig
					input(-a);
					return true;
				} else {
					return false;
				}
			}
		});
	}

	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		min = Math.min(w, h);
		width = w;
		height = h;
		updateButtons();
	}

	public void updateButtons() {
		for (int i = 0; i < virtualButtons.size(); i++) {
			ArrayList<PointF> positionList = calculatePath(i,
					Math.min(this.getWidth(), this.getHeight()),
					this.getWidth(), this.getHeight());
			virtualButtons.get(i).updatePositions(positionList,
					(int) (BUTTON_SIZE * min));
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
				virtualButtons.get(i).setPressed();
				System.out.println("Button matched: " + i + " with Theta: "
						+ theta);
				handler.postDelayed(r, BUTTON_CLICKED_TIME);
				break;
			}
		}
		postInvalidate();
	}

	public void addVirtualButton(String text) {
		addVirtualButton(new RadialButton(text));
	}

	public void addVirtualButton(RadialButton button) {
		virtualButtons.add(button);
		button.setLinePainter(linePainter);
		button.setFilledPainter(filledPainter);
		button.setView(this);
		for (int i = 0; i < virtualButtons.size(); i++) {
			RadialButton vButton = virtualButtons.get(i);
			vButton.setThetaMin(i * TWOPI / virtualButtons.size());
			vButton.setThetaMax((i + 1) * TWOPI / virtualButtons.size());

		}
		this.updateButtons();
		// invalidate View because a new Button was added.
		postInvalidate();
	}

	public void removeVirtualButton(RadialButton button) {
		virtualButtons.remove(button);
		updateButtons();
		// invalidate View because a Button was removed.
		postInvalidate();
	}

	public void removeVirtualButton() {
		if (virtualButtons.size() > 0) {
			virtualButtons.remove(virtualButtons.size() - 1);
			updateButtons();
		}

		// invalidate View because a Button was removed.
		postInvalidate();
	}

	public int getVirtualButtonCount() {
		return virtualButtons.size();
	}

	private static final double PI2 = Math.PI / 2.0;
	private static final double TWOPI = Math.PI * 2.0;

	private float[] getXY(int i, float min, float width, float height) {
		float max = Math.max(width, height);
		float x = (float) Math.sin(((double) i) / virtualButtons.size() * TWOPI
				+ PI2 + thetaOffset)
				* max;
		float y = (float) Math.cos(((double) i) / virtualButtons.size() * TWOPI
				+ PI2 + thetaOffset)
				* max;

		float startX = 0.0f;
		float startY = 0.0f;
		
//		System.out.println("h2 = "+ height/2 + " w2:" + width/2);
//		
//		System.out.println("X: " + x + " Y: "+ y);

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
		
//		System.out.println(" After X: " + x + " Y: "+ y);

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

	private ArrayList<PointF> calculatePath(int i, float min, float width,
			float height) {
		ArrayList<PointF> path = new ArrayList<PointF>();

		float[] coords = getXY(i, min, width, height);

		float startX = Math.round(coords[0]);
		float startY = Math.round(coords[1]);
		float x = Math.round(coords[2]);
		float y = Math.round(coords[3]);

		coords = getXY(i + 1, min, width, height);

		float startX1 = Math.round(coords[0]);
		float startY1 = Math.round(coords[1]);
		float x1 = Math.round(coords[2]);
		float y1 = Math.round(coords[3]);

		path.add(new PointF(startX + width / 2, startY + height / 2));
		path.add(new PointF(x + width / 2, y + height / 2));

		this.addCorners(path, (int) (x + width / 2), (int) (y + height / 2),
				(int) (x1 + width / 2), (int) (y1 + height / 2), (int) width,
				(int) height, 0, 0, false);
		path.add(new PointF(x1 + width / 2, y1 + height / 2));
		path.add(new PointF(startX1 + width / 2, startY1 + height / 2));
		this.addCorners(path, (int) (startX + width / 2.0),
				(int) (startY + height / 2.0), (int) (startX1 + width / 2),
				(int) (startY1 + height / 2), (int) (width
						- (int) (2.0 * BUTTON_SIZE * min)), (int) (height
						- (int) (2.0 * BUTTON_SIZE * min)),
				(int) (BUTTON_SIZE * min), (int) (BUTTON_SIZE * min), true);
		return path;
	}

	private void addCorners(ArrayList<PointF> path, int startX, int startY,
			int endX, int endY, int width, int height, int minX, int minY,
			boolean reverse) {
		
		boolean b1 = true, b2 = true, b3 = true, b4 =true;

		boolean oneButton = (endY == startY && endX == startX);

		ArrayList<PointF> linesTo = new ArrayList<PointF>();

//		 System.out.println("startX, " + startX + "endX: " + endX + " width: "
//		 + width + " startY: " + startY + " endY: " + endY + " height: "
//		 + height + " min: " + minX);

		int x = startX;
		int y = startY;

		for (int i = 0; i < 2; i++) {

			if (x == width + minX && b1) {
				for (int y1 = y; y1 >= minY
						&& (!(y1 == endY && x == endX) || oneButton); y1--) {
					if (y1 == minY || y1 == height + minY) {
						linesTo.add(new PointF(x, y1));
					}
					y = y1;
				}
				if (y < minY) {
					y = minY;
				}
				b1 = false;
			}

			if (y == minY && b2) {
				for (int x1 = x; x1 >= minX
						&& (!(x1 == endX && y == endY) || oneButton); x1--) {
					if (x1 == minX || x1 == width + minX) {
						linesTo.add(new PointF(x1, y));
					}
					x = x1;
				}
				if (x < minX) {
					x = minX;
				}
				b2 = false;
			}

			if (x == minX && b3) {
				for (int y1 = y; y1 <= height + minY
						&& (!(y1 == endY && x == endX) || oneButton); y1++) {
					if (y1 == minY || y1 == height + minY) {
						linesTo.add(new PointF(x, y1));
					}
					y = y1;
				}
				if (y > height + minY) {
					y = height + minY;
				}
				b3 = false;
			}

			if (y == height + minY && b4) {
				for (int x1 = x; x1 <= width + minX
						&& (!(x1 == endX && y == endY) || oneButton); x1++) {
					if (x1 == minX || x1 == width + minX) {
						linesTo.add(new PointF(x1, y));
					}
					x = x1;
				}
				if (x > width + minX) {
					x = width + minX;
				}
				b4 = false;
			}
		}

		if (reverse) {
			for (int i = linesTo.size() - 1; i >= 0; i--) {
				path.add(linesTo.get(i));
				//System.out.println("Reverse: " + linesTo.get(i).x + ", " + linesTo.get(i).y);
			}
		} else {
			for (int i = 0; i < linesTo.size(); i++) {
				path.add(linesTo.get(i));
				//System.out.println("Not reverse: " + linesTo.get(i).x + ", " + linesTo.get(i).y);
			}
		}

	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		canvas.drawColor(Color.WHITE);
		super.dispatchDraw(canvas);
		for (int i = 0; i < virtualButtons.size(); i++) {
			virtualButtons.get(i).paint(canvas, linePainter);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		width = r - l;
		height = b - t;
		min = Math.min(width, height);

		if (this.getChildCount() > 0) {
			this.getChildAt(0).layout((int) (l + BUTTON_SIZE * min),
					(int) (t + BUTTON_SIZE * min),
					(int) (r - BUTTON_SIZE * min),
					(int) (b - BUTTON_SIZE * min));

		}

		//System.out.println("ChILD COUNT: " + this.getChildCount());
	}
}