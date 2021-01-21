package coreFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import LabPluginutility.Distance;
import LabPluginutility.GetNearest;
import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import pluginTools.InteractiveAnalysis;
import tracking.Regionobject;
import tracking.Roiobject;
import skeletor.*;

public class FilamentEnder {


	final InteractiveAnalysis parent;
	int label;

	public FilamentEnder(final InteractiveAnalysis parent, 
			final int label) {

		this.parent = parent;
        this.label = label;
	}
	

	
	public Pair<ArrayList<RealLocalizable>, ArrayList<RealLocalizable>>  displays() {

		
		
		
		ArrayList<Roiobject> Allrois = new ArrayList<Roiobject>();
		String uniqueID = Integer.toString(parent.thirdDimension);
		System.out.println("Label:" + label + " time" + parent.thirdDimension);
		
		Pair<ArrayList<RealLocalizable>, ArrayList<RealLocalizable>>  currentskel = new ValuePair<ArrayList<RealLocalizable>, ArrayList<RealLocalizable>>(null, null);

				
				ArrayList<Roiobject> currentrois = new ArrayList<Roiobject>();
				ArrayList<Roiobject> rejrois = new ArrayList<Roiobject>();
				// Input the integer image of bud with the label and output the binary border
				// for that label
				Regionobject PairCurrentViewBit = CurrentLabelBinaryImage(
						parent.CurrentViewInt, label);

				// For each bud get the list of points
				List<RealLocalizable> truths = DisplayListOverlay.GetCoordinatesBit(PairCurrentViewBit.Boundaryimage);
				List<RealLocalizable> bodytruths = DisplayListOverlay.GetCoordinatesBit(PairCurrentViewBit.Interiorimage);
				
				Comparator<RealLocalizable> comp = new Comparator<RealLocalizable>() {

					@Override
					public int compare(final RealLocalizable A, final RealLocalizable B) {
		                 
						int i = 0;
						while (i < A.numDimensions()) {
							if (A.getDoublePosition(i) != B.getDoublePosition(i)) {
								return (int) Math.signum(A.getDoublePosition(i) - B.getDoublePosition(i));
							}
							i++;
						}
						
					
						return A.hashCode() - B.hashCode();

					}
					};
				
				
				 Collections.sort(truths,comp);
					 Collections.sort(bodytruths,comp);
				// Get the center point of each bud
			
				// If we did not compute the skeletons before we compute it for each label
				if(parent.OvalRois.get(uniqueID)==null) {
				    
					
					// End and split points
					currentskel = SkeletonCreator(PairCurrentViewBit, truths);
					currentrois = DisplayListOverlay.SkeletonEndDisplay(parent, currentskel.getA(), label, parent.Color);
					
				}
				
				// If we have a pre-computation/manual marked skeleton point we load it for the current label
				if (parent.OvalRois.get(uniqueID)!=null){
					
							ArrayList<Roiobject> rois = 	parent.OvalRois.get(uniqueID);
							ArrayList<RealLocalizable> currentskelA = new ArrayList<RealLocalizable>();
							ArrayList<RealLocalizable> rejskel = new ArrayList<RealLocalizable>();
							for (Roiobject currentroi: rois) {
								
								if(currentroi.color == parent.Color && currentroi.Label == label) {
									
									double LocationX = currentroi.point.getDoublePosition(0);
									double LocationY = currentroi.point.getDoublePosition(1);
									
								RealPoint point = new RealPoint(new double[] {LocationX, LocationY});
								
								currentskelA.add(point);
								
							}
								
	                       if(currentroi.color == parent.RemoveColor && currentroi.Label == label  ) {
									
									double LocationX = currentroi.point.getDoublePosition(0);
									double LocationY = currentroi.point.getDoublePosition(1);
									
								RealPoint point = new RealPoint(new double[] {LocationX, LocationY});
								
								rejskel.add(point);
								
							}
								
							}
							
							currentrois = DisplayListOverlay.SkeletonEndDisplay(parent, currentskelA, label, parent.Color);
							rejrois = DisplayListOverlay.SkeletonEndDisplay(parent, rejskel, label, parent.RemoveColor);

							currentskelA.addAll(rejskel);
							
							currentskel = new ValuePair<ArrayList<RealLocalizable>, ArrayList<RealLocalizable>>(currentskelA, null);
				 }
				 
				 currentrois.addAll(rejrois);
				
				 Allrois.addAll(currentrois);
				 
				 
					parent.OvalRois.put(uniqueID, Allrois);

					parent.imp.updateAndDraw();
			
					
					return currentskel;
				
			}
			
		
	
	
	
	
	

	
	public Pair<ArrayList<RealLocalizable>, ArrayList<RealLocalizable>> SkeletonCreator(Regionobject  PairCurrentViewBit, List<RealLocalizable> truths) {
		
		
		// Skeletonize Filaments
		OpService ops = parent.ij.op();
		
		SkeletonCreator<BitType> skelmake = new SkeletonCreator<BitType>(PairCurrentViewBit.Interiorimage, ops);
		skelmake.setClosingRadius(0);
		skelmake.run();
		ArrayList<RandomAccessibleInterval<BitType>> Allskeletons = skelmake.getSkeletons();

		Pair<ArrayList<RealLocalizable>, ArrayList<RealLocalizable>> skeletontEndSplitPoints  = AnalyzeSkeleton(Allskeletons,truths, ops);
		
		return skeletontEndSplitPoints;
		
	}





