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
	public void itemStateChanged(ItemEvent e) {
		
	if (e.getStateChange() == ItemEvent.SELECTED) {
		
			
			parent.DoTrackMate = true;
		    parent.DoNotTrackMate = false;
		}
		
	
if (e.getStateChange() == ItemEvent.DESELECTED) {
	
		
		parent.DoTrackMate = false;
		parent.DoNotTrackMate = true;
	
	}
	}

}
