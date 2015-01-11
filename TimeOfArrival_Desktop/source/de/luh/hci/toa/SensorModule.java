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
	public static double defaultPropSpeed = 0.0001; //600 m/s

	/**
	 * Unschärfefaktor bei simulierten Erschuetterungen<br>
	 * 
	 * <code>0.01</code> -> Ankunftszeiten varieeren bis +/-1% vom theoretischen Wert
	 */
	public static double fuzzyFactor = 0.01;

	public static class Sensor {
		final Point2D position;
		double propSpeed = defaultPropSpeed;
		
		public Sensor(double x, double y) {
			position = new Point2D.Double(x, y);
		}
	}
	
	public SensorEq[] lastEqs;
	public Point2D[] lastSolution;

	private List<Sensor> sensors = new ArrayList<>();
	private List<TapListener> listeners = new ArrayList<>();
	private OutputStream out = null;
	private DataInputStream in = null;
	
	private boolean isCalibrating = false;
	private PointProvider calibrationProvider = null;

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

		for(Sensor s : sensors) {
			center.setLocation(center.getX()+s.position.getX(), center.getY()+s.position.getY());
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
	public List<Sensor> getSensors() {
		return sensors;
	}

	/**
	 * Die Anzahl der benoetigten Sensorgleichungen
	 * @return
	 */
	public int getEquationCount(int sensorCount) {
		int n = sensorCount;

		return (n*n-n)/2;
	}

	/**
	 * Fuegt einen neuen Sensor hinzu
	 * @param sensor
	 */
	public void addSensor(double x, double y) {
		sensors.add(new Sensor(x, y));
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
	
	public void startCalibration(PointProvider pointProvider) {
		isCalibrating = true;
		calibrationProvider = pointProvider;
		
		System.out.println("yo");
	}
	
	public void calibrate(long[] dt) {
		Point2D point = calibrationProvider.getPoint();

		if(point == null) return;
		
		double accu = 0;
		int n = 0;
		
		for(int i=0; i<getSensorCount(); ++i) {
			for(int j=i+1; j<getSensorCount(); ++j) {
				Sensor si = sensors.get(i);
				Sensor sj = sensors.get(j);
				
				double di = si.position.distance(point);
				double dj = sj.position.distance(point);
				
				double dist = dj-di;
				long t = dt[j]-dt[i];
				
				
				accu += dist/t;
				n++;
			}
		}
		
		double pSpeed = accu/n;
		
		for(Sensor s : sensors) {
			s.propSpeed = pSpeed;
		}
		
		System.out.println(String.format("%.3f m/s", pSpeed*1000000));
	}
	
	public void endCalibration() {
		isCalibrating = false;
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
		for(Sensor s : sensors) {
			double dist = s.position.distance(x, y);
			dt[i] = (long)fuzzy(dist/s.propSpeed, fuzzyFactor);
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

		System.out.println("["+new SimpleDateFormat("HH:mm:ss:SSS").format(new Date())+"]>"+
				Arrays.toString(dt));
		
		if(isCalibrating) {
			calibrate(dt);
			return;
		}
		
		//erstelle sensorgleichungen zwischen allen moeglichen Sensorpaaren
		lastEqs = new SensorEq[getEquationCount(getSensorCount())];
		int k = 0;
		for(int i=0; i<getSensorCount(); ++i) {
			for(int j=i+1; j<getSensorCount(); ++j) {
				Sensor si = sensors.get(i);
				Sensor sj = sensors.get(j);
				lastEqs[k++] = new SensorEq(si.position, sj.position, si.propSpeed*dt[i]-sj.propSpeed*dt[j]);
			}
		}

		//finde punkt an dem das signal als ersten angekommen ist -> startpunkt fuers gradientenferfahren
		Point2D nearest = new Point2D.Double(0, 0);
		for(int i=0; i<getSensorCount(); ++i) {
			if(dt[i]==0) {
				nearest = sensors.get(i).position;
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
	
	private void calculateSensorError(long[] dt) {
		
		SensorEq[] eqs = new SensorEq[getEquationCount(getSensorCount()-1)];
		
		for(int k=0; k<getSensorCount(); ++k) {
			Sensor sk = sensors.get(k);
			
			int l = 0;
			for(int i=0; i<getSensorCount(); ++i) {
				for(int j=i+1; j<getSensorCount(); ++j) {
					if(i==k || j==k) continue;
					Sensor si = sensors.get(i);
					Sensor sj = sensors.get(j);
					eqs[l++] = new SensorEq(si.position, sj.position, si.propSpeed*dt[i]-sj.propSpeed*dt[j]);
				}
			}
			
			
			
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
