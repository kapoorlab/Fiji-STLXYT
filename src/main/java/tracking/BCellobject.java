package tracking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import TrackBuddy.plugin.trackmate.Dimension;
import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.RealLocalizable;
import net.imglib2.Point;
import net.imglib2.RealLocalizable;

	
public class BCellobject extends AbstractEuclideanSpace implements RealLocalizable, Comparable<BCellobject> {
		
		private final ConcurrentHashMap< String, Double > features = new ConcurrentHashMap< String, Double >();
		public static AtomicInteger IDcounter = new AtomicInteger( -1 );
		private final int ID;
		/** A user-supplied name for this cell. */
		private String name;
		public int time;
		public final Cellobject currentcell;
		static int totalfeatures = 14;
		
		public BCellobject( final RealLocalizable currentcell) {
			
			
			
			super(3);
			this.ID = IDcounter.incrementAndGet();
			putFeature( STARTPOSITION_X, Double.valueOf( currentcell.getDoublePosition(0) ) );
			putFeature( STARTPOSITION_Y, Double.valueOf( currentcell.getDoublePosition(1) ) );
			putFeature( ENDPOSITION_X, Double.valueOf( currentcell.getDoublePosition(0) ) );
			putFeature( ENDPOSITION_Y, Double.valueOf( currentcell.getDoublePosition(1) ) );
			putFeature( POSITION_X, Double.valueOf( currentcell.getDoublePosition(0) ) );
			putFeature( POSITION_Y, Double.valueOf( currentcell.getDoublePosition(1) ) );
			putFeature( POSITION_Z, Double.valueOf( currentcell.getDoublePosition(2) ) );
			putFeature( Radi_X, Double.valueOf( 1 ) );
			putFeature( Radi_Y, Double.valueOf( 1) );
			putFeature( Radi_Z, Double.valueOf( 1) );
			putFeature( Size, Double.valueOf( 1));
			putFeature( INTENSITY, Double.valueOf( 1) );
			this.currentcell = null;
			this.name = "ID" + ID;
			
		}
		
	
		public BCellobject( final int ID )
		{
			super( 3 );
			this.ID = ID;
			synchronized ( IDcounter )
			{
				if ( IDcounter.get() < ID )
				{
					IDcounter.set( ID );
				}
			}
			this.currentcell = null;
		
		}
		public BCellobject( final Cellobject currentcell,  final int time) {
			
			
			
			super(3);
			this.ID = IDcounter.incrementAndGet();
			putFeature( STARTPOSITION_X, Double.valueOf( currentcell.StartLocation.getDoublePosition(0) ) );
			putFeature( STARTPOSITION_Y, Double.valueOf( currentcell.StartLocation.getDoublePosition(1) ) );
			putFeature( ENDPOSITION_X, Double.valueOf( currentcell.EndLocation.getDoublePosition(0) ) );
			putFeature( ENDPOSITION_Y, Double.valueOf( currentcell.EndLocation.getDoublePosition(1) ) );
			putFeature( POSITION_X, Double.valueOf( 0.5 * (currentcell.StartLocation.getDoublePosition(0) + currentcell.EndLocation.getDoublePosition(0) ) ) );
			putFeature( POSITION_Y, Double.valueOf( 0.5 * (currentcell.StartLocation.getDoublePosition(1) + currentcell.EndLocation.getDoublePosition(1)  )) );
			putFeature( POSITION_Z, Double.valueOf( 0.5 * (currentcell.StartLocation.getDoublePosition(2) + currentcell.EndLocation.getDoublePosition(2)  )) );
			putFeature( Radi_X, Double.valueOf( currentcell.extents[0] ) );
			putFeature( Radi_Y, Double.valueOf( currentcell.extents[1] ) );
			putFeature( Radi_Z, Double.valueOf( currentcell.extents[2] ) );
			putFeature( Size, (Double.valueOf( currentcell.extents[0] ) + Double.valueOf( currentcell.extents[1] ) + Double.valueOf(currentcell.extents[2])  )/3 );
			putFeature( INTENSITY, Double.valueOf( currentcell.totalIntensity ) );
			putFeature( POSITION_T, Double.valueOf( time) );
			
			this.currentcell = currentcell;
			this.time = time;
			this.name = "ID" + ID;
			
		}
		
