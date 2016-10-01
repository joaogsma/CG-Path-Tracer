package renderer.objects;

import renderer.Color3;
import geometry.Quadric;

public class QuadricObject extends Quadric implements SceneObject {
	private BSDF bsdf;
	private Color3 color;
	private double ray_kr, ray_kt, ior;
	public QuadricObject(double a, double b, double c, double d, double e, double f, double g, double h, double i,
			double j, double color_r, double color_g, double color_b, BSDF bsdf, double ray_kr, double ray_kt, double ior) {
		super(a, b, c, d, e, f, g, h, i, j);
		this.bsdf = bsdf;
		this.color = new Color3(color_r, color_g, color_b);
		this.ray_kr = ray_kr;
		this.ray_kt = ray_kt;
		this.ior = ior;
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
}
