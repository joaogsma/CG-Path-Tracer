package io;

import geometry.Point3;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import common.Tuple;
import renderer.Color3;
import renderer.Image;
import renderer.Scene;
import renderer.Window;
import renderer.lights.AreaLight;
import renderer.lights.Light;
import renderer.lights.PointLight;
import renderer.objects.BSDF;
import renderer.objects.QuadricObject;
import renderer.objects.SceneObject;
import renderer.objects.TriangulatedObject;

public class ProjectIO {
	
	private static Tuple<double[][], int[][]> read_object(String filename) {
		File file = new File(filename);
		
		BufferedReader reader = null;
		
		ArrayList<double[]> points = new ArrayList<double[]>();
		ArrayList<int[]> triangles = new ArrayList<int[]>();
		
		try {
			reader = new BufferedReader(new FileReader(file));

			while(reader.ready()) {
				String line = reader.readLine();
				
				if (line == null || line.length() == 0)
					continue;
				
				String[] instruction = line.split(" ");
				
				// ---------- COMMENT ----------
				if (instruction[0].charAt(0) == '#') {
					continue; // Comment
				} else if (instruction[0].equals("v")) {
					double x = Double.parseDouble(instruction[1]);
					double y = Double.parseDouble(instruction[2]);
					double z = Double.parseDouble(instruction[3]);
					
					double[] vertex = {x, y, z};
					points.add(vertex);
				} else if (instruction[0].equals("f")) {
					int v1 = Integer.parseInt(instruction[1]);
					int v2 = Integer.parseInt(instruction[2]);
					int v3 = Integer.parseInt(instruction[3]);
					
					int[] triangle = {v1, v2, v3};
					triangles.add(triangle);
				} else {
					throw new RuntimeException("Invalid Command.");
				}
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Object file '" + filename + "' does not exist.");
		} catch (IOException e) {
			throw new RuntimeException("Could not read object file '" + filename + "'.");
		} finally {
			if (reader != null) {
				try { reader.close();
				} catch (IOException e) {
//					throw new RuntimeException("Reader could not be closed.");
				}
			}
		}
		
		
		double[][] points_array = points.toArray(new double[0][0]);
		int[][] triangles_array = triangles.toArray(new int[0][0]);
		
		return new Tuple<double[][], int[][]>(points_array, triangles_array);
	}
	
	private static QuadricObject read_quadric(String[] instruction) {
		// Ax^2 + By^2 + Cz^2 + Dxy + Exz + Fyz + Gx + Hy + Iz + J = 0
		double a = Double.parseDouble(instruction[1]);
		double b = Double.parseDouble(instruction[2]);
		double c = Double.parseDouble(instruction[3]);
		double d = Double.parseDouble(instruction[4]);
		double e = Double.parseDouble(instruction[5]);
		double f = Double.parseDouble(instruction[6]);
		double g = Double.parseDouble(instruction[7]);
		double h = Double.parseDouble(instruction[8]);
		double i = Double.parseDouble(instruction[9]);
		double j = Double.parseDouble(instruction[10]);
		double red = Double.parseDouble(instruction[11]);
		double green = Double.parseDouble(instruction[12]);
		double blue = Double.parseDouble(instruction[13]);
		double ka = Double.parseDouble(instruction[14]);
		double kd = Double.parseDouble(instruction[15]);
		double ks = Double.parseDouble(instruction[16]);
		double alpha = Double.parseDouble(instruction[17]);
		double ray_kr = Double.parseDouble(instruction[18]);
		double ray_kt = Double.parseDouble(instruction[19]);
		double ior = Double.parseDouble(instruction[20]);
		
		if (!(0 <= red && red <= 1 && 0 <= green && green <= 1 && 0 <= blue && blue <= 1))
			throw new RuntimeException("RGB values must be between 0 and 1.");
		if (ka < 0 || ka > 1 || kd < 0 || kd > 1 || ks < 0 || ks > 1 )
			throw new RuntimeException("Ka, Kd and Ks must be between 0 and 1.");
		if (ray_kr < 0 || ray_kr > 1 || ray_kt < 0 || ray_kt > 1)
			throw new RuntimeException("Kr and Kt must be between 0 and 1.");
		
		return new QuadricObject(a, b, c, d, e, f, g, h, i, j, red, green, blue, 
				new BSDF(ka, kd, ks, alpha), ray_kr, ray_kt, ior);
	}
	
	private static PointLight read_point_light(String[] instruction) {
		double x = Double.parseDouble(instruction[1]);
		double y = Double.parseDouble(instruction[2]);
		double z = Double.parseDouble(instruction[3]);
		double r = Double.parseDouble(instruction[4]);
		double g = Double.parseDouble(instruction[5]);
		double b = Double.parseDouble(instruction[6]);
		double ip = Double.parseDouble(instruction[7]);
		
		if (!(0 <= r && r <= 1 && 0 <= g && g <= 1 && 0 <= b && b <= 1 && 0 <= ip && ip <= 1)) {
			throw new RuntimeException(
					"RGB and Ip values for the light at " + new Point3(x, y, z).to_string() + "must be between 0 and 1.");
		}
		
		return new PointLight(x, y, z, r, g, b, ip);
		
	}
	
	private static AreaLight read_area_light(String[] instruction) {
		// light luz.obj red green blue Ip
		String light_filename = instruction[1];
		double r = Double.parseDouble(instruction[2]);
		double g = Double.parseDouble(instruction[3]);
		double b = Double.parseDouble(instruction[4]);
		double ip = Double.parseDouble(instruction[5]);
		
		if (!(0 <= r && r <= 1 && 0 <= g && g <= 1 && 0 <= b && b <= 1 && 0 <= ip && ip <= 1)) {
			throw new RuntimeException(
					"RGB and Ip values for the light at '" + light_filename + "' must be between 0 and 1.");
		}
		
		Tuple<double[][], int[][]> params = read_object(light_filename);
		return new AreaLight(params.fst, params.snd, r, g, b, ip);
	}
	
	private static TriangulatedObject read_triangulated_object(String[] instruction) {
		String obj_filename = instruction[1];
		double offset_x = Double.parseDouble(instruction[2]);
		double offset_y = Double.parseDouble(instruction[3]);
		double offset_z = Double.parseDouble(instruction[4]);
		double r = Double.parseDouble(instruction[5]);
		double g = Double.parseDouble(instruction[6]);
		double b = Double.parseDouble(instruction[7]);
		double ka = Double.parseDouble(instruction[8]);
		double kd = Double.parseDouble(instruction[9]);
		double ks = Double.parseDouble(instruction[10]);
		double alpha = Double.parseDouble(instruction[11]);
		double ray_kr = Double.parseDouble(instruction[12]);
		double ray_kt = Double.parseDouble(instruction[13]);
		double ior = Double.parseDouble(instruction[14]);
		
		if (!(0 <= r && r <= 1 && 0 <= g && g <= 1 && 0 <= b && b <= 1))
			throw new RuntimeException("RGB values must be between 0 and 1.");
		if (ka < 0 || ka > 1 || kd < 0 || kd > 1 || ks < 0 || ks > 1 )
			throw new RuntimeException("Ka, Kd and Ks must be between 0 and 1.");
		if (ray_kr < 0 || ray_kr > 1 || ray_kt < 0 || ray_kt > 1)
			throw new RuntimeException("Kr and Kt must be between 0 and 1.");
		
		Tuple<double[][], int[][]> params = read_object(obj_filename);
		for (double[] coordinates : params.fst) {
			coordinates[0] += offset_x;
			coordinates[1] += offset_y;
			coordinates[2] += offset_z;
		}
		
		return new TriangulatedObject(params.fst, params.snd, r, g, b,
				new BSDF(ka, kd, ks, alpha), ray_kr, ray_kt, ior);
	}
	
	public static RunData read_scene(String filename) {
		File file = new File(filename);
		
		String out_filename = filename + " (output).png";
		ArrayList<SceneObject> obj_list = new ArrayList<SceneObject>();
		ArrayList<Light> light_list = new ArrayList<Light>();
		Color3 background = new Color3(0, 0, 0);
		double ia = -1;
		Point3 camera = null;
		Window window = null;
		double x0 = 0, y0 = 0, x1 = 0, y1 = 0;
		int width = 0, height = 0;
		boolean window_coordinates = false, window_size = false;
		int n_paths = 0, max_depth = 0, n_shadow_rays = 20;
		double tonemapping = 1;
		int light_resolution_w = -1;
		int light_resolution_h = -1;
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));

