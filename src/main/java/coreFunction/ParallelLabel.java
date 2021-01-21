package coreFunction;

import java.util.ArrayList;

import LabPluginutility.GetNearest;
import pluginTools.InteractiveAnalysis;
import tracking.BCellobject;
import tracking.Cellobject;

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
		  
	
	    try {
			Currentcelllist  = GetNearest.getAllInteriorCells(parent);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      
		
     	for(Cellobject currentbudcell:Currentcelllist) {
			
     		
			BCellobject budncell = new BCellobject(currentbudcell, parent.thirdDimension);
			
            parent.cells.add(budncell, parent.thirdDimension);  
            
		
	}

	
	
	}
	
}
