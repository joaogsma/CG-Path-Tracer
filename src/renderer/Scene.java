package renderer;

import renderer.lights.Light;
import renderer.objects.SceneObject;

public class Scene {
	public SceneObject[] objects;
	public Light[] lights;
	public Color3 background;
	public double ia;
	
	public Scene(SceneObject[] objects, Light[] lights, double backg_r, double backg_g, double backg_b, double ia) {
		this.objects = objects;
		this.lights = lights;
		background = new Color3(backg_r, backg_g, backg_b);
		this.ia = ia;
	}
}
