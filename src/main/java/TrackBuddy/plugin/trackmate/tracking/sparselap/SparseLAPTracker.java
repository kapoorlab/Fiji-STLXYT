package TrackBuddy.plugin.trackmate.tracking.sparselap;

import static TrackBuddy.plugin.trackmate.tracking.LAPUtils.checkFeatureMap;
import static TrackBuddy.plugin.trackmate.tracking.TrackerKeys.KEY_ALLOW_GAP_CLOSING;
import static TrackBuddy.plugin.trackmate.tracking.TrackerKeys.KEY_ALLOW_TRACK_MERGING;
import static TrackBuddy.plugin.trackmate.tracking.TrackerKeys.KEY_ALLOW_TRACK_SPLITTING;
import static TrackBuddy.plugin.trackmate.tracking.TrackerKeys.KEY_ALTERNATIVE_LINKING_COST_FACTOR;
import static TrackBuddy.plugin.trackmate.tracking.TrackerKeys.KEY_BLOCKING_VALUE;
import static TrackBuddy.plugin.trackmate.tracking.TrackerKeys.KEY_CUTOFF_PERCENTILE;
import static TrackBuddy.plugin.trackmate.tracking.TrackerKeys.KEY_GAP_CLOSING_FEATURE_PENALTIES;
import static TrackBuddy.plugin.trackmate.tracking.TrackerKeys.KEY_GAP_CLOSING_MAX_DISTANCE;
import static TrackBuddy.plugin.trackmate.tracking.TrackerKeys.KEY_GAP_CLOSING_MAX_FRAME_GAP;
import static TrackBuddy.plugin.trackmate.tracking.TrackerKeys.KEY_LINKING_FEATURE_PENALTIES;
import static TrackBuddy.plugin.trackmate.tracking.TrackerKeys.KEY_LINKING_MAX_DISTANCE;
import static TrackBuddy.plugin.trackmate.tracking.TrackerKeys.KEY_MERGING_FEATURE_PENALTIES;
import static TrackBuddy.plugin.trackmate.tracking.TrackerKeys.KEY_MERGING_MAX_DISTANCE;
import static TrackBuddy.plugin.trackmate.tracking.TrackerKeys.KEY_SPLITTING_FEATURE_PENALTIES;
import static TrackBuddy.plugin.trackmate.tracking.TrackerKeys.KEY_SPLITTING_MAX_DISTANCE;
import static TrackBuddy.plugin.trackmate.util.TMUtils.checkMapKeys;
import static TrackBuddy.plugin.trackmate.util.TMUtils.checkParameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import TrackBuddy.plugin.trackmate.Logger;
import TrackBuddy.plugin.trackmate.Logger.SlaveLogger;
import TrackBuddy.plugin.trackmate.BCellobjectCollection;
import TrackBuddy.plugin.trackmate.tracking.BCellobjectTracker;
import tracking.BCellobject;
import net.imglib2.algorithm.MultiThreadedBenchmarkAlgorithm;

public class SparseLAPTracker extends MultiThreadedBenchmarkAlgorithm implements BCellobjectTracker
{
	private final static String BASE_ERROR_MESSAGE = "[SparseLAPTracker] ";

	private SimpleWeightedGraph< BCellobject, DefaultWeightedEdge > graph;

	private Logger logger = Logger.VOID_LOGGER;

	private final BCellobjectCollection BCellobjects;

	private final Map< String, Object > settings;

	/*
	 * CONSTRUCTOR
	 */

	public SparseLAPTracker( final BCellobjectCollection BCellobjects, final Map< String, Object > settings )
	{
		this.BCellobjects = BCellobjects;
		this.settings = settings;
	}

	/*
	 * METHODS
	 */

	@Override
	public SimpleWeightedGraph< BCellobject, DefaultWeightedEdge > getResult()
	{
		return graph;
	}

	@Override
	public boolean checkInput()
	{
		return true;
	}

