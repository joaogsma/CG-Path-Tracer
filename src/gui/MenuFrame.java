package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class MenuFrame extends JFrame{
	private static final long serialVersionUID = 5978509499176920981L;

	public MenuFrame() {
		init_gui();
	}
	
	private void init_gui(){
		JPanel panel = new JPanel();
		panel.setLayout(null);
		add(panel);
		
		
		JLabel scene_filename = new JLabel("Scene file: ");
		Dimension d = scene_filename.getPreferredSize();
		scene_filename.setBounds(25, 20, d.width, d.height);
		panel.add(scene_filename);
		
		JTextField scene_textfield = new JTextField();
		scene_textfield.setBounds(105, 19, 120, 20);
		panel.add(scene_textfield);
		
		
		JProgressBar percentage_bar = new JProgressBar();
		percentage_bar.setStringPainted(true);
		percentage_bar.setBounds(26, 55, 305, 30);
		panel.add(percentage_bar);

		UpdateBarListener pct_bar_listener = new UpdateBarListener(percentage_bar);
		Timer timer = new Timer(1000, pct_bar_listener);
		timer.start();
		
		JButton render_button = new JButton("Render");
		d = render_button.getPreferredSize();
		render_button.setBounds(255, 15, d.width, d.height);
		render_button.addActionListener(new RenderActionListener(this, scene_textfield, pct_bar_listener));
		panel.add(render_button);

		JFrame parent = this;
		ActionListener instruction_listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(parent, instructions(), "Instructions", JOptionPane.PLAIN_MESSAGE);
			}
		};
		
		JButton instructions_button = new JButton("Instructions");
		d = instructions_button.getPreferredSize();
		int x = (365/2) - (d.width/2);
		// int x = 131;
		instructions_button.setBounds(x, 100, d.width, d.height);
		instructions_button.addActionListener(instruction_listener);
		panel.add(instructions_button);
		
		
		
		setLocationRelativeTo(null);
		setTitle("Path Tracer");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(365, 170);
		setResizable(false);
	}
	
	private String instructions() {
		StringBuffer instructions = new StringBuffer();
		instructions.append("1. Comments:\n#...");		
		instructions.append("\n");
		instructions.append("2. Output file:\noutput filename");
		instructions.append("\n");
		instructions.append("3. Camera coordinates (Z must be greater than 0):\n" + "eye X Y Z");
		instructions.append("\n");
		instructions.append("4. Image window:\northo x0 y0 x1 y1\nWindow is defined by lower left point (x0, y0) and upper right point (x1, y1).");
		instructions.append("\n");
		instructions.append("5. Size (image resolution):\nsize width height");
		instructions.append("\n");
		instructions.append("6. Background color:\nbackground R G B\nBackground color used for rays which do not hit any object.");
		instructions.append("\n");
		instructions.append("7. Ambient light intensity (0 <= Ia <= 1):\nambient Ia");
		instructions.append("\n");
		instructions.append("8. Point light:\nlightpoint X Y Z R G B Ip\nPoint light at coordinates (x, y, z), with color (R, G, B) and intensity Ip (R, G, B and Ip between 0 and 1).");
		instructions.append("\n");
		instructions.append("9. Area light:\nlightarea filename R G B Ip\nArea light with vertices and triangles specified in the given file, color (R, G, B) and intensity Ip (R, G, B and Ip between 0 and 1).");
		instructions.append("\n");
		instructions.append("10. Light division resolution:\nlightresolution width height\nSpecification of the grid in which to divide area lights. The light will be divided into (width*height) rectangles, with width being the division in the X dimension and \nheight being the division in the Z dimension. If this command is present, the number of shadow rays does not matter, since this approach is used instead.");
		instructions.append("\n");
		instructions.append("11. Maximum recursion depth (depth >= 0):\nmaxdepth depth");
		instructions.append("\n");
		instructions.append("12. Number of paths per pixel (Rays/pixel, paths >= 0):\nnpaths paths");
		instructions.append("\n");
		instructions.append("13. Number of shadow rays (srays >= 0):\nnshadowrays srays\nMaximum number of shadow rays computed per pixel (only applies to area lights). This command is irrelevant if the lightresolution command is present, since \ndividing the light into a grid is the approach used.");
		instructions.append("\n");
		instructions.append("14. Tonemapping operator value (tmp > 0):\ntonemapping tmp");
		instructions.append("\n");
		instructions.append("15. Quadric object:\nobjectquadric a b c d e f g h i j R G B ka kd ks alfa Kr Kt ior\nQuadric object specified by the equation Ax^2 + By^2 + Cz^2 + Dxy + Exz + Fyz + Gx + Hy + Iz + J = 0. R, G, B, ka, kd, ks, Kr and Kt values must be between 0 and 1.");
		instructions.append("\n");
		instructions.append("16. Triangulated object:\nobject filename offsetX offsetY offsetZ R G B ka kd ks alfa Ks Kt ior\nTriangulated object with vertices and triangles specified in the given file.\nR, G, B, ka, kd, ks, Kr and Kt values must be between 0 and 1. The offset values are an optional translation of the object (0 if no translatoins is desired).");
		instructions.append("\n");
		instructions.append("17. Files with object and area light descriptions must specify the vertices with the command 'v X Y Z' and the triangles with 'f v1 v2 v3'.\nAlso, the vertices must be all specified before thr triangles.");
		
		return instructions.toString();
	}
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				MenuFrame frame = new MenuFrame();
				frame.setVisible(true);
			}
		});
		
		
	}
}
