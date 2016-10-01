package renderer.objects;

import renderer.Color3;
import geometry.Ray;
import geometry.SurfacePoint;

public interface SceneObject {
//	public SurfacePointOrLineSegment[] intersect(Ray ray);
	public SurfacePoint[] intersect(Ray ray);
	public BSDF bsdf();
	public Color3 color();
	public double ray_kr();
	public double ray_kt();
	public double ior();
}
