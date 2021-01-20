package listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import pluginTools.FileChooser;

public class GoTMListener implements ItemListener {
	
	
	public FileChooser parent;
	
	public GoTMListener(FileChooser parent) {
		
		parent = this.parent;
		
	}

	@Override
	public void itemStateChanged(ItemEvent arg0) {
		
		parent.TrackMate = true;
		
	}

}