		public BCellobject( final double x, final double y, final double z,   final double radius,  final String name )
		{
			super( 3 );
			this.ID = IDcounter.incrementAndGet();
			putFeature( STARTPOSITION_X, Double.valueOf( x ) );
			putFeature( STARTPOSITION_Y, Double.valueOf( y ) );
			putFeature( ENDPOSITION_X, Double.valueOf( x ) );
			putFeature( ENDPOSITION_Y, Double.valueOf( y ) );
			putFeature( POSITION_X, Double.valueOf( x ) );
			putFeature( POSITION_Y, Double.valueOf( y ) );
			putFeature( POSITION_Z, Double.valueOf( z ) );
			putFeature( Size, Double.valueOf( radius ) );
			if ( null == name )
			{
				this.name = "ID" + ID;
			}
			else
			{
				this.name = name;
			}
			this.currentcell = null;
			
		}

		
		
		/** The name of the cell X position feature. */
		public static final String STARTPOSITION_X = "STARTPOSITION_X";

		/** The name of the cell Y position feature. */
		public static final String STARTPOSITION_Y = "STARTPOSITION_Y";
		
		/** The name of the cell X position feature. */
		public static final String ENDPOSITION_X = "ENDPOSITION_X";

		/** The name of the cell Y position feature. */
		public static final String ENDPOSITION_Y = "ENDPOSITION_Y";
		
		
		/** The name of the cell X position feature. */
		public static final String POSITION_X = "POSITION_X";

		/** The name of the cell Y position feature. */
		public static final String POSITION_Y = "POSITION_Y";
		
		/** The name of the cell Y position feature. */
		public static final String POSITION_Z = "POSITION_Z";
		
		/** The name of the cell X position feature. */
		public static final String Radi_X = "Radi_X";

		/** The name of the cell Y position feature. */
		public static final String Radi_Y = "Radi_Y";
		

		/** The name of the cell Y position feature. */
		public static final String Radi_Z = "Radi_Z";
		
		/** The name of the Size position feature. */
		public static final String Size = "Size";

		/** The name of the cell Radius feature. */
		public static final String[] RADIUS = new String[] {Radi_X, Radi_Y, Radi_Z};
		
		/** The name of the cell Radius feature. */
		public static final String INTENSITY = "INTENSITY";

		/** The name of the cell T position feature. */
		public static final String POSITION_T = "POSITION_T";

		/** The position features. */
		public final static String[] POSITION_FEATURES = new String[] { STARTPOSITION_X, STARTPOSITION_Y, ENDPOSITION_X, ENDPOSITION_Y , POSITION_X, POSITION_Y, POSITION_Z };

		
		public final static Collection< String > FEATURES = new ArrayList< >( totalfeatures );

		/** The 4 privileged cell feature names. */
		public final static Map< String, String > FEATURE_NAMES = new HashMap< >( totalfeatures );

		/** The 4 privileged cell feature short names. */
		public final static Map< String, String > FEATURE_SHORT_NAMES = new HashMap< >( totalfeatures );

		/** The 4 privileged cell feature dimensions. */
		public final static Map< String, Dimension > FEATURE_DIMENSIONS = new HashMap< >( totalfeatures );

		/** The 4 privileged cell feature isInt flags. */
		public final static Map< String, Boolean > IS_INT = new HashMap< >( totalfeatures );
		/**
		 * @return the name for this cell.
		 */
		public String getName()
		{
			return this.name;
		}

