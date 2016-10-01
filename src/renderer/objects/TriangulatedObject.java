package renderer.objects;

import java.util.ArrayList;

import renderer.Color3;
import geometry.Box;
import geometry.Point3;
import geometry.Ray;
import geometry.SurfacePoint;
import geometry.Triangle;
import geometry.Vertex3;

public class TriangulatedObject implements SceneObject {
	public Triangle[] triangles;
	public Box bounding_box;
	private BSDF bsdf;
	private Color3 color;
	private double ray_kr, ray_kt, ior;
	
	public TriangulatedObject(double[][] points, int[][] triangles, double r, double g, double b, BSDF bsdf, double ray_kr, double ray_kt, double ior) {
		this.bsdf = bsdf;
		this.ray_kr = ray_kr;
		this.ray_kt = ray_kt;
		this.ior = ior;
		color = new Color3(r, g, b);
		
		Point3 center = new Point3(0, 0, 0);
		double max_x = Double.NEGATIVE_INFINITY, max_y = Double.NEGATIVE_INFINITY, max_z = Double.NEGATIVE_INFINITY;
		double min_x = Double.POSITIVE_INFINITY, min_y = Double.POSITIVE_INFINITY, min_z = Double.POSITIVE_INFINITY;
		
		Vertex3[] vertices = new Vertex3[points.length];
		this.triangles = new Triangle[triangles.length];
		
		// Create the vertices and compute the data for the bounding box
		for (int i = 0; i < points.length; i++) {
			vertices[i] = new Vertex3(points[i][0], points[i][1], points[i][2]);

			center.x += vertices[i].x;
			center.y += vertices[i].y;
			center.z += vertices[i].z;
			
			max_x = Math.max(max_x, vertices[i].x);
			max_y = Math.max(max_y, vertices[i].y);
			max_z = Math.max(max_z, vertices[i].z);
			
			min_x = Math.min(min_x, vertices[i].x);
			min_y = Math.min(min_y, vertices[i].y);
			min_z = Math.min(min_z, vertices[i].z);
		}
		center.x /= points.length;
		center.y /= points.length;
		center.z /= points.length;
		
		bounding_box = new Box(center, max_x - min_x, max_y - min_y, max_z - min_z);
		
		// Create the triangles
		for (int i = 0; i < triangles.length; i++) {
			int v1_index = triangles[i][0] - 1, v2_index = triangles[i][1] - 1, v3_index = triangles[i][2] - 1;
			this.triangles[i] = new Triangle(vertices[v1_index], vertices[v2_index], vertices[v3_index]);
		}
		
		// Compute the vertices' normals
		for (Triangle t : this.triangles) {
			for (Point3 v : t.vertices) {
				((Vertex3) v).normal.x += t.normal.x;
				((Vertex3) v).normal.y += t.normal.y;
				((Vertex3) v).normal.z += t.normal.z;
			}
		}
		
		// Normalize the vertices' normals
		for (Vertex3 v : vertices) v.normal.normalize();
	}
	
	public SurfacePoint[] intersect(Ray ray) {
		if (bounding_box.intersect(ray) == null)
			return null;
		
		ArrayList<SurfacePoint> intersections = new ArrayList<>(triangles.length);
		
		for (Triangle t : triangles) {
			SurfacePoint p = t.intersect(ray);
			if (p != null) {
				// Add the intersection if it's not a repetition
//				boolean repetition = false;
//				for (SurfacePoint q : intersections) {
//					if (q.equals(p)) {
//						repetition = true;
//						break;
//					}
//				}
//				
				if (!intersections.contains(p))
					intersections.add(p);
			}
		}
		
		return intersections.toArray(new SurfacePoint[0]);
	}
	
	public BSDF bsdf() {
		return bsdf;
	}
	
	public Color3 color() {
		return color;
	}
	
	@Override
	public double ray_kr() {
		return ray_kr;
	}

	@Override
	public double ray_kt() {
		return ray_kt;
	}

