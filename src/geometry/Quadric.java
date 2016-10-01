package geometry;

import java.util.LinkedList;

public class Quadric {
	public double a, b, c, d, e, f, g, h, i, j;
	
	// RayCife:
	// ax^2 + by^2 + cz^2 + 2dxy + 2eyz + 2fxz + 2gx + 2hy + 2jz + k = 0
	// Projeto:
	// Ax^2 + By^2 + Cz^2 + Dxy + Exz + Fyz + Gx + Hy + Iz + J = 0
	public Quadric(double a, double b, double c, double d, double e, double f, double g, double h, double i,
			double j) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.e = e;
		this.f = f;
		this.g = g;
		this.h = h;
		this.i = i;
		this.j = j;
	}
	
	public int test_point(Point3 p) {
		double result = a*p.x*p.x + b*p.y*p.y + c*p.z*p.z + d*p.x*p.y+ e*p.x*p.z + f*p.y*p.z + g*p.x + h*p.y + i*p.z + j; 
//		double result = a*Math.pow(p.x, 2) + 2*b*p.x*p.y + 2*c*p.x*p.z + 2*d*p.x + 
//				e*Math.pow(p.y, 2) + 2*f*p.y*p.z + 2*g*p.y + h*Math.pow(p.z, 2) + i*p.z + j;
		return (Math.abs(result) <= 1e-10) ? 0 : (int) Math.signum(result);
	}
	
	public SurfacePoint[] intersect(Ray ray) {
		SurfacePoint[] intersections;
		LinkedList<Double> ts = new LinkedList<Double>();
		double xo = ray.origin.x, yo = ray.origin.y, zo = ray.origin.z;
		double xv = ray.direction.x, yv = ray.direction.y, zv = ray.direction.z;

		// a_, b_ and c_ are the constants from the intersection quadratic
		// equation a_*t^2 + b_*t + c_ = 0
		double a_ = a*Math.pow(xv, 2) + b*Math.pow(yv, 2) + c*Math.pow(zv, 2) + d*xv*yv + e*xv*zv + f*yv*zv;
		double b_ = 2*a*xo*xv + 2*b*yo*yv + 2*c*zo*zv + d*(xo*yv + xv*yo) + e*(xo*zv + xv*zo) + f*(yo*zv + yv*zo) 
				+ g*xv + h*yv + i*zv;
		double c_ = a*Math.pow(xo, 2) + b*Math.pow(yo, 2) + c*Math.pow(zo, 2) + d*xo*yo + e*xo*zo + f*yo*zo + g*xo
				+ h*yo + i*zo + j;
		
		if (a_ == 0){
			// a_ being 0 means the intersection equation is a linear equation
			double t = (-1 * c_) / b_;
			ts.add(t);
		} else {
			// The intersection equation is a quadratic equation
			double discriminant = Math.pow(b_, 2) - 4*a_*c_;
			// Check to see if there are intersections
			if (discriminant < 0){ return null; }
			
			if (discriminant == 0){
				// Only 1 intersection
				double t = (-1 * b_) / (2 * a_);
				ts.add(t);
			} else {
				// Two intersections
				double t0 = ((-1 * b_) - Math.sqrt(discriminant)) / (2 * a_);
				double t1 = ((-1 * b_) + Math.sqrt(discriminant)) / (2 * a_);
				ts.add(t0);
				ts.add(t1);
			}
		}
		
		for (int c = 0; c < ts.size(); c++){
			if (ts.get(c) < 0) ts.remove(c--);
		}
		if (ts.size() == 0)
			return null;
				
		intersections = new SurfacePoint[ts.size()];
		for (int c = 0; c < intersections.length; c++){
			Point3 inters = ray.point(ts.get(c));
			Vector3 normal = get_normal(inters);
			
			assert inters != null;
			assert normal != null;
			
			intersections[c] = new SurfacePoint(inters.x, inters.y, inters.z, normal.x, normal.y, normal.z);
		}
		
		return intersections;
	}

	protected Vector3 get_normal(Point3 p) {
		if (test_point(p) != 0)
			return null;
		
		Vector3 normal = new Vector3(
				2*a*p.x + d*p.y + e*p.z + g, 
				2*b*p.y + d*p.x + f*p.z + h,
				2*c*p.z + e*p.x + f*p.y + i);
		normal.normalize();
		return normal;
	}
	
	public String to_string() {
		String s = a + "x^2 + " + b + "y^2 + " + c + "z^2 + " + d + "xy + " + e + "xz + " + f + "yz + " + g + "x + " + h + "y + " + i + "z + " + j + " = 0";
		return s;
	}
}