		/**
		 * Set the name of this cell.
		 * 
		 * @param name
		 *            the name to use.
		 */
		public void setName( final String name )
		{
			this.name = name;
		}
		static
		{
			FEATURES.add( STARTPOSITION_X );
			FEATURES.add( STARTPOSITION_Y );
			
			FEATURES.add( ENDPOSITION_X );
			FEATURES.add( ENDPOSITION_Y );
			
			FEATURES.add( POSITION_X );
			FEATURES.add( POSITION_Y );
			FEATURES.add( POSITION_Z );
			
			FEATURES.add( POSITION_T );
			FEATURES.add( Radi_X );
			FEATURES.add( Radi_Y );
			FEATURES.add( Radi_Z );
			FEATURES.add( INTENSITY );
			FEATURES.add( Size );
			
			FEATURE_NAMES.put( STARTPOSITION_X, "StartX" );
			FEATURE_NAMES.put( STARTPOSITION_Y, "StartY" );
			
			FEATURE_NAMES.put( ENDPOSITION_X, "EndX" );
			FEATURE_NAMES.put( ENDPOSITION_Y, "EndY" );
			
			FEATURE_NAMES.put( POSITION_X, "X" );
			FEATURE_NAMES.put( POSITION_Y, "Y" );
			FEATURE_NAMES.put( POSITION_Z, "Z" );
			FEATURE_NAMES.put( POSITION_T, "T" );

			FEATURE_NAMES.put( Radi_X, "Radi_X" );
			FEATURE_NAMES.put( Radi_Y, "Radi_Y" );
			FEATURE_NAMES.put( Radi_Z, "Radi_Z" );
			FEATURE_NAMES.put( INTENSITY, "Intensity" );
			FEATURE_NAMES.put( Size, "Size" );
			
			FEATURE_SHORT_NAMES.put( STARTPOSITION_X, "StartX" );
			FEATURE_SHORT_NAMES.put( STARTPOSITION_Y, "StartY" );
			
			FEATURE_SHORT_NAMES.put( STARTPOSITION_X, "EndX" );
			FEATURE_SHORT_NAMES.put( STARTPOSITION_Y, "EndY" );
			
			FEATURE_SHORT_NAMES.put( POSITION_X, "X" );
			FEATURE_SHORT_NAMES.put( POSITION_Y, "Y" );
			FEATURE_SHORT_NAMES.put( POSITION_Y, "Z" );
			FEATURE_SHORT_NAMES.put( POSITION_T, "T" );
			FEATURE_SHORT_NAMES.put( Radi_X, "Radi_X" );
			FEATURE_SHORT_NAMES.put( Radi_Y, "Radi_Y" );
			FEATURE_SHORT_NAMES.put( Radi_Z, "Radi_Z" );
			FEATURE_SHORT_NAMES.put( INTENSITY, "I" );
			FEATURE_SHORT_NAMES.put( Size, "Size" );
			
			FEATURE_DIMENSIONS.put( STARTPOSITION_X, Dimension.POSITION );
			FEATURE_DIMENSIONS.put( STARTPOSITION_Y, Dimension.POSITION );
			FEATURE_DIMENSIONS.put( ENDPOSITION_X, Dimension.POSITION );
			FEATURE_DIMENSIONS.put( ENDPOSITION_Y, Dimension.POSITION );
			FEATURE_DIMENSIONS.put( POSITION_X, Dimension.POSITION );
			FEATURE_DIMENSIONS.put( POSITION_Y, Dimension.POSITION );
			FEATURE_DIMENSIONS.put( POSITION_Z, Dimension.POSITION );
			
			FEATURE_DIMENSIONS.put( POSITION_T, Dimension.TIME );
			FEATURE_DIMENSIONS.put( Radi_X, Dimension.LENGTH );
			FEATURE_DIMENSIONS.put( Radi_Y, Dimension.LENGTH );
			FEATURE_DIMENSIONS.put( Radi_Z, Dimension.LENGTH );
			FEATURE_DIMENSIONS.put( Size, Dimension.LENGTH );
			FEATURE_DIMENSIONS.put( INTENSITY, Dimension.LENGTH );
			
			IS_INT.put( STARTPOSITION_X, Boolean.FALSE );
			IS_INT.put( STARTPOSITION_Y, Boolean.FALSE );
			
			IS_INT.put( ENDPOSITION_X, Boolean.FALSE );
			IS_INT.put( ENDPOSITION_Y, Boolean.FALSE );
			
			IS_INT.put( POSITION_X, Boolean.FALSE );
			IS_INT.put( POSITION_Y, Boolean.FALSE );
			IS_INT.put( POSITION_Z, Boolean.FALSE );
			IS_INT.put( POSITION_T, Boolean.FALSE );
			IS_INT.put( Radi_X, Boolean.FALSE );
			IS_INT.put( Radi_Y, Boolean.FALSE );
			IS_INT.put( Radi_Z, Boolean.FALSE );
			IS_INT.put( Size, Boolean.FALSE );
			IS_INT.put( INTENSITY, Boolean.FALSE );
		}

