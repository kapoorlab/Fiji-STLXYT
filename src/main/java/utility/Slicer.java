package utility;

import mpicbg.imglib.util.Util;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.view.Views;

public class Slicer {

	
	public static  < T extends NumericType< T > & NativeType< T > > RandomAccessibleInterval<T> getCurrentView(RandomAccessibleInterval<T> originalimg,
			int thirdDimension, int thirdDimensionSize) {

		final T type = originalimg.randomAccess().get().createVariable();
		long[] dim = { originalimg.dimension(0), originalimg.dimension(1) };
		final ImgFactory<T> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(originalimg, type);
		RandomAccessibleInterval<T> totalimg = factory.create(dim, type);
		if (thirdDimensionSize == 0) {

			totalimg = originalimg;
		}
		else {
			totalimg = Views.hyperSlice(originalimg,originalimg.numDimensions() - 1 , thirdDimension - 1);

			
		}

		return totalimg;

	}
	
	public static float computeValueFromScrollbarPosition(final int scrollbarPosition, final float min, final float max,
			final int scrollbarSize) {
		return min + (scrollbarPosition / (float) scrollbarSize) * (max - min);
	}

	public static int computeScrollbarPositionFromValue(final float sigma, final float min, final float max,
			final int scrollbarSize) {
		return Util.round(((sigma - min) / (max - min)) * scrollbarSize);
	}
}
