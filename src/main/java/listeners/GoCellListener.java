	package listeners;

	import java.awt.event.ItemEvent;
	import java.awt.event.ItemListener;

	import pluginTools.FileChooser;

public class GoCellListener implements ItemListener {
		
		
		public FileChooser parent;
		
		public GoCellListener(FileChooser parent) {
			
			this.parent = parent;
			
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			
		if (e.getStateChange() == ItemEvent.SELECTED) {
			
				
				parent.FilamentTrackMate = false;
			    parent.NotFilamentTrackMate = true;
			}
		if (e.getStateChange() == ItemEvent.SELECTED) {
			
			
			parent.FilamentTrackMate = true;
		    parent.NotFilamentTrackMate = false;
		}
			
		}

	}

	

