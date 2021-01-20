package TrackBuddy.plugin.trackmate.tracking.oldlap;

import TrackBuddy.plugin.trackmate.BCellobjectCollection;
import TrackBuddy.plugin.trackmate.tracking.oldlap.hungarian.AssignmentAlgorithm;
import TrackBuddy.plugin.trackmate.tracking.oldlap.hungarian.JonkerVolgenantAlgorithm;

import java.util.Map;

public class FastLAPTracker extends LAPTracker {

	public FastLAPTracker( final BCellobjectCollection spots, final Map< String, Object > settings )
	{
		super( spots, settings );
	}

	@Override
	protected AssignmentAlgorithm createAssignmentProblemSolver() {
		return new JonkerVolgenantAlgorithm();
	}
}
