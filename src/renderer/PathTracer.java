package renderer;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import common.Tuple;
import renderer.lights.AreaLight;
import renderer.lights.Light;
import renderer.lights.PointLight;
import renderer.objects.BSDF;
import renderer.objects.SceneObject;
import geometry.Point3;
import geometry.Ray;
import geometry.SurfacePoint;
import geometry.Vector3;

public class PathTracer {
	public Scene scene;
	public Point3 camera;
	public Window window;
	private AtomicInteger pxs_finished;
	
	public PathTracer(Scene scene, Point3 camera, Window window){
		this.scene = scene;
		this.camera = camera;
		this.window = window;
		pxs_finished = new AtomicInteger(0);;
	}
	
	private class PixelHandler extends Thread{
		PathTracer pt;
		Image image;
		int px_i, n_paths, max_depth, n_shadow_rays;
		int light_resolution_w;
		int light_resolution_h;
		double y;
		AtomicInteger pxs_finished;
		
		PixelHandler(PathTracer path_tracer, int n_shadow_rays, int n_rays, int max_depth, int px_i, double y, Image image, AtomicInteger pxs_finished, int light_resolution_w, int light_resolution_h) {
			pt = path_tracer;
			this.n_shadow_rays = n_shadow_rays;
			this.n_paths = n_rays;
			this.max_depth = max_depth;
			this.px_i = px_i;
			this.y = y;
			this.image = image;
			this.pxs_finished = pxs_finished;
			this.light_resolution_w = light_resolution_w;
			this.light_resolution_h = light_resolution_h;
		}
		
		public void run() {
			int px_j = 0;
			for (double x = window.upper_left.x + (window.px_w/2); x < window.lower_right.x; x += window.px_w) {
				Point3 px_center = new Point3(x, y, 0);
				Ray ray = new Ray(camera, new Vector3(camera, px_center));
				Color3 px_color =  pt.cast_px_rays(ray, n_paths, max_depth, n_shadow_rays, light_resolution_w, light_resolution_h);
				
				image.set(px_i, px_j, px_color);
				px_j++;

				pxs_finished.incrementAndGet();
				
				if (px_j % 50 == 0)
					Thread.yield();
			}
		}
	}
	
	private class IntersectedPoint extends SurfacePoint {
		boolean is_light;
		BSDF bsdf;
		Color3 surface_color;
		double ray_kr, ray_kt, obj_ior;
		
		public IntersectedPoint(SurfacePoint point, Object surface) {
			super(point.x, point.y, point.z, point.normal.x, point.normal.y, point.normal.z);
			
			if (surface instanceof Light) {
				Light light = (Light) surface;
				is_light = true;
				surface_color = new Color3(light.color.r, light.color.g, light.color.b);
				surface_color.times_scalar(light.ip);
			} else {				
				SceneObject obj = (SceneObject) surface;
				
				bsdf = obj.bsdf();
				surface_color = obj.color();
				ray_kr = obj.ray_kr();
				ray_kt = obj.ray_kt();
				obj_ior = obj.ior();
				is_light = false;
			}
		}
	}
	
	public float status() {
		float finished = pxs_finished.get();
		finished /= window.height*window.width;

		return finished;
	}
	
