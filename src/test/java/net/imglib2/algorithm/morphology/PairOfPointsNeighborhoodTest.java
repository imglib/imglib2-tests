package net.imglib2.algorithm.morphology;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;

import java.io.File;

import net.imglib2.algorithm.neighborhood.PairOfPointsShape;
import net.imglib2.algorithm.neighborhood.Shape;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

public class PairOfPointsNeighborhoodTest
{
	public static < T extends RealType< T > & NativeType< T >> void main( final String[] args )
	{

		ImageJ.main( args );
		final File file = new File( "DrosophilaWing.tif" );
		// final File file = new File(
		// "/Users/JeanYves/Desktop/Data/brightblobs.tif" );
		final ImagePlus imp = IJ.openImage( file.getAbsolutePath() );
		final Img< T > img = ImagePlusAdapter.wrap( imp );

		final long start = System.currentTimeMillis();

		final Shape shape = new PairOfPointsShape( new long[] { -10, 20 } );
		final Img< T > target = Dilation.dilate( img, shape, 1 );

		final long end = System.currentTimeMillis();

		System.out.println( "Processing done in " + ( end - start ) + " ms." );

		ImageJFunctions.show( img );
		ImageJFunctions.show( target );

		final Shape shape2 = new PairOfPointsShape( new long[] { 10, -20 } );
		final Img< T > target2 = Dilation.dilate( img, shape2, 1 );
		ImageJFunctions.show( target2 );

		final Shape shape3 = new PairOfPointsShape( new long[] { 10, 20 } );
		final Img< T > target3 = Dilation.dilate( img, shape3, 1 );
		ImageJFunctions.show( target3 );

	}

}
