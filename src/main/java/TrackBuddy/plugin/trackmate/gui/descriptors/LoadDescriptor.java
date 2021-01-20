package TrackBuddy.plugin.trackmate.gui.descriptors;

import TrackBuddy.plugin.trackmate.Logger;
import TrackBuddy.plugin.trackmate.Model;
import TrackBuddy.plugin.trackmate.Settings;
import TrackBuddy.plugin.trackmate.TrackMate;
import TrackBuddy.plugin.trackmate.gui.GuiUtils;
import TrackBuddy.plugin.trackmate.gui.TrackMateGUIController;
import TrackBuddy.plugin.trackmate.io.IOUtils;
import TrackBuddy.plugin.trackmate.io.TmXmlReader;
import TrackBuddy.plugin.trackmate.io.TmXmlReader_v12;
import TrackBuddy.plugin.trackmate.io.TmXmlReader_v20;
import TrackBuddy.plugin.trackmate.providers.EdgeAnalyzerProvider;
import TrackBuddy.plugin.trackmate.providers.BCellobjectAnalyzerProvider;
import TrackBuddy.plugin.trackmate.providers.TrackAnalyzerProvider;
import TrackBuddy.plugin.trackmate.providers.TrackerProvider;
import TrackBuddy.plugin.trackmate.providers.ViewProvider;
import TrackBuddy.plugin.trackmate.util.TMUtils;
import TrackBuddy.plugin.trackmate.util.Version;
import TrackBuddy.plugin.trackmate.visualization.TrackMateModelView;
import TrackBuddy.plugin.trackmate.visualization.hyperstack.HyperStackDisplayer;
import TrackBuddy.plugin.trackmate.visualization.trackscheme.BCellobjectImageUpdater;
import TrackBuddy.plugin.trackmate.visualization.trackscheme.TrackScheme;
import pluginTools.InteractiveAnalysis;

import java.io.File;
import java.util.Collection;
import java.util.Map;

public class LoadDescriptor extends SomeDialogDescriptor
{

	private static final String KEY = "Loading";

	private final TrackMate trackmate;

	private final TrackMateGUIController controller;

	public LoadDescriptor( final TrackMateGUIController controller )
	{
		super( controller.getGUI().getLogPanel() );
		this.controller = controller;
		this.trackmate = controller.getPlugin();
	}

	@Override
	public void displayingPanel()
	{

		if ( null == file )
		{
			try
			{
				final File folder = new File( trackmate.getSettings().imp.getOriginalFileInfo().directory );
				file = new File( folder.getPath() + File.separator + trackmate.getSettings().imp.getShortTitle() + ".xml" );
			}
			catch ( final NullPointerException npe )
			{
				final File folder = new File( System.getProperty( "user.dir" ) ).getParentFile().getParentFile();
				file = new File( folder.getPath() + File.separator + "TrackMateData.xml" );
			}
		}

		final Logger logger = logPanel.getLogger();
		final File tmpFile = IOUtils.askForFileForLoading( file, "Load a TrackMate XML file", controller.getGUI(), logger );
		if ( null == tmpFile ) { return; }
		file = tmpFile;

		// Read the file content
		TmXmlReader reader = new TmXmlReader( file );
		final Version version = new Version( reader.getVersion() );
		if ( version.compareTo( new Version( "2.0.0" ) ) < 0 )
		{
			logger.log( "Detecting a file version " + version + ". Using the right reader.\n", Logger.GREEN_COLOR );
			reader = new TmXmlReader_v12( file );
		}
		else if ( version.compareTo( new Version( "2.1.0" ) ) < 0 )
		{
			logger.log( "Detecting a file version " + version + ". Using the right reader.\n", Logger.GREEN_COLOR );
			reader = new TmXmlReader_v20( file );
		}
		if ( !reader.isReadingOk() )
		{
			logger.error( reader.getErrorMessage() );
			logger.error( "Aborting.\n" ); // If I cannot even open the xml
											// file, it is not worth going on.
			return;
		}

		// Log
		final String logText = reader.getLog() + '\n';
		// Model
		final InteractiveAnalysis parent = reader.getParent();
		
		final Model model = new Model();
		// Settings -> empty for now.
		final Settings settings = new Settings();

		// With this we can create a new controller from the provided one:
		final TrackMate lTrackmate = new TrackMate( parent, model, settings );
		final TrackMateGUIController newcontroller = controller.createOn(parent,  lTrackmate );

		// We feed then the reader with the providers taken from the NEW
		// controller.
		final TrackerProvider trackerProvider = newcontroller.getTrackerProvider();
		final EdgeAnalyzerProvider edgeAnalyzerProvider = newcontroller.getEdgeAnalyzerProvider();
		final TrackAnalyzerProvider trackAnalyzerProvider = newcontroller.getTrackAnalyzerProvider();
		reader.readSettings( settings, trackerProvider, edgeAnalyzerProvider, trackAnalyzerProvider );

		// GUI position
		GuiUtils.positionWindow( newcontroller.getGUI(), settings.imp.getWindow() );

		// GUI state
		final String guiState = reader.getGUIState();

		// Views
		final ViewProvider viewProvider = newcontroller.getViewProvider();
		final Collection< TrackMateModelView > views = reader.getViews( parent,  viewProvider, model, settings, newcontroller.getSelectionModel() );
		for ( final TrackMateModelView view : views )
		{
			if ( view instanceof TrackScheme )
			{
				final TrackScheme trackscheme = ( TrackScheme ) view;
				trackscheme.setBCellobjectImageUpdater( new BCellobjectImageUpdater( settings ) );
			}
		}

		if ( !reader.isReadingOk() )
		{
			final Logger newlogger = newcontroller.getGUI().getLogger();
			newlogger.error( "Some errors occured while reading file:\n" );
			newlogger.error( reader.getErrorMessage() );
		}
		newcontroller.setGUIStateString( guiState );

		// Setup and render views
		if ( views.isEmpty() )
		{ // at least one view.
			views.add( new HyperStackDisplayer(parent,  model, newcontroller.getSelectionModel(), settings.imp ) );
		}
		final Map< String, Object > displaySettings = newcontroller.getGuimodel().getDisplaySettings();
		for ( final TrackMateModelView view : views )
		{
			for ( final String key : displaySettings.keySet() )
			{
				newcontroller.getGuimodel().addView( view );
				view.setDisplaySettings( key, displaySettings.get( key ) );
			}
			view.render();
		}

		// Close the old one
		controller.quit();

		// Text
		newcontroller.getGUI().getLogPanel().setTextContent( logText );
		model.getLogger().log( "File loaded on " + TMUtils.getCurrentTimeString() + '\n', Logger.BLUE_COLOR );
	}

	@Override
	public String getKey()
	{
		return KEY;
	}

}
