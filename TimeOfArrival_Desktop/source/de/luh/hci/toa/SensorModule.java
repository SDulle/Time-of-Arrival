package de.luh.hci.toa;

import java.awt.geom.Point2D;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SensorModule extends Thread{

	/**
	 * Ausbreitungsgeschwindigkeit von Schall in dem verwendetem Material in m/µs
	 */
	public static double propSpeed = 0.00018; //600 m/s

	/**
	 * Unschärfefaktor bei simulierten Erschuetterungen<br>
	 * 
	 * <code>0.01</code> -> Ankunftszeiten varieeren bis +/-1% vom theoretischen Wert
	 */
	public static double fuzzyFactor = 0.01;

	public SensorEq[] lastEqs;
	public Point2D[] lastSolution;

	private List<Point2D> sensors = new ArrayList<>();
	private List<TapListener> listeners = new ArrayList<>();
	private OutputStream out = null;
	private DataInputStream in = null;

	private static double fuzzy(double value, double deviation) {
		return value*(1+Math.random()*deviation*2-deviation);
	}

	private static long[] normalize(long[] t) {
		long min = Long.MAX_VALUE;

		for(int i=0; i<t.length; ++i) {
			min = Math.min(min, t[i]);
		}

		for(int i=0; i<t.length; ++i) {
			t[i]-=min;
		}

		return t;
	}

	public Point2D getCenter() {
		Point2D center = new Point2D.Double();

		for(Point2D s : sensors) {
			center.setLocation(center.getX()+s.getX(), center.getY()+s.getY());
		}

		center.setLocation(center.getX()/sensors.size(), center.getY()/sensors.size());

		return center;
	}

	/**
	 * Die Anzahl der derzeitigen Sensoren
	 * @return
	 */
	public int getSensorCount() {
		return sensors.size();
	}

	/**
	 * Alle derzeitigen Sensoren
	 * @return
	 */
	public List<Point2D> getSensors() {
		return sensors;
	}

	/**
	 * Die Anzahl der benoetigten Sensorgleichungen
	 * @return
	 */
	public int getEquationCount() {
		int n = getSensorCount();

		return (n*n-n)/2;
	}

	/**
	 * Fuegt einen neuen Sensor hinzu
	 * @param sensor
	 */
	public void addSensor(double x, double y) {
		sensors.add(new Point2D.Double(x, y));
	}

	public void addTapListener(TapListener tapListener) {
		listeners.add(tapListener);
	}

	/**
	 * Loescht alle Sensoren
	 */
	public void resetSensors() {
		sensors.clear();
	}

	public void setOut(OutputStream out) {
		this.out = out;
	}

	public void setIn(InputStream in) {
		this.in = new DataInputStream(in);
	}

	/**
	 * Simuliert eine Erschuetterung an der Position und ruft dann <code>trigger(long[] dt)</code> mit den Verzoegerungszeiten auf
	 * @param x -Position der Erschuetterung
	 * @param y -Position der Erschuetterung
	 * @return siehe <code>trigger(long[] dt)</code>
	 */
	public void trigger(double x, double y) {

		long[] dt = new long[getSensorCount()];
		long min = Long.MAX_VALUE;

		int i=0;
		for(Point2D s : sensors) {
			double dist = s.distance(x, y);
			dt[i] = (long)fuzzy(dist/propSpeed, fuzzyFactor);
			min = Math.min(min, dt[i]);
			++i;
		}

		for(int j=0; j<dt.length; ++j) {
			dt[j]-=min;
		}

		trigger(dt);
	}

	/**
	 * Berechnet zu gegebenen Signalankunftszeiten die Position der Erschuetterung<br>
	 * <b>Wichtig:</b> Es muessen Ankunftszeiten für jeden Sensor gegeben werden. Es sollte daher immer:
	 * <code>dt.length==getSensorCount()</code>
	 * @param dt die gemessenen Ankunftszeiten in Microsekunden. dt[0] -> Ankunftszeit am Sensor 0, usw..
	 * @return den ungefaere Punkt an dem die Erschuetterung auftrat
	 */
	public void trigger(long[] dt) {

		System.out.println("["+new SimpleDateFormat("HH:mm:ss:SSS").format(Date.from(Instant.now()))+"]>"+
				Arrays.toString(dt));
		
		//erstelle sensorgleichungen zwischen allen moeglichen Sensorpaaren
		lastEqs = new SensorEq[getEquationCount()];
		int k = 0;
		for(int i=0; i<getSensorCount(); ++i) {
			for(int j=i+1; j<getSensorCount(); ++j) {
				lastEqs[k++] = new SensorEq(sensors.get(i), sensors.get(j), propSpeed*(dt[i]-dt[j]));
			}
		}

		//finde punkt an dem das signal als ersten angekommen ist -> startpunkt fuers gradientenferfahren
		Point2D nearest = new Point2D.Double(0, 0);
		for(int i=0; i<getSensorCount(); ++i) {
			if(dt[i]==0) {
				nearest = sensors.get(i);
				break;
			}
		}

		//startet das gradientenverfahren
		lastSolution = SensorEq.solve(lastEqs, nearest);

		double x = lastSolution[lastSolution.length-1].getX();
		double y = lastSolution[lastSolution.length-1].getY();

		Point2D c = getCenter();
		double theta = Math.atan2(y-c.getY(), x-c.getX());

		for(TapListener tl : listeners) {
			tl.onTap(x, y, theta);
		}
	}

	@Override
	public void run() {
		while(true) {
			long[] times = new long[getSensorCount()];

			for(int i=0; i<times.length; ++i) {
				try {
					times[i] = in.readInt()&0x00000000ffffffffL;
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}

			trigger(normalize(times));
		}
	}
}
