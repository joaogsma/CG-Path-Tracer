package geometry;

public class Parallelogram {
	public final Point3[] vertices = new Point3[4];
	private final Triangle t1, t2;
	public final Vector3 normal;
	
	public Triangle[] get_triangles() {
		Triangle[] return_ = {t1, t2}; 
		return return_;
	}
	
	public Parallelogram(Vertex3 v1, Vertex3 v2, Vertex3 v3, Vertex3 v4) {
		vertices[0] = v1;
		vertices[1] = v2;
		vertices[2] = v3;
		vertices[3] = v4;
		
		t1 = new Triangle(v1, v2, v3);
		t2 = new Triangle(v3, v4, v1);
		
		assert t1.normal.equals(t2.normal);
		
		normal = new Vector3(t1.normal.x, t1.normal.y, t1.normal.z);
	}
	
//	public Parallelogram(Point3 v1, Point3 v2, Point3 v3, Point3 v4) {
//		vertices[0] = new Point3(v1.x, v1.y, v1.z);
//		vertices[1] = new Point3(v2.x, v2.y, v2.z);
//		vertices[2] = new Point3(v3.x, v3.y, v3.z);
//		vertices[3] = new Point3(v4.x, v4.y, v4.z);
//		
//		t1 = new Triangle(v1, v2, v3);
//		t2 = new Triangle(v3, v4, v1);
//		
//		normal = Calculations.cross_product(new Vector3(v1, v2), new Vector3(v1, v4));
//		normal.normalize();
//	}
	
//	public PointOrLineSegment intersect(Ray ray) {
//		PointOrLineSegment t1_intersection = t1.intersect(ray);
//		PointOrLineSegment t2_intersection = t2.intersect(ray);
//		
//		// Separate the intersections
//		ArrayList<Point3> points = new ArrayList<>();
//		ArrayList<LineSegment> segments = new ArrayList<>();
//		if (t1_intersection != null) { 
//			if (t1_intersection instanceof Point3) points.add((Point3) t1_intersection); 
//			else segments.add((LineSegment) t1_intersection); 
//		}
//		if (t2_intersection != null) { 
//			if (t2_intersection instanceof Point3) points.add((Point3) t2_intersection); 
//			else segments.add((LineSegment) t2_intersection); 
//		}
//		// Remove duplicate intersections
//		if (points.size() == 2 && points.get(0).equals(points.get(1))) { points.remove(1); }
//		if (segments.size() == 2 && segments.get(0).equals(segments.get(1))) { segments.remove(1); }
//		
//		if (segments.size() == 0 && points.size() == 0) {
//			// Ray does not intersect the parallelogram
//			return null;
//		} else if (segments.size() == 0) {
//			// Ray intersects the parallelogram at one point
//			return points.get(0);
//		} else if (segments.size() == 1) {
//			// Ray intersects either only one triangle, or both in their shared side
////			segments.get(0).adjust_orientation(ray.origin);
//			return segments.get(0);
//		} else if (segments.size() == 2) {
//			// Ray intersects the parallelogram at one segment in each triangle
//			LineSegment seg1 = segments.get(0);
//			LineSegment seg2 = segments.get(1);
//			Point3 a = (seg1.fst.equals(seg2.fst) || seg1.fst.equals(seg2.snd)) ? seg1.snd : seg1.fst;
//			Point3 b = (seg2.fst.equals(seg1.fst) || seg2.fst.equals(seg1.snd)) ? seg2.snd : seg2.fst;
//			
//			LineSegment return_ = new LineSegment(a, b);
////			return_.adjust_orientation(ray.origin);
//			return return_;
//		} else {
//			System.out.println("This should not happen (Parallelogram).");
//			return null;
//		}
//	}
	
	public SurfacePoint intersect(Ray ray) {
		SurfacePoint t1_intersection = t1.intersect(ray);		
		if (t1_intersection != null) 
			return t1_intersection;
		
		SurfacePoint t2_intersection = t2.intersect(ray);
		if (t2_intersection != null)
			return t2_intersection;
		
		return null;
	}
}