	@Override
	public double ior() {
		return ior;
	}
	
//	@Override
//	public SurfacePointOrLineSegment[] intersect(Ray ray) {
//		// Avoids computing intersections for all triangles if the bounding
//		// box is not intersected
//		if (bounding_box.intersect(ray) == null)
//			return null;
//
//		// Compute all intersections
//		List<Tuple<Point3, Triangle>> points = new ArrayList<>();
//		List<LineSegment> segments = new ArrayList<>();
//		for (Triangle t : triangles) {
//			PointOrLineSegment inters = t.intersect(ray);
//			if (inters == null)
//				continue;
//			// If the triangle t intersects the ray, store the intersection
//			store_intersection(inters, t, points, segments);
//		}
//		
//		// Remove point intersections that appear on line segment intersections
//		remove_point_line_repetitions(points, segments);
//
//		for (LineSegment seg : segments)
//			seg.adjust_orientation(ray.origin);
//
//		// Join adjacent line segments ([a, b] and [b, c] become [a, c])
//		for (int i = 0; i < segments.size(); i++) {
//			for (int j = 0; j < segments.size(); j++) {
//				if (segments.get(i).snd.equals(segments.get(j).fst)) {
//					LineSegment seg1 = segments.get(i);
//					LineSegment seg2 = segments.get(j);
//					segments.remove(seg1);
//					segments.remove(seg2);
//					i--;
//					segments.add(new LineSegment(seg1.fst, seg2.snd));
//					break;
//				}
//			}
//		}
//
//		SurfacePointOrLineSegment[] return_ = new SurfacePointOrLineSegment[points.size() + segments.size()];
//		int index = 0;
//		while (index < points.size()) {
//			return_[index] = compute_point_normal(points.get(index).fst, points.get(index).snd);
//			index++;
//		}
//		for (LineSegment seg : segments) {
//			return_[index++] = seg;
//		}
//
//		return return_;
//
//	}
	
//	private SurfacePoint compute_point_normal(Point3 p, Triangle t) {
//		double[] bar_coord = t.baricentric_coordinates(p);
//		double alpha = bar_coord[0], beta = bar_coord[1], gama = bar_coord[2];
//		
//		Vector3 n1 = ((Vertex) t.vertices[0]).normal;
//		Vector3 n2 = ((Vertex) t.vertices[1]).normal;
//		Vector3 n3 = ((Vertex) t.vertices[2]).normal;
//				
//		SurfacePoint return_ = new SurfacePoint(p);
//		
//		return_.normal.x = alpha*n1.x + beta*n2.x + gama*n3.x;
//		return_.normal.y = alpha*n1.y + beta*n2.y + gama*n3.y;
//		return_.normal.z = alpha*n1.z + beta*n2.z + gama*n3.z;
//		return_.normal.normalize();
//		
//		return return_;
//	}

//	private void store_intersection(PointOrLineSegment inters, Triangle t, List<Tuple<Point3, Triangle>> points,
//			List<LineSegment> segments) {
//		if (inters instanceof Point3) {
//			Point3 inters_ = (Point3) inters;
//
//			// Check if this intersection is a repetition
//			boolean repetition = false;
//			for (Tuple<Point3, Triangle> tuple : points) {
//				if (tuple.fst.equals(inters_)) {
//					repetition = true;
//					break;
//				}
//			}
//
//			// Add the intersection if it is not a repetition
//			if (!repetition)
//				points.add(new Tuple<Point3, Triangle>(inters_, t));
//		} else {
//			LineSegment inters_ = (LineSegment) inters;
//
//			// Check if this intersection is a repetition
//			boolean repetition = false;
//			for (LineSegment seg : segments) {
//				if (seg.fst.equals(inters_)) {
//					repetition = true;
//					break;
//				}
//			}
//
//			// Add the intersection if it is not a repetition
//			if (!repetition)
//				segments.add(inters_);
//		}
//	}
//	
//	private void remove_point_line_repetitions(List<Tuple<Point3, Triangle>> points,
//			List<LineSegment> segments) {
//		for (int i = 0; i < points.size(); i++) {
//			boolean repetition = false;
//			for (LineSegment seg : segments) {
//				Point3 p1 = seg.fst;
//				Point3 p2 = seg.snd;
//				if (points.get(i).fst.equals(p1) || points.get(i).fst.equals(p2)) {
//					repetition = true;
//					break;
//				}
//			}
//			
//			if (repetition)
//				points.remove(i--);
//		}
//	}
}
