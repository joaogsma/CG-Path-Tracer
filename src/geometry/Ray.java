package geometry;

import common.intersection_types.PointOrRay;

public class Ray implements PointOrRay {
	public Point3 origin;
	public Vector3 direction;
	
	public Ray(Point3 origin, Vector3 direction){
		this.origin = new Point3(origin.x, origin.y, origin.z);
		this.direction = new Vector3(direction.x, direction.y, direction.z);
		this.direction.normalize();
	}
	
	public Point3 point(double t){
		return new Point3(origin.x + t*direction.x, origin.y + t*direction.y, origin.z + t*direction.z);
	}
}
