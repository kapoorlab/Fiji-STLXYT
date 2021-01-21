package coreFunction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JProgressBar;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.view.Views;
import pluginTools.InteractiveAnalysis;
import pluginTools.InteractiveAnalysis.ValueChange;

public class TMobjectCollector {

	final InteractiveAnalysis parent;
	final JProgressBar jpb;

	public TMobjectCollector(final InteractiveAnalysis parent, final JProgressBar jpb) {

		this.parent = parent;
		this.jpb = jpb;

	}

	public void ShowObjectTime() {

		int percent = 0;
		// set up executor service
		for (int t = 1; t < parent.thirdDimensionSize; ++t) {
			parent.thirdDimension = t;
			if(parent.imp.getOverlay()!=null)
				parent.overlay.clear();
			 //we start from zero
			parent.ZTRois = new ArrayList<int[]>();
			parent.updatePreview(ValueChange.THIRDDIMmouse);

			CollectEachCell compute = new CollectEachCell(parent, percent);
			compute.displayCells();

			percent++;

		}

	}

	public void GetPixelList(RandomAccessibleInterval<IntType> intimg) {

		IntType min = new IntType();
		IntType max = new IntType();
		computeMinMax(Views.iterable(intimg), min, max);
		Cursor<IntType> intCursor = Views.iterable(intimg).cursor();
		// Neglect the background class label
		parent.pixellist.clear();

		while (intCursor.hasNext()) {
			intCursor.fwd();
			int i = intCursor.get().get();

			if (!parent.pixellist.contains(i) && i > 0) {
				parent.pixellist.add(i);

			}

		}

	}

	public <T extends Comparable<T> & Type<T>> void computeMinMax(final Iterable<T> input, final T min, final T max) {
		// create a cursor for the image (the order does not matter)
		final Iterator<T> iterator = input.iterator();

		// initialize min and max with the first image value
		T type = iterator.next();

		min.set(type);
		max.set(type);

		// loop over the rest of the data and determine min and max value
		while (iterator.hasNext()) {
			// we need this type more than once
			type = iterator.next();

			if (type.compareTo(min) < 0)
				min.set(type);

			if (type.compareTo(max) > 0)
				max.set(type);
		}
	}


}