	@Override
	public boolean process()
	{

		/*
		 * Check input now.
		 */

		// Check that the objects list itself isn't null
		if ( null == BCellobjects )
		{
			errorMessage = BASE_ERROR_MESSAGE + "The BCellobject collection is null.";
			return false;
		}

		// Check that the objects list contains inner collections.
		if ( BCellobjects.keySet().isEmpty() )
		{
			errorMessage = BASE_ERROR_MESSAGE + "The BCellobject collection is empty.";
			return false;
		}

		// Check that at least one inner collection contains an object.
		boolean empty = true;
		for ( final int frame : BCellobjects.keySet() )
		{
			if ( BCellobjects.getNBCellobjects( frame ) > 0 )
			{
				empty = false;
				break;
			}
		}
		if ( empty )
		{
			errorMessage = BASE_ERROR_MESSAGE + "The BCellobject collection is empty.";
			return false;
		}
		// Check parameters
		final StringBuilder errorHolder = new StringBuilder();
		if ( !checkSettingsValidity( settings, errorHolder ) )
		{
			errorMessage = BASE_ERROR_MESSAGE + "Incorrect settings map:\n" + errorHolder.toString();
			return false;
		}

		/*
		 * Process.
		 */

		final long start = System.currentTimeMillis();

		/*
		 * 1. Frame to frame linking.
		 */


		// Prepare settings object
		final Map< String, Object > ftfSettings = new HashMap< >();
		ftfSettings.put( KEY_LINKING_MAX_DISTANCE, settings.get( KEY_LINKING_MAX_DISTANCE ) );
		ftfSettings.put( KEY_ALTERNATIVE_LINKING_COST_FACTOR, settings.get( KEY_ALTERNATIVE_LINKING_COST_FACTOR ) );
		ftfSettings.put( KEY_LINKING_FEATURE_PENALTIES, settings.get( KEY_LINKING_FEATURE_PENALTIES ) );

		final SparseLAPFrameToFrameTracker frameToFrameLinker = new SparseLAPFrameToFrameTracker( BCellobjects, ftfSettings );
		frameToFrameLinker.setNumThreads( numThreads );
		final SlaveLogger ftfLogger = new SlaveLogger( logger, 0, 0.5 );
		frameToFrameLinker.setLogger( ftfLogger );

		if ( !frameToFrameLinker.checkInput() || !frameToFrameLinker.process() )
		{
			errorMessage = frameToFrameLinker.getErrorMessage();
			return false;
		}

		graph = frameToFrameLinker.getResult();

		/*
		 * 2. Gap-closing, merging and splitting.
		 */

		// Prepare settings object
		final Map< String, Object > slSettings = new HashMap< >();

		slSettings.put( KEY_ALLOW_GAP_CLOSING, settings.get( KEY_ALLOW_GAP_CLOSING ) );
		slSettings.put( KEY_GAP_CLOSING_FEATURE_PENALTIES, settings.get( KEY_GAP_CLOSING_FEATURE_PENALTIES ) );
		slSettings.put( KEY_GAP_CLOSING_MAX_DISTANCE, settings.get( KEY_GAP_CLOSING_MAX_DISTANCE ) );
		slSettings.put( KEY_GAP_CLOSING_MAX_FRAME_GAP, settings.get( KEY_GAP_CLOSING_MAX_FRAME_GAP ) );

		slSettings.put( KEY_ALLOW_TRACK_SPLITTING, settings.get( KEY_ALLOW_TRACK_SPLITTING ) );
		slSettings.put( KEY_SPLITTING_FEATURE_PENALTIES, settings.get( KEY_SPLITTING_FEATURE_PENALTIES ) );
		slSettings.put( KEY_SPLITTING_MAX_DISTANCE, settings.get( KEY_SPLITTING_MAX_DISTANCE ) );

		slSettings.put( KEY_ALLOW_TRACK_MERGING, settings.get( KEY_ALLOW_TRACK_MERGING ) );
		slSettings.put( KEY_MERGING_FEATURE_PENALTIES, settings.get( KEY_MERGING_FEATURE_PENALTIES ) );
		slSettings.put( KEY_MERGING_MAX_DISTANCE, settings.get( KEY_MERGING_MAX_DISTANCE ) );

		slSettings.put( KEY_ALTERNATIVE_LINKING_COST_FACTOR, settings.get( KEY_ALTERNATIVE_LINKING_COST_FACTOR ) );
		slSettings.put( KEY_CUTOFF_PERCENTILE, settings.get( KEY_CUTOFF_PERCENTILE ) );

		// Solve.
		final SparseLAPSegmentTracker segmentLinker = new SparseLAPSegmentTracker( graph, slSettings );
		segmentLinker.setNumThreads( numThreads );
		final SlaveLogger slLogger = new SlaveLogger( logger, 0.5, 0.5 );
		segmentLinker.setLogger( slLogger );

		if ( !segmentLinker.checkInput() || !segmentLinker.process() )
		{
			errorMessage = segmentLinker.getErrorMessage();
			return false;
		}

		logger.setStatus( "" );
		logger.setProgress( 1d );
		final long end = System.currentTimeMillis();
		processingTime = end - start;


		return true;
	}

