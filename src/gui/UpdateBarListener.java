package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JProgressBar;

import renderer.PathTracer;

public class UpdateBarListener implements ActionListener {
	private PathTracer pt;
	private JProgressBar p_bar;
	
	public UpdateBarListener(JProgressBar p_bar) {
		this.p_bar = p_bar;
	}
	
	public void setPathTracer(PathTracer pt) {
		if (this.pt != null && pt != null)
			throw new RuntimeException("Wait until the previous scene has been rendered.");
			
		this.pt = pt;		
	}
	
	public void actionPerformed(ActionEvent arg0) {
		if (pt == null) {
			p_bar.setValue(0);
		} else {
			
			int percentage = (int) (100 * pt.status());
			
			if (percentage > p_bar.getValue())
				p_bar.setValue(percentage);
		}
	}

}
