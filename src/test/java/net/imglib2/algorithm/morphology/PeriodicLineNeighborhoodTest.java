package net.imglib2.algorithm.morphology;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;

import java.io.File;

import net.imglib2.algorithm.neighborhood.PeriodicLineShape;
import net.imglib2.algorithm.neighborhood.Shape;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

public class PeriodicLineNeighborhoodTest {

	public static <T extends RealType<T> & NativeType<T>> void main(final String[] args) {

		ImageJ.main(args);
		final File file = new File( "DrosophilaWing.tif" );
		final ImagePlus imp = IJ.openImage(file.getAbsolutePath());
		final Img<T> img = ImagePlusAdapter.wrap(imp);

		final long start = System.currentTimeMillis();

		final Shape shape = new PeriodicLineShape(2, new int[] { 20, -15 });
		final Img< T > target = Erosion.erode( img, shape, 1 );

		final long end = System.currentTimeMillis();

		System.out.println( "Processing done in " + ( end - start ) + " ms." );

		ImageJFunctions.show(img);
		ImageJFunctions.show(target);

	}
}
