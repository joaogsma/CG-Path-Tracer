package geometry;

import common.intersection_types.SurfacePointOrLineSegment;

public class SurfacePoint extends Point3 implements SurfacePointOrLineSegment {
	public Vector3 normal;
	
	public SurfacePoint(double x, double y, double z) {
		super(x, y, z);
		normal = new Vector3(0, 0, 0);
	}
	
	public SurfacePoint(double x, double y, double z, double nx, double ny, double nz) {
		super(x, y, z);
		normal = new Vector3(nx, ny, nz);
	}
	
	public SurfacePoint(Point3 p) {
		super(p.x, p.y, p.z);
		normal = new Vector3(0, 0, 0);
	}
}
