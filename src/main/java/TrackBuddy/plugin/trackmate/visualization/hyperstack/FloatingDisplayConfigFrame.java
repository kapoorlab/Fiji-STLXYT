package TrackBuddy.plugin.trackmate.visualization.hyperstack;

import static TrackBuddy.plugin.trackmate.visualization.TrackMateModelView.KEY_COLORMAP;

import java.awt.BorderLayout;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import TrackBuddy.plugin.trackmate.Model;
import TrackBuddy.plugin.trackmate.TrackMateOptionUtils;
import TrackBuddy.plugin.trackmate.features.edges.EdgeVelocityAnalyzer;
import TrackBuddy.plugin.trackmate.features.track.TrackIndexAnalyzer;
import TrackBuddy.plugin.trackmate.gui.DisplaySettingsEvent;
import TrackBuddy.plugin.trackmate.gui.DisplaySettingsListener;
import TrackBuddy.plugin.trackmate.gui.TrackMateGUIModel;
import TrackBuddy.plugin.trackmate.gui.TrackMateWizard;
import TrackBuddy.plugin.trackmate.gui.panels.ConfigureViewsPanel;
import TrackBuddy.plugin.trackmate.visualization.BCellobjectColorGenerator;
import TrackBuddy.plugin.trackmate.visualization.BCellobjectColorGeneratorPerTrackFeature;
import TrackBuddy.plugin.trackmate.visualization.FeatureColorGenerator;
import TrackBuddy.plugin.trackmate.visualization.ManualBCellobjectColorGenerator;
import TrackBuddy.plugin.trackmate.visualization.ManualEdgeColorGenerator;
import TrackBuddy.plugin.trackmate.visualization.PerEdgeFeatureColorGenerator;
import TrackBuddy.plugin.trackmate.visualization.PerTrackFeatureColorGenerator;
import TrackBuddy.plugin.trackmate.visualization.TrackMateModelView;
import tracking.BCellobject;

public class FloatingDisplayConfigFrame extends JFrame
{
	private static final long serialVersionUID = 1L;

	protected Model model;

	public FloatingDisplayConfigFrame( final Model model, final TrackMateModelView view )
	{
		this( model, view, null );
	}

	public FloatingDisplayConfigFrame( final Model model, final TrackMateModelView view, final String title )
	{
		this.model = model;
		setTitle( "TrackMate display config" );
		setIconImage( TrackMateWizard.TRACKMATE_ICON.getImage() );
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setSize( 310, 450 );
		setLocationByPlatform( true );
		final JPanel contentPane = new JPanel();
		contentPane.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
		contentPane.setLayout( new BorderLayout( 0, 0 ) );
		setContentPane( contentPane );

		final ConfigureViewsPanel panel = new ConfigureViewsPanel( model );
		contentPane.add( panel );

		final TrackMateGUIModel guimodel = new TrackMateGUIModel();

		final Map< String, Object > displaySettings = view.getDisplaySettings();
		panel.setBCellobjectColorGenerator( createBCellobjectColorGenerator() );
		panel.setTrackColorGenerator( createTrackColorGenerator() );
		panel.setEdgeColorGenerator( createEdgeColorGenerator() );
		panel.setBCellobjectColorGeneratorPerTrackFeature( createBCellobjectColorGeneratorPerTrackFeature() );
		panel.setManualEdgeColorGenerator( createManualEdgeColorGenerator() );

		panel.getTrackSchemeButton().setVisible( false );
		panel.getDoAnalysisButton().setVisible( false );

		if ( null != title )
			panel.getTitleJLabel().setText( "Display options for " + title + "." );

		displaySettings.put( KEY_COLORMAP, TrackMateOptionUtils.getOptions().getPaintScale() );
		guimodel.setDisplaySettings( displaySettings );

		guimodel.addView( view );
		final DisplaySettingsListener displaySettingsListener = new DisplaySettingsListener()
		{
			@Override
			public void displaySettingsChanged( final DisplaySettingsEvent event )
			{
				guimodel.getDisplaySettings().put( event.getKey(), event.getNewValue() );
				for ( final TrackMateModelView lView : guimodel.getViews() )
				{
					lView.setDisplaySettings( event.getKey(), event.getNewValue() );
					lView.refresh();
				}
			}
		};
		panel.addDisplaySettingsChangeListener( displaySettingsListener );
		panel.refreshGUI();

	}


	protected FeatureColorGenerator< BCellobject > createBCellobjectColorGenerator()
	{
		return new BCellobjectColorGenerator( model );
	}

	protected PerEdgeFeatureColorGenerator createEdgeColorGenerator()
	{
		return new PerEdgeFeatureColorGenerator( model, EdgeVelocityAnalyzer.VELOCITY );
	}

	protected PerTrackFeatureColorGenerator createTrackColorGenerator()
	{
		final PerTrackFeatureColorGenerator generator = new PerTrackFeatureColorGenerator( model, TrackIndexAnalyzer.TRACK_INDEX );
		return generator;
	}

	protected ManualBCellobjectColorGenerator createManualBCellobjectColorGenerator()
	{
		return new ManualBCellobjectColorGenerator();
	}

	protected ManualEdgeColorGenerator createManualEdgeColorGenerator()
	{
		return new ManualEdgeColorGenerator( model );
	}

	protected FeatureColorGenerator< BCellobject > createBCellobjectColorGeneratorPerTrackFeature()
	{
		final FeatureColorGenerator< BCellobject > generator = new BCellobjectColorGeneratorPerTrackFeature( model, TrackIndexAnalyzer.TRACK_INDEX );
		return generator;
	}

}
