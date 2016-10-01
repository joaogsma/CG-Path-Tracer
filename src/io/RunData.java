package io;

import renderer.Scene;
import renderer.Window;
import geometry.Point3;

public class RunData {
	public Scene scene;
	public Point3 camera;
	public Window window;
	public String out_filename;
	public int n_paths;
	public int max_depth;
	public int n_shadow_rays;
	public double tonemapping;
	public int light_resolution_w;
	public int light_resolution_h;
	
	public RunData(Scene scene, Point3 camera, Window window, String out_filename, int n_paths, int max_depth, int n_shadow_rays, double tonemapping, int light_resolution_w, int light_resolution_h) {
		this.scene = scene;
		this.camera = camera;
		this.window = window;
		this.out_filename = out_filename;
		this.n_paths = n_paths;
		this.max_depth = max_depth;
		this.n_shadow_rays = n_shadow_rays;
		this.tonemapping = tonemapping;
		this.light_resolution_w = light_resolution_w;
		this.light_resolution_h = light_resolution_h;
	}
}
