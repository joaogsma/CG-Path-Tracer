package renderer;

import geometry.Point3;

public class Window {
	public final int width, height;
	public final double px_w, px_h;
	public final Point3 lower_left, upper_right, upper_left, lower_right;
	
	public Window(double x0, double y0, double x1, double y1, int width, int height) {
		lower_left = new Point3(x0, y0, 0);
		upper_right = new Point3(x1, y1, 0);
		upper_left = new Point3(x0, y1, 0);
		lower_right = new Point3(x1, y0, 0);
		px_w = (x1 - x0) / width;
		px_h = (y1 - y0) / height;
		this.width = width;
		this.height = height;
		
	}
}
