package renderer;

public class Color3 {
	public double r, g, b;
	
	public Color3(double r, double g, double b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public void add(Color3 color) {
		r += color.r;
		g += color.g;
		b += color.b;
	}
	
	public void times_scalar(double scalar) {
		r *= scalar;
		g *= scalar;
		b *= scalar;
	}
	
	public boolean equals(Color3 color) {
		return r == color.r && g == color.g && b == color.b;
	}
	
	public String to_string() {
		return "(" + r + ", " + g + ", " + b +")";
	}
}
