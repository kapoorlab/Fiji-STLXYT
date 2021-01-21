package coreFunction;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import budDetector.Budregionobject;
import kalmanGUI.CovistoKalmanPanel;
import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.algorithm.neighborhood.DiamondShape;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import pluginTools.InteractiveAnalysis;
import tracking.BCellobject;
import tracking.Cellobject;
import tracking.Regionobject;
import tracking.Roiobject;
import utility.Distance;
import utility.GetNearest;
import ij.IJ;
import skeletor.*;
import ij.gui.OvalRoi;

public class FilamentEnder {


	final InteractiveAnalysis parent;
	final int maxlabel;
	int percent;
    ArrayList<Cellobject> celllist = new ArrayList<Cellobject>();

	public FilamentEnder(final InteractiveAnalysis parent, 
			 ArrayList<Cellobject> celllist,final int maxlabel,
			final int percent) {

		this.parent = parent;
		this.maxlabel = maxlabel;
		this.percent = percent;

        this.celllist = celllist;
	}
	
	

	
    public ArrayList<Cellobject> returnBCellobjectlist(){
		
		
		return celllist;
	}
	
	
	public void displays() {

		
		
		
		 ArrayList<Roiobject> Allrois = new ArrayList<Roiobject>();
		String uniqueID = Integer.toString(parent.thirdDimension);
		Iterator<Integer> setiter = parent.pixellist.iterator();
		parent.overlay.clear();
		
		while (setiter.hasNext()) {

			percent++;
			int label = setiter.next();

			if (label > 0) {

				
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
				
				if (parent.jpb != null)
					utility.ProgressBar.SetProgressBar(parent.jpb,
							100 * (percent) / (parent.thirdDimensionSize + parent.pixellist.size()),
							"Computing Filament Ends = " + parent.thirdDimension + "/" + parent.thirdDimensionSize + " Total Filaments = "
									+ (parent.pixellist.size() ));
				// If we did not compute the skeletons before we compute it for each label
				if(parent.OvalRois.get(uniqueID)==null) {
				    
					
					
					List<RealLocalizable> currentskel = SkeletonCreator(PairCurrentViewBit, truths);
					currentrois = DisplayListOverlay.SkeletonEndDisplay(parent, currentskel, label, parent.Color);
					FillArrays(currentskel,truths, currentpoint, label);
					
				}
				
				// If we have a pre-computation/manual marked skeleton point we load it for the current label
				if (parent.OvalRois.get(uniqueID)!=null){
					
							ArrayList<Roiobject> rois = 	parent.OvalRois.get(uniqueID);
							List<RealLocalizable> currentskel = new ArrayList<RealLocalizable>();
							List<RealLocalizable> rejskel = new ArrayList<RealLocalizable>();
							for (Roiobject currentroi: rois) {
								
								if(currentroi.color == parent.Color && currentroi.Label == label) {
									
									double LocationX = currentroi.point.getDoublePosition(0);
									double LocationY = currentroi.point.getDoublePosition(1);
									
								RealPoint point = new RealPoint(new double[] {LocationX, LocationY});
								
								currentskel.add(point);
								
							}
								
	                       if(currentroi.color == parent.RemoveColor && currentroi.Label == label  ) {
									
									double LocationX = currentroi.point.getDoublePosition(0);
									double LocationY = currentroi.point.getDoublePosition(1);
									
								RealPoint point = new RealPoint(new double[] {LocationX, LocationY});
								
								rejskel.add(point);
								
							}
								
							}
							
							currentrois = DisplayListOverlay.SkeletonEndDisplay(parent, currentskel, label, parent.Color);
							rejrois = DisplayListOverlay.SkeletonEndDisplay(parent, rejskel, label, parent.RemoveColor);

							currentskel.addAll(rejskel);
							FillArrays(currentskel,truths, currentpoint, label);
				 }
				 
				 currentrois.addAll(rejrois);
				
				 Allrois.addAll(currentrois);
				
			}
			
		}
	
		 
			parent.OvalRois.put(uniqueID, Allrois);

			parent.imp.updateAndDraw();
	
	}
	
	

	
	public List<RealLocalizable> SkeletonCreator(Regionobject  PairCurrentViewBit, List<RealLocalizable> truths) {
		
		
		// Skeletonize Filaments
		OpService ops = parent.ij.op();
		List<RealLocalizable> skeletonEndPoints = new ArrayList<RealLocalizable>();
		SkeletonCreator<BitType> skelmake = new SkeletonCreator<BitType>(PairCurrentViewBit.Interiorimage, ops);
		skelmake.setClosingRadius(0);
		skelmake.run();
		ArrayList<RandomAccessibleInterval<BitType>> Allskeletons = skelmake.getSkeletons();

		skeletonEndPoints = AnalyzeSkeleton(Allskeletons,truths, ops);
		
		return skeletonEndPoints;
		
	}




	public void FillArrays(List<RealLocalizable> skeletonEndPoints,
			List<RealLocalizable> truths, RealLocalizable centerpoint, int label) {
		
		for (RealLocalizable budpoints : skeletonEndPoints) {

			pointobject point = new pointobject(centerpoint, truths, skeletonEndPoints,
					truths.size() * parent.calibrationX, label,
					new double[] { budpoints.getDoublePosition(0), budpoints.getDoublePosition(1) },
					parent.thirdDimension, 0);

			pointlist.add(point);

		}
	
	          celllist = GetNearest.getAllInteriorCells(parent, parent.CurrentViewInt);

		
	}

	public static ArrayList<RealLocalizable> AnalyzeSkeleton(ArrayList<RandomAccessibleInterval<BitType>> Allskeletons, List<RealLocalizable> truths,
			OpService ops) {

		ArrayList<RealLocalizable> endPoints = new ArrayList<RealLocalizable>();

		for (RandomAccessibleInterval<BitType> skeleton : Allskeletons) {

			SkeletonAnalyzer<BitType> skelanalyze = new SkeletonAnalyzer<BitType>(skeleton, ops);
			RandomAccessibleInterval<BitType> Ends = skelanalyze.getEndpoints();

			Cursor<BitType> skelcursor = Views.iterable(Ends).localizingCursor();
			while (skelcursor.hasNext()) {

				skelcursor.next();

				RealPoint addPoint = new RealPoint(skelcursor);
				if (skelcursor.get().getInteger() > 0) {
					
					
					endPoints.add(addPoint);

				}

			}
			
			
		}
		
		
		return endPoints;

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
		long[] minVal = { Intimg.max(0), Intimg.max(1), Intimg.max(2) };
		long[] maxVal = { Intimg.min(0), Intimg.min(1),Intimg.min(2) };

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
        
	    RandomAccessibleInterval<BitType> smalloutimg = Views.offsetInterval(outimg, minVal, maxVal);
		Point min = new Point(minVal.length);
		// Gradient image gives us the bondary points
		RandomAccessibleInterval<BitType> smallgradimg = GradientmagnitudeImage(smalloutimg);
		RandomAccessibleInterval<BitType> gradimg = GradientmagnitudeImage(outimg);
		Budregionobject smallregion = new Budregionobject(smallgradimg, smalloutimg, min,  size);
		Budregionobject region = new Budregionobject(gradimg, outimg, min,  size);
		return new ValuePair<Budregionobject, Budregionobject>( smallregion, region);

	}
	

}