	@Override
	public void setLogger( final Logger logger )
	{
		this.logger = logger;
	}

	private static final boolean checkSettingsValidity( final Map< String, Object > settings, final StringBuilder str )
	{
		if ( null == settings )
		{
			str.append( "Settings map is null.\n" );
			return false;
		}

		boolean ok = true;
		// Linking
		ok = ok & checkParameter( settings, KEY_LINKING_MAX_DISTANCE, Double.class, str );
		ok = ok & checkFeatureMap( settings, KEY_LINKING_FEATURE_PENALTIES, str );
		// Gap-closing
		ok = ok & checkParameter( settings, KEY_ALLOW_GAP_CLOSING, Boolean.class, str );
		ok = ok & checkParameter( settings, KEY_GAP_CLOSING_MAX_DISTANCE, Double.class, str );
		ok = ok & checkParameter( settings, KEY_GAP_CLOSING_MAX_FRAME_GAP, Integer.class, str );
		ok = ok & checkFeatureMap( settings, KEY_GAP_CLOSING_FEATURE_PENALTIES, str );
		// Splitting
		ok = ok & checkParameter( settings, KEY_ALLOW_TRACK_SPLITTING, Boolean.class, str );
		ok = ok & checkParameter( settings, KEY_SPLITTING_MAX_DISTANCE, Double.class, str );
		ok = ok & checkFeatureMap( settings, KEY_SPLITTING_FEATURE_PENALTIES, str );
		// Merging
		ok = ok & checkParameter( settings, KEY_ALLOW_TRACK_MERGING, Boolean.class, str );
		ok = ok & checkParameter( settings, KEY_MERGING_MAX_DISTANCE, Double.class, str );
		ok = ok & checkFeatureMap( settings, KEY_MERGING_FEATURE_PENALTIES, str );
		// Others
		ok = ok & checkParameter( settings, KEY_CUTOFF_PERCENTILE, Double.class, str );
		ok = ok & checkParameter( settings, KEY_ALTERNATIVE_LINKING_COST_FACTOR, Double.class, str );

		// Check keys
		final List< String > mandatoryKeys = new ArrayList< >();
		mandatoryKeys.add( KEY_LINKING_MAX_DISTANCE );
		mandatoryKeys.add( KEY_ALLOW_GAP_CLOSING );
		mandatoryKeys.add( KEY_GAP_CLOSING_MAX_DISTANCE );
		mandatoryKeys.add( KEY_GAP_CLOSING_MAX_FRAME_GAP );
		mandatoryKeys.add( KEY_ALLOW_TRACK_SPLITTING );
		mandatoryKeys.add( KEY_SPLITTING_MAX_DISTANCE );
		mandatoryKeys.add( KEY_ALLOW_TRACK_MERGING );
		mandatoryKeys.add( KEY_MERGING_MAX_DISTANCE );
		mandatoryKeys.add( KEY_ALTERNATIVE_LINKING_COST_FACTOR );
		mandatoryKeys.add( KEY_CUTOFF_PERCENTILE );
		final List< String > optionalKeys = new ArrayList< >();
		optionalKeys.add( KEY_LINKING_FEATURE_PENALTIES );
		optionalKeys.add( KEY_GAP_CLOSING_FEATURE_PENALTIES );
		optionalKeys.add( KEY_SPLITTING_FEATURE_PENALTIES );
		optionalKeys.add( KEY_MERGING_FEATURE_PENALTIES );
		optionalKeys.add( KEY_BLOCKING_VALUE );
		ok = ok & checkMapKeys( settings, mandatoryKeys, optionalKeys, str );

		return ok;
	}
}
