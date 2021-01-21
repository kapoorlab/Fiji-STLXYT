package LabPluginutility;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import coreFunction.ObjectMaker;
import ij.IJ;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import net.imglib2.Cursor;
import net.imglib2.KDTree;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import pluginTools.InteractiveAnalysis;
import tracking.Cellobject;

public class GetNearest {
	public static ArrayList<Cellobject> getLabelInteriorCells(InteractiveAnalysis parent,
			final RandomAccessibleInterval<IntType> CurrentViewInt, ArrayList<Cellobject> InteriorCells,
			int currentlabel) {

		ArrayList<Cellobject> AllLabelcells = new ArrayList<Cellobject>();

		RandomAccess<IntType> intranac = CurrentViewInt.randomAccess();

		for (Cellobject currentcell : InteriorCells) {

			RealLocalizable cellcenter = currentcell.StartLocation;
			intranac.setPosition((Localizable) cellcenter);
			int label = intranac.get().get();

			if (label == currentlabel) {

				AllLabelcells.add(currentcell);
			}

		}

		return AllLabelcells;

	}

	public static boolean isInterior(List<RealLocalizable> skelpoints, RealLocalizable centerpoint,
			RealLocalizable targetpoint) {

		double distbudcenter = Distance.DistanceSqrt(centerpoint, targetpoint);

		RealLocalizable skelpoint = getNearestskelPoint(skelpoints, targetpoint);

		double distcenterskel = Distance.DistanceSqrt(centerpoint, skelpoint);

		boolean isInterior = (distbudcenter < distcenterskel) ? true : false;

		return isInterior;
	}

	public static RealLocalizable getNearestskelPoint(final List<RealLocalizable> skelPoints,
			RealLocalizable ClickedPoint) {

		RealLocalizable KDtreeroi = null;

		List<RealLocalizable> Allrois = skelPoints;
		final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Allrois.size());
		final List<FlagNode<RealLocalizable>> targetNodes = new ArrayList<FlagNode<RealLocalizable>>(Allrois.size());
		for (int index = 0; index < Allrois.size(); ++index) {

			RealLocalizable r = Allrois.get(index);

			targetCoords.add(new RealPoint(r));

			targetNodes.add(new FlagNode<RealLocalizable>(Allrois.get(index)));

		}

		if (targetNodes.size() > 0 && targetCoords.size() > 0) {

			final KDTree<FlagNode<RealLocalizable>> Tree = new KDTree<FlagNode<RealLocalizable>>(targetNodes,
					targetCoords);

			final NNFlagsearchKDtree<RealLocalizable> Search = new NNFlagsearchKDtree<RealLocalizable>(Tree);

			final RealLocalizable source = ClickedPoint;
			final RealPoint sourceCoords = new RealPoint(source);
			Search.search(sourceCoords);

			final FlagNode<RealLocalizable> targetNode = Search.getSampler().get();

			KDtreeroi = targetNode.getValue();

		}

		return KDtreeroi;

	}

	public static ArrayList<Cellobject> getAllInteriorCells(InteractiveAnalysis parent) throws InterruptedException {

		ArrayList<Cellobject> Allcells = new ArrayList<Cellobject>();
		HashMap<Integer, Boolean> InsideCellList = new HashMap<Integer, Boolean>();

		if (parent.CSVInfoFile.get(parent.thirdDimension) == null) {

			Cursor<IntType> intcursor = Views.iterable(parent.CurrentViewInt).localizingCursor();
			int nThreads = Runtime.getRuntime().availableProcessors();
			// set up executor service
			final ExecutorService taskExecutor = Executors.newFixedThreadPool(nThreads);
			List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
			while (intcursor.hasNext()) {

				intcursor.fwd();

				int labelyellow = intcursor.get().get();
				if (labelyellow > 0) {
					InsideCellList.put(labelyellow, true);
                    
					
				}
			}
		
			for (Integer labelgreen : InsideCellList.keySet()) {
				Boolean isInterior = InsideCellList.get(labelgreen);
				if (isInterior) {
					tasks.add(Executors.callable(new ObjectMaker(parent, Allcells, labelgreen)));

				}
			}

		

				taskExecutor.invokeAll(tasks);

	parent.CSVInfoFile.put(parent.thirdDimension, Allcells);

		}

		else {

			Allcells = parent.CSVInfoFile.get(parent.thirdDimension);

		}

		return Allcells;

	}

	public static <T extends RealType<T>> double[] radiusXYZ(final RandomAccessibleInterval<T> img) {

		double radiusX = img.realMax(0) - img.realMin(0);
		double radiusY = img.realMax(1) - img.realMin(1);
		double radiusZ = img.realMax(2) - img.realMin(2);

		return new double[] { radiusX, radiusY, radiusZ };
	}

	public static <T extends RealType<T>> double[] radiusXY(final RandomAccessibleInterval<T> img) {

		double radiusX = img.realMax(0) - img.realMin(0);
		double radiusY = img.realMax(1) - img.realMin(1);

		return new double[] { radiusX, radiusY, 1 };
	}

	public static <T extends RealType<T>> double Volume(final RandomAccessibleInterval<T> img) {

		Cursor<T> cur = Views.iterable(img).localizingCursor();
		double Vol = 0;

		while (cur.hasNext()) {

			cur.fwd();
			if (cur.get().getRealFloat() > 0)
				Vol++;
		}

		return Vol;

	}

	public static double getIntensity(InteractiveAnalysis parent, RandomAccessibleInterval<BitType> Regionimage) {

		double intensity = 0;

		Cursor<BitType> cursor = Views.iterable(Regionimage).localizingCursor();

		RandomAccess<FloatType> intran = parent.CurrentView.randomAccess();

		while (cursor.hasNext()) {

			cursor.fwd();

			intran.setPosition(cursor);
			if (cursor.get().getInteger() > 0)
				intensity += intran.get().get();

		}

		return intensity;

	}

	public static OvalRoi getNearestRois(ArrayList<OvalRoi> Allrois, double[] Clickedpoint) {

		OvalRoi KDtreeroi = null;

		final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Allrois.size());
		final List<FlagNode<OvalRoi>> targetNodes = new ArrayList<FlagNode<OvalRoi>>(Allrois.size());
		for (int index = 0; index < Allrois.size(); ++index) {

			Roi r = Allrois.get(index);
			Rectangle rect = r.getBounds();

			targetCoords.add(new RealPoint(rect.x + rect.width / 2.0, rect.y + rect.height / 2.0));

			targetNodes.add(new FlagNode<OvalRoi>(Allrois.get(index)));

		}

		if (targetNodes.size() > 0 && targetCoords.size() > 0) {

			final KDTree<FlagNode<OvalRoi>> Tree = new KDTree<FlagNode<OvalRoi>>(targetNodes, targetCoords);

			final NNFlagsearchKDtree<OvalRoi> Search = new NNFlagsearchKDtree<OvalRoi>(Tree);
			final double[] source = Clickedpoint;
			final RealPoint sourceCoords = new RealPoint(source);
			Search.search(sourceCoords);
			final FlagNode<OvalRoi> targetNode = Search.getSampler().get();
			KDtreeroi = targetNode.getValue();

		}

		return KDtreeroi;
	}

	
}
