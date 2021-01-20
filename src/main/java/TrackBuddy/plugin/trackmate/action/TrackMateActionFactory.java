package TrackBuddy.plugin.trackmate.action;

import TrackBuddy.plugin.trackmate.TrackMateModule;
import TrackBuddy.plugin.trackmate.gui.TrackMateGUIController;

public interface TrackMateActionFactory extends TrackMateModule
{
	public TrackMateAction create( TrackMateGUIController controller );
}
