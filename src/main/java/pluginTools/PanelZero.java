package pluginTools;

import javax.swing.JFrame;


public class PanelZero {

	public static void main(String[] args) {
		
		JFrame frame = new JFrame("");
		FileChooser panel = new FileChooser();

		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());
		
		
	}
	
	
}
