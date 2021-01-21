package LabPluginutility;


import ij.IJ;
import pluginTools.InteractiveAnalysis;

public class ShowView {

	
	final InteractiveAnalysis parent;
	
	
	public ShowView(final InteractiveAnalysis parent) {
		
		this.parent = parent;
		
	}
	
	

	
	
	public void shownewT() {

		if (parent.thirdDimension > parent.thirdDimensionSize) {
			IJ.log("Max time point exceeded, moving to last time point instead");
			parent.thirdDimension = parent.thirdDimensionSize;
			
			
			parent.CurrentView = LabPluginutility.Slicer.getCurrentLabView(parent.originalimg,(int) parent.thirdDimension,
					(int)parent.thirdDimensionSize);
			if(parent.Segoriginalimg!=null)
				parent.CurrentViewInt = LabPluginutility.Slicer.getCurrentLabView(parent.Segoriginalimg,(int) parent.thirdDimension,
						(int)parent.thirdDimensionSize);
			
		} else {

			parent.CurrentView = LabPluginutility.Slicer.getCurrentLabView(parent.originalimg,(int) parent.thirdDimension,
					(int)parent.thirdDimensionSize);
			if(parent.Segoriginalimg!=null)
				parent.CurrentViewInt = LabPluginutility.Slicer.getCurrentLabView(parent.Segoriginalimg,(int) parent.thirdDimension,
						(int)parent.thirdDimensionSize);
			
		}

		
		
	

		
	}
	
}
