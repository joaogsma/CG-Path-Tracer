package geometry;

import common.intersection_types.PointOrLineSegment;
import common.intersection_types.PointOrRay;

public class Point3 implements PointOrRay, PointOrLineSegment {
	public double x, y, z;
	
	public Point3(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public String to_string() {
		return "(" + x + ", " + y + ", " + z +")";
	}
	
	public boolean equals(Point3 p) {
		return distance(p) == 0;
	}
	
	public double distance(Point3 p) {
		final double epsilon = 1e-10;
		double dist = Math.sqrt(Math.pow(x - p.x, 2) + Math.pow(y - p.y, 2) + Math.pow(z - p.z, 2));

		return (dist < epsilon) ? 0 : dist;
	}
}
