package TrackBuddy.plugin.trackmate.gui.descriptors;

import TrackBuddy.plugin.trackmate.TrackMate;
import TrackBuddy.plugin.trackmate.gui.TrackMateGUIController;
import TrackBuddy.plugin.trackmate.gui.panels.ConfigureViewsPanel;
import TrackBuddy.plugin.trackmate.visualization.FeatureColorGenerator;
import TrackBuddy.plugin.trackmate.visualization.ManualBCellobjectColorGenerator;
import TrackBuddy.plugin.trackmate.visualization.ManualEdgeColorGenerator;
import TrackBuddy.plugin.trackmate.visualization.PerEdgeFeatureColorGenerator;
import TrackBuddy.plugin.trackmate.visualization.PerTrackFeatureColorGenerator;
import tracking.BCellobject;

public class ConfigureViewsDescriptor implements WizardPanelDescriptor
{

	public static final String KEY = "ConfigureViews";

	private final ConfigureViewsPanel panel;

	private final TrackMateGUIController controller;

	public ConfigureViewsDescriptor( final TrackMate trackmate, final FeatureColorGenerator< BCellobject > BCellobjectColorGenerator, final PerEdgeFeatureColorGenerator edgeColorGenerator, final PerTrackFeatureColorGenerator trackColorGenerator, final FeatureColorGenerator< BCellobject > BCellobjectColorGeneratorPerTrackFeature, final ManualBCellobjectColorGenerator manualBCellobjectColorGenerator, final ManualEdgeColorGenerator manualEdgeColorGenerator, final TrackMateGUIController controller )
	{
		this.controller = controller;
		this.panel = new ConfigureViewsPanel( trackmate.getModel() );
		panel.setBCellobjectColorGenerator( BCellobjectColorGenerator );
		panel.setEdgeColorGenerator( edgeColorGenerator );
		panel.setTrackColorGenerator( trackColorGenerator );
		panel.setManualBCellobjectColorGenerator( manualBCellobjectColorGenerator );
		panel.setManualEdgeColorGenerator( manualEdgeColorGenerator );
		panel.setBCellobjectColorGeneratorPerTrackFeature( BCellobjectColorGeneratorPerTrackFeature );
	}

	@Override
	public ConfigureViewsPanel getComponent()
	{
		return panel;
	}

	@Override
	public void aboutToDisplayPanel()
	{
		panel.refreshGUI();
		controller.getGUI().setNextButtonEnabled( true );
	}

	@Override
	public void displayingPanel()
	{
		panel.refreshColorFeatures();
	}

	@Override
	public void aboutToHidePanel()
	{}

	@Override
	public void comingBackToPanel()
	{
		panel.refreshGUI();
		panel.refreshColorFeatures();
	}

	@Override
	public String getKey()
	{
		return KEY;
	}
}
