package coreFunction;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import Buddy.plugin.trackmate.TrackMatePlugIn_;
import pluginTools.InteractiveAnalysis;

public class BackgroundTMFunction extends SwingWorker<Void, Void> {
		
		
		final InteractiveAnalysis parent;
		final JProgressBar jpb;
		
		
		public BackgroundTMFunction(final InteractiveAnalysis parent,  final JProgressBar jpb) {
			
			
			this.parent = parent;
			this.jpb = jpb;
			
		}

		
		public Void doInBackground() throws Exception {
			
			TMobjectCollector newtrack = new TMobjectCollector(parent, jpb);
			newtrack.ShowObjectTime();
			
			
			
			return null;
			
		}
		@Override
		protected void done() {
			
			parent.jpb.setIndeterminate(false);
			if(parent.jpb!=null )
				utility.ProgressBar.SetProgressBar(parent.jpb, 100 ,
						"Collected all cells, starting TrackMate");
			
	
			parent.imp.close();
			TrackMatePlugIn_ plugin = new TrackMatePlugIn_(parent);
			plugin.run("threeD");
			
		}

}