		public final Double getFeature(final String feature) {
			
			return features.get(feature);
		}
		public final void putFeature( final String feature, final Double value )
		{
			features.put( feature, value );
		}
		

		public int ID()
		{
			return ID;
		}

		@Override
		public int compareTo(BCellobject o) {
			return hashCode() - o.hashCode();
		}

		@Override
		public void localize(float[] position) {
			int n = position.length;
			for (int d = 0; d < n; ++d)
				position[d] = getFloatPosition(d);
			
		}

		@Override
		public void localize(double[] position) {
			int n = position.length;
			for (int d = 0; d < n; ++d)
				position[d] = getFloatPosition(d);
			
		}

		@Override
		public float getFloatPosition(int d) {
			return (float) 0.5 *( currentcell.StartLocation.getFloatPosition(d) + currentcell.EndLocation.getFloatPosition(d));
		}

		@Override
		public double getDoublePosition(int d) {
			return 0.5 * (currentcell.StartLocation.getDoublePosition(d) + currentcell.EndLocation.getDoublePosition(d)) ;
		}
		
		/**
		 * Returns the difference between the location of two clouds, this operation
		 * returns ( <code>A.diffTo(B) = - B.diffTo(A)</code>)
		 *
		 * @param target
		 *            the Cloud to compare to.
		 * @param int
		 *            n n = 0 for X- coordinate, n = 1 for Y- coordinate
		 * @return the difference in co-ordinate specified.
		 */
		public double diffTo(final BCellobject target, int n) {

			final double thisBloblocation = 0.5 * (currentcell.StartLocation.getDoublePosition(n) + currentcell.EndLocation.getDoublePosition(n));
			final double targetBloblocation = 0.5 * (target.currentcell.StartLocation.getDoublePosition(n) + target.currentcell.EndLocation.getDoublePosition(n));
			return thisBloblocation - targetBloblocation;
		}
		/**
		 * Returns the difference of the feature value for this cell with the one of
		 * the specified cell. By construction, this operation is anti-symmetric (
		 * <code>A.diffTo(B) = - B.diffTo(A)</code>).
		 * <p>
		 * Will generate a {@link NullPointerException} if one of the cells does not
		 * store the named feature.
		 *
		 * @param s
		 *            the cell to compare to.
		 * @param feature
		 *            the name of the feature to use for calculation.
		 * @return the difference in feature value.
		 */
		public double diffTo( final BCellobject s, final String feature )
		{
			final double f1 = features.get( feature ).doubleValue();
			final double f2 = s.getFeature( feature ).doubleValue();
			return f1 - f2;
		}
		
