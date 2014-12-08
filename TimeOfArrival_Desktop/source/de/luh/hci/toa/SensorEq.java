package de.luh.hci.toa;

import java.awt.geom.Point2D;
import java.util.Arrays;

public class SensorEq {
	
	public static enum LayerFunction {
		Average,
		Median,
		Min
	}
	
	public static LayerFunction layerFunction = LayerFunction.Average;
	
	private Point2D si, sj;
	private double cdt;
	
	public SensorEq(Point2D si, Point2D sj, double cdt) {
		this.si = si;
		this.sj = sj;
		this.cdt = cdt;
	}
	
	private static double square(double d) {
		return d*d;
	}
	
	
	private static double[] normalize(double[] vec) {
		double len = Math.sqrt(square(vec[0])+square(vec[1]));
		vec[0] /= len;
		vec[1] /= len;
		
		return vec;
	}
	
	public double getValue(double x, double y) {
		return square(
				si.distance(x, y) -
				sj.distance(x, y) -
				cdt);
	}
	
	public double[] getGradient(double x, double y) {
		
		double dist_i = si.distance(x, y);
		double dist_j = sj.distance(x, y);
		
		double v = dist_i - dist_j - cdt;
		
		/*
		 * http://www.wolframalpha.com/input/?i=d%2Fdx+%28sqrt%28%28a-x%29^2%2B%28b-y%29^2%29+-+sqrt%28%28c-x%29^2%2B%28d-y%29^2%29+-+k%29^2
		 */
		double gradX = 2*(((x-si.getX())/dist_i) + ((sj.getX()-x)/dist_j)) * v;
		
		/*
		 * http://www.wolframalpha.com/input/?i=d%2Fdy+%28sqrt%28%28a-x%29^2%2B%28b-y%29^2%29+-+sqrt%28%28c-x%29^2%2B%28d-y%29^2%29+-+k%29^2
		 */
		double gradY = 2*(((y-si.getY())/dist_i) + ((sj.getY()-y)/dist_j)) * v;
		
		return new double[] {gradX, gradY};
	}
	
	public static double getValue(SensorEq[] eqs, double x, double y) {
		
		if(layerFunction == LayerFunction.Average) {
			double ret = 0;
			for(SensorEq e : eqs) {
				ret+=e.getValue(x, y);
			}
			return ret;
		} 
		
		if(layerFunction == LayerFunction.Min) {
			double min = Double.POSITIVE_INFINITY;
			
			for(SensorEq e : eqs) {
				min = Math.min(e.getValue(x, y), min);
			}
			return min;
		}
		
		if(layerFunction == LayerFunction.Median) {
			double[] vals = new double[eqs.length];
			
			for(int i=0; i<vals.length; ++i) {
				vals[i] = eqs[i].getValue(x, y);
			}
			
			Arrays.sort(vals);
			
			return vals[vals.length/2];
		}

		
		
		return 0;
	}
	
	public static double[] getGradient(SensorEq[] eqs, double x, double y) {
		double[] ret = new double[] {0, 0};
		for(SensorEq e : eqs) {
			double[] tmp = e.getGradient(x, y);
			ret[0]+=tmp[0];
			ret[1]+=tmp[1];
		}
		return ret;
	}
	
	public static Point2D[] solve(SensorEq[] eqs, Point2D startPoint) {
		
		Point2D p = new Point2D.Double(startPoint.getX()+0.001, startPoint.getY());
		double[] g = new double[] {0 , 0};
		
		double a = 1;
		
		Point2D[] ret = new Point2D[100];
		
		for(int i=0; i<100; ++i) {
			ret[i] = p;
			
			g = normalize(getGradient(eqs, p.getX(), p.getY()));
			
			p = new Point2D.Double(
					p.getX()-(0.1*a*g[0]), 
					p.getY()-(0.1*a*g[1]));
			a-=0.01;
		}
		
		return ret;
	}
}
