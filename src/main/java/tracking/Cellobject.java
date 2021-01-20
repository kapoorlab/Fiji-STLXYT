package tracking;

import net.imglib2.Localizable;

public class Cellobject {



		
		public final double cellVolume;
		
		public final double cellPerimeter;
		
		public final Localizable StartLocation;
		
		public final Localizable EndLocation;
		
		public final int time;
		
		public final int label;
		
		public final double[] extents;
		
		public final double totalIntensity;
		
		
		
		
		public Cellobject( Localizable StartLocation, Localizable EndLocation, int time, int label, double totalIntensity, double cellVolume, double cellPerimeter, double[] extents ) {
			
			
			this.StartLocation = StartLocation;
			
			this.EndLocation = EndLocation;
			
			this.time = time;
			
			this.label = label;
			
			this.cellVolume = cellVolume;
			
			this.cellPerimeter = cellPerimeter;
			
			this.extents = extents;
			
			this.totalIntensity = totalIntensity;
			
		}
		
	
	
}
