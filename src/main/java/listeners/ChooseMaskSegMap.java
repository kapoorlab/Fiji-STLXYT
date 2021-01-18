package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import ij.WindowManager;
import pluginTools.FileChooser;

public class ChooseMaskSegMap implements ActionListener {
	
	
	final FileChooser parent;
	final JComboBox<String> choice;
	
	
	public ChooseMaskSegMap(final FileChooser parent, final JComboBox<String> choice ) {
		
		
		this.parent = parent;
		this.choice = choice;
		
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		
		String imagename = (String) choice.getSelectedItem();
      	parent.impMask = WindowManager.getImage(imagename);
    	

		
	}
	

}
