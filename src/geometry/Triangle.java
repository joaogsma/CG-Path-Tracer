package geometry;

public class Triangle {
	public Point3[] vertices = new Point3[3];
	public Vector3 normal;
//	private Plane plane;
	
//	public Triangle(Point3 v1, Point3 v2, Point3 v3) {
////		plane = new Plane(v1, v2, v3);
////		normal = plane.normal;
//		normal = Calculations.cross_product(new Vector3(v1, v2),  new Vector3(v1, v3));
//		normal.normalize();
//		vertices[0] = new Point3(v1.x, v1.y, v1.z);
//		vertices[1] = new Point3(v2.x, v2.y, v2.z);
//		vertices[2] = new Point3(v3.x, v3.y, v3.z);
//	}
	
	public Triangle(Vertex3 v1, Vertex3 v2, Vertex3 v3) {
//		plane = new Plane(v1, v2, v3);
//		normal = plane.normal;
		normal = new Vector3(v1, v2).cross(new Vector3(v1, v3));
		normal.normalize();
		vertices[0] = v1;
		vertices[1] = v2;
		vertices[2] = v3;
	}

	public double[] baricentric_coordinates(Point3 p) {
		Point3 a = vertices[0], b = vertices[1], c = vertices[2];
		double area_abc = new Vector3(a, b).cross(new Vector3(a, c)).magnitude();
		
		double area_pbc = new Vector3(p, b).cross(new Vector3(p, c)).magnitude();
		double alpha = area_pbc / area_abc;
		
		double area_pca = new Vector3(p, c).cross(new Vector3(p, a)).magnitude();
		double beta = area_pca / area_abc;
		
		double area_pab = new Vector3(p, a).cross(new Vector3(p, b)).magnitude();
		double gama = area_pab / area_abc;
		
		double[] return_ = {alpha, beta, gama};
		return return_;
	}
	
//	public PointOrLineSegment intersect(Ray ray){
//		Point3 v1 = vertices[0], v2 = vertices[1], v3 = vertices[2];
//		PointOrRay plane_intersection = plane.intersect(ray);
//		
//		if (plane_intersection == null) {
//			// Ray does not intersect the triangle's plane
//			return null;
//		} else if (plane_intersection instanceof Point3) {
//			// Ray intersects the triangle's plane in a point
//			Point3 p = (Point3) plane_intersection;			
//			if (inside_triangle(p))
//				return p;
//			return null;
//		} else {
//			// Ray is contained in the triangle's plane
//			LineSegment seg1 = new LineSegment(v1, v2);
//			LineSegment seg2 = new LineSegment(v2, v3);
//			LineSegment seg3 = new LineSegment(v3, v1);
//			
//			PointOrLineSegment seg1_inters = seg1.intersect(ray);
//			PointOrLineSegment seg3_inters = seg2.intersect(ray);
//			PointOrLineSegment seg2_inters = seg3.intersect(ray);
//			
//			if (seg1_inters == null && seg2_inters == null && seg3_inters == null) {
//				// The ray does not cross the triangle
//				return null;
//			} else {
//				ArrayList<Point3> points = new ArrayList<Point3>();
//				ArrayList<LineSegment> segments = new ArrayList<LineSegment>();
//				if (seg1_inters != null) { 
//					if (seg1_inters instanceof Point3) points.add((Point3) seg1_inters); 
//					else segments.add((LineSegment) seg1_inters); 
//				} 
//				if (seg2_inters != null) { 
//					if (seg2_inters instanceof Point3) points.add((Point3) seg2_inters); 
//					else segments.add((LineSegment) seg2_inters); 
//				} 
//				if (seg3_inters != null) { 
//					if (seg3_inters instanceof Point3) points.add((Point3) seg3_inters); 
//					else segments.add((LineSegment) seg3_inters); 
//				}
//				// If the ray intersects a vertex, remove the duplicate from points
//				if (points.size() == 2 && points.get(0).equals(points.get(1))) { points.remove(1); }
//				
//				if (segments.size() == 1) {
//					// Ray contains one of the segments
//					segments.get(0).adjust_orientation(ray.origin);
//					return segments.get(0);
//				} else if (segments.size() == 3) {
//					// Degenerate zero-area triangle
//					double dist1 = Math.pow(v1.x - ray.origin.x, 2)
//							+ Math.pow(v1.y - ray.origin.y, 2) 
//							+ Math.pow(v1.z - ray.origin.z, 2);
//					double dist2 = Math.pow(v2.x - ray.origin.x, 2)
//							+ Math.pow(v2.y - ray.origin.y, 2) 
//							+ Math.pow(v2.z - ray.origin.z, 2);
//					double dist3 = Math.pow(v3.x - ray.origin.x, 2)
//							+ Math.pow(v3.y - ray.origin.y, 2) 
//							+ Math.pow(v3.z - ray.origin.z, 2);
//					// a is the vertex closest to the ray and b is the farthest
//					Point3 a = (dist1 <= dist2 && dist1 <= dist3) ? 
//							v1 : ((dist2 <= dist1 && dist2 <= dist3) ? v2 : v3);
//					Point3 b = (dist1 >= dist2 && dist1 >= dist3) ? 
//							v1 : ((dist2 >= dist1 && dist2 >= dist3) ? v2 : v3);
//					// If the triangle is degenerate to a point, return a Point.
//					// Otherwise, return the segment [a, b]
//					return a.equals(b) ? new Point3(a.x, a.y, a.z) : new LineSegment(a, b); 
//				} else if (inside_triangle(ray.origin)) {
//					// Ray's origin is inside the triangle, the boundary is only crossed once
//					Point3 intersection = points.get(0);
//					// If the ray intersects only at it's origin, return the origin. 
//					// Otherwise, return the segment from the origin to the intersection
//					return (intersection.equals(ray.origin)) ? intersection : new LineSegment(ray.origin, intersection);
//				} else if (points.size() == 1) {
//					// Ray intersects the triangle at a vertex
//					return points.get(0);
//				} else if (points.size() == 2) {
//					// Ray crosses the triangle
//					// Compute the distances from the intersection points to the ray's origin
//					LineSegment return_ = new LineSegment(points.get(0), points.get(1));
//					return_.adjust_orientation(ray.origin);
//					return return_;
//				} 
//				return null; 
//			}
//		}
//	}	

//	public boolean inside_triangle(Point3 p) {
//		Point3 v1 = vertices[0], v2 = vertices[1], v3 = vertices[2];
//		// Compute cross vectors for each segment of the triangle's boundary
//		Vector3 v1_v2 = new Vector3(v1, v2);
//		Vector3 v1_p = new Vector3(v1, p);
//		Vector3 cross_product1 = v1_v2.cross(v1_p);		
////		System.out.println("cross prod 1: " + cross_product1.to_string());
//		Vector3 v2_v3 = new Vector3(v2, v3);
//		Vector3 v2_p = new Vector3(v2, p);
//		Vector3 cross_product2 = v2_v3.cross(v2_p);
////		System.out.println("cross prod 2: " + cross_product2.to_string());
//		Vector3 v3_v1 = new Vector3(v3, v1);
//		Vector3 v3_p = new Vector3(v3, p);
//		Vector3 cross_product3 = v3_v1.cross(v3_p);
////		System.out.println("cross prod 3: " + cross_product3.to_string());
//		// Compute the orientation (right/left/on) of the cross products with respect 
//		// to the triangle's plane
//		double sgn1 = Math.signum(cross_product1.dot(plane.normal));
//		double sgn2 = Math.signum(cross_product2.dot(plane.normal));
//		double sgn3 = Math.signum(cross_product3.dot(plane.normal));
////		System.out.println("sgn1: " + sgn1 + "   sgn2: " + sgn2 + "   sgn3: " + sgn3);		
//		// Check if there are cross vectors to both sides
//		boolean has_one = sgn1 == 1 || sgn2 == 1 || sgn3 == 1;
//		boolean has_minus_one = sgn1 == -1 || sgn2 == -1 || sgn3 == -1;
//		
//		return !(has_one && has_minus_one);
//	}
	
	
	public SurfacePoint intersect(Ray ray) {
		Vector3 e1 = new Vector3(vertices[0], vertices[1]);
		Vector3 e2 = new Vector3(vertices[0], vertices[2]);
		Vector3 q = ray.direction.cross(e2);
		
		double a = e1.dot(q);
		
		Vector3 s = new Vector3(vertices[0], ray.origin);
		Vector3 r = s.cross(e1);
		
		// Barycentric vertex weights
		double[] weights = new double[3];
		weights[1] = s.dot(q) / a;
		weights[2] = ray.direction.dot(r) / a;
		weights[0] = 1 - (weights[1] + weights[2]);
		
		double dist = e2.dot(r) / a;
		
		final double epsilon = 1e-7f;
		final double epsilon2 = 1e-10;
		
		if (Math.abs(a) <= epsilon || weights[0] < -epsilon2 || weights[1] < -epsilon2 || weights[2] < -epsilon2 || dist <= 0) {
			// The ray is nearly parallel to the triangle, or the intersection lies outside 
			// the triangle or behind the ray origin: intersection is null
			return null;
		} else {
			SurfacePoint p  = new SurfacePoint(ray.origin.x + dist*ray.direction.x,
					ray.origin.y + dist*ray.direction.y, ray.origin.z + dist*ray.direction.z);
			
			double[] bar_coord = baricentric_coordinates(p);

			// Compute the intersection point's normal vector
			p.normal.x = bar_coord[0]*((Vertex3) vertices[0]).normal.x + bar_coord[1]*((Vertex3) vertices[1]).normal.x + 
					bar_coord[2]*((Vertex3) vertices[2]).normal.x;
			p.normal.y = bar_coord[0]*((Vertex3) vertices[0]).normal.y + bar_coord[1]*((Vertex3) vertices[1]).normal.y + 
					bar_coord[2]*((Vertex3) vertices[2]).normal.y;
			p.normal.z = bar_coord[0]*((Vertex3) vertices[0]).normal.z + bar_coord[1]*((Vertex3) vertices[1]).normal.z + 
					bar_coord[2]*((Vertex3) vertices[2]).normal.z;
			p.normal.normalize();
			
			return p;
		}
	}
}
