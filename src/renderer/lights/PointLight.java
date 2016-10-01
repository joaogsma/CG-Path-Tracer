package renderer.lights;

import renderer.Color3;
import geometry.Point3;
import geometry.Ray;
import geometry.Vector3;

public class PointLight extends Light {
	Point3 position;
	
	public PointLight(double x, double y, double z, double r, double g,
			double b, double ip) {
		position = new Point3(x, y, z);
		color = new Color3(r, g, b);
		this.ip = ip;
	}
	
	public Point3 position() {
		return new Point3(position.x, position.y, position.z);
	}
	
	public Point3 intersect(Ray ray) {
		Vector3 difference = new Vector3(ray.origin, position);
		difference.normalize();
		
		if (difference.equals(ray.direction))
			return position();
		else
			return null;
	}
}
