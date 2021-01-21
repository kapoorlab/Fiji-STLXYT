package coreFunction;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.view.Views;
import pluginTools.InteractiveAnalysis;
import tracking.BCellobject;
import tracking.Cellobject;
import tracking.Regionobject;
import utility.Distance;
import utility.GetNearest;

public class CollectEachCell  implements Runnable {


		final InteractiveAnalysis parent;
		int percent;
		ArrayList<Cellobject> Celllist = new ArrayList<Cellobject>();
		
		
		public CollectEachCell(final InteractiveAnalysis parent,
				final int percent) {

			this.parent = parent;
			this.percent = percent;
		}
		
		public ArrayList<Cellobject> returnlist(){
			
			
			return Celllist;
		}
		
		public void displayCells() {
			
			String uniqueID = Integer.toString(parent.thirdDimension);
			
			parent.overlay.clear();

			int nThreads = Runtime.getRuntime().availableProcessors();
			// set up executor service
			final ExecutorService taskExecutor = Executors.newFixedThreadPool(nThreads);
			List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
					// For each bud get the list of points

												// For each bud get the list of points
												if (parent.jpb != null)
													utility.ProgressBar.SetProgressBar(parent.jpb,
															100 * (percent) / (parent.thirdDimensionSize + parent.pixellist.size()),
															"Runnint at T = " + parent.thirdDimension + "/" + parent.thirdDimensionSize );

												tasks.add(Executors.callable(new ParallelLabel(parent, Celllist,  uniqueID)));
		try {
				
				taskExecutor.invokeAll(tasks);
		
		} catch (InterruptedException e1) {

			
		}

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

				cursor.get().setReal(Math.sqrt(gradient));

			}

			return gradientimg;
		}

		public static Regionobject BudCurrentLabelBinaryImage(
				RandomAccessibleInterval<IntType> Intimg, int currentLabel) {
			int n = Intimg.numDimensions();
			long[] position = new long[n];
			Cursor<IntType> intCursor = Views.iterable(Intimg).cursor();

			RandomAccessibleInterval<BitType> outimg = new ArrayImgFactory<BitType>().create(Intimg, new BitType());
			RandomAccess<BitType> imageRA = outimg.randomAccess();

			// Go through the whole image and add every pixel, that belongs to
			// the currently processed label
			long[] minVal = { Intimg.max(0), Intimg.max(1), Intimg.max(2)  };
			long[] maxVal = { Intimg.min(0), Intimg.min(1), Intimg.min(2) };

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


		@Override
		public void run() {
			displayCells();
			
		}


		


	

}
