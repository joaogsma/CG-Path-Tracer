package geometry;

import common.intersection_types.PointOrRay;

public class Plane {
	public double a, b, c, d;
	public Vector3 normal;
	
	public Plane(double a, double b, double c, double d){
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
	
	public Plane(Point3 p1, Point3 p2, Point3 p3) {
		Vector3 v1 = new Vector3(p1, p2);
		Vector3 v2 = new Vector3(p1, p3);
		
		normal = v1.cross(v2);
		
		a = normal.x;
		b = normal.y;
		c = normal.z;
		d = - normal.x*p1.x - normal.y*p1.y - normal.z*p1.z;
		
		normal.normalize();
	}
	
	public PointOrRay intersect(Ray ray){
		double xo = ray.origin.x, yo = ray.origin.y, zo = ray.origin.z;
		double xv = ray.direction.x, yv = ray.direction.y, zv = ray.direction.z;

		double numerator = (-1) * (a*xo + b*yo + c*zo + d);
		double denominator = a*xv + b*yv + c*zv;
		
		if (numerator == 0 && denominator == 0){
			return ray; // Intersection is the entire ray
		} else if (denominator == 0){
			return null; // Ray and plane are parallel 
		} else {
			// The ray's line intersects the plane
			double t = numerator/denominator;
			if (t < 0) return null; // Ray's line intersects, but not the ray
			
			return ray.point(t);
		}
	}

}
