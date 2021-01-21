package coreFunction;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import ij.gui.OvalRoi;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.type.logic.BitType;
import net.imglib2.view.Views;
import pluginTools.InteractiveAnalysis;
import tracking.Roiobject;

public class DisplayListOverlay {

	
	
	// Get all the non-zero co ordinates of a binary image 
	public static ArrayList<RealLocalizable> GetCoordinatesBit(
			RandomAccessibleInterval<BitType> actualRoiimg) {

		ArrayList<RealLocalizable> coordinatelist = new ArrayList<RealLocalizable>();
		int ndims = actualRoiimg.numDimensions();
		final Cursor<BitType> center = Views.iterable(actualRoiimg).localizingCursor();
		while(center.hasNext()) {
			
			center.fwd();
			
			double[] posf = new double[ndims];
			center.localize(posf);
			final RealPoint rpos = new RealPoint(posf);
		
			if(center.get().getInteger() > 0) {
				
					
				coordinatelist.add(rpos);
				
				
			}
			
		}
		
	
	
		
		return coordinatelist;
	}
	


	public static void BoundaryCenterDisplay(InteractiveAnalysis parent, List<RealLocalizable> truths, RealLocalizable currentpoint) {
		
		
		Color displayColor; 
		
		displayColor = Color.GREEN;
		
		for (int i = 0; i < truths.size() ; i += 1) {

			double X = Math.round(truths.get(i).getDoublePosition(0));
			double Y = Math.round(truths.get(i).getDoublePosition(1));


			OvalRoi points =  new OvalRoi((int) X, (int) Y,
					2, 2);
			
			points.setStrokeColor(displayColor);
			parent.overlay.add(points);
		
		}

		OvalRoi oval = new OvalRoi((int) currentpoint.getDoublePosition(0), (int) currentpoint.getDoublePosition(1),
				parent.Dotsize, parent.Dotsize);
		oval.setStrokeWidth(parent.Dotsize);
		oval.setStrokeColor(displayColor);
		parent.overlay.add(oval);
		parent.imp.updateAndDraw();
	}
	
	public static ArrayList<Roiobject> SkeletonEndDisplay(InteractiveAnalysis parent, List<RealLocalizable> skeletonEndPoints, int label, Color color) {
		
		
		 ArrayList<Roiobject> Allrois = new ArrayList<Roiobject>();
		  for (int i = 0; i < skeletonEndPoints.size(); i++) {
				
				double X = Math.round(skeletonEndPoints.get(i).getFloatPosition(0));
				double Y = Math.round(skeletonEndPoints.get(i).getFloatPosition(1));
				
				OvalRoi points =  new OvalRoi((int) X, (int) Y,
						parent.Dotsize, parent.Dotsize);
				points.setStrokeColor(color);
				points.setStrokeWidth(parent.Dotsize);
				parent.overlay.add(points);
				Roiobject roicolor = new Roiobject(color, points, new RealPoint(new double[] {X,Y}), label);
		        		Allrois.add(roicolor);
		        		
			}
		  parent.imp.updateAndDraw();
		return Allrois;
	}

	
	
	
}
