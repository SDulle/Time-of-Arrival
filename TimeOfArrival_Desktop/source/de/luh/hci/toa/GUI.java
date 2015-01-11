package de.luh.hci.toa;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GUI extends JPanel implements TapListener {

	//1px == 0.25cm
	double dy = 0.001;
	double dx = 0.001;

	Point2D lastTap;

	SensorModule sm;

	public boolean showField = false;
	public boolean showIterations = true;
	public boolean autoTap = false;
	
	private double theta;

	public GUI(final SensorModule sm) {
		super();

		setBackground(Color.GRAY);

		this.sm = sm;

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.getButton() == 3) {
					Point2D p = translate(e.getX(), e.getY());
					
					sm.addSensor(p.getX(), p.getY());
					if(lastTap != null) {
						tap(lastTap);
					} else {
						repaint();
					}
				} else if(e.getButton() == 1) {
					tap(translate(e.getX(), e.getY()));
				}
			}
		});

		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if(autoTap) {
					tap(translate(e.getX(), e.getY()));
				}
			}
		});
		
		sm.addTapListener(this);
	}

	public void reset() {
		sm.resetSensors();
		if(lastTap != null) {
			tap(lastTap);
		} else {
			repaint();
		}
	}

	private void tap(Point2D p) {
		lastTap = p;
		sm.trigger(p.getX(), p.getY());
	}

	private static void drawCross(Graphics g, int x, int y) {
		g.drawLine(x-5, y-5, x+5, y+5);
		g.drawLine(x-5, y+5, x+5, y-5);
	}

	//berechnet zu einem funktionswert eine passende farbe
	public Color getColorForValue(double value) {
		float f = (float)(-Math.log(value));

		return Color.getHSBColor(f/10, 1, 1);
	}

	//uebersetzt einen punkt vom bildschirm-koordinatensystem ins sensor-koordinatensystem
	private Point2D translate(int x, int y) {
		return new Point2D.Double((x-getWidth()/2)*dx, -(y-getHeight()/2)*dy);
	}

	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2d = (Graphics2D)g;
		
		//Flaeche zeichnen
		if(showField && sm.lastEqs != null) {
			for(int y=0; y<getHeight(); ++y) {
				for(int x=0; x<getWidth(); ++x) {
					double xt = (x-getWidth()/2)*dx;
					double yt = -(y-getHeight()/2)*dy;

					double value = SensorEq.getValue(sm.lastEqs, xt, yt);
					g2d.setColor(getColorForValue(value));
					g2d.fillRect(x, y, 1, 1);

				}
			}
		}

		//Transformation, sodass Ursprung in der Mitte liegt
		g2d.translate(getWidth()/2, getHeight()/2);
		g2d.scale(1, -1);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		//Koordinaten-Achsen zeichnen
		g2d.setColor(Color.black);
		g2d.drawLine(0, getHeight(), 0, -getHeight());
		g2d.drawLine(getWidth(), 0, -getWidth(), 0);

		//Sensoren zeichnen
		g2d.setColor(Color.BLACK);
		for(SensorModule.Sensor s : sm.getSensors()) {
			g2d.fillOval(-5+(int)(s.position.getX()/dx), -5+(int)(s.position.getY()/dy), 10, 10);
		}

		//Kreuz an der getippten stelle zeichnen
		if(lastTap != null) {
			g2d.setColor(Color.BLACK);
			drawCross(g2d, (int)(lastTap.getX()/dx), (int)(lastTap.getY()/dy));
		}

		//Loesung zeichnen
		if(sm.lastSolution != null) {
			if(showIterations) { //Iterationsschritte zeichnen
				g2d.setColor(Color.BLACK);
				Path2D path = new Path2D.Double();

				boolean bool = false;

				for(Point2D p : sm.lastSolution) {
					int x = (int)(p.getX()/dx);
					int y = (int)(p.getY()/dy);

					if(!bool) {
						path.moveTo(x, y);
						bool = true;
					} else {
						path.lineTo(x, y);
					}
				}
				g2d.draw(path);
			}

			Point2D end = sm.lastSolution[sm.lastSolution.length-1];

			int x = (int)(end.getX()/dx);
			int y = (int)(end.getY()/dy);
			g2d.setColor(Color.WHITE);
			g2d.fillOval(x-5, y-5, 10, 10);
		}

		//Winkel zeichnen
		g.setColor(Color.WHITE);
		Point2D center = sm.getCenter();
		int cx = (int)(center.getX()/dx);
		int cy = (int)(center.getY()/dy);
		g2d.drawOval(cx-5, cy-5, 10, 10);
		g2d.drawArc(cx-20, cy-20, 40, 40, 0, (int)(-theta*(180/Math.PI)));
		
	}

	@Override
	public void onTap(double x, double y, double theta) {
		this.theta = theta;
		repaint();
	}

}