		public double normalizeDiffTo( final BCellobject s, final String feature )
		{
			final double a = features.get( feature ).doubleValue();
			final double b = s.getFeature( feature ).doubleValue();
			if ( a == -b )
				return 0d;
			
			return Math.abs( a - b ) / ( ( a + b ) / 2 );
		}
		public final static Comparator< BCellobject > featureComparator( final String feature )
		{
			final Comparator< BCellobject > comparator = new Comparator< BCellobject >()
			{
				@Override
				public int compare( final BCellobject o1, final BCellobject o2 )
				{
					final double diff = o2.diffTo( o1, feature );
					if ( diff == 0 )
						return 0;
					else if ( diff < 0 )
						return 1;
					else
						return -1;
				}
			};
			return comparator;
		}
		/** A comparator used to sort cells by ascending time frame. */
		public final static Comparator<BCellobject> frameComparator = featureComparator( POSITION_T );
		/**
		 * Returns the squared distance between two clouds.
		 *
		 * @param target
		 *            the Cloud to compare to.
		 *
		 * @return the distance to the current cloud to target cloud specified.
		 */

		public RealLocalizable add(RealLocalizable source, RealLocalizable target) {
			
			assert source.numDimensions() == target.numDimensions();
			
			long [] Added = new long[source.numDimensions()];
			
			for(int d = 0; d < source.numDimensions(); ++d) {
				
				Added[d] = (long)(0.5 * (source.getDoublePosition(d) + target.getDoublePosition(d)));
				
			}
			
			Point addedPoint = new Point(Added);
			
			return addedPoint;
		}
		
		public double squareDistanceTo(BCellobject target) {
			// Returns squared distance between the source Blob and the target Blob.

			final RealLocalizable sourceLocationStart = currentcell.StartLocation;
			final RealLocalizable sourceLocationEnd = currentcell.EndLocation;
			
			
			final RealLocalizable targetLocationStart = target.currentcell.StartLocation;
			final RealLocalizable targetLocationEnd = target.currentcell.EndLocation;
			
			final RealLocalizable sourceLocation = add(sourceLocationStart, sourceLocationEnd);
			final RealLocalizable targetLocation = add(targetLocationStart, targetLocationEnd);
			
			double distance = 0;
			
			for (int d = 0; d < sourceLocation.numDimensions(); ++d) {

				distance += (sourceLocation.getDoublePosition(d) - targetLocation.getDoublePosition(d)) * (sourceLocation.getDoublePosition(d) - targetLocation.getDoublePosition(d));
			}

			return distance;
		}
		public double DistanceTo(BCellobject target, final double alpha, final double beta) {
			// Returns squared distance between the source Blob and the target Blob.

			final RealLocalizable sourceLocationStart = currentcell.StartLocation;
			final RealLocalizable sourceLocationEnd = currentcell.EndLocation;
			
			
			final RealLocalizable targetLocationStart = target.currentcell.StartLocation;
			final RealLocalizable targetLocationEnd = target.currentcell.EndLocation;
			
			final RealLocalizable sourceLocation = add(sourceLocationStart, sourceLocationEnd);
			final RealLocalizable targetLocation = add(targetLocationStart, targetLocationEnd);

			double distance = 0;

			for (int d = 0; d < sourceLocation.numDimensions(); ++d) {

				distance += (sourceLocation.getDoublePosition(d) - targetLocation.getDoublePosition(d)) * (sourceLocation.getDoublePosition(d) - targetLocation.getDoublePosition(d));
			}

				return distance;
		}

		/**
		 * A comparator used to sort cells by name. The comparison uses numerical
		 * natural sorting, So that "cell_4" comes before "cell_122".
		 */
		public final static Comparator< BCellobject > nameComparator = new Comparator< BCellobject >()
		{
			private final AlphanumComparator comparator = AlphanumComparator.instance;

			@Override
			public int compare( final BCellobject o1, final BCellobject o2 )
			{
				return comparator.compare( o1.getName(), o2.getName() );
			}
		};


		/**
		 * Exposes the storage map of features for this cell. Altering the returned
		 * map will alter the cell.
		 *
		 * @return a map of {@link String}s to {@link Double}s.
		 */
		public Map< String, Double > getFeatures()
		{
			return features;
		}


}
