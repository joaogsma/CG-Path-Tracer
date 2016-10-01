package renderer.lights;

import renderer.Color3;
import geometry.Point3;
import geometry.Ray;

public abstract class Light {
//	private Point3 pontual;
	public Color3 color;
	public double ip;
	
//	public Light(double x, double y, double z, double r, double g, double b, double ip) {
//		pontual = new Point3(x, y, z);
//		color = new Color3(r, g, b);
//		this.ip = ip;
//	}
	
//	public Light(double[][] points, int[][] triangles, double r, double g, double b, double ip) {
//		geometry = new TriangulatedObject(points, triangles, null, null);
//		color = new Color3(r, g, b);
//		this.ip = ip;
//	}
	
	public abstract Point3 position();
	public abstract Point3 intersect(Ray ray);
	
//	public Point3 position() {
//		return pontual;
////		if (pontual != null) {
////			// Point light
////			return pontual;
////		}
////		
////		// Continuous light. Pick a random point in its area
////		int idx = (int) Math.round(Math.random()*(geometry.triangles.length-1));
////		Triangle t = geometry.triangles[idx];
////		double alpha, beta, gama;
////		do {
////			alpha = Math.random();
////			beta = Math.random();
////			gama = 1 - alpha - beta;
////		} while(alpha + beta > 1);
////
////		Point3 position = new Point3(
////				alpha*t.vertices[0].x + beta*t.vertices[1].x + gama*t.vertices[2].x,
////				alpha*t.vertices[0].y + beta*t.vertices[1].y + gama*t.vertices[2].y,
////				alpha*t.vertices[0].z + beta*t.vertices[1].z + gama*t.vertices[2].z);
////		return position;
//	}
}