package de.luh.hci.toa.applications.borderbuttons;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;

public class RadialButton {
	Paint linePainter;
	Paint filledPainter;

	/**
	 * @param linePainter
	 *            the linePainter to set
	 */
	public void setLinePainter(Paint linePainter) {
		this.linePainter = linePainter;
	}

	/**
	 * @param filledPainter
	 *            the filledPainter to set
	 */
	public void setFilledPainter(Paint filledPainter) {
		this.filledPainter = filledPainter;
	}

	ArrayList<PointF> corners;
	PointF textPos = null;
	int turnText = 0;
	Path path = new Path();
	String name;
	double thetaMin;
	double thetaMax;
	View view;
	long pressedTime;

	public void setPressed() {
		pressedTime = System.currentTimeMillis() + 1000;
	}

	/**
	 * @param view
	 *            the view to set
	 */
	public void setView(View view) {
		this.view = view;
	}

	private IRadialButtonClickHandler clickHandler;

	/**
	 * @param thetaMin
	 *            the thetaMin to set
	 */
	public void setThetaMin(double thetaMin) {
		this.thetaMin = thetaMin;
	}

	/**
	 * @param thetaMax
	 *            the thetaMax to set
	 */
	public void setThetaMax(double thetaMax) {
		this.thetaMax = thetaMax;
	}

	public RadialButton(String name) {
		this(name, null);
	}

	public RadialButton(String name, IRadialButtonClickHandler clickHandler) {
		corners = new ArrayList<PointF>();
		this.name = name;
		this.clickHandler = clickHandler;
		textPos = new PointF();
	}

	public void setClickHandler(IRadialButtonClickHandler clickHandler) {
		this.clickHandler = clickHandler;
	}

	public void updatePositions(ArrayList<PointF> positionList, int width) {
		corners = positionList;
		updateTextPosition(width);
		path = new Path();
		for (int i = 0; i < positionList.size(); i++) {
			PointF f = positionList.get(i);
			if (i == 0) {
				path.moveTo(f.x, f.y);
			} else {
				path.lineTo(f.x, f.y);
			}
			if (i == positionList.size() - 1)
				path.lineTo(positionList.get(0).x, positionList.get(0).y);
		}
		path.close();
	}

	private void updateTextPosition(int width) {
		float maxDistance = 0.0f;
		PointF p1, p2;
		float distance;

		textPos = new PointF();
		Rect textBounds = new Rect();
		linePainter.getTextBounds(name, 0, name.length(), textBounds);
		//System.out.println("WEite von Text: " + textBounds.width());

		for (int i = 0; i < corners.size(); i++) {

			p1 = corners.get(i);
			p2 = corners.get((i + 1) % corners.size());
			distance = (float) Math.sqrt((p1.x - p2.x) * (p1.x - p2.x)
					+ (p1.y - p2.y) * (p1.y - p2.y));
			if (distance > maxDistance) {
				maxDistance = distance;
				textPos.x = Math.min(p1.x, p2.x)
						+ Math.abs((p1.x - p2.x) / 2.0f);
				textPos.y = Math.min(p1.y, p2.y)
						+ Math.abs((p1.y - p2.y) / 2.0f);
				if (p1.x == p2.x) {
					if (p1.x == 0.0f) {
						textPos.x += 3.0 * width / 4.0f;
						
						turnText = 270;
						textPos.y += textBounds.width()/2;
					} else {
						turnText = 90;
						textPos.x -= 3.0f * width / 4.0f;
						textPos.y -= textBounds.width() /2;
					}
					
				} else if (p1.y == p2.y) {
					if (p1.y == 0.0f) {
						
						textPos.y += 3.0f * width / 4.0f;
						turnText = 0;
					} else {
						textPos.y -= width / 4.0f;
						turnText = 0;
					}
					textPos.x -= textBounds.width()/2;
				}
			}
		}
		//System.out.println("Punkt: " + textPos.x + ", " + textPos.y);
	}

	public String getName() {
		return name;
	}

	public boolean checkClick(double theta) {
		if (theta > thetaMin && theta <= thetaMax) {
			if (clickHandler != null) {
				clickHandler.performClick(new RadialButtonEvent(this, System
						.currentTimeMillis(), theta));
			} else {
				System.err.println("Empty Clickhandler");
			}
			view.postInvalidate();
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}

		if (!(o instanceof RadialButton)) {
			return false;
		}
		RadialButton other = (RadialButton) o;
		if (!other.name.equals(this.name) || other.thetaMax != this.thetaMax
				|| other.thetaMin != this.thetaMin) {
			return false;
		}

		return true;
	}

	public void paint(Canvas canvas, Paint paint) {

		if (this.pressedTime > System.currentTimeMillis()) {
			filledPainter.setColor(Color.BLUE);
		}

		canvas.drawPath(path, filledPainter);
		filledPainter.setColor(Color.WHITE);

		canvas.drawPath(path, linePainter);

		if (turnText != 0) {

			canvas.save();

			canvas.rotate(turnText, textPos.x, textPos.y);
			canvas.drawText(name, textPos.x, textPos.y, linePainter);

			linePainter.setAntiAlias(false);

			canvas.restore();
			linePainter.setAntiAlias(true);

		} else {
			canvas.drawText(name, textPos.x, textPos.y, linePainter);
		}
	}
}
