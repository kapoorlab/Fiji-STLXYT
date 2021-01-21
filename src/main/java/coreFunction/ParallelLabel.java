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
		  
	
	    Currentcelllist  = GetNearest.getAllInteriorCells(parent);
	      
		
		
     	for(Cellobject currentbudcell:Currentcelllist) {
			
			BCellobject budncell = new BCellobject(currentbudcell, parent.thirdDimension);
            parent.cells.add(budncell, parent.thirdDimension);  
            
		
	}

	
	
	}
	
}