			while (reader.ready()) {
				String line = reader.readLine();
				
				if (line == null || line.length() == 0)
					continue;
				
				String[] instruction = line.split(" ");
				
					// ---------- COMMENT ----------
				if (instruction[0].charAt(0) == '#') {
					continue; // Comment
				
					// ---------- OUTPUT ----------
				} else if (instruction[0].equals("output")) {
					out_filename = instruction[1]; // Output file name
				
					// ---------- CAMERA ----------
				} else if (instruction[0].equals("eye")) {
					// Camera coordinates
					double x = Double.parseDouble(instruction[1]);
					double y = Double.parseDouble(instruction[2]);
					double z = Double.parseDouble(instruction[3]);
					
					if (z <= 0) 
						throw new RuntimeException("Camera z coordinate must be greater than 0.");
					
					camera = new Point3(x, y, z);
				
					// ---------- WINDOW ----------
				} else if (instruction[0].equals("ortho")) {
					x0 = Double.parseDouble(instruction[1]);
					y0 = Double.parseDouble(instruction[2]);
					x1 = Double.parseDouble(instruction[3]);
					y1 = Double.parseDouble(instruction[4]);
					
					if (x0 > x1 || y0 > y1)
						throw new RuntimeException("Invalid window (ortho) coordinates.");
					
					window_coordinates = true;
					if (window_size)
						window = new Window(x0, y0, x1, y1, width, height);
				
					// ---------- RESOLUTION ----------
				} else if (instruction[0].equals("size")) {
					width = Integer.parseInt(instruction[1]);
					height = Integer.parseInt(instruction[2]);
					
					if (width <= 0 || height <= 0)
						throw new RuntimeException("Resolution values width and height must be greater than 0.");
					
					window_size = true;
					if (window_coordinates)
						window = new Window(x0, y0, x1, y1, width, height);
					
					// ---------- AMB. LIGHT COLOR ----------
				} else if (instruction[0].equals("background")) {
					double r = Double.parseDouble(instruction[1]);
					double g = Double.parseDouble(instruction[2]);
					double b = Double.parseDouble(instruction[3]);
					
					if (!(0 <= r && r <= 1 && 0 <= g && g <= 1 && 0 <= b && b <= 1))
						throw new RuntimeException("RGB background values must be between 0 and 1.");
					
					background = new Color3(r, g, b);
				
					// ---------- AMB. LIGHT INTENSITY ----------
				} else if (instruction[0].equals("ambient")) {
					ia = Double.parseDouble(instruction[1]);
					
					if (ia < 0 || ia > 1)
						throw new RuntimeException("Ia value must be between 0 and 1.");

					// ---------- POINT LIGHT ----------
				} else if (instruction[0].equals("lightpoint")) {
					PointLight light = read_point_light(instruction);
					light_list.add(light);
					
					// ---------- AREA LIGHT ----------
				} else if (instruction[0].equals("lightarea")) {
					AreaLight light = read_area_light(instruction);
					light_list.add(light);
					
					// ---------- RAYS/PIXEL ----------
				} else if (instruction[0].equals("npaths")) {
					n_paths = Integer.parseInt(instruction[1]);
					
					if (n_paths < 0)
						throw new RuntimeException("Rays/pixel (npaths) value must be positive.");
				
					// ---------- RECURSION MAX DEPTH ----------
				} else if (instruction[0].equals("maxdepth")) {
					max_depth = Integer.parseInt(instruction[1]);
					
					if (max_depth < 0)
						throw new RuntimeException("Recursion maximum depth (maxdepth) must be positive.");
				
					// ---------- QUADRIC OBJ ----------
				} else if (instruction[0].equals("objectquadric")) {
					QuadricObject obj = read_quadric(instruction);
					obj_list.add(obj);
					
					// ---------- TRIANGULATED OBJ ----------
				} else if (instruction[0].equals("object")) {
					TriangulatedObject obj = read_triangulated_object(instruction);
					obj_list.add(obj);
					
					// ---------- TONEMAPPING ----------
				} else if (instruction[0].equals("tonemapping")) {
					tonemapping = Double.parseDouble(instruction[1]);
					
					if (tonemapping <= 0)
						throw new RuntimeException("Tonemapping operator value (tonemapping) must be greater than 0.");
					
					// ---------- NUMBER OF SHADOW RAYS ----------
				} else if (instruction[0].equals("nshadowrays")) {
					n_shadow_rays = Integer.parseInt(instruction[1]);
					
					if (n_shadow_rays < 0)
						throw new RuntimeException("The number of shadow rays cast per intersection (nshadowrays) must be positive."); 
					
					// ---------- LIGHT 'PIXEL RESOLUTION' ----------
				} else if (instruction[0].equals("lightresolution")) {
					light_resolution_w = Integer.parseInt(instruction[1]);
					light_resolution_h = Integer.parseInt(instruction[2]);
					
					if (light_resolution_w <= 0 || light_resolution_h <= 0)
						throw new RuntimeException("The width and height of area lights' grid division must be greater than 0."); 
					
					// ---------- INVALID COMMAND ----------
				} else {
					throw new RuntimeException("Invalid command in file '" + filename + "'.");
				}
			}
		
		} catch (RuntimeException e) {
			throw e;
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Scene file does not exist.");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try { reader.close();
				} catch (IOException e) {
//					throw new RuntimeException("Reader could not be closed.");
				}
			}
		}
		
		SceneObject[] obj_array = obj_list.toArray(new SceneObject[0]);
		Light[] light_array = light_list.toArray(new Light[0]);
		
		Scene scene = new Scene(obj_array, light_array, background.r, background.g, background.b, ia);
		
		return new RunData(scene, camera, window, out_filename, n_paths, max_depth, n_shadow_rays, tonemapping, light_resolution_w, light_resolution_h);
	}
	
	public static void save_image(Image image, String out_filename, double tonemapping) {
		BufferedImage out = new BufferedImage(image.width(), image.height(), BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < image.width(); i++) {
			for (int j = 0; j < image.height(); j++) {
				double ro = image.get(i, j).r / (image.get(i, j).r + tonemapping);
				double go = image.get(i, j).g / (image.get(i, j).g + tonemapping);
				double bo = image.get(i, j).b / (image.get(i, j).b + tonemapping);
				
				int r = (int) Math.round(255*ro);
				int g = (int) Math.round(255*go);
				int b = (int) Math.round(255*bo);
				
				int color = (r << 16) | (g << 8) | b;
				out.setRGB(j, i, color);
			}
		}
		
		File fimage = new File(out_filename);
		try {
			ImageIO.write(out, "PNG", fimage);
		} catch (IOException e) {
			throw new RuntimeException("Could not save image file.");
		}
		
	}
	