	public static Pair<ArrayList<RealLocalizable>, ArrayList<RealLocalizable>> AnalyzeSkeleton(ArrayList<RandomAccessibleInterval<BitType>> Allskeletons, List<RealLocalizable> truths,
			OpService ops) {

		ArrayList<RealLocalizable> endPoints = new ArrayList<RealLocalizable>();
		
		ArrayList<RealLocalizable> splitPoints = new ArrayList<RealLocalizable>();

		for (RandomAccessibleInterval<BitType> skeleton : Allskeletons) {

			SkeletonAnalyzer<BitType> skelanalyze = new SkeletonAnalyzer<BitType>(skeleton, ops);
			RandomAccessibleInterval<BitType> Ends = skelanalyze.getEndpoints();
			RandomAccessibleInterval<BitType> Splits = skelanalyze.getBranchpoints();

			
			Cursor<BitType> skelcursor = Views.iterable(Ends).localizingCursor();
			while (skelcursor.hasNext()) {

				skelcursor.next();

				RealPoint addPoint = new RealPoint(skelcursor);
				if (skelcursor.get().getInteger() > 0) 
					endPoints.add(addPoint);
			}
			
			Cursor<BitType> skelsplitcursor = Views.iterable(Splits).localizingCursor();
			while (skelsplitcursor.hasNext()) {

				skelsplitcursor.next();

				RealPoint addPoint = new RealPoint(skelsplitcursor);
				if (skelsplitcursor.get().getInteger() > 0) 
					splitPoints.add(addPoint);
			}
			
		}
		
		//Remove close lying split points
		ArrayList<RealLocalizable> CleansplitPoints = RemoveClose( splitPoints, 10);
		ArrayList<RealLocalizable> CleanendPoints = RemoveClose( endPoints, 10);
		Pair<ArrayList<RealLocalizable>, ArrayList<RealLocalizable>> EndSplit = new ValuePair<ArrayList<RealLocalizable>, ArrayList<RealLocalizable>>(CleanendPoints, CleansplitPoints);
		
		return EndSplit;
	}
	
	
	public static ArrayList<RealLocalizable> RemoveClose(ArrayList<RealLocalizable> endPoints, double mindist) {
		
		ArrayList<RealLocalizable> farPoints = new ArrayList<RealLocalizable>(endPoints);
		
		
		for(RealLocalizable addPoint:endPoints) {
			
			
			farPoints.remove(addPoint);
			
			RealLocalizable closestdynamicskel = GetNearest.getNearestskelPoint(farPoints, addPoint);
			
			farPoints.add(addPoint);
			
			
			double closestGrowthPoint = Distance.DistanceSqrt(addPoint, closestdynamicskel);
			if (closestGrowthPoint < mindist && closestGrowthPoint!=0)
				farPoints.remove(closestdynamicskel);
				
		}
		

		return farPoints;
		
	}
	
	
	

