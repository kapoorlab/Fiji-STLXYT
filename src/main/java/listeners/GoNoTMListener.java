package listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import pluginTools.FileChooser;

public class GoNoTMListener implements ItemListener {
	
	
	public FileChooser parent;
	
	public GoNoTMListener(FileChooser parent) {
		
		parent = this.parent;
		
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		
	if (e.getStateChange() == ItemEvent.SELECTED) {
		
			
			parent.DoTrackMate = false;
		    parent.DoNotTrackMate = true;
		}
		
	
if (e.getStateChange() == ItemEvent.DESELECTED) {
	
		
		parent.DoTrackMate = true;
		parent.DoNotTrackMate = false;
	
	}
	}

}
