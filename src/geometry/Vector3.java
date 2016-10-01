package geometry;

public class Vector3 {
	public double x, y, z;
	
	public Vector3(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3 (Point3 origin, Point3 destination){
		this.x = destination.x - origin.x;
		this.y = destination.y - origin.y;
		this.z = destination.z - origin.z;
	}
	
	public void normalize(){
		double length = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
		// Normalization is not defined for the zero vector
		if (length == 0) return;
		
		x /= length;
		y /= length;
		z /= length;
	}
	
	public double magnitude() {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
	}
	
	public boolean equals(Vector3 v) {
		final double epsilon = 1e-10;
		double dist = Math.sqrt(Math.pow(x - v.x, 2) + Math.pow(y - v.y, 2) + Math.pow(z - v.z, 2));
		
		return dist <= epsilon;
	}
	
	public void times_scalar(double scalar) {
		x *= scalar;
		y *= scalar;
		z *= scalar;
	}
	
	public String to_string() {
		return "(" + x + ", " + y + ", " + z +")";
	}
	
	public Vector3 cross(Vector3 v){
		double x = this.y*v.z - this.z*v.y;
		double y = this.z*v.x - this.x*v.z;
		double z = this.x*v.y - this.y*v.x;

		return new Vector3(x, y, z);
	}
	
	public double dot(Vector3 v) {
		return this.x*v.x + this.y*v.y + this.z*v.z;
	}
}
