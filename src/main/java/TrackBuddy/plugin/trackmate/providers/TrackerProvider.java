package TrackBuddy.plugin.trackmate.providers;

import TrackBuddy.plugin.trackmate.tracking.BCellobjectTrackerFactory;

public class TrackerProvider extends AbstractProvider< BCellobjectTrackerFactory >
{


	public TrackerProvider()
	{
		super( BCellobjectTrackerFactory.class );
	}

	public static void main( final String[] args )
	{
		final TrackerProvider provider = new TrackerProvider();
		System.out.println( provider.echo() );
	}
}