//	private static void normalize_coordinates(String filename) throws FileNotFoundException {
//		Tuple<double[][], int[][]> tuple = read_object(filename);
//		
//		double greatest = Double.NEGATIVE_INFINITY;
//		for (double[] coordinates : tuple.fst) {
//			for (double c : coordinates) {
//				greatest = Math.max(greatest, Math.abs(c));
//			}
//		}
//		
//		greatest /= 2;
//		for (double[] coordinates : tuple.fst) {
//			for (int i = 0; i < 3; i++) {
//				coordinates[i] /= greatest;
//				if (i == 2) coordinates[i] -= 20;
//			}
//		}
//		
//		PrintWriter writer = new PrintWriter(new File(filename));
//		for (double[] coordinates : tuple.fst) {
//			writer.print("v");
//			for (double c : coordinates) {
//				writer.print(" " + c);
//			}
//			writer.println();
//		}
//		writer.println();
//		for (int[] vertices : tuple.snd) {
//			writer.print("f");
//			for (int v : vertices) {
//				writer.print(" " + v);
//			}
//			writer.println();
//		}
//		
//		writer.close();
//	}
	
//	private static void permanent_translation(String filename, double delta_x, double delta_y, double delta_z) throws FileNotFoundException {
//		Tuple<double[][], int[][]> tuple = read_object(filename);
//		
//		for (double[] coordinates : tuple.fst) {
//			coordinates[0] += delta_x;
//			coordinates[1] += delta_y;
//			coordinates[2] += delta_z;
//		}
//		
//		PrintWriter writer = new PrintWriter(new File(filename));
//		for (double[] coordinates : tuple.fst) {
//			writer.print("v");
//			for (double c : coordinates) {
//				writer.print(" " + c);
//			}
//			writer.println();
//		}
//		writer.println();
//		for (int[] vertices : tuple.snd) {
//			writer.print("f");
//			for (int v : vertices) {
//				writer.print(" " + v);
//			}
//			writer.println();
//		}
//		
//		writer.close();
//	}
}
