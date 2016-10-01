package geometry;

import java.util.ArrayList;

public class Box {
	public Point3[] vertices = new Point3[8];
	private Parallelogram[] sides = new Parallelogram[6];
	
	public Triangle[] get_tringles() {
		Triangle[] return_ = new Triangle[12];
		int i = 0;
		
		for (Parallelogram paral : sides) {
			Triangle[] temp = paral.get_triangles(); 
			return_[i++] = temp[0];
			return_[i++] = temp[1];
		}
		
		return return_;
	}
	
	public Box(Point3 center, double x_length, double y_length, double z_length) {
		Vertex3 v1, v2, v3, v4, v5, v6, v7, v8;
		Parallelogram p1, p2, p3, p4, p5, p6;
		double x_delta = x_length/2;
		double y_delta = y_length/2;
		double z_delta = z_length/2;
		
		v1 = new Vertex3(center.x + x_delta, center.y + y_delta, center.z + z_delta);
		v2 = new Vertex3(center.x + x_delta, center.y + y_delta, center.z - z_delta);
		v3 = new Vertex3(center.x - x_delta, center.y + y_delta, center.z - z_delta);
		v4 = new Vertex3(center.x - x_delta, center.y + y_delta, center.z + z_delta);
		v5 = new Vertex3(center.x + x_delta, center.y - y_delta, center.z + z_delta);
		v6 = new Vertex3(center.x + x_delta, center.y - y_delta, center.z - z_delta);
		v7 = new Vertex3(center.x - x_delta, center.y - y_delta, center.z - z_delta);
		v8 = new Vertex3(center.x - x_delta, center.y - y_delta, center.z + z_delta);
		
//		p1 = new Parallelogram(v1, v2, v6, v5);
		p1 = new Parallelogram(v1, v5, v6, v2);
		
//		p2 = new Parallelogram(v2, v3, v7, v6);
		p2 = new Parallelogram(v2, v6, v7, v3);
		
//		p3 = new Parallelogram(v3, v4, v8, v7);
		p3 = new Parallelogram(v3, v7, v8, v4);
		
//		p4 = new Parallelogram(v4, v1, v5, v8);
		p4 = new Parallelogram(v4, v8, v5, v1);
		
		p5 = new Parallelogram(v1, v2, v3, v4);
		
//		p6 = new Parallelogram(v5, v6, v7, v8);
		p6 = new Parallelogram(v8, v7, v6, v5);
		
		vertices[0] = v1;
		vertices[1] = v2;
		vertices[2] = v3;
		vertices[3] = v4;
		vertices[4] = v5;
		vertices[5] = v6;
		vertices[6] = v7;
		vertices[7] = v8;
		
		sides[0] = p1;
		sides[1] = p2;
		sides[2] = p3;
		sides[3] = p4;
		sides[4] = p5;
		sides[5] = p6;
		
		for (Parallelogram side : sides) {
			for (Point3 p : side.vertices) {
				((Vertex3) p).normal.x += side.normal.x;
				((Vertex3) p).normal.y += side.normal.y;
				((Vertex3) p).normal.z += side.normal.z;
			}
		}
		for (Point3 p : vertices) {
			((Vertex3) p).normal.normalize();
		}
	}
	
//	public PointOrLineSegment[] intersect(Ray ray) {
//		LinkedList<Point3> points = new LinkedList<>();
//		LinkedList<LineSegment> segments = new LinkedList<>();
//		// Separate the intersections
//		for (Parallelogram side : sides) {
//			PointOrLineSegment intersection = side.intersect(ray);
//			if (intersection != null) { 
//				if (intersection instanceof Point3) points.add((Point3) intersection);
//				else segments.add((LineSegment) intersection);
//			}
//		}
//		// Remove duplicates
//		for (int i = 0; i < points.size(); i++) {
//			int qnt = 0;
//			for (Point3 q : points) { if (points.get(i).equals(q)) qnt++; }
//			if (qnt > 1) points.remove(i--);
//		}
//		// Remove duplicates
//		for (int i = 0; i < segments.size(); i++) {
//			int qnt = 0;
//			for (LineSegment seg : segments) { if (segments.get(i).equals(seg)) qnt++; }
//			if (qnt > 1) segments.remove(i--);
//		}
//		
//		if (segments.size() == 0 && points.size() == 0) {
//			// Ray does not intersect the box
//			return null;
//		} else if (segments.size() == 0 && points.size() == 2) {
//			// Ray crosses the box
//			Point3[] return_ = {points.get(0), points.get(1)}; 
//			return return_;
//		} else if (segments.size() == 0 && points.size() == 1) {
//			// Ray touches the box at one point
//			Point3[] return_ = {points.get(0)};
//			return return_;
//		} else if (segments.size() == 1) {
//			// Ray touches the box at a segment
////			segments.get(0).adjust_orientation(ray.origin);
//			LineSegment[] return_ = {segments.get(0)};
//			return return_;
//		} else {
//			System.out.println("This should not happen (Box).");
//			return null;
//		}
//	}
	
	public SurfacePoint[] intersect(Ray ray){
		ArrayList<SurfacePoint> points = new ArrayList<SurfacePoint>();
		
//		SurfacePoint inters1 = null;
//		SurfacePoint inters2 = null;
		
		for (Parallelogram side : sides) {
			SurfacePoint inters = side.intersect(ray);
			if (inters != null && !points.contains(inters)) {
				points.add(inters);
//				if ((inters1 != null && inters.equals(inters1)) || (inters2 != null && inters.equals(inters2)))
//					// This is a repeated intersection
//					continue;
//				// Store the intersection as the first or the second found
//				if (inters1 == null)
//					inters1 = inters;
//				else
//					inters2 = inters;
			}
		}
		
		if (points.size() == 0)
			return null;
		else
			return points.toArray(new SurfacePoint[0]);
//		if (inters1 != null && inters2 != null) {
//			SurfacePoint[] return_ = {inters1, inters2};
//			return return_;
//		} else if (inters2 == null) {
//			SurfacePoint[] return_ = {inters1};
//			return return_;
//		}
//		return null;
	}
}
