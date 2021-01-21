	package listeners;

	import java.awt.event.ItemEvent;
	import java.awt.event.ItemListener;

	import pluginTools.FileChooser;

public class GoFilamentListener implements ItemListener {
		
		
		public FileChooser parent;
		
		public GoFilamentListener(FileChooser parent) {
			
			this.parent = parent;
			
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			
		if (e.getStateChange() == ItemEvent.SELECTED) {
			

			    parent.DoTrackMate = true;
		        parent.DoNotTrackMate = false;
				parent.FilamentTrackMate = true;
			    parent.NotFilamentTrackMate = false;
			}
		
		
		
		}

	}

	

