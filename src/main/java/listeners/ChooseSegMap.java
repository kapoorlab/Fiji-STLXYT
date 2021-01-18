package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import ij.WindowManager;
import net.imglib2.type.numeric.integer.IntType;
import pluginTools.FileChooser;
import pluginTools.simplifiedio.SimplifiedIO;

public class ChooseSegMap implements ActionListener {
	
	
	final FileChooser parent;
	final JComboBox<String> choice;
	
	
	public ChooseSegMap(final FileChooser parent, final JComboBox<String> choice ) {
		
		
		this.parent = parent;
		this.choice = choice;
		
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		
		String imagename = (String) choice.getSelectedItem();
      	parent.impSeg = WindowManager.getImage(imagename);
    	
      	parent.imageSeg = SimplifiedIO.openImage(
      			parent.impSeg.getOriginalFileInfo().directory + parent.impSeg.getOriginalFileInfo().fileName,
				new IntType());
	

	}
	

}
