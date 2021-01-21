package coreFunction;

import java.util.ArrayList;
import pluginTools.InteractiveAnalysis;
import tracking.BCellobject;
import tracking.Cellobject;
import utility.GetNearest;

public class ParallelLabel implements Runnable {

	final InteractiveAnalysis parent;
	ArrayList<Cellobject> Currentcelllist;
	final String uniqueID;

	
	public ParallelLabel(InteractiveAnalysis parent, ArrayList<Cellobject> Currentcelllist , String uniqueID) {
		
		
		this.Currentcelllist = Currentcelllist;
		this.parent = parent;
		this.uniqueID = uniqueID;
		
		
	}
	
	
	@Override
	public void run() {
		  
		// If we are only collecting cells we only make a list of cell position and other shape information
		if(parent.FilamentMode == false) 
	    Currentcelllist  = GetNearest.getAllInteriorCells(parent, parent.CurrentViewInt);
	      
		// If we are collecitng microtubule ends we make a list of the end points to feed into the tracker
		if(parent.FilamentMode == true)
			Currentcelllist = GetNearest.getMicrotubuleEnds(parent, parent.CurrentViewInt);
		
     	for(Cellobject currentbudcell:Currentcelllist) {
			
			BCellobject budncell = new BCellobject(currentbudcell, parent.thirdDimension);
            parent.cells.add(budncell, parent.thirdDimension);  
            
		
	}

	
	
	}
	
}