	public static RandomAccessibleInterval<BitType> GradientmagnitudeImage(RandomAccessibleInterval<BitType> inputimg) {

		RandomAccessibleInterval<BitType> gradientimg = new ArrayImgFactory<BitType>().create(inputimg, new BitType());
		Cursor<BitType> cursor = Views.iterable(gradientimg).localizingCursor();
		RandomAccessible<BitType> view = Views.extendBorder(inputimg);
		RandomAccess<BitType> randomAccess = view.randomAccess();

		// iterate over all pixels
		while (cursor.hasNext()) {
			// move the cursor to the next pixel
			cursor.fwd();

			// compute gradient and its direction in each dimension
			double gradient = 0;

			for (int d = 0; d < inputimg.numDimensions(); ++d) {
				// set the randomaccess to the location of the cursor
				randomAccess.setPosition(cursor);

				// move one pixel back in dimension d
				randomAccess.bck(d);

				// get the value
				double Back = randomAccess.get().getRealDouble();

				// move twice forward in dimension d, i.e.
				// one pixel above the location of the cursor
				randomAccess.fwd(d);
				randomAccess.fwd(d);

				// get the value
				double Fwd = randomAccess.get().getRealDouble();

				gradient += ((Fwd - Back) * (Fwd - Back)) / 4;

			}

			cursor.get().setReal((Math.sqrt(gradient)));

		}

		return gradientimg;
	}

	public static Regionobject CurrentLabelBinaryImage(
			RandomAccessibleInterval<IntType> Intimg, int currentLabel) {
		int n = Intimg.numDimensions();
		long[] position = new long[n];
		Cursor<IntType> intCursor = Views.iterable(Intimg).cursor();

		RandomAccessibleInterval<BitType> outimg = new ArrayImgFactory<BitType>().create(Intimg, new BitType());
		RandomAccess<BitType> imageRA = outimg.randomAccess();

		// Go through the whole image and add every pixel, that belongs to
		// the currently processed label
		long[] minVal = { Intimg.max(0), Intimg.max(1) };
		long[] maxVal = { Intimg.min(0), Intimg.min(1) };

		while (intCursor.hasNext()) {
			intCursor.fwd();
			imageRA.setPosition(intCursor);
			int i = intCursor.get().get();
			if (i == currentLabel) {

				intCursor.localize(position);
				for (int d = 0; d < n; ++d) {
					if (position[d] < minVal[d]) {
						minVal[d] = position[d];
					}
					if (position[d] > maxVal[d]) {
						maxVal[d] = position[d];
					}

				}

				imageRA.get().setOne();
			} else
				imageRA.get().setZero();

		}
		
		double size = Math.sqrt(Distance.DistanceSq(minVal, maxVal));

	
		Point min = new Point(minVal.length);
		// Gradient image gives us the bondary points
		RandomAccessibleInterval<BitType> gradimg = GradientmagnitudeImage(outimg);
		
		Regionobject region = new Regionobject(gradimg, outimg, min,  size);
		return region;

	}
	public static Pair<Regionobject,Regionobject> DualCurrentLabelBinaryImage(
			RandomAccessibleInterval<IntType> Intimg, int currentLabel) {
		int n = Intimg.numDimensions();
		long[] position = new long[n];
		Cursor<IntType> intCursor = Views.iterable(Intimg).cursor();

		RandomAccessibleInterval<BitType> outimg = new ArrayImgFactory<BitType>().create(Intimg, new BitType());
		RandomAccess<BitType> imageRA = outimg.randomAccess();

		// Go through the whole image and add every pixel, that belongs to
		// the currently processed label
		long[] minVal = { Intimg.max(0), Intimg.max(1) };
		long[] maxVal = { Intimg.min(0), Intimg.min(1) };

		while (intCursor.hasNext()) {
			intCursor.fwd();
			imageRA.setPosition(intCursor);
			int i = intCursor.get().get();
			if (i == currentLabel) {

				intCursor.localize(position);
				for (int d = 0; d < n; ++d) {
					if (position[d] < minVal[d]) {
						minVal[d] = position[d];
					}
					if (position[d] > maxVal[d]) {
						maxVal[d] = position[d];
					}

				}

				imageRA.get().setOne();
			} else
				imageRA.get().setZero();

		}
		
		double size = Math.sqrt(Distance.DistanceSq(minVal, maxVal));
        
	    RandomAccessibleInterval<BitType> smalloutimg = Views.interval(outimg, minVal, maxVal);
		Point min = new Point(minVal.length);
		// Gradient image gives us the bondary points
		RandomAccessibleInterval<BitType> smallgradimg = GradientmagnitudeImage(smalloutimg);
		RandomAccessibleInterval<BitType> gradimg = GradientmagnitudeImage(outimg);
		Regionobject smallregion = new Regionobject(smallgradimg, smalloutimg, min,  size);
		Regionobject region = new Regionobject(gradimg, outimg, min,  size);
		return new ValuePair<Regionobject, Regionobject>( smallregion, region);

	}
	

}
