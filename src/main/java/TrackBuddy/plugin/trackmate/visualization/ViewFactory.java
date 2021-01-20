package TrackBuddy.plugin.trackmate.visualization;

import TrackBuddy.plugin.trackmate.Model;
import TrackBuddy.plugin.trackmate.SelectionModel;
import TrackBuddy.plugin.trackmate.Settings;
import TrackBuddy.plugin.trackmate.TrackMateModule;
import pluginTools.InteractiveAnalysis;

public interface ViewFactory extends TrackMateModule
{

	/**
	 * Returns a new instance of the concrete view.
	 *
	 * @param model
	 *            the model to display in the view.
	 * @param settings
	 *            a {@link Settings} object, which specific implementation might
	 *            use to display the model.
	 * @param selectionModel
	 *            the {@link SelectionModel} model to share in the created view.
	 * @return a new view of the specified model.
	 */
	public TrackMateModelView create(final InteractiveAnalysis parent,  final Model model, final Settings settings, final SelectionModel selectionModel );

}
