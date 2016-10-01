package gui;

import io.ProjectIO;
import io.RunData;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import renderer.Image;
import renderer.PathTracer;

public class RenderActionListener implements ActionListener {
	PathTracer pt;
	JTextField scene_textfield;
	UpdateBarListener pct_bar_listener;
	JFrame parent;
	
	public  RenderActionListener(JFrame parent, JTextField scene_textfield, UpdateBarListener pct_bar_listener) {
		this.parent = parent;
		this.scene_textfield = scene_textfield;
		this.pct_bar_listener = pct_bar_listener;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String scene_filename = scene_textfield.getText();

		try {
			RunData data = ProjectIO.read_scene(scene_filename);

			pt = new PathTracer(data.scene, data.camera, data.window);
			
			Thread thread = new Thread() {
				public void run() {
					Image image = pt.render(data.n_paths, data.max_depth, data.n_shadow_rays, data.light_resolution_w, data.light_resolution_h);				
					ProjectIO.save_image(image, data.out_filename, data.tonemapping);
					
					pct_bar_listener.setPathTracer(null);
					JOptionPane.showMessageDialog(parent, "Success!", "Message", JOptionPane.PLAIN_MESSAGE);
				};
			};
			
			pct_bar_listener.setPathTracer(pt);

			thread.start();
		} catch (RuntimeException e) {
			JOptionPane.showMessageDialog(parent, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

}
