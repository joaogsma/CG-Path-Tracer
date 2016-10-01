package renderer.lights;

import common.Tuple;

import geometry.Point3;
import geometry.Ray;
import geometry.SurfacePoint;
import geometry.Triangle;
import renderer.Color3;
import renderer.objects.TriangulatedObject;

public class AreaLight extends Light{
	TriangulatedObject area;
	
	public AreaLight(double[][] points, int[][] triangles, double r, double g, double b, double ip) {
		area = new TriangulatedObject(points, triangles, r, g, b, null, 0, 0, 1);
		color = new Color3(r, g, b);
		this.ip = ip;
	}
	
	public Point3 position() {
		// Continuous light. Pick a random point in its area
		int idx = (int) Math.round(Math.random()*(area.triangles.length-1));
		Triangle t = area.triangles[idx];
		double alpha, beta, gama;
		do {
			alpha = Math.random();
			beta = Math.random();
			gama = 1 - alpha - beta;
		} while(alpha + beta > 1);

		Point3 position = new Point3(
				alpha*t.vertices[0].x + beta*t.vertices[1].x + gama*t.vertices[2].x,
				alpha*t.vertices[0].y + beta*t.vertices[1].y + gama*t.vertices[2].y,
				alpha*t.vertices[0].z + beta*t.vertices[1].z + gama*t.vertices[2].z);
		return position;
	}
	
	public Point3 intersect(Ray ray) {
		SurfacePoint[] intersections = area.intersect(ray);
		
		if (intersections == null)
			return null;
		
		double dist = Double.POSITIVE_INFINITY;
		Point3 closest = null;
		
		for (Point3 inters : intersections) {
			if (ray.origin.distance(inters) < dist)
				closest = inters;
		}

		return closest;
	}
	
	public Point3 get_center() {
		Point3 center = new Point3(0, 0, 0);
		int qnt = 0;
		for (Triangle t : area.triangles) {
			for (Point3 v : t.vertices) {
				center.x += v.x;
				center.y += v.y;
				center.z += v.z;
				qnt++;
			}
		}
		center.x /= qnt;
		center.y /= qnt;
		center.z /= qnt;
		
		return center;
	}
	
	public Tuple<Point3, Point3> lower_left(Point3 camera) {
		Point3 lower_left = new Point3(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		Point3 upper_right = new Point3(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		for (Triangle t : area.triangles) {
			for (Point3 v : t.vertices) {
				if (v.x < lower_left.x || v.y < lower_left.y || v.z > lower_left.z) {
					lower_left = new Point3(v.x, v.y, v.z);
				} else if (v.x > upper_right.x || v.y > upper_right.y || v.z < upper_right.z) {
					upper_right = new Point3(v.x, v.y, v.z);
				}
			}
		}
		
		return new Tuple<Point3, Point3>(lower_left, upper_right);
	}
}
