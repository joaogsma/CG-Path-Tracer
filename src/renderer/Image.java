package renderer;

public class Image {
	private int width, height;
	private Color3[][] data;
	
	public Image(int width, int height) {
		this.width = width;
		this.height = height;
		data = new Color3[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				data[i][j] = new Color3(0, 0, 0);
			}
		}
	}
	
	public int width() {
		return width;
	}
	
	public int height() {
		return height;
	}
	
	public void set(int i, int j, Color3 value) {
		data[i][j] = value;
	}
	
	public Color3 get(int i, int j) {
		return data[i][j];
	}
}