	public Image render(int n_paths, int max_depth, int n_shadow_rays, int light_resolution_w, int light_resolution_h) {
		LinkedList<Thread> threads = new LinkedList<Thread>();
		Image image = new Image(window.width, window.height);
		int px_i = 0;
		
		// Loop iterating through every pixel
		for (double y = window.upper_left.y - (window.px_h/2); y > window.lower_right.y; y -= window.px_h) {
			
			Thread pixel_handler = new PixelHandler(this, n_shadow_rays, n_paths, max_depth, px_i, y, image, pxs_finished, light_resolution_w, light_resolution_h);
			threads.add(pixel_handler);
			pixel_handler.start();
			
			px_i++;
		}
		
		while(!threads.isEmpty()) {
			try {
				threads.pollLast().join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		pxs_finished.set(0);
		return image;
	}
	
	private IntersectedPoint find_closest_intersection(Ray ray) {
		SurfacePoint closest_point = null;
		double closest_distance = Double.POSITIVE_INFINITY;
		Object closest_surface = null;
		
		for (SceneObject obj : scene.objects) {
			SurfacePoint[] intersections = obj.intersect(ray);

			if (intersections == null)
				continue;
			
			for (SurfacePoint inters : intersections) {
				// If the intersection is the ray's origin, ignore it
				if (inters.equals(ray.origin))
					continue;
				
				double inters_dist = inters.distance(ray.origin);
				if (inters_dist < closest_distance) {
					closest_distance = inters_dist;
					closest_point = inters;
					closest_surface = obj;
				}
			}
		}
		
		for (Light light : scene.lights) {
			Point3 inters = light.intersect(ray);
			
			if (inters != null && inters.distance(ray.origin) < closest_distance) {
				
//				System.out.println(closest_distance);
//				System.out.println(inters.distance(ray.origin));
//				System.out.println(light.color.to_string());
				
				closest_distance = inters.distance(ray.origin);
				closest_point = new SurfacePoint(inters);
				closest_surface = light;
			}
		}
		
		if (closest_point == null)
			return null;
		
		return new IntersectedPoint(closest_point, closest_surface);
	}
	
	private void compute_dif_spec_illumination(Light light, Point3 light_position, Vector3 l, Ray ray, IntersectedPoint inters, Color3 local_illumination, double fraction) {
		double kd = inters.bsdf.kd, ks = inters.bsdf.ks;
		Color3 obj_color = inters.surface_color;
		Vector3 normal = inters.normal;
		normal.normalize();
		double l_dot_normal = l.dot(normal);
		
		// R vector
		Vector3 r = new Vector3(2*l_dot_normal*normal.x, 2*l_dot_normal*normal.y, 2*l_dot_normal*normal.z);
		r.x -= l.x;
		r.y -= l.y;
		r.z -= l.z;
		r.normalize();
		
//		System.out.println("vector R: " + r.to_string());
		
		// V vector
		Vector3 v = new Vector3(-ray.direction.x, -ray.direction.y, -ray.direction.z);
		double r_dot_v = r.dot(v);

//		System.out.println("vector V: " + v.to_string());
//		System.out.println("R.V: " + r_dot_v);
		
		
		if (l_dot_normal > 0) {
			// Diffuse illumination
			local_illumination.r += (light.ip * light.color.r / fraction) * (kd * obj_color.r) * l_dot_normal;
			local_illumination.g += (light.ip * light.color.g / fraction) * (kd * obj_color.g) * l_dot_normal;
			local_illumination.b += (light.ip * light.color.b / fraction) * (kd * obj_color.b) * l_dot_normal;
		}
		
		// There's no specular component if n.v <= 0 or r.v <= 0
		if (r_dot_v > 0) {
			// Specular illumination
			double pow_r_dot_v = Math.pow(r_dot_v, inters.bsdf.alpha);
			
			local_illumination.r += (light.ip * light.color.r / fraction) * ks * pow_r_dot_v;
			local_illumination.g += (light.ip * light.color.g / fraction) * ks * pow_r_dot_v;
			local_illumination.b += (light.ip * light.color.b / fraction) * ks * pow_r_dot_v;
		}
	}
	
	private Color3 point_local_illumination(IntersectedPoint inters, Ray ray, int n_shadow_rays, int light_resolution_w, int light_resolution_h) {
		double ka = inters.bsdf.ka;
		double ia = scene.ia;
		Color3 obj_color = inters.surface_color;
//		Color3 backg_color = scene.background;
		
		Vector3 normal = inters.normal;
		normal.normalize();
		
		// Initialize the local illumination with the ambient illumination
		Color3 local_illumination = new Color3(
				ia * (ka * obj_color.r),
				ia * (ka * obj_color.g),
				ia * (ka * obj_color.b));
//		Color3 local_illumination = new Color3(
//				(ia * backg_color.r) * (ka * obj_color.r),
//				(ia * backg_color.g) * (ka * obj_color.g),
//				(ia * backg_color.b) * (ka * obj_color.b));
		
		if (n_shadow_rays < 1)
			return local_illumination;
		
		for (Light light : scene.lights) {
			// L vector
			Point3 light_position;
			Vector3 l = null;
			double l_dot_normal = 0;
			boolean obstructed = true;
			
			if (light instanceof AreaLight && light_resolution_w > 0 && light_resolution_h > 0) {
				Tuple<Point3, Point3> tuple = ((AreaLight) light).lower_left(camera);
				Point3 ll = tuple.fst, up = tuple.snd;
//				Point3 center = ((AreaLight) light).get_center();
				
//				System.out.println("ll: " + ll.to_string() + "   up: " + up.to_string());
				
				double w = Math.abs(up.x - ll.x) / light_resolution_w;
				double h = Math.abs(ll.z - up.z) / light_resolution_h;
				double fraction = light_resolution_w*light_resolution_h;
				
				for (double x = ll.x + (w/2); x < up.x; x += w) {
					for (double z = ll.z - (h/2); z > up.z; z -= h) {
						light_position = new Point3(x, ll.y, z);
						
//						System.out.println(light_position.to_string());
						
						l = new Vector3(inters, light_position);
						l.normalize();
						
						l_dot_normal = l.dot(normal);
						
						if (l_dot_normal <= 0)
							continue;
						
						Ray shadow_ray = new Ray(inters, l);
						IntersectedPoint inters2 = find_closest_intersection(shadow_ray);
						
						obstructed = inters2 != null && !inters2.is_light;
						
						if (obstructed)
							continue;
						
						compute_dif_spec_illumination(light, light_position, l, ray, inters, local_illumination, fraction);
					}
				}
			} else {
				int remaining_shadow_rays = (light instanceof PointLight) ? 1 : n_shadow_rays;
				do {
					remaining_shadow_rays--;
					light_position = light.position();
					l = new Vector3(inters, light_position);
					l.normalize();

					//			System.out.println("vector L: " + l.to_string());

					l_dot_normal = l.dot(normal);

					// Surface is not facing the light
					if (l_dot_normal <= 0)
						continue;

					//			System.out.println("L.N: " + l_dot_normal);

					Ray shadow_ray = new Ray(inters, l);
					IntersectedPoint inters2 = find_closest_intersection(shadow_ray);

					// Light is obstructed
					obstructed = inters2 != null && !inters2.is_light;
					//				obstructed = inters2 != null && inters.distance(inters2) < inters.distance(light_position);
					//				if (inters2 != null && inters.distance(inters2) < inters.distance(light_position))
					//					continue;
				} while(obstructed && remaining_shadow_rays > 0);

				if (obstructed)
					continue;

				compute_dif_spec_illumination(light, light_position, l, ray, inters, local_illumination, 1);
				/*
				// R vector
				Vector3 r = new Vector3(2*l_dot_normal*normal.x, 2*l_dot_normal*normal.y, 2*l_dot_normal*normal.z);
				r.x -= l.x;
				r.y -= l.y;
				r.z -= l.z;
				r.normalize();

				//			System.out.println("vector R: " + r.to_string());

				// V vector
				Vector3 v = new Vector3(-ray.direction.x, -ray.direction.y, -ray.direction.z);
				double r_dot_v = r.dot(v);

				//			System.out.println("vector V: " + v.to_string());
				//			System.out.println("R.V: " + r_dot_v);


				if (l_dot_normal > 0) {
					// Diffuse illumination
					local_illumination.r += (light.ip * light.color.r) * (kd * obj_color.r) * l_dot_normal;
					local_illumination.g += (light.ip * light.color.g) * (kd * obj_color.g) * l_dot_normal;
					local_illumination.b += (light.ip * light.color.b) * (kd * obj_color.b) * l_dot_normal;
				}

				// There's no specular component if n.v <= 0 or r.v <= 0
				if (r_dot_v > 0) {
					// Specular illumination
					double pow_r_dot_v = Math.pow(r_dot_v, inters.bsdf.alpha);

					local_illumination.r += (light.ip * light.color.r) * ks * pow_r_dot_v;
					local_illumination.g += (light.ip * light.color.g) * ks * pow_r_dot_v;
					local_illumination.b += (light.ip * light.color.b) * ks * pow_r_dot_v;
				}
				*/
			}

		}
		return local_illumination;
	}
	
	private Color3 cast_px_rays(Ray ray, int n_paths, int max_depth, int n_shadow_rays, int light_resolution_w, int light_resolution_h) {
		Color3 average = new Color3(0, 0, 0);
		
		if (n_paths <= 0)
			return average;
		
		IntersectedPoint closest = find_closest_intersection(ray);
		
		if (closest != null) {
			for (int i = 0; i < n_paths; i++) {
				Color3 color = cast_ray(ray, closest, n_shadow_rays, max_depth, 1, light_resolution_w, light_resolution_h);
				average.add(color);
			}
			
			average.times_scalar(1.0/n_paths);

//			System.out.println("pixel color: " + average.to_string());
		} else {
			average.r = scene.background.r;
			average.g = scene.background.g;
			average.b = scene.background.b;
		}
		

		return average;
	}

	private Vector3 random_hemisphere_direction(Vector3 normal) {		
		Vector3 candidate;
		final double epsilon = 1e-5;
		double u, v, theta, phi, x, y, z;
		
		do {
			u = Math.random();
			v = Math.random();
			theta = 2*Math.PI*u;
			phi = Math.acos(2*v - 1);
			
			x = Math.cos(theta) * Math.sin(phi);
			y = Math.sin(theta) * Math.sin(phi);
			z = Math.cos(phi);
			
			candidate = new Vector3(x, y, z);
		} while(Math.abs(candidate.dot(normal)) <= epsilon);
		
		if (candidate.dot(normal) < 0)
			candidate.times_scalar(-1);
		
		return candidate;
	}
	
	private Color3 cast_ray(Ray ray, IntersectedPoint given_inters, int n_shadow_rays, int remaining_depths, double current_ior, int light_resolution_w, int light_resolution_h) {
		// Maximum depth reached: return black
		if (remaining_depths <= -1)
			return new Color3(0, 0, 0);
		
		IntersectedPoint inters;
		if (given_inters != null)			
			inters = given_inters;
		else
			inters = find_closest_intersection(ray);
		
		// No objects intersected: return background
		if (inters == null) {
			return new Color3(scene.background.r, scene.background.g, scene.background.b);
		}
		
		// Intersection is a light source
		if (inters.is_light)
			return inters.surface_color;

		Color3 local_illum = point_local_illumination(inters, ray, n_shadow_rays, light_resolution_w, light_resolution_h);

		Color3 returned_color;
		
		// Decide and cast one random type of ray
		double k_total = inters.bsdf.kd + inters.bsdf.ks + inters.ray_kt;
		double choice = Math.random() * k_total;
		if (choice <= inters.bsdf.kd) {
			
//			System.out.println("diffuse");
			
			// ------ DIFFUSE NEW RAY ------
			Vector3 new_direction = random_hemisphere_direction(inters.normal);
			Ray new_ray = new Ray(inters, new_direction);
			
			returned_color = cast_ray(new_ray, null, n_shadow_rays, remaining_depths-1, current_ior, light_resolution_w, light_resolution_h);
			returned_color.times_scalar(inters.bsdf.kd);
			returned_color.times_scalar(new_ray.direction.dot(inters.normal));
		} else if (choice <= inters.bsdf.kd + inters.bsdf.ks) {
			
//			System.out.println("specular");
			
			// ------ SPECULAR NEW RAY ------
//			Vector3 v = new Vector3(-ray.direction.x, -ray.direction.y, -ray.direction.z);

//			Vector3 new_direction = new Vector3(v.x, v.y, v.z);
//			double scalar = -2 * v.dot(inters.normal);
//			new_direction.x += scalar * inters.normal.x;
//			new_direction.y += scalar * inters.normal.y;
//			new_direction.z += scalar * inters.normal.z;

//			try {
//				assert new_direction.equals(reflection_angle(ray.direction, inters.normal));
//			} catch (Error e) {
//				System.out.println("mine: " + new_direction.to_string());
//				System.out.println("article's: " + reflection_angle(ray.direction, inters.normal).to_string());
//			}
			
//			System.out.println("vector V: " + v.to_string());
//			System.out.println("new direction: " + new_direction.to_string());

			Vector3 new_direction = reflection_angle(ray.direction, inters.normal);
			
			Ray new_ray = new Ray(inters, new_direction);

			returned_color = cast_ray(new_ray, null, n_shadow_rays, remaining_depths-1, current_ior, light_resolution_w, light_resolution_h);
			returned_color.times_scalar(inters.ray_kr);
		} else {
			
//			System.out.println("refraction");
			
			// ------ REFRACTED NEW RAY ------
			Vector3 v = new Vector3(-ray.direction.x, -ray.direction.y, -ray.direction.z);

			Vector3 normal = new Vector3(inters.normal.x, inters.normal.y, inters.normal.z);
			if (v.dot(normal) < 0) 
				normal.times_scalar(-1);

			double new_ior = (current_ior == 1) ? inters.obj_ior : 1;
//			double refraction_ratio = current_refr_idx / new_refr_idx;
//			double scalar1 = -v.dot(normal);
//			double scalar2 = Math.sqrt(1 - Math.pow(refraction_ratio, 2) * (1 - Math.pow(scalar1, 2)));
//
//			Vector3 new_direction = new Vector3(v.x, v.y, v.z);
//			new_direction.times_scalar(refraction_ratio);
//
//			double scalar3 = refraction_ratio * scalar1 - scalar2;
//			new_direction.x += scalar3 * normal.x;
//			new_direction.y += scalar3 * normal.y;
//			new_direction.z += scalar3 * normal.z;

//			assert new_direction.equals(refraction_angle(ray.direction, normal, current_refr_idx, new_refr_idx));
			
			
//			System.out.println(remaining_depths);
//			System.out.println("refr idx: " + current_idx_refraction);
//			System.out.println("point: " + inters.to_string());
//			System.out.println("point test: " + ((QuadricObject) scene.objects[0]).test_point(inters));
//			System.out.println("new direction: " + new_direction.to_string());
//			System.out.println("NewDirection.Normal: " + new_direction.dot(inters.normal));

			Vector3 new_direction = refraction_direction(ray.direction, normal, current_ior, new_ior);
			
			if (new_direction != null) {
				Ray new_ray = new Ray(inters, new_direction);
	
				returned_color = cast_ray(new_ray, null, n_shadow_rays, remaining_depths-1, new_ior, light_resolution_w, light_resolution_h);
				returned_color.times_scalar(inters.ray_kt);
			} else {
				returned_color = new Color3(scene.background.r, scene.background.g, scene.background.b);
			}
		}

		local_illum.add(returned_color);
		return local_illum;
	}
	
	private Vector3 reflection_angle(Vector3 incident, Vector3 normal) {
		normal = new Vector3(normal.x, normal.y, normal.z);
		
		double cosI = - incident.dot(normal);
		normal.times_scalar(2*cosI);
		normal.x += incident.x;
		normal.y += incident.y;
		normal.z += incident.z;
		
		return normal;
	}
	
	private Vector3 refraction_direction(Vector3 incident, Vector3 normal, double ior1, double ior2) {
		normal = new Vector3(normal.x, normal.z, normal.z);
		
		double ratio = ior1 / ior2;
		double cosI = - incident.dot(normal);
		double sinT2 = ratio * ratio * (1 - cosI * cosI);
		
		if (sinT2 > 1)
			return null;
		
		double cosT = Math.sqrt(1 - sinT2);
		
		normal.times_scalar(ratio * cosI - cosT);
		normal.x += ratio * incident.x;
		normal.y += ratio * incident.y;
		normal.z += ratio * incident.z;
		
		return normal;
	}
}
