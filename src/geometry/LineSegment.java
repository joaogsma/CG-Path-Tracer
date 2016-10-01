package geometry;

import common.intersection_types.PointOrLineSegment;
import common.intersection_types.SurfacePointOrLineSegment;

// Not being used
public class LineSegment implements PointOrLineSegment, SurfacePointOrLineSegment{
	public Point3 fst, snd;
	
	public LineSegment(Point3 a, Point3 b){
		this.fst = new Point3(a.x, a.y, a.z);
		this.snd = new Point3(b.x, b.y, b.z);
	}
	
	public LineSegment(Vertex3 a, Vertex3 b) {
		this.fst = a;
		this.snd = b;
	}
	
	public String to_string() {
		return "[" + fst.to_string() + ", " + snd.to_string() + "]";
	}
	
	public double length() {
		return Math.sqrt(Math.pow(fst.x-snd.x, 2) + Math.pow(fst.y-snd.y, 2) + Math.pow(fst.z-snd.z, 2)); 
	}
	
	public boolean equals(LineSegment seg) {
		return (fst.equals(seg.fst) && snd.equals(seg.snd)) || (fst.equals(seg.snd) && snd.equals(seg.fst));
	}
	
	public void adjust_orientation(Point3 p) {
		double dist_fst = Math.pow(fst.x - p.x, 2) + Math.pow(fst.y - p.y, 2) + Math.pow(fst.z - p.z, 2);
		double dist_snd = Math.pow(snd.x - p.x, 2) + Math.pow(snd.y - p.y, 2) + Math.pow(snd.z - p.z, 2);
		
		if (dist_snd < dist_fst) {
			Point3 temp = fst;
			fst = snd;
			snd = temp;
		}
	}
	
	private static double cross_product_2D(Vector3 v, Vector3 w) {
		Vector3 cp = v.cross(w);
		return cp.magnitude();
	}
	
	public PointOrLineSegment intersect(Ray ray){
		Ray seg_ray = new Ray(fst, new Vector3(fst, snd));
		Point3 p = ray.origin, q = seg_ray.origin;
		Vector3 r = ray.direction, s = seg_ray.direction;
//		System.out.println("p: " + p.to_string() + "   r: " + r.to_string());
//		System.out.println("q: " + q.to_string() + "   s: " + s.to_string());
		Vector3 q_minus_p = new Vector3(p, q);
//		System.out.println("q_minus_p: " + q_minus_p.to_string());
		double r_cross_s = cross_product_2D(r, s);
//		System.out.println("r_cross_s: " + r_cross_s);
		double t = cross_product_2D(q_minus_p, s) / r_cross_s;
		double u = cross_product_2D(q_minus_p, r) / r_cross_s;
		
		if (r_cross_s == 0 && cross_product_2D(q_minus_p, r) == 0) {
			// Collinear
//			System.out.println("The ray and the segment are colinear");
			double t_fst = q_minus_p.dot(r) / r.dot(r);
			double t_snd = new Vector3(p, snd).dot(r) / r.dot(r);
			
			LineSegment intersection;
			if (t_fst >= 0 && t_snd >= 0) {
				// The whole segment is contained in the ray
//				System.out.println("The whole segment is contained in the ray");
				intersection = new LineSegment(fst, snd);
			} else if (t_fst >= 0) {
				// Only seg.fst is contained in the ray
//				System.out.println("Only seg.fst is contained in the ray");
				intersection = new LineSegment(ray.origin, fst);
			} else if (t_snd >= 0) {
				// Only seg.snd is contained in the ray
//				System.out.println("Only seg.snd is contained in the ray");
				intersection = new LineSegment(ray.origin, snd);
			} else {
				// The segment is in the ray's line, but not in the ray
//				System.out.println("The segment is in the ray's line, but not in the ray");
				return null;
			}
			
			if (intersection.fst.x == intersection.snd.x && intersection.fst.y == intersection.snd.y
					&& intersection.fst.z == intersection.snd.z) {
				return intersection.fst;
			}
			return intersection;
		} else if (r_cross_s == 0) {
			// Parallel
//			System.out.println("The ray and the segment are parallel");
			return null;
		} else {
			// Find the correct signs (+, -) for t and u
			for (int i_t = 1; i_t >= -1; i_t -= 2){
				for (int i_u = 1; i_u >= -1; i_u -= 2){
					double t_ = t*i_t, u_ = u*i_u;
					if (p.x + t_*r.x == q.x + u_*s.x && p.y + t_*r.y == q.y + u_*s.y
							&& p.z + t_*r.z == q.z + u_*s.z) {
						t = t_;
						u = u_;
						break;
					}
				}
			}
			
			// Intersection at t and u. Check if t >= 0, u >= 0 and magnitude(q + u*s) <= magnitude(seg.snd-seg.fst)
//			System.out.println("The lines intersect at a point");
//			System.out.println("t: " + t + "   u:" + u);
			// Intersection is outside the rays
			if (t < 0 || u < 0) return null;
			
			Vector3 u_times_s = new Vector3(u*s.x, u*s.y, u*s.z);
			Vector3 seg_vector = new Vector3(fst, snd);
			// Intersection is outside the line segment
			if (u_times_s.magnitude() > seg_vector.magnitude()) return null;
			// Valid intersection
			Point3 result = new Point3(q.x + u_times_s.x, q.y + u_times_s.y, q.z + u_times_s.z);
			return result;
		}
	}
	
}
